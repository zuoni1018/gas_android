package com.yuncommunity.gas.base;

import android.os.Bundle;

import com.oldfeel.base.BaseActivity;
import com.yuncommunity.gas.R;
import com.yuncommunity.gas.conf.UserInfo;

import java.util.List;

/**
 * Created by mini on 2017/4/8.
 */
public class MyActivity extends BaseActivity {
    public UserInfo userInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userInfo = UserInfo.getInstance(this);
    }

    public void showTitle(String title) {
        super.showTitle(title);
    }

    public void showTitleBlue(String title) {
        super.showTitle(title);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back_blue);
    }

    @Override
    public void onPermissionsGranted(List<String> perms) {

    }

    @Override
    public void onPermissionsDenied(List<String> perms) {

    }

    @Override
    public void onRequestDenied() {

    }
}
