package com.oldfeel.base;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.oldfeel.utils.DialogUtil;
import com.oldfeel.utils.FileUtil;
import com.oldfeel.utils.ImageUtil;
import com.oldfeel.utils.NetUtil;
import com.oldfeel.utils.StringUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;

import me.nereo.multi_image_selector.MultiImageSelectorActivity;

/**
 * 上传单张图片的fragment
 * Created by oldfeel on 4/4/15.
 */
public class UploadImageFragment extends BaseFragment {
    private static final int REQUEST_SELECT_IMAGE = 3;
    protected int userId;
    protected String imageUrl, fileSavePath, api;

    protected ImageView imageView;
    protected boolean isDestory;
    protected File protraitFile;
    protected Uri origUri;
    protected OnUploadListener onUploadListener;
    protected boolean isCrop = true; // true 为选择图片后剪切,false 为选择图片后压缩
    protected int cropWidth = 500; // 剪切图片的宽度
    private boolean isAutoUpload = true; // true 为自动上传图片

    public static UploadImageFragment newInstace(int defaultImageId, int userId, String fileSavePath, String api) {
        return newInstace(defaultImageId, userId, null, fileSavePath, api);
    }

    public static UploadImageFragment newInstace(int defaultImageId, int userId, String imageUrl, String fileSavePath, String api) {
        UploadImageFragment fragment = new UploadImageFragment();
        fragment.defaultImageId = defaultImageId;
        fragment.userId = userId;
        fragment.imageUrl = imageUrl;
        fragment.fileSavePath = fileSavePath;
        fragment.api = api;
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        imageView = new ImageView(getActivity());
        return imageView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        imageLoader.displayImage(imageUrl, imageView, options);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });
    }

    /**
     * 添加图片
     */
    public void selectImage() {
        Intent intent = new Intent(getActivity(), MultiImageSelectorActivity.class);
        // 是否显示拍摄图片
        intent.putExtra(MultiImageSelectorActivity.EXTRA_SHOW_CAMERA, true);
        // 最大可选择图片数量
        intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_COUNT, 1);
        // 选择模式
        intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_MODE, MultiImageSelectorActivity.MODE_SINGLE);
        startActivityForResult(intent, REQUEST_SELECT_IMAGE);
    }

    /**
     * @return 上传照片的绝对路径
     */
    private Uri getImageTemp() {
        String storageState = Environment.getExternalStorageState();
        if (storageState.equals(Environment.MEDIA_MOUNTED)) {
            File savedir = new File(fileSavePath);
            if (!savedir.exists()) {
                savedir.mkdirs();
            }
        } else {
            showToast("无法保存上传的头像，请检查SD卡是否挂载");
            return null;
        }
        String timeStamp = StringUtil.getTimeStamp();
        // 照片命名
        String cropFileName = userId
                + "_" + timeStamp + ".jpg";
        // 裁剪头像的绝对路径
        String protraitPath = fileSavePath + "/" + cropFileName;
        protraitFile = new File(protraitPath);
        origUri = Uri.fromFile(protraitFile);
        return origUri;
    }

//    public boolean isHaveImage() {
//        if (protraitFile != null && protraitFile.length() > 0) {
//            getToken();
//            return true;
//        }
//        return false;
//    }

    public File getImageFile() {
        return protraitFile;
    }

    /**
     * 上传照片
     */
    public void uploadImage() {
        if (isDestory) {
            return;
        }
        NetUtil net = new NetUtil(getActivity(), api);
        net.setFile("image", protraitFile);
        net.sendPost(new NetUtil.OnJsonResultListener() {
            @Override
            public void onSuccess(String data) {
                DialogUtil.getInstance().cancelPd();
                if (onUploadListener != null) {
                    onUploadListener.OnComplete(protraitFile.getName());
                    protraitFile.deleteOnExit();
                }
            }

            @Override
            public void onFail(int code, String data) {
                DialogUtil.getInstance().cancelPd();
                showToast("上传图片出错,请重新选择");
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        if (requestCode == REQUEST_SELECT_IMAGE) {
            ArrayList<String> mSelectPath = data.getStringArrayListExtra(MultiImageSelectorActivity.EXTRA_RESULT);
            if (mSelectPath != null && mSelectPath.size() > 0) {
                if (isCrop) {
                    cropImage(Uri.fromFile(new File(mSelectPath.get(0))));
                } else {
                    protraitFile = new File(mSelectPath.get(0));
                    String path = "file://" + mSelectPath.get(0);
                    imageLoader.displayImage(path, imageView, options);
                }
            }
        } else if (requestCode == ImageUtil.REQUEST_CODE_GETIMAGE_BYCROP) {
            Bitmap bitmap = data.getParcelableExtra("data");
            if (bitmap != null) {
                imageView.setImageBitmap(bitmap);
                getImageTemp();
                try {
                    FileOutputStream out = new FileOutputStream(protraitFile);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            } else {
                imageLoader.displayImage(Uri.fromFile(protraitFile).toString(), imageView, options);
            }
            if (isAutoUpload) {
                uploadImage();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void cropImage(Uri data) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(data, "image/*");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, getImageTemp());
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);// 裁剪框比例
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", cropWidth);// 输出图片大小
        intent.putExtra("outputY", cropWidth);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, ImageUtil.REQUEST_CODE_GETIMAGE_BYCROP);
    }

    @Override
    public void onDestroy() {
        DialogUtil.getInstance().cancelPd();
        isDestory = true;
        FileUtil.deleteTempImage(getActivity(), fileSavePath, userId + "_");
        super.onDestroy();
    }

    public void setCrop(boolean isCrop, int cropWidth) {
        this.isCrop = isCrop;
        this.cropWidth = cropWidth;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setAutoUpload(boolean isAutoUpload) {
        this.isAutoUpload = isAutoUpload;
    }

    public interface OnUploadListener {
        public void OnComplete(String image);
    }

    public void setOnUploadListener(OnUploadListener onUploadListener) {
        this.onUploadListener = onUploadListener;
    }

    public boolean isNoImageUrl() {
        return imageUrl == null || imageUrl.length() == 0;
    }

    public boolean isHaveUpdate() {
        return protraitFile != null && protraitFile.length() != 0;
    }
}
