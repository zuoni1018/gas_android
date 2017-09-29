package com.oldfeel.utils;

import android.app.Activity;
import android.content.Context;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * 处理edittext的帮助类
 *
 * @author oldfeel
 */
public class ETUtil {
    /**
     * 获取edittext中内容
     *
     * @param et
     * @return
     */
    public static String getString(EditText et) {
        return et.getText().toString().trim();
    }

    public static String getString(Spinner spinner) {
        return spinner.getSelectedItem().toString();
    }

    /**
     * 获取edittext中的int类型
     *
     * @param et
     * @return
     */
    public static int getInt(EditText et) {
        String string = getString(et);
        if (string == null || string.length() == 0) {
            return 0;
        }
        return Integer.valueOf(getString(et));
    }

    /**
     * 判断edittext是否为空
     *
     * @param editTexts
     * @return
     */
    public static boolean isEmpty(TextView... editTexts) {
        for (TextView editText : editTexts) {
            String content = editText.getText().toString();
            if (content == null || content.length() == 0) {
                return true;
            }
        }
        return false;
    }

    /**
     * 将edittext回复为空
     *
     * @param editTexts
     */
    public static void setEmpty(TextView... editTexts) {
        for (TextView editText : editTexts) {
            editText.setText("");
        }
    }

    /**
     * 判断是否为手机号码
     *
     * @param mobile
     * @return
     */
    public static boolean isMobileNO(EditText mobile) {
        if (isEmpty(mobile)) {
            mobile.setError("必填");
            mobile.requestFocus();
            return false;
        }
        if (!StringUtil.isPhone(mobile.getText().toString())) {
            mobile.setError("手机号码格式错误");
            mobile.requestFocus();
            return false;
        }
        return true;
    }

    /**
     * 隐藏软键盘
     *
     * @param editText
     */
    public static void hideSoftKeyboard(Context context, EditText editText) {
        InputMethodManager imm = (InputMethodManager) context
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }

    /**
     * 打卡软键盘
     *
     * @param mEditText 输入框
     * @param mContext  上下文
     */
    public static void openKeybord(EditText mEditText, Context mContext) {
        InputMethodManager imm = (InputMethodManager) mContext
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(mEditText, InputMethodManager.RESULT_SHOWN);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,
                InputMethodManager.HIDE_IMPLICIT_ONLY);
    }

    /**
     * 判断两个edittext内容是否一致
     *
     * @param et1
     * @param et2
     * @return
     */
    public static boolean isEquals(EditText et1, EditText et2) {
        return isEquals("输入的两次密码不同", et1, et2);
    }

    public static boolean isEquals(String error, EditText et1, EditText et2) {
        if (isEmpty(et1) || isEmpty(et2)) {
            return false;
        }
        boolean isEquals = getString(et1).equals(getString(et2));
        if (!isEquals) {
            et2.setError(error);
        }
        return isEquals;
    }

    /**
     * 将edittext中光标移动到尾部
     */
    public static void setEnd(EditText et) {
        if (!isEmpty(et)) {
            et.setSelection(getString(et).length());
        }
    }

    /**
     * 设置editetext是否可编辑
     *
     * @param focusable
     * @param editTexts
     */
    public static void setETFocus(boolean focusable, EditText... editTexts) {
        for (EditText editText : editTexts) {
            editText.setFocusable(focusable);
        }
    }

    /**
     * 判断edittext中内容,如果为空这提示"必填"
     *
     * @param editTexts
     */
    public static boolean isHaveNull(TextView... editTexts) {
        return isHaveNull("必填", editTexts);
    }

    /**
     * 判断edittext中内容,如果为空这提示"error"
     *
     * @param editTexts
     */
    public static boolean isHaveNull(String error, TextView... editTexts) {
        for (TextView editText : editTexts) {
            if (isEmpty(editText)) {
                editText.requestFocus();
                editText.setError(error);
                return true;
            }
        }
        return false;
    }

    public static void setInputType(int inputType, EditText... ets) {
        for (EditText et : ets) {
            et.setInputType(inputType);
        }
    }

    public static boolean isSex(EditText etSex) {
        if (getString(etSex).equals("男") || getString(etSex).equals("女")) {
            return true;
        }
        etSex.setError("性别错误,请输入 男 或者 女");
        return false;
    }

    public static boolean isBirthday(TextView tvDate) {
        String date = tvDate.getText().toString();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        try {
            Date temp = format.parse(date);
            if (temp != null) {
                return true;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean isPassword(EditText etPassword) {
        int length = etPassword.length();
        if (length < 6 || length > 16) {
            etPassword.setError("密码长度为6到16位");
            return false;
        }
        return true;
    }

    public static void setEnabled(boolean isEnable, TextView... textViews) {
        for (TextView textView : textViews) {
            textView.setEnabled(isEnable);
        }
    }

    public static boolean isEmail(EditText email) {
        if (!StringUtil.isEmail(getString(email))) {
            email.setError("邮箱格式错误!");
            email.requestFocus();
            return false;
        }
        return true;
    }

    public static void hideSoftKeyboard(Activity activity) {
        activity.getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    public static void showSoftKeyboard(Activity activity, EditText view) {
        view.requestFocus();
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(view, InputMethodManager.SHOW_FORCED);
    }
}
