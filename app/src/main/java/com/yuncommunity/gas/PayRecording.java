package com.yuncommunity.gas;

import android.os.Bundle;
import android.widget.TextView;

import com.yuncommunity.gas.base.MyActivity;
import com.yuncommunity.gas.list.PayRecordingList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by mini on 2017/4/9.
 */

public class PayRecording extends MyActivity {
    @Bind(R.id.btn_back)
    TextView btnBack;
    @Bind(R.id.toolbar_title)
    TextView toolbarTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pay_recording);
        ButterKnife.bind(this);
        toolbarTitle.setText("缴费记录");
        String communicateNo = getIntent().getStringExtra("communicateNo");
        getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, PayRecordingList.newInstance(communicateNo)).commit();
    }

    @OnClick(R.id.btn_back)
    public void btnBack() {
        onBackPressed();
    }

}
