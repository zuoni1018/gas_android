package com.yuncommunity.gas.utils;

import com.oldfeel.utils.StringUtil;
import com.yuncommunity.gas.base.Net;

import org.ksoap2.serialization.SoapObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by oldfeel on 17-4-14.
 */

public class MyUtils {
    public static SoapObject getSoapObject(SoapObject soapObject, String key) {
        if (soapObject == null)
            return null;
        try {
            return (SoapObject) soapObject.getProperty(key);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String getString(SoapObject soapObject, String key) {
        try {
            return soapObject.getProperty(key).toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public static String getMonth(String payTime) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM");
        String currentMonth = format.format(new Date());
        if (payTime.startsWith(currentMonth)) {
            return "本月";
        }
        return Integer.valueOf(payTime.split("-")[1]) + "月";
    }

    public static String getWeek(String payTime) {
        payTime = payTime.replace("T", " ");
        return StringUtil.getWeekByDate(payTime);
    }

    public static String getMonthAndDay(String payTime) {
        return payTime.substring(5, 10);
    }

    public static int getInt(SoapObject temp, String key) {
        return Integer.valueOf(temp.getProperty(key).toString());
    }

    public static String getDate(String time) {
        time = time.substring(0, 10);
        time = time.replaceFirst("-", "年");
        time = time.replaceFirst("-", "月");
        time = time + "日";
        return time;
    }

    public static String getMonthBefore(int before) {
        SimpleDateFormat format = new SimpleDateFormat("yyyyMM");
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, -before);
        return format.format(calendar.getTime());
    }

    public static String getImage(String imageName) {
        return Net.ROOT_URL + imageName;
    }
}
