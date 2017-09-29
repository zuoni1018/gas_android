package com.oldfeel.base;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.oldfeel.utils.DensityUtil;
import com.oldfeel.utils.DialogUtil;
import com.oldfeel.utils.FileUtil;
import com.oldfeel.utils.ImageUtil;
import com.oldfeel.utils.LogUtil;
import com.oldfeel.utils.NetUtil;
import com.oldfeel.utils.R;
import com.oldfeel.utils.ScreenUtil;
import com.oldfeel.utils.StringUtil;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import easypermissions.AfterPermissionGranted;
import easypermissions.EasyPermissions;
import me.nereo.multi_image_selector.MultiImageSelector;
import me.nereo.multi_image_selector.MultiImageSelectorActivity;

/**
 * 上传多张图片的fragment
 *
 * @author oldfeel
 *         <p/>
 *         Create on: 2014年11月15日
 */
public class UploadMultiImageFragment extends BaseFragment {
    private static final int REQUEST_MULTI_SELECT_IMAGE = 123;
    private static final int RC_READ_EXTERNAL_STORAGE = 124;
    private int userId;
    private String fileSavePath, api;
    private static final int COMP_FAIL = 2; // 压缩失败
    private static final int COMP_MULTI_COMPLETE = 4; // 多张图片压缩完成
    private GridView gvUploadImage;
    private UploadImageAdapter imageAdapter;
    private boolean isDestory;
    private List<String> uploadImageList = new ArrayList<>();
    private OnUploadListener onUploadListener;
    private int maxCount = 6;
    private int numColumns = 3;
    private String uploadImages = "";
    private NetUtil netUtil;
    private int imageWidth;
    private List<String> netImages; // 网络图片
    private int netImageCount = 0;
    private String[] images;

    public static UploadMultiImageFragment newInstance(int defaultImageId, int userId, String fileSavePath) {
        UploadMultiImageFragment fragment = new UploadMultiImageFragment();
        fragment.defaultImageId = defaultImageId;
        fragment.userId = userId;
        fragment.fileSavePath = fileSavePath;
        return fragment;
    }

    public static UploadMultiImageFragment newInstance(int defaultImageId, int userId, String fileSavePath, String api) {
        UploadMultiImageFragment fragment = newInstance(defaultImageId, userId, fileSavePath);
        fragment.api = api;
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.upload_multi_image_fragment, container, false);
        gvUploadImage = (GridView) view
                .findViewById(R.id.upload_multi_image_grid);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        imageAdapter = new UploadImageAdapter();
        gvUploadImage.setNumColumns(numColumns);
        gvUploadImage.setAdapter(imageAdapter);

        imageWidth = (ScreenUtil.getScreenWidth(getActivity()) - // 屏幕宽度
                DensityUtil.dip2px(getActivity(), 32) - // gridview 内边距
                (DensityUtil.dip2px(getActivity(), 4) * (numColumns - 1))) // item 横向间隔
                / numColumns;

        setImages(images);

        checkREAD_EXTERNAL_STORAGE();
    }

    @AfterPermissionGranted(RC_READ_EXTERNAL_STORAGE)
    private void checkREAD_EXTERNAL_STORAGE() {
        if (!EasyPermissions.hasPermissions(getActivity(), Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            EasyPermissions.requestPermissions(getActivity(), "上传照片需要拍照和读写sd卡的权限",
                    RC_READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, getActivity());
    }

    public void setNumColumns(int numColumns) {
        this.numColumns = numColumns;
    }

    public void setMaxCount(int maxCount) {
        this.maxCount = maxCount;
    }

    /**
     * 添加图片
     */
    public void selectImage() {
        int netImagesCount = 0;
        if (netImages != null) {
            netImagesCount = netImages.size();
        }
        MultiImageSelector selector = MultiImageSelector.create()
                .multi()
                .showCamera(true)
                .count(maxCount - netImagesCount)
                .origin(imageAdapter.getSelectPath());
        selector.start(this, REQUEST_MULTI_SELECT_IMAGE);
        if (imageAdapter.getCount() != 1) {
        }
    }

    /**
     * 获取上传照片的绝对路径
     */
    private String getImageTemp() {
        String storageState = Environment.getExternalStorageState();
        if (storageState.equals(Environment.MEDIA_MOUNTED)) {
            File savedir = new File(fileSavePath);
            if (!savedir.exists()) {
                savedir.mkdirs();
            }
        } else {
            showToast("无法保存上传的图片，请检查SD卡是否挂载");
            return null;
        }
        String timeStamp = StringUtil.getTimeStamp();
        // 照片命名
        String cropFileName = userId
                + "_" + timeStamp + ".jpg";
        // 图片的绝对路径
        return fileSavePath + "/" + cropFileName;
    }

    /**
     * 如果有图片的话就启动压缩
     *
     * @return
     */
    public boolean isHaveImage() {
        if (imageAdapter.getCount() > 1) {
            compressionImage(imageAdapter.getSelectPath(), "正在压缩图片...");
            return true;
        }
        return false;
    }

    public void startCompression(OnCompListener onCompListener) {
        this.onCompListener = onCompListener;
        compressionImage(imageAdapter.getSelectPath(), null);
    }

    /**
     * 上传照片
     */
    public void uploadImage(final int position) {
        if (isDestory) {
            return;
        }
        if (position > uploadImageList.size() - 1) {
            DialogUtil.getInstance().cancelPd();
            if (onUploadListener != null) {
                LogUtil.showLog(uploadImages);
                onUploadListener.onComplete(uploadImages);
            }
            return;
        }
        final File file = new File(uploadImageList.get(position));
        if (file == null || file.length() == 0) {
            uploadImage(position + 1);
            return;
        }
        if (netUtil == null) {
            netUtil = new NetUtil(getActivity(), api);
        }
        netUtil.setFile("image", file);
        netUtil.sendPost(new NetUtil.OnJsonResultListener() {
            @Override
            public void onSuccess(String data) {
                if (uploadImages.length() == 0) {
                    uploadImages = data;
                } else {
                    uploadImages += "," + data;
                }
                uploadImage(position + 1);
                file.delete();
            }

            @Override
            public void onFail(int code, String data) {
                LogUtil.showLog("upload image onFail " + data);
            }
        });
    }

    public void uploadImage(final int position, final OnUploadListener onUploadListener, final NetUtil netUtil, final String key) {
        if (isDestory) {
            return;
        }
        if (position > uploadImageList.size() - 1) { // 上传完成
            onUploadListener.uploadSuccess(uploadImageList.size(), uploadImageList.size());
            return;
        }
        final File file = new File(uploadImageList.get(position));
        if (file.length() == 0) {
            showToast("文件 " + uploadImageList.get(position) + " 不存在");
            uploadImage(position + 1);
            return;
        }
        netUtil.setFile(key, file);
        netUtil.sendPost(new NetUtil.OnJsonResultListener() {
            @Override
            public void onSuccess(String data) {
                if (onUploadListener != null) {
                    onUploadListener.uploadSuccess(position + 1, uploadImageList.size());
                }
                uploadImage(position + 1, onUploadListener, netUtil, key);
                file.delete();
            }

            @Override
            public void onFail(int code, String data) {
                if (onUploadListener != null) {
                    onUploadListener.uploadSuccess(position + 1, uploadImageList.size());
                }
                uploadImage(position + 1, onUploadListener, netUtil, key);
                file.delete();
                showToast("上传失败" + file.getAbsolutePath());
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_MULTI_SELECT_IMAGE) {
            ArrayList<String> selectPath = data.getStringArrayListExtra(MultiImageSelectorActivity.EXTRA_RESULT);
            imageAdapter.setSelectPath(selectPath);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void compressionImage(final ArrayList<String> selectPath, String message) {
        if (message != null) {
            DialogUtil.getInstance().showPd(getActivity(), message, false);
        }
        uploadImageList.clear();
        uploadImages = "";
        new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    for (int i = 0; i < selectPath.size(); i++) {
                        String path = selectPath.get(i);
                        if (path.startsWith("http")) {
                            continue;
                        }
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(
                                getActivity().getContentResolver(), Uri.fromFile(new File(path)));
                        int degree = ImageUtil
                                .readPictureDegree(path);
                        if (degree != 0) {
                            bitmap = ImageUtil.rotaingImageView(degree, bitmap);
                        }
                        String tempPath = getImageTemp();
                        ImageUtil.compAndSaveImage(getActivity(),
                                tempPath, bitmap);
                        uploadImageList.add(tempPath);
                    }
                    handler.sendEmptyMessage(COMP_MULTI_COMPLETE);
                } catch (FileNotFoundException e) {
                    handler.sendEmptyMessage(COMP_FAIL);
                    e.printStackTrace();
                } catch (IOException e) {
                    handler.sendEmptyMessage(COMP_FAIL);
                    e.printStackTrace();
                }
            }
        }.start();
    }

    /**
     * 压缩图片
     */
    Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case COMP_FAIL:
                    showToast("压缩失败");
                    DialogUtil.getInstance().cancelPd();
                    break;
                case COMP_MULTI_COMPLETE:
                    DialogUtil.getInstance().cancelPd();
                    if (onCompListener != null) {
                        onCompListener.onCompComplete(uploadImageList);
                    }
                    if (!StringUtil.isEmpty(api)) {
                        uploadImage(0);
                    }
                    break;
                default:
                    break;
            }
        }

        ;
    };

    public void setNetImages(List<String> netImages) {
        this.netImages = netImages;
        this.netImageCount = netImages == null ? 0 : netImages.size();
    }

    public void deleteNetImage(String netImageUrl) {
        netImages.remove(netImageUrl);
        netImageCount--;
        imageAdapter.notifyDataSetChanged();
    }

    /**
     * 压缩监听
     */
    public interface OnCompListener {
        /**
         * 压缩完成
         *
         * @param uploadImageList
         */
        public void onCompComplete(List<String> uploadImageList);
    }

    OnCompListener onCompListener;

    public void setOnCompListener(OnCompListener onCompListener) {
        this.onCompListener = onCompListener;
    }

    @Override
    public void onDestroy() {
        DialogUtil.getInstance().cancelPd();
        isDestory = true;
        FileUtil.deleteTempImage(getActivity(), fileSavePath, userId + "_");
        super.onDestroy();
    }

    public int getImageCount() {
        return imageAdapter.getSelectPath().size();
    }

    public int getUploadCount() {
        return uploadImageList.size();
    }

    public void setImages(String[] images) {
        if (images == null || images.length == 0) {
            return;
        }
        this.images = images;
        if (imageAdapter != null) {
            ArrayList<String> temp = new ArrayList<>();
            for (int i = 0; i < images.length; i++) {
                String path = images[i].replace("file://", "");
                temp.add(path);
            }
            imageAdapter.setSelectPath(temp);
        }
    }

    /**
     * 图片名称,多张图片用逗号隔开
     *
     * @return
     */
    public String getImages() {
        StringBuilder sb = new StringBuilder();
        String[] images = imageAdapter.getImages();
        for (int i = 0; i < images.length; i++) {
            sb.append(i == 0 ? images[i] : "," + images[i]);
        }
        return sb.toString();
    }

    public List<String> getImageList() {
        return imageAdapter.getSelectPath();
    }

    public void clear() {
        imageAdapter.clear();
    }

    public class UploadImageAdapter extends BaseBaseAdapter<File> {

        @Override
        public int getCount() {
            int count = netImageCount; // 网络图片
            if (list != null) {
                count += list.size(); // 本地图片
            }
            if (count < maxCount) {
                count += 1;// 添加按钮
            }
            return count;
        }

        @Override
        public View getView(LayoutInflater inflater, ViewGroup parent, final int position) {
            View view = inflater.inflate(
                    R.layout.item_upload_multi_image, null);
            ImageView ivImage = (ImageView) view
                    .findViewById(R.id.item_upload_multi_image);
            ImageButton ibDelete = (ImageButton) view.findViewById(R.id.item_upload_multi_image_delete);

            ibDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (position < netImageCount) {
                        if (onNetImageDeleteListener != null) {
                            onNetImageDeleteListener.onDelete(netImages.get(position));
                        }
                    } else {
                        list.remove(position - netImageCount);
                        notifyDataSetChanged();
                    }
                }
            });

            if (getCount() < maxCount && position == getCount() - 1) { // 添加图片按钮
                ibDelete.setVisibility(View.GONE);
                ivImage.setImageResource(defaultImageId);
                ivImage.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        if (onAddClickListener != null) {
                            onAddClickListener.onAddClicked();
                        } else {
                            selectImage();
                        }
                    }
                });
            } else {
                ivImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getActivity(), LookBigImage.class);
                        intent.putExtra("images", getImages());
                        intent.putExtra("position", position);
                        startActivity(intent);
                    }
                });
                // 显示图片
                Picasso.with(getActivity())
                        .load(getImage(position))
                        .placeholder(R.drawable.default_error)
                        .resize(imageWidth, imageWidth)
                        .centerCrop()
                        .into(ivImage);
            }
            return view;
        }

        private Uri getImage(int position) {
            if (position < netImageCount) {
                return Uri.parse(netImages.get(position));
            }
            return Uri.fromFile(list.get(position - netImageCount));
        }

        public String[] getImages() {
            String[] images = new String[getCount() - 1];
            if (netImages != null) {
                for (int i = 0; i < netImages.size(); i++) {
                    images[i] = netImages.get(i);
                }
            }
            for (int i = netImageCount; i < list.size(); i++) {
                images[i + netImageCount] = Uri.fromFile(list.get(i)).toString();
            }
            return images;
        }

        public void setSelectPath(ArrayList<String> mSelectPath) {
            list.clear();
            for (int i = 0; i < mSelectPath.size(); i++) {
                list.add(new File(mSelectPath.get(i)));
            }
            notifyDataSetChanged();
        }

        public ArrayList<String> getSelectPath() {
            ArrayList<String> selectPath = new ArrayList<>();
            for (int i = 0; i < list.size(); i++) {
                selectPath.add(list.get(i).getAbsolutePath());
            }
            return selectPath;
        }
    }

    public interface OnNetImageDeleteListener {
        public void onDelete(String netImageUrl);
    }

    OnNetImageDeleteListener onNetImageDeleteListener;

    public void setOnNetImageDeleteListener(OnNetImageDeleteListener onNetImageDeleteListener) {
        this.onNetImageDeleteListener = onNetImageDeleteListener;
    }

    public interface OnAddClickListener {
        public void onAddClicked();
    }

    OnAddClickListener onAddClickListener;

    public void setOnAddClickListener(OnAddClickListener onAddClickListener) {
        this.onAddClickListener = onAddClickListener;
    }

    public interface OnUploadListener {
        public void uploadSuccess(int uploadedCount, int allCount);

        public void onComplete(String images);
    }

    public void setOnUploadListener(OnUploadListener onUploadListener) {
        this.onUploadListener = onUploadListener;
    }
}
