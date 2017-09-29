package com.oldfeel.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import okhttp3.Cookie;
import okhttp3.HttpUrl;

/**
 * Created by oldfeel on 16-11-24.
 */
public class MyCookieStore {

    public MyCookieStore(Context context) {
        sp = PreferenceManager.getDefaultSharedPreferences(context);
        editor = sp.edit();
        editor.apply();
    }

    SharedPreferences sp;
    SharedPreferences.Editor editor;

    public List<Cookie> get(String url) {
        String cookies = sp.getString("cookies_" + url, "");
        LogUtil.showLog("myCookieStore get " + url + " " + cookies);
        if (StringUtil.isEmpty(cookies)) {
            return new ArrayList<>();
        }
        return Arrays.asList(new Gson().fromJson(cookies, Cookie[].class));
    }

    public long getStoreTime(String url) {
        return sp.getLong("cookies_time_" + url, 0);
    }

    public void put(HttpUrl url, List<Cookie> cookies) {
        LogUtil.showLog("myCookieStore put " + url + " " + new Gson().toJson(cookies));
        editor.putString("cookies_" + url, new Gson().toJson(cookies));
        editor.putLong("cookies_time_" + url, System.currentTimeMillis());
        editor.commit();
    }
}
