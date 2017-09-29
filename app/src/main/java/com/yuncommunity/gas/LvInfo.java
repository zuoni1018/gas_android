package com.yuncommunity.gas;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.yuncommunity.gas.base.MyActivity;
import com.yuncommunity.gas.base.Net;
import com.yuncommunity.gas.conf.JsonApi;
import com.yuncommunity.gas.utils.MyUtils;

import org.ksoap2.serialization.SoapObject;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by mini on 2017/4/9.
 */

public class LvInfo extends MyActivity {
    @Bind(R.id.lv_first_price)
    TextView lvFirstPrice;
    @Bind(R.id.first_bar)
    SeekBar firstBar;
    @Bind(R.id.lv_second_price)
    TextView lvSecondPrice;
    @Bind(R.id.second_bar)
    SeekBar secondBar;
    @Bind(R.id.lv_third_price)
    TextView lvThirdPrice;
    @Bind(R.id.third_bar)
    SeekBar thirdBar;
    @Bind(R.id.lv_time)
    TextView lvTime;
    @Bind(R.id.lv_pay_num)
    EditText lvPayNum;
    @Bind(R.id.lv_pay_money)
    TextView lvPayMoney;
    @Bind(R.id.lv_compute_money)
    LinearLayout lvComputeMoney;
    @Bind(R.id.lv_submit)
    Button lvSubmit;

    String communicateNo;
    @Bind(R.id.lv_info_1)
    TextView lvInfo1;
    @Bind(R.id.lv_info_2)
    TextView lvInfo2;
    @Bind(R.id.lv_info_3)
    TextView lvInfo3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lv_info);
        ButterKnife.bind(this);
        showTitleBlue("阶梯使用量明细");

        communicateNo = getIntent().getStringExtra("communicateNo");
        getData();
        firstBar.setEnabled(false);
        secondBar.setEnabled(false);
        thirdBar.setEnabled(false);

        lvComputeMoney.setVisibility(View.GONE);
        lvSubmit.setVisibility(View.GONE);
    }

    private void getData() {
        Net net = new Net(this, JsonApi.GetLVAllByCommunicateNo);
        net.setParams("communicateNo", communicateNo);
        net.sendRequest("", new Net.Callback() {
            @Override
            public void success(SoapObject soapObject) {
                if (soapObject != null) {
                    for (int i = 0; i < soapObject.getPropertyCount(); i++) {
                        SoapObject temp = (SoapObject) soapObject.getProperty(i);
                        if (i == 0) {
                            putTimeToView(temp);
                        }
                        int ladderNo = MyUtils.getInt(temp, "ladderNo");
                        switch (ladderNo) {
                            case 1:
                                lvFirstPrice.setText("单价:" + MyUtils.getString(temp, "price") + "元/方");
                                lvInfo1.setText(Html.fromHtml("余量<font color='#56ad78'>" + MyUtils.getString(temp, "currentVolume") + "</font>方/总计<font color='#56ad78'>" + MyUtils.getString(temp, "maxVolume") + "</font>方"));
                                int progress = getProgress(MyUtils.getString(temp, "currentVolume"), MyUtils.getString(temp, "maxVolume"));
                                firstBar.setProgress(progress);
                                formatProgress(firstBar);
                                break;
                            case 2:
                                lvSecondPrice.setText("单价:" + MyUtils.getString(temp, "price") + "元/方");
                                lvInfo2.setText(Html.fromHtml("余量<font color='#56ad78'>" + MyUtils.getString(temp, "currentVolume") + "</font>方/总计<font color='#56ad78'>" + MyUtils.getString(temp, "maxVolume") + "</font>方"));
                                secondBar.setProgress(getProgress(MyUtils.getString(temp, "currentVolume"), MyUtils.getString(temp, "maxVolume")));
                                formatProgress(secondBar);
                                break;
                            case 3:
                                lvThirdPrice.setText("单价:" + MyUtils.getString(temp, "price") + "元/方");
                                lvInfo3.setText(Html.fromHtml("余量<font color='#56ad78'>" + MyUtils.getString(temp, "currentVolume") + "</font>方/总计<font color='#56ad78'>" + MyUtils.getString(temp, "maxVolume") + "</font>方"));
                                thirdBar.setProgress(getProgress(MyUtils.getString(temp, "currentVolume"), MyUtils.getString(temp, "maxVolume")));
                                formatProgress(thirdBar);
                                break;
                        }
                    }
                }
            }
        });
    }

    /**
     * 当余量为0时,显示一点点红色
     *
     * @param firstBar
     */
    private void formatProgress(SeekBar firstBar) {
        int progress = firstBar.getProgress();
        if (progress < 10) {
            PorterDuff.Mode mMode = PorterDuff.Mode.SRC_ATOP;

            LayerDrawable layerDrawable = (LayerDrawable) firstBar.getProgressDrawable();
            Drawable progressD = layerDrawable.findDrawableByLayerId(android.R.id.progress);
            progressD.setColorFilter(Color.RED, mMode);
            // Applying Tinted Drawables
            layerDrawable.setDrawableByLayerId(android.R.id.progress, progressD);
            if (progress < 3) {
                firstBar.setProgress(3);
            }
        }
    }

    private void putTimeToView(SoapObject temp) {
        lvTime.setText("阶梯使用量周期 : " + MyUtils.getDate(MyUtils.getString(temp, "startTime")) + "至" + MyUtils.getDate(MyUtils.getString(temp, "endTime")));
    }

    private int getProgress(String currentVolume, String maxVolume) {
        double current = Double.valueOf(currentVolume);
        double max = Double.valueOf(maxVolume);
        return (int) (current / max * 100);
    }
}
