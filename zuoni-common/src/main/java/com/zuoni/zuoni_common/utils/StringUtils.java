package com.zuoni.zuoni_common.utils;

import android.text.TextUtils;

import java.math.BigDecimal;

/**
 * Created by zangyi_shuai_ge on 2017/9/28
 */

public class StringUtils {


    public static boolean isEmpty(String input) {
        return input == null || "".equals(input.trim());
    }

    public static int toInt(String str, int defValue) {
        try {
            return Integer.parseInt(str);
        } catch (Exception ignored) {
        }
        return defValue;
    }

    /**
     * 对象转整数
     *
     * @param obj
     * @return 转换异常返回 0
     */
    public static long toLong(String obj) {
        try {
            return Long.parseLong(obj);
        } catch (Exception e) {
        }
        return 0;
    }

    /**
     * @param
     * @return
     * @throws
     * @Title: float2str
     * @Description: 浮点型转换文本，有小数保留小数点
     */
    public static String float2str(float val) {
        /*
        int fv = (int) fvalue;
		return Integer.toString(fv);
		*/
        int fpi = (int) val;
        float fpf = (float) fpi;
        if (fpf != val) {
            BigDecimal bd = new BigDecimal(val);
            BigDecimal bd1 = bd.setScale(2, bd.ROUND_HALF_UP);// 最多显示小数点后两位
            return (String.valueOf(bd1.floatValue()));
        } else {
            return (String.valueOf(fpi));
        }
    }

    /**
     * @param
     * @return
     * @throws
     * @Title: double2str
     * @Description: 浮点型转换文本，有小数保留小数点
     */
    public static String double2str(double dvalue) {
        int dv = (int) dvalue;
        return Integer.toString(dv);
    }

    public static String floatToString(double val) {
        return floatToString(val, 2);
    }

    public static String floatToString(double val, int tailSize) {
        try {
            if (tailSize < 1)
                tailSize = 1;
            if (val % 1.0 != 0) {
                BigDecimal bd = new BigDecimal(val);
                BigDecimal bd1 = bd.setScale(tailSize, bd.ROUND_HALF_UP);// 最多显示小数点后两位
                return bd1.toPlainString();
            } else {
                BigDecimal bdint = new BigDecimal(val);
                return bdint.toPlainString();
            }
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * 使用java正则表达式去掉多余的.与0
     *
     * @param s
     * @return
     */
    public static String subZeroAndDot(String s) {
        if (s.indexOf(".") > 0) {
            s = s.replaceAll("0+?$", "");//去掉多余的0
            s = s.replaceAll("[.]$", "");//如最后一位是.则去掉
        }
        return s;
    }

    /**
     * 半角字符转全角
     *
     * @param input
     * @return
     */
    public static String toDBC(String input) {
        char[] c = input.toCharArray();
        for (int i = 0; i < c.length; i++) {
            if (c[i] == 12288) {
                c[i] = (char) 32;
                continue;
            }
            if (c[i] > 65280 && c[i] < 65375)
                c[i] = (char) (c[i] - 65248);
        }
        return new String(c);
    }

    public static int getWordCount(String s) {
        int length = 0;
        for (int i = 0; i < s.length(); i++) {
            int ascii = Character.codePointAt(s, i);
            if (ascii >= 0 && ascii <= 255)
                length++;
            else
                length += 2;

        }
        return length;
    }

    public static String VerifyName(String name) {
        String result = null;
        if (TextUtils.isEmpty(name)) {
            result = "姓名不能为空";
        } else {
            int strlen = StringUtils.getWordCount(name);
            if (strlen >= 4 && strlen <= 20) {
                if (!name.matches("^(?!_)(?!.*?_$)[a-zA-Z\u4e00-\u9fa5]+$")) {
                    result = "请输入中文或英文名字";
                }
            } else {
                result = "姓名长度为4-20个字符";
            }
        }
        return result;
    }

    public static String VerifyIdentityName(String identityName) {
        String result = null;
        if (TextUtils.isEmpty(identityName)) {
            result = "身份证名不能为空";
        } else {
            int strlen = StringUtils.getWordCount(identityName);
            if (strlen >= 4 && strlen <= 20) {
                if (!identityName
                        .matches("^(?!_)(?!.*?_$)[a-zA-Z\u4e00-\u9fa5]+$")) {
                    result = "请输入中文或英文身份证名";
                }
            } else {
                result = "身份证名长度为4-20个字符";
            }
        }
        return result;
    }

    public static String VerifyPhone(String phone) {
        String result = null;

        if (TextUtils.isEmpty(phone)) {
            result = "手机号码不能为空";
        } else {
            if (phone.length() != 11) {
                result = "请输入正确的手机号码";
            }
        }
        return result;
    }

    public static String verifyIdentity(String identity) {
        String result = null;
        if (TextUtils.isEmpty(identity)) {
            result = "身份证号不能为空";
        }
        if (identity.length() != 15 && identity.length() != 18) {
            result = "身份证号长度无效！";
        }
        return result;
    }

    public static String remove(String resource, char ch) {
        StringBuffer buffer = new StringBuffer();
        int position = 0;
        char currentChar;

        while (position < resource.length()) {
            currentChar = resource.charAt(position++);
            if (currentChar != ch)
                buffer.append(currentChar);
        }
        return buffer.toString();
    }

    /**
     * 给手机号码添加星号
     */
    public static String replaceStartCharPhoneNum(String phoneNum) {
        if (TextUtils.isEmpty(phoneNum)) {
            return "";
        }
        if (phoneNum.length() != 11) {
            return phoneNum;
        }
        StringBuilder stringBuilder = new StringBuilder();
        //138
        for (int i = 0; i < 3; i++) {
            stringBuilder.append(phoneNum.charAt(i));
        }
        //****
        stringBuilder.append("****");
        //6565
        for (int i = 7; i < 11; i++) {
            stringBuilder.append(phoneNum.charAt(i));
        }
        return stringBuilder.toString();
    }

    /**
     * 字符串不能返回null
     */
    public static String unableNullStr(String str) {
        if (str == null) {
            return "";
        } else {
            return str;
        }
    }

    public static boolean isValidPrice(String price) {
        if (!TextUtils.isEmpty(price)) {
            try {
                if (Float.parseFloat(price) <= 999999.99)
                    return true;
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        return false;
    }
}
