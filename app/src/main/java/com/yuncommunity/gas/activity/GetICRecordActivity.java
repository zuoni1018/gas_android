package com.yuncommunity.gas.activity;

import android.os.Bundle;

import com.github.jdsjlzx.recyclerview.LRecyclerView;
import com.yuncommunity.gas.R;
import com.yuncommunity.gas.activity.base.BaseTitleActivity;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by zangyi_shuai_ge on 2017/10/11
 * 充值记录界面
 */

public class GetICRecordActivity extends BaseTitleActivity {
    @Bind(R.id.mRecyclerView)
    LRecyclerView mRecyclerView;

    @Override
    protected int setLayout() {
        return R.layout.activity_get_ic_record;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
    }
}
