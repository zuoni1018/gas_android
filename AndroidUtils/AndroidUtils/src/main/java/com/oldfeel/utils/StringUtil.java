package com.oldfeel.utils;

import android.graphics.Color;
import android.util.Base64;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.security.MessageDigest;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 字符串操作工具包
 *
 * @author liux (http://my.oschina.net/liux)
 * @version 1.0
 * @created 2012-3-21
 */
public class StringUtil {
    private final static ThreadLocal<SimpleDateFormat> dateFormater = new ThreadLocal<SimpleDateFormat>() {
        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        }
    };

    private final static ThreadLocal<SimpleDateFormat> dateFormater2 = new ThreadLocal<SimpleDateFormat>() {
        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat("yyyy-MM-dd");
        }
    };

    /**
     * 将字符串转位日期类型
     *
     * @param sdate
     * @return
     */
    public static Date toDate(String sdate) {
        if (sdate.length() > 19) {
            sdate = sdate.substring(0, 19);
            sdate = sdate.replace("T", " ");
        }
        try {
            if (sdate.length() > 10) {
                return dateFormater.get().parse(sdate);
            } else {
                return dateFormater2.get().parse(sdate);
            }
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 以友好的方式显示时间
     *
     * @param sdate
     * @return
     */
    public static String friendly_time(String sdate) {
        Date time = toDate(sdate);
        if (time == null) {
            return "Unknown";
        }
        String ftime = "";
        Calendar cal = Calendar.getInstance();

        // 判断是否是同一天
        String curDate = dateFormater2.get().format(cal.getTime());
        String paramDate = dateFormater2.get().format(time);

        if (curDate.equals(paramDate)) {
            int hour = (int) ((cal.getTimeInMillis() - time.getTime()) / 3600000);
            if (hour == 0) {
                ftime = Math.max((cal.getTimeInMillis() - time.getTime()) / 60000, 1) + "分钟前";
            } else {
                ftime = hour + "小时前";
            }
            return ftime;
        }

        int days = (int) ((cal.getTimeInMillis() - time.getTime()) / 86400000);
        if (days == 0) {
            int hour = (int) ((cal.getTimeInMillis() - time.getTime()) / 3600000);
            if (hour == 0) {
                ftime = Math.max((cal.getTimeInMillis() - time.getTime()) / 60000, 1) + "分钟前";
            } else {
                ftime = hour + "小时前";
            }
            return ftime;
        }
        try {
            cal.setTime(dateFormater2.get().parse(sdate));
            long time1 = cal.getTimeInMillis();
            cal.setTime(dateFormater2.get().parse(getCurrentDate()));
            long time2 = cal.getTimeInMillis();
            days = (int) ((time2 - time1) / (1000 * 3600 * 24));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (days == 1) {
            ftime = "昨天";
        } else if (days == 2) {
            ftime = "前天";
        } else if (days > 2 && days <= 10) {
            ftime = days + "天前";
        } else if (days > 10) {
            ftime = dateFormater2.get().format(time);
        }
        return ftime;
    }

    /**
     * 判断给定字符串时间是否为今日
     *
     * @param sdate
     * @return boolean
     */
    public static boolean isToday(String sdate) {
        boolean b = false;
        Date time = toDate(sdate);
        Date today = new Date();
        if (time != null) {
            String nowDate = dateFormater2.get().format(today);
            String timeDate = dateFormater2.get().format(time);
            if (nowDate.equals(timeDate)) {
                b = true;
            }
        }
        return b;
    }

    /**
     * 返回long类型的今天的日期
     *
     * @return
     */
    public static long getToday() {
        Calendar cal = Calendar.getInstance();
        String curDate = dateFormater2.get().format(cal.getTime());
        curDate = curDate.replace("-", "");
        return Long.parseLong(curDate);
    }

    /**
     * 判断给定字符串是否空白串。 空白串是指由空格、制表符、回车符、换行符组成的字符串 若输入字符串为null或空字符串，返回true
     *
     * @param string
     * @return boolean
     */
    public static boolean isEmpty(String string) {
        if (string == null || "".equals(string))
            return true;

        for (int i = 0; i < string.length(); i++) {
            char c = string.charAt(i);
            if (c != ' ' && c != '\t' && c != '\r' && c != '\n') {
                return false;
            }
        }
        return true;
    }

    /**
     * 判断是不是一个合法的电子邮件地址
     *
     * @param email
     * @return
     */
    public static boolean isEmail(String email) {
        if (email == null || email.trim().length() == 0)
            return false;
        Pattern emailer = Pattern
                .compile("\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*");
        return emailer.matcher(email).matches();
    }

    /**
     * 将一个InputStream流转换成字符串
     *
     * @param is
     * @return
     */
    public static String toConvertString(InputStream is) {
        StringBuffer res = new StringBuffer();
        InputStreamReader isr = new InputStreamReader(is);
        BufferedReader read = new BufferedReader(isr);
        try {
            String line;
            line = read.readLine();
            while (line != null) {
                res.append(line);
                line = read.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (null != isr) {
                    isr.close();
                    isr.close();
                }
                if (null != read) {
                    read.close();
                }
                if (null != is) {
                    is.close();
                }
            } catch (IOException e) {
            }
        }
        return res.toString();
    }

    /**
     * 判断是否为手机号码
     *
     * @param mobile
     * @return
     */
    public static boolean isPhone(String mobile) {
        if (StringUtil.isEmpty(mobile)) {
            return false;
        }
        String REGEX_MOBILE = "^[1][3,4,5,7,8][0-9]{9}$";

        return Pattern.matches(REGEX_MOBILE, mobile);
    }

    /**
     * 获取当前日期,yyyy-MM-dd格式
     *
     * @return
     */
    public static String getCurrentDate() {
        String date = dateFormater2.get().format(new Date());
        return date;
    }

    /**
     * 获取当前时间
     *
     * @return
     */
    public static String getCurrentTime() {
        return dateFormater.get().format(new Date());
    }

    /**
     * 获取时间戳
     *
     * @return
     */
    public static String getTimeStamp() {
        return new SimpleDateFormat("yyyyMMddHHmmssSSS", Locale.getDefault())
                .format(new Date());
    }

    /**
     * @param url 文件网址
     * @return 文件名
     */
    public static String getFileName(String url) {
        if (StringUtil.isEmpty(url) || !url.contains("/")) {
            return url;
        }
        return url.substring(url.lastIndexOf("/") + 1);
    }

    public static String getFileType(String url) {
        if (StringUtil.isEmpty(url) || !url.contains(".")) {
            return url;
        }
        return url.substring(url.lastIndexOf(".") + 1);
    }

    /**
     * 获取带单位的距离
     *
     * @param distance
     * @return
     */
    public static String friendly_distance(double distance) {
        if (distance == 0) {
            return "附近100米";
        }
        if (distance / 1000 > 1) {
            return new java.text.DecimalFormat("#.00").format(distance / 1000)
                    + "KM";
        }
        return new java.text.DecimalFormat("#.00").format(distance) + "M";
    }

    public static String getWeek(int week) {
        switch (week) {
            case 0:
                return "周日";
            case 1:
                return "周一";
            case 2:
                return "周二";
            case 3:
                return "周三";
            case 4:
                return "周四";
            case 5:
                return "周五";
            case 6:
                return "周六";
            default:
                return "未知日期";
        }
    }

    /**
     * @param date yyyy-MM-dd 格式
     * @return
     */
    public static String getWeekByDate(String date) {
        String[] weeks = {"周日", "周一", "周二", "周三", "周四", "周五", "周六"};
        Calendar cal = Calendar.getInstance();
        try {
            if (date.length() == 10) {
                cal.setTime(dateFormater2.get().parse(date));
            } else {
                cal.setTime(dateFormater.get().parse(date));
            }
            int week_index = cal.get(Calendar.DAY_OF_WEEK) - 1;
            if (week_index < 0) {
                week_index = 0;
            }
            return weeks[week_index];
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "未知日期";
    }

    public static String formatPhone(String phone) {
        if (!isPhone(phone)) {
            return "未知号码";
        }
        return phone.substring(0, 3) + "****" + phone.substring(7, 11);
    }

    /**
     * 获取日期所在周的周一
     *
     * @param date
     * @return
     */
    public static String getWeekMonday(String date) {
        if (date != null && date.length() >= 10) {
            date = date.substring(0, 10);
            SimpleDateFormat format = dateFormater2.get();
            try {
                Date d = format.parse(date);
                Calendar c = Calendar.getInstance();
                c.setTime(d);
                c.setFirstDayOfWeek(Calendar.MONDAY);
                c.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
                return format.format(c.getTime());
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static String getDateString(Date date) {
        return dateFormater2.get().format(date);
    }

    public static String getCurrentMonth() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM", Locale.getDefault());
        return format.format(new Date());
    }

    public static String getCurrentDate(String template) {
        SimpleDateFormat format = new SimpleDateFormat(template, Locale.getDefault());
        return format.format(new Date());
    }

    public static boolean isNumber(String str) {
        if (isEmpty(str)) {
            return false;
        }
        for (int i = 0; i < str.length(); i++) {
            if (!Character.isDigit(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    public static boolean isColorDark(int color) {
        double darkness = 1 - (0.299 * Color.red(color) + 0.587 * Color.green(color) + 0.114 * Color.blue(color)) / 255;
        return darkness > 0.5;
    }

    public static String unicodeToString(String str) {
        Pattern pattern = Pattern.compile("(\\\\u(\\p{XDigit}{4}))");
        Matcher matcher = pattern.matcher(str);
        char ch;
        while (matcher.find()) {
            ch = (char) Integer.parseInt(matcher.group(2), 16);
            str = str.replace(matcher.group(1), ch + "");
        }
        return str;
    }

    public static String getFriendlyTime(long time) {
        return friendly_time(dateFormater.get().format(new Date(time)));
    }

    public static String formatTime(long time) {
        time = time / 1000;
        return formatTimeStr(time / 60 / 60) + ":" + formatTimeStr(time / 60) + ":" + formatTimeStr(time % 60);
    }

    private static String formatTimeStr(long time) {
        return time < 10 ? "0" + time : "" + time;
    }

    public static String md5(String string) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] array = md.digest(string.getBytes());
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < array.length; ++i) {
                sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).substring(1, 3));
            }
            return sb.toString();
        } catch (java.security.NoSuchAlgorithmException e) {
        }
        return null;
    }

    public static String encodeFileToBase64Binary(File file) {
        byte[] bytes = new byte[0];
        String encodedString = null;
        try {
            bytes = loadFile(file);
            byte[] encoded = Base64.encode(bytes, Base64.DEFAULT);
            encodedString = new String(encoded);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return encodedString;
    }

    private static byte[] loadFile(File file) throws IOException {
        InputStream is = new FileInputStream(file);

        long length = file.length();
        if (length > Integer.MAX_VALUE) {
            // File is too large
        }
        byte[] bytes = new byte[(int) length];

        int offset = 0;
        int numRead = 0;
        while (offset < bytes.length
                && (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0) {
            offset += numRead;
        }

        if (offset < bytes.length) {
            throw new IOException("Could not completely read file " + file.getName());
        }

        is.close();
        return bytes;
    }

    /**
     * 判断首字符是否是字母
     *
     * @param str
     * @return
     */
    public static boolean isLetter(String str) {
        if (isEmpty(str)) {
            return false;
        }
        char c = str.charAt(0);
        if (((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z'))) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 两位小数点
     *
     * @param d
     * @return
     */
    public static String getDecimal2(double d) {
        if (d == 0) {
            return "0.00";
        }
        DecimalFormat df = new DecimalFormat("#.00");
        String result = df.format(d);
        if (result.startsWith(".")) {
            return "0" + result;
        }
        return result;
    }

    public static String getDecimal1(double d) {
        if (d == 0) {
            return "0.0";
        }
        DecimalFormat df = new DecimalFormat("#.0");
        String result = df.format(d);
        if (result.startsWith(".")) {
            return "0" + result;
        }
        return result;
    }

    /**
     * 判断字符串是否是英文字母/数字/下划线/./@组成
     *
     * @param charSequence
     * @return
     */
    public static boolean isWeixin(CharSequence charSequence) {
        Pattern emailer = Pattern
                .compile("^[@._a-zA-Z0-9]+$");
        return emailer.matcher(charSequence).matches();
    }

    public static Map<String, String> getParams(String originUrl) throws UnsupportedEncodingException, MalformedURLException {
        URL url = new URL(originUrl);
        Map<String, String> query_pairs = new LinkedHashMap<String, String>();
        String query = url.getQuery();
        String[] pairs = query.split("&");
        for (String pair : pairs) {
            int idx = pair.indexOf("=");
            query_pairs.put(URLDecoder.decode(pair.substring(0, idx), "UTF-8"), URLDecoder.decode(pair.substring(idx + 1), "UTF-8"));
        }
        return query_pairs;
    }

    /**
     * 如果小于10,前置0
     *
     * @param i
     * @return
     */
    public static String getZeroBefore(int i) {
        if (i < 10) {
            return "0" + i;
        }
        return "" + i;
    }

    /**
     * @param second
     * @return 时分秒
     */
    public static String getHMS(int second) {
        int hour = second / 60 / 60;
        int minute = second / 60 % 60;
        second = second % 60;
        String hms = "";
        if (hour > 0) {
            hms += hour + "小时";
        }
        if (minute > 0) {
            hms += minute + "分钟";
        }
        if (second > 0) {
            hms += second + "秒";
        }
        return hms;
    }

    /**
     * @return 随机的 length 个字符
     */
    public static String getRandom(int length) {
        String str = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random = new Random();
        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < length; i++) {
            int num = random.nextInt(62);
            buf.append(str.charAt(num));
        }
        return buf.toString();
    }

    public static double getDouble(Object obj) {
        if (obj == null) {
            return 0;
        }
        try {
            return Double.valueOf(obj.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 获取字符串的长度，如果有中文，则每个中文字符计为2位
     *
     * @param value 指定的字符串
     * @return 字符串的长度
     */
    public static int chineseLength(String value) {
        int valueLength = 0;
        String chinese = "[\u0391-\uFFE5]";
        /* 获取字段值的长度，如果含中文字符，则每个中文字符长度为2，否则为1 */
        for (int i = 0; i < value.length(); i++) {
            /* 获取一个字符 */
            String temp = value.substring(i, i + 1);
            /* 判断是否为中文字符 */
            if (temp.matches(chinese)) {
                /* 中文字符长度为2 */
                valueLength += 2;
            } else {
                /* 其他字符长度为1 */
                valueLength += 1;
            }
        }
        return valueLength;
    }

    public static long getLong(String value) {
        if (isEmpty(value))
            return 0;
        return Long.valueOf(value);
    }

    /**
     * @param time yyyy-MM-dd 格式开头的日期
     * @return
     */
    public static String getMonth(String time) {
        if (time.length() < 7 || !time.contains("-"))
            return "";
        return Integer.valueOf(time.split("-")[1]) + "";
    }
}
