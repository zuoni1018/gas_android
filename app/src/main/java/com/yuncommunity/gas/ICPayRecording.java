package com.yuncommunity.gas;

import android.os.Bundle;
import android.widget.TextView;

import com.yuncommunity.gas.base.MyActivity;
import com.yuncommunity.gas.list.ICPayRecordingList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by oldfeel on 17-5-10.
 * GetICRecord
 */

public class ICPayRecording extends MyActivity {
    @Bind(R.id.btn_back)
    TextView btnBack;
    @Bind(R.id.toolbar_title)
    TextView toolbarTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pay_recording);
        ButterKnife.bind(this);
        toolbarTitle.setText("充值记录");
        String meterNo = getIntent().getStringExtra("meterNo");
        getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, ICPayRecordingList.newInstance(meterNo)).commit();
    }

    @OnClick(R.id.btn_back)
    public void btnBack() {
        onBackPressed();
    }
}
