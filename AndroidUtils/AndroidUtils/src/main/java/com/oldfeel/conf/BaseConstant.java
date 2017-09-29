package com.oldfeel.conf;

import android.os.Environment;

/**
 * 框架中需要的一些常量
 *
 * @author oldfeel
 *         <p/>
 *         Create on: 2014年10月8日
 */
public class BaseConstant {

    public static final String SDCARD_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/";

    private static BaseConstant baseConstant;
    public boolean isDebug = true;
    private long pageSize = 20; // listview 每次加载的item数量
    private String rootUrl; // 网络请求的根url

    public static BaseConstant getInstance() {
        if (baseConstant == null) {
            baseConstant = new BaseConstant();
        }
        return baseConstant;
    }

    public long getPageSize() {
        return pageSize;
    }

    public void setPageSize(long pageSize) {
        this.pageSize = pageSize;
    }

    public String getRootUrl() {
        return rootUrl;
    }

    public void setRootUrl(String rootUrl) {
        this.rootUrl = rootUrl;
    }

    public boolean isDebug() {
        return isDebug;
    }

    public void setDebug(boolean debug) {
        isDebug = debug;
    }
}
