package com.yuncommunity.gas;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.oldfeel.utils.LogUtil;
import com.oldfeel.utils.StringUtil;
import com.yuncommunity.gas.base.MyActivity;
import com.yuncommunity.gas.base.Net;
import com.yuncommunity.gas.conf.JsonApi;
import com.yuncommunity.gas.utils.MyUtils;

import org.ksoap2.serialization.SoapObject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by mini on 2017/4/9.
 */

public class GasInfo extends MyActivity {
    @Bind(R.id.btn_back)
    TextView btnBack;
    @Bind(R.id.pay_recording)
    TextView payRecording;
    @Bind(R.id.setting)
    ImageView setting;
    @Bind(R.id.user_num)
    TextView userNum;
    @Bind(R.id.user_name)
    TextView userName;
    @Bind(R.id.user_adr)
    TextView userAdr;
    @Bind(R.id.gas_image)
    ImageView gasImage;
    @Bind(R.id.gas_image_ll)
    LinearLayout gasImageLl;
    @Bind(R.id.gas_money)
    TextView gasMoney;
    @Bind(R.id.gas_unit)
    TextView gasUnit;
    @Bind(R.id.btn_payment)
    Button btnPayment;
    @Bind(R.id.lv_info)
    TextView lvInfo;
    @Bind(R.id.toolbar_title)
    TextView toolbarTitle;
    @Bind(R.id.no_record)
    TextView noRecord;
    private String no;
    private String payableAmount;
    private String id;
    private int meterTypeNo;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gas_info);
        ButterKnife.bind(this);
        toolbarTitle.setText("XX在线燃气缴费");
        lvInfo.setPaintFlags(lvInfo.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        no = getIntent().getStringExtra("no");

        Net net1 = new Net(this, JsonApi.GETUSERLIST);
        net1.setParams("communicateNo", no);
        net1.sendRequest("", new Net.Callback() {
            @Override
            public void success(SoapObject soapObject) {
                soapObject = MyUtils.getSoapObject(soapObject, "UsersList");
                if (soapObject != null) {
                    userInfo.addNo(no);
                    userNum.setText("用户编号 : " + getInfo("customerNo", soapObject));
                    userName.setText("用户名称 : " + getInfo("customerName", soapObject));
                    userAdr.setText("用户地址 : " + getInfo("address", soapObject));
                    meterTypeNo = MyUtils.getInt(soapObject, "meterTypeNo");
                } else {
                    userNum.setText("用户编号 : 暂无数据");
                    userName.setText("用户名称 : 暂无数据");
                    userAdr.setText("用户地址 : 暂无数据");
                }

                getListByCommunicateNo();
            }
        });
    }

    private void getListByCommunicateNo() {
        Net net2 = new Net(this, JsonApi.GETLISTBYCOMMUNICATENO);
        net2.setParams("communicateNo", no);
        net2.sendRequest("", new Net.Callback() {
            @Override
            public void success(SoapObject soapObject) {
                soapObject = MyUtils.getSoapObject(soapObject, "BalanceFee");
                if (soapObject != null) {
                    gasMoney.setText(Html.fromHtml("本期燃气消费量<font color='#ee941a'>" + getInfo("currentVolume", soapObject)
                            + "</font>立方,燃气费<font color='#ee941a'>" + getInfo("payableAmount", soapObject) + "</font>元"));
                    payableAmount = getInfo("payableAmount", soapObject);
                    id = getInfo("Id", soapObject);

                    if (meterTypeNo == 10) {
                        gasImageLl.setVisibility(View.VISIBLE);
                        getImage(MyUtils.getString(soapObject, "copyId"));
                    }
                    noRecord.setVisibility(View.GONE);
                } else {
                    gasMoney.setText("本期燃气消费量 暂无数据");
                    gasMoney.setVisibility(View.GONE);
                    gasImageLl.setVisibility(View.GONE);
                    btnPayment.setVisibility(View.GONE);
                    noRecord.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void getImage(String copyId) {
        if (StringUtil.isEmpty(copyId)) {
            return;
        }
        Net net = new Net(this, JsonApi.GETCAMEGETIMAGENAMEBYID);
        net.setParams("CameraId", copyId);
        net.sendRequest("", new Net.Callback() {
            @Override
            public void success(SoapObject soapObject) {
                if (soapObject != null) {
                    LogUtil.showLog("getImage " + soapObject);
                    String imageName = soapObject.getProperty("imageName").toString();
                    LogUtil.showLog("imageName is " + MyUtils.getImage(imageName));
                    imageLoader.displayImage(MyUtils.getImage(imageName), gasImage, options);
                }
            }
        });
    }

    private String getInfo(String key, SoapObject soapObject) {
        if (soapObject.getProperty(key).toString().equals("anyType{}")) {
            return "";
        }
        return soapObject.getProperty(key).toString();
    }

    @OnClick(R.id.lv_info)
    public void lvInfo() {
        Intent intent = new Intent(this, LvInfo.class);
        intent.putExtra("communicateNo", no);
        startActivity(intent);
    }

    @OnClick(R.id.btn_payment)
    public void btnPayment() {
        Intent intent = new Intent(this, Payment.class);
        intent.putExtra("communicateNo", no);
        intent.putExtra("payableAmount", payableAmount);
        intent.putExtra("id", id);
        intent.putExtra("from", Payment.FROM_NORMAL);
        startActivity(intent);
    }

    @OnClick(R.id.btn_back)
    public void btnBack() {
        onBackPressed();
    }

    @OnClick(R.id.pay_recording)
    public void payRecording() {
        Intent intent = new Intent(this, PayRecording.class);
        intent.putExtra("communicateNo", no);
        startActivity(intent);
    }
}
