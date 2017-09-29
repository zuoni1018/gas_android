package com.yuncommunity.gas;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yuncommunity.gas.base.MyActivity;

import java.util.Random;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.yuncommunity.gas.R.id.btn_pay;

/**
 * Created by mini on 2017/4/9.
 */

public class Payment extends MyActivity {
    public static final int FROM_NORMAL = 1;
    public static final int FROM_IC = 2;
    @Bind(R.id.btn_back)
    TextView btnBack;
    @Bind(R.id.weixin_pay_check)
    ImageView weixinPayCheck;
    @Bind(R.id.weixin_pay)
    LinearLayout weixinPay;
    @Bind(R.id.zhifubao_pay_check)
    ImageView zhifubaoPayCheck;
    @Bind(R.id.zhifubao_pay)
    LinearLayout zhifubaoPay;
    @Bind(btn_pay)
    Button btnPay;
    @Bind(R.id.toolbar_title)
    TextView toolbarTitle;
    @Bind(R.id.tv_payableAmount)
    TextView tvPayableAmount;

    String communicateNo;
    boolean isWeixin = true;
    private String payableAmount;
    private String id;
    private int from;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.payment);
        ButterKnife.bind(this);
        toolbarTitle.setText("立即缴费");

        communicateNo = getIntent().getStringExtra("communicateNo");
        payableAmount = getIntent().getStringExtra("payableAmount");
        id = getIntent().getStringExtra("id");
        tvPayableAmount.setText("¥ " + payableAmount);
        btnPay.setText("确认支付 ¥ " + payableAmount);

        from = getIntent().getIntExtra("from", FROM_NORMAL);
    }

    @OnClick(R.id.weixin_pay_check)
    public void weixinPayCheck() {
        weixinPayCheck.setImageResource(R.drawable.xuanze);
        zhifubaoPayCheck.setImageResource(R.drawable.yuan);
        isWeixin = true;
    }

    @OnClick(R.id.zhifubao_pay_check)
    public void zhifubaoPayCheck() {
        weixinPayCheck.setImageResource(R.drawable.yuan);
        zhifubaoPayCheck.setImageResource(R.drawable.xuanze);
        isWeixin = false;
    }

    @OnClick(R.id.btn_back)
    public void btnback() {
        onBackPressed();
    }

    @OnClick(btn_pay)
    public void btnPay() {
        // 随机提示支付成功/失败
        int i = new Random().nextInt(2);
        Intent intent = new Intent(this, PayResult.class);
        intent.putExtra("from", from);
        intent.putExtra("result", i == 1);
        startActivity(intent);
//        Net net = new Net(this, JsonApi.RECHARGE);
////        net.setParams("id", 3);
////        net.setParams("type", isWeixin ? 6 : 5);
////        net.setParams("payMoney", "2162");
////        net.setParams("payId", "00000000000000000000");
//        net.setParams("id", id);
//        net.setParams("type", isWeixin ? 6 : 5);
//        net.setParams("payMoney", StringUtil.getDouble(payableAmount));
//        net.setParams("payId", "00000000000000000000");
//        net.sendRequest("正在充值...", new Net.Callback() {
//            @Override
//            public void success(SoapObject soapObject) {
//
//            }
//
//            @Override
//            public void otherSuccess(Object object) {
//                LogUtil.showLog("object is " + object);
//                Intent intent = new Intent(Payment.this, PayResult.class);
//                intent.putExtra("result", Boolean.valueOf(object.toString()));
//                startActivity(intent);
//            }
//        });
    }
}

