package com.oldfeel.utils;

import android.util.Log;

/**
 * 打印日志工具类
 *
 * @author oldfeel
 *         <p/>
 *         Created on: 2014-1-10
 */
public class LogUtil {

    /**
     * 打印日志
     *
     * @param log
     */
    public static void showLog(Object log) {
        if (log == null) {
            log = "null";
        }
        Log.i("example", log.toString());
    }
}
