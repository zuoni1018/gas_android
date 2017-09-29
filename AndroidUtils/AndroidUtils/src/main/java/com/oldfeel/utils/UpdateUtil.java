package com.oldfeel.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Yohann on 2015/12/7.
 */
public class UpdateUtil {
    private Context mContext;
    private String version_code;
    private String version_url;
    private String savePath; //保存路径
    private int count;  //计算当前下载数
    private int progress; //计算当前进度
    private int downLoadflag = 1; //判断下载状态
    private static final int DOWNLOAD_CANCLE = 0;
    private static final int DOWNLOADING = 1;
    private static final int DOWNLOAD_OVER = 2;
    private static final int NO_PERMISSION = 3;

    private ProgressBar progressBar;
    private AlertDialog alertDialog;
    private TextView textView;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case DOWNLOADING:
                    progressBar.setProgress(progress);
                    textView.setText(progress + "%");
                    break;
                case DOWNLOAD_OVER:
                    new AlertDialog.Builder(mContext)
                            .setTitle("提示")
                            .setMessage("下载完成是否安装?")
                            .setPositiveButton("安装", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    installAPK();
                                }
                            })
                            .setNegativeButton("取消", null)
                            .show();
                    alertDialog.dismiss();
                    break;
                case DOWNLOAD_CANCLE:
                    alertDialog.dismiss();
                    break;
                case NO_PERMISSION:
                    new AlertDialog.Builder(mContext).setMessage("更新版本需要有存储空间的权限,请在应用权限管理中打开存储空间权限.")
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                }
                            })
                            .setPositiveButton("取消", null)
                            .show();
                    alertDialog.dismiss();
                    break;
            }
        }
    };

    public void installAPK() {
        File apkfile = new File(savePath, getApkName());
        if (!apkfile.exists()) {
            DialogUtil.getInstance().showToast(mContext, "文件不存在:" + apkfile.getAbsolutePath());
            return;
        }
        Intent intent = new Intent(Intent.ACTION_VIEW);
        Uri uri = Uri.parse("file://" + apkfile.toString());
        intent.setDataAndType(uri, "application/vnd.android.package-archive");
        mContext.startActivity(intent);
    }

    private String getApkName() {
        return version_code + ".apk";
    }

    public static void checkUpdate(final Context context, final String savePath, final String downloadUrl, final int version_code, boolean isShowNoUpdate) {
        if (version_code > AppUtil.getVersionCode(context)) {
            new AlertDialog.Builder(context).setTitle("检测到新版本,是否更新?")
                    .setPositiveButton("现在更新", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            UpdateUtil updateUtil = new UpdateUtil();
                            updateUtil.mContext = context;
                            updateUtil.savePath = savePath;
                            updateUtil.version_url = downloadUrl;
                            updateUtil.version_code = version_code + "";
                            updateUtil.showDownLoadAlert();
                        }
                    })
                    .setNegativeButton("取消", null)
                    .show();
        } else if (isShowNoUpdate) {
            noUpdate(context);
        }
    }

    public static void noUpdate(Context context) {
        new AlertDialog.Builder(context).setTitle("当前版本已经是最新版本")
                .setPositiveButton("确定", null)
                .show();
    }

    public void showDownLoadAlert() {
        View view = LayoutInflater.from(mContext).inflate(R.layout.download_alert, null);
        progressBar = (ProgressBar) view.findViewById(R.id.proBar);
        textView = (TextView) view.findViewById(R.id.pst);
        alertDialog = new AlertDialog.Builder(mContext)
                .setTitle("下载中")
                .setView(view)
                .setPositiveButton("取消下载", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        downLoadflag = 0;
                    }
                })
                .show();
        alertDialog.setCanceledOnTouchOutside(false);
        downloadAPK();
    }

    public void downloadAPK() {
        new Thread() {
            @Override
            public void run() {
                try {
                    File dir = new File(savePath);
                    if (!dir.exists()) {
                        if (!dir.mkdirs()) {
                            handler.sendEmptyMessage(NO_PERMISSION);
                            return;
                        }
                    }
                    HttpURLConnection httpURLConnection = (HttpURLConnection) new URL(version_url).openConnection();
                    httpURLConnection.connect();
                    InputStream is = httpURLConnection.getInputStream();
                    final int flength = httpURLConnection.getContentLength();
                    File apkfile = new File(savePath, getApkName());
                    FileOutputStream fos = new FileOutputStream(apkfile);
                    byte[] buffer = new byte[1024];
                    while (true) {
                        int num = is.read(buffer);
                        count += num;
                        if (num < 0) {
                            handler.sendEmptyMessage(DOWNLOAD_OVER);
                            break;
                        }
                        if (downLoadflag == 0) {
                            //取消下载
                            apkfile.delete();
                            handler.sendEmptyMessage(0);
                            break;
                        }
                        progress = (int) (((float) count / flength) * 100);
                        handler.sendEmptyMessage(DOWNLOADING);
                        fos.write(buffer, 0, num);
                    }
                    fos.close();
                    is.close();

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }.start();
    }
}
