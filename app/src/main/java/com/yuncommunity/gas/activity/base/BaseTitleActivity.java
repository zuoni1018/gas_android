package com.yuncommunity.gas.activity.base;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yuncommunity.gas.R;


/**
 * Created by zangyi_shuai_ge on 2017/4/21
 */

public abstract class BaseTitleActivity extends BaseActivity {

    private TextView tvTitle;
    private LinearLayout layoutBack;


    private Animation anim_btn_begin;
    private Animation anim_btn_end;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(setLayout());

        tvTitle = (TextView) findViewById(R.id.tvTitle);

        layoutBack = (LinearLayout) findViewById(R.id.layoutBack);
        layoutBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishActivity();
            }
        });
    }

    protected void setTitle(String title) {
        tvTitle.setText(title);
    }




    protected void finishActivity() {
        finish();
    }

    protected abstract int setLayout();

    @Override
    public void onBackPressed() {
        finishActivity();
    }


    public void  setTitleOnClickListener(View.OnClickListener onClickListener){
        tvTitle.setOnClickListener(onClickListener);
    }

    public void go2Activity(Class<?> cls){
        Intent mIntent=new Intent(this,cls);
        startActivity(mIntent);
    }

}
