package com.oldfeel.utils;

import org.json.JSONObject;

/**
 * json工具类
 *
 * @author oldfeel
 *         Created on: 2014年2月17日
 */
public class JsonUtil {

    /**
     * @return true 为成功,false为失败
     */
    public static boolean isSuccess(String result) {
        return getCode(result) == 0;
    }

    public static String getData(String result) {
        try {
            JSONObject json = new JSONObject(result);
            if (json.has("data")) {
                return json.getString("data");
            }
            if (json.has("Data")) {
                return json.getString("Data");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static int getCode(String result) {
        try {
            JSONObject json = new JSONObject(result);
            if (json.has("code")) {
                return json.getInt("code");
            }
            if (json.has("Code")) {
                return json.getInt("Code");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    public static String getUptoken(String data) {
        try {
            return new JSONObject(data).getString("uptoken");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }
}
