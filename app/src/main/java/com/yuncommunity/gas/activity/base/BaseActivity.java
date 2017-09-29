package com.yuncommunity.gas.activity.base;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;


/**
 * Created by zangyi_shuai_ge on 2017/9/14
 */

public class BaseActivity extends AppCompatActivity {

    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        context = this;
        ActivityCollector.addActivity(this);
    }

    @Override
    protected void onDestroy() {
        ActivityCollector.removeActivity(this);
        super.onDestroy();
    }

    public Context getContext() {
        return context;
    }

    public void showToast(String message) {
        Toast.makeText(BaseActivity.this,message,Toast.LENGTH_SHORT).show();
    }

}
