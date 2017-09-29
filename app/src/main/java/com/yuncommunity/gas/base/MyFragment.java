package com.yuncommunity.gas.base;

import android.os.Bundle;

import com.oldfeel.base.BaseFragment;
import com.yuncommunity.gas.conf.UserInfo;

/**
 * Created by mini on 2017/4/8.
 */
public class MyFragment extends BaseFragment {
    public UserInfo userInfo;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        userInfo = UserInfo.getInstance(getActivity());
    }
}
