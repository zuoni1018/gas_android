package me.nereo.multi_image_selector;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.Toast;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by oldfeel on 16-11-19.
 */

public class OFAdd {
    public static final String IS_CROP = "is_crop";
    public static final String FILE_SAVE_PATH = "file_save_path";
    public static final String CROP_FILE_PATH = "crop_file_path";
    public static final int REQUEST_GETIMAGE_BYCROP = 119;

    public static File cropImage(MultiImageSelectorActivity activity, Uri data, String fileSavePath) {
        Uri orinUri;
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(data, "image/*");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, orinUri = getImageTemp(activity, fileSavePath));
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);// 裁剪框比例
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 480);// 输出图片大小
        intent.putExtra("outputY", 480);
//        intent.putExtra("return-data", true);
        intent.putExtra("scale", true);// 去黑边
        intent.putExtra("scaleUpIfNeeded", true);// 去黑边
        activity.startActivityForResult(intent, REQUEST_GETIMAGE_BYCROP);
        File myFile = new File(orinUri.getPath());
        return myFile;
    }


    /**
     * @param fileSavePath
     * @return 上传照片的绝对路径
     */
    private static Uri getImageTemp(Activity activity, String fileSavePath) {
        String storageState = Environment.getExternalStorageState();
        if (storageState.equals(Environment.MEDIA_MOUNTED)) {
            File savedir = new File(fileSavePath);
            if (!savedir.exists()) {
                savedir.mkdirs();
            }
        } else {
            Toast.makeText(activity, "无法保存上传的头像，请检查SD卡是否挂载", Toast.LENGTH_SHORT).show();
            return null;
        }
        String timeStamp = new SimpleDateFormat("yyyyMMddHHmmssSSS", Locale.getDefault())
                .format(new Date());
        // 照片命名
        String cropFileName = timeStamp + ".jpg";
        // 裁剪头像的绝对路径
        String protraitPath = fileSavePath + "/" + cropFileName;
        File protraitFile = new File(protraitPath);
        Uri origUri = Uri.fromFile(protraitFile);
        return origUri;
    }
}
