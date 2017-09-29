package com.yuncommunity.gas.conf;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.Gson;
import com.oldfeel.utils.StringUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by mini on 2017/4/8.
 */

public class UserInfo {
    private static UserInfo userInfo;
    private SharedPreferences sp;
    private SharedPreferences.Editor editor;

    public UserInfo(Context context) {
        sp = PreferenceManager.getDefaultSharedPreferences(context);
        editor = sp.edit();
    }

    public static UserInfo getInstance(Context context) {
        if (userInfo == null) {
            userInfo = new UserInfo(context);
        }
        return userInfo;
    }

    public void addNo(String no) {
        if (StringUtil.isEmpty(no)) {
            return;
        }
        List<String> list = getNoList();
        list.add(0, no);
        for (int i = 1; i < list.size(); i++) {
            if (no.equals(list.get(i))) {
                list.remove(i);
            }
        }
        editor.putString("history", new Gson().toJson(list));
        editor.commit();
    }

    public List<String> getNoList() {
        String history = sp.getString("history", "");
        if (!StringUtil.isEmpty(history)) {
            return new ArrayList<>(Arrays.asList(new Gson().fromJson(history, String[].class)));
        }
        return new ArrayList<>();
    }

    public void deleteNo(String no) {
        List<String> list = getNoList();
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).equals(no)) {
                list.remove(i);
            }
        }
        editor.putString("history", new Gson().toJson(list));
        editor.commit();
    }
}
