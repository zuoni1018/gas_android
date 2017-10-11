package com.yuncommunity.gas;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.oldfeel.utils.StringUtil;
import com.yuncommunity.gas.base.MyActivity;
import com.yuncommunity.gas.base.MyApplication;
import com.yuncommunity.gas.base.Net;
import com.yuncommunity.gas.conf.JsonApi;

import org.ksoap2.serialization.SoapObject;

import java.util.Random;

import butterknife.Bind;
import butterknife.ButterKnife;



public class ReaderResult extends MyActivity {
    @Bind(R.id.image)
    ImageView image;
    @Bind(R.id.info)
    TextView info;
    @Bind(R.id.desc)
    TextView desc;

    Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reader_result);
        ButterKnife.bind(this);
        showTitleBlue("正在写卡");
        desc.setVisibility(View.GONE);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                int i = new Random().nextInt(2);
                boolean isSuccess = i == 1;
                image.setImageResource(isSuccess ? R.drawable.duka_success : R.drawable.duka_fail);
                info.setText(isSuccess ? "写卡成功!" : "写卡失败!");
                desc.setVisibility(isSuccess ? View.GONE : View.VISIBLE);
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (!isFinishing()) {
                            onBackPressed();
                        }
                    }
                }, 3 * 1000);
            }
        }, 3 * 1000);

        String id = getIntent().getStringExtra("id");

        if (!StringUtil.isEmpty(id)) { // 从未写卡的充值记录过来的写卡
            Net net = new Net(this, JsonApi.ChangeICRecord);
            net.setParams("Id", id);
            net.sendRequest("", new Net.Callback() {
                @Override
                public void success(SoapObject soapObject) {

                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        Activity payResult = ((MyApplication) getApplication()).getActivity(PayResult.class);
        if (payResult != null && !payResult.isFinishing()) {
            payResult.finish();
        }
        Activity payment = ((MyApplication) getApplication()).getActivity(Payment.class);
        if (payment != null && !payment.isFinishing()) {
            payment.finish();
        }
        super.onBackPressed();
    }
}
