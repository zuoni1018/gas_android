package com.yuncommunity.gas;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.TextView;

import com.yuncommunity.gas.base.MyActivity;
import com.yuncommunity.gas.base.MyApplication;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by mini on 2017/4/9.
 */

public class PayResult extends MyActivity {
    @Bind(R.id.icon)
    ImageView icon;
    @Bind(R.id.text)
    TextView text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pay_result);
        ButterKnife.bind(this);
        showTitleBlue("缴费结果");
        final boolean result = getIntent().getBooleanExtra("result", false);
        icon.setImageResource(result ? R.drawable.success : R.drawable.fail);
        text.setText(result ? "缴费成功!" : "缴费失败!");

        int from = getIntent().getIntExtra("from", Payment.FROM_NORMAL);
        if (result && from == Payment.FROM_IC) { // 支付成功,IC读卡
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    openActivity(ReaderResult.class);
                }
            }, 3 * 1000);
        } else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (!isFinishing())
                        onBackPressed();
                }
            }, 3 * 1000);
        }
    }

    @Override
    public void onBackPressed() {
        Activity payment = ((MyApplication) getApplication()).getActivity(Payment.class);
        if (payment != null && !payment.isFinishing()) {
            payment.finish();
        }
        super.onBackPressed();
    }
}
