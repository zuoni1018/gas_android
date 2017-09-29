package com.oldfeel.view;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.AttributeSet;
import android.view.View;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.oldfeel.utils.DialogUtil;
import com.oldfeel.utils.LogUtil;
import com.oldfeel.utils.StringUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import easypermissions.EasyPermissions;
import me.nereo.multi_image_selector.MultiImageSelector;
import me.nereo.multi_image_selector.MultiImageSelectorActivity;

/**
 * Created by oldfeel on 4/25/15.
 */
public class UploadImageView extends CircleImageView {
    public static final int REQUEST_SELECT_IMAGE = 10;
    public static final int REQUEST_GETIMAGE_BYCROP = 100;
    private static final int EXTERNAL_STORAGE_REQ_CODE = 200;
    private int userId;
    private String fileSavePath;
    protected String imageUrl;

    protected File protraitFile;
    protected Uri origUri;
    protected boolean isCrop = false; // true 为选择图片后剪切,false 为选择图片后压缩
    protected int cropWidth = 500; // 剪切图片的宽度

    protected DisplayImageOptions options;
    protected ImageLoader imageLoader = ImageLoader.getInstance();

    private int position;
    private int cropHeight;

    public UploadImageView(Context context) {
        super(context);
    }

    public UploadImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public UploadImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void init(int defaultImageId, int userId, String imageUrl, String fileSavePath, int position) {
        this.userId = userId;
        this.fileSavePath = fileSavePath;
        this.position = position;
        this.imageUrl = imageUrl;
        options = new DisplayImageOptions.Builder()
                .showImageForEmptyUri(defaultImageId).showImageOnFail(defaultImageId)
                .cacheInMemory(true).cacheOnDisk(true).build();
        imageLoader.displayImage(imageUrl, this, options);
        setOnClickListener(new OnClickListener() {
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
        LogUtil.showLog("select image");
        if (EasyPermissions.hasPermissions(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            doSelectImage();
        } else {
            EasyPermissions.requestPermissions((Activity) getContext(), "选择图片需要存储权限", EXTERNAL_STORAGE_REQ_CODE, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
    }

    private void doSelectImage() {
        MultiImageSelector.create()
                .single()
                .showCamera(true)
                .start((Activity) getContext(), REQUEST_SELECT_IMAGE + position);
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

    private void showToast(String s) {
        DialogUtil.getInstance().showToast(getContext(), s);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        if (requestCode == REQUEST_SELECT_IMAGE + position) {
            ArrayList<String> mSelectPath = data.getStringArrayListExtra(MultiImageSelectorActivity.EXTRA_RESULT);
            if (mSelectPath != null && mSelectPath.size() > 0) {
                if (isCrop) {
                    cropImage(Uri.fromFile(new File(mSelectPath.get(0))));
                } else {
                    protraitFile = new File(mSelectPath.get(0));
                    String path = "file://" + mSelectPath.get(0);
                    imageLoader.displayImage(path, this, options);
                    if (onCropListener != null) {
                        onCropListener.selected(protraitFile);
                    }
                }
            }
        } else if (requestCode == REQUEST_GETIMAGE_BYCROP + position) {
            Bitmap bitmap = data.getParcelableExtra("data");
            if (bitmap != null) {
                setImageBitmap(bitmap);
                getImageTemp();
                try {
                    FileOutputStream out = new FileOutputStream(protraitFile);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            } else {
                imageLoader.displayImage(Uri.fromFile(protraitFile).toString(), this, options);
            }
            if (onCropListener != null) {
                onCropListener.onCrop(protraitFile);
            }
        } else if (requestCode == EXTERNAL_STORAGE_REQ_CODE) {
            doSelectImage();
        }
    }

    private void cropImage(Uri data) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(data, "image/*");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, getImageTemp());
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", cropWidth);// 裁剪框比例
        intent.putExtra("aspectY", cropHeight);
        intent.putExtra("outputX", cropWidth);// 输出图片大小
        intent.putExtra("outputY", cropHeight);
//        intent.putExtra("return-data", true);
        intent.putExtra("scale", true);// 去黑边
        intent.putExtra("scaleUpIfNeeded", true);// 去黑边
        ((Activity) getContext()).startActivityForResult(intent, REQUEST_GETIMAGE_BYCROP + position);
    }

    public void setCrop(boolean isCrop, int cropWidth) {
        setCrop(isCrop, cropWidth, cropWidth);
    }

    public void setCrop(boolean isCrop, int cropWidth, int cropHeight) {
        this.isCrop = isCrop;
        this.cropWidth = cropWidth;
        this.cropHeight = cropHeight;
    }

    public interface OnCropListener {
        /**
         * 剪切图片
         *
         * @param file
         */
        public void onCrop(File file);

        /**
         * 选择图片
         *
         * @param file
         */
        public void selected(File file);
    }

    OnCropListener onCropListener;

    public void setOnCropListener(OnCropListener onCropListener) {
        this.onCropListener = onCropListener;
    }
}
