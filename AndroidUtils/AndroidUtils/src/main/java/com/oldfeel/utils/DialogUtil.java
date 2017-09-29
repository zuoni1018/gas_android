package com.oldfeel.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.provider.Settings;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;

/**
 * 该类中的toast会覆盖上一个toast
 */
public class DialogUtil {
    private static DialogUtil tu;
    private boolean isShowPd = false;
    private boolean notRemind;
    private Dialog openNetDialog;

    public static DialogUtil getInstance() {
        if (tu == null) {
            tu = new DialogUtil();
        }
        return tu;
    }

    private Dialog dialog;
    private String dialogTitleAndMessage;

    /**
     * 显示一个单一的dialog
     *
     * @param activity
     * @param title
     * @param message
     * @param okListener
     * @param cancelListener
     */
    public void showSingleDialog(Activity activity, String title,
                                 String message, DialogInterface.OnClickListener okListener,
                                 DialogInterface.OnClickListener cancelListener) {
        if (dialog != null &&
                (!(title + message).equals(dialogTitleAndMessage) || // 传递的文字内容不同
                        (dialog.getOwnerActivity() == null || dialog.getOwnerActivity().isFinishing()))) { // 创建 dialog 的 activity 已被销毁
            dialog.cancel();
            dialog = null;
        }

        dialogTitleAndMessage = title + message;

        if (activity == null || activity.isFinishing()) {
            return;
        }

        if (dialog == null) {
            dialog = new AlertDialog.Builder(activity).setTitle(title)
                    .setMessage(message).setPositiveButton("确定", okListener)
                    .setNegativeButton("取消", cancelListener).create();
        }
        dialog.show();
    }

    private static Toast toast;

    public void showToast(Context context, String text) {
        if (context == null) {
            return;
        }
        if (toast != null) {
            toast.cancel();
        }
        toast = Toast.makeText(context, text, Toast.LENGTH_LONG);
        toast.show();
    }

    public void cancelToast() {
        if (toast != null) {
            toast.cancel();
        }
    }

    private ProgressDialog pd;

    public void showPd(Context context, String message, boolean cancel) {
        if (pd != null) {
            pd.cancel();
        }
        pd = new ProgressDialog(context);
        pd.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                isShowPd = false;
            }
        });
        pd.setMessage(message);
        pd.setCanceledOnTouchOutside(cancel);
        pd.show();
        isShowPd = true;
    }

    public void showPd(Context context, String message) {
        showPd(context, message, true);
    }

    public void cancelPd() {
        if (pd != null) {
            pd.cancel();
        }
        isShowPd = false;
    }

    /**
     * 显示一个简易的dialog
     *
     * @param activity
     * @param text
     */
    public AlertDialog showSimpleDialog(Activity activity, String text,
                                        OnClickListener listener) {
        return new AlertDialog.Builder(activity).setMessage(text)
                .setPositiveButton("确定", listener).show();
    }

    /**
     * 显示一个简易的dialog
     *
     * @param activity
     * @param text
     */
    public void showSimpleDialog(Activity activity, String text) {
        showSimpleDialog(activity, text, null);
    }

    /**
     * 日期选择
     *
     * @param activity
     * @param onDatePickerListener
     */
    public void showDatePickerDialog(Activity activity, final OnDatePickerListener onDatePickerListener) {
        Calendar c = Calendar.getInstance();
        new DatePickerDialog(activity, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                onDatePickerListener.onDatePicker(year + "-" + StringUtil.getZeroBefore(++monthOfYear) + "-" + StringUtil.getZeroBefore(dayOfMonth));
            }
        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
    }

    public void showDateDetailPickerDialog(Activity activity, final OnDateDetailPickerListener onDateDetailPickerListener) {
        Calendar c = Calendar.getInstance();
        new DatePickerDialog(activity, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                String yearStr = year + "";
                String monthStr = StringUtil.getZeroBefore(++monthOfYear);
                String dayStr = StringUtil.getZeroBefore(dayOfMonth);
                onDateDetailPickerListener.onDatePicker(yearStr, monthStr, dayStr, yearStr + "-" + monthStr + "-" + dayStr);
            }
        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
    }

    /**
     * 时间选择
     *
     * @param activity
     * @param onDatePickerListener
     */
    public void showTimePicker(Activity activity, final OnDatePickerListener onDatePickerListener) {
        Calendar calendar = Calendar.getInstance();
        new TimePickerDialog(activity, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                onDatePickerListener.onDatePicker(formatTime(hourOfDay) + ":" + formatTime(minute));
            }
        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show();
    }

    private String formatTime(int time) {
        return StringUtil.getZeroBefore(time);
    }

    public boolean isShowPd() {
        return isShowPd;
    }

    public interface OnDatePickerListener {
        public void onDatePicker(String date);
    }

    public interface OnDateDetailPickerListener {
        public void onDatePicker(String year, String month, String day, String date);
    }

    public void showOpenNetDialog(final Activity activity, final NetUtil.OnCancelListener cancelListener) {
        if (notRemind) {
            return;
        }

        if (openNetDialog != null && openNetDialog.isShowing()) {
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("网络木有连接");
        builder.setMessage("是否打开网络连接");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                activity.startActivity(new Intent(
                        Settings.ACTION_WIFI_SETTINGS));
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (cancelListener != null) {
                    cancelListener.cancel();
                }
            }
        });
        builder.setNeutralButton("不再提醒", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                notRemind = true;
            }
        });
        openNetDialog = builder.create();
        openNetDialog.show();
    }
}
