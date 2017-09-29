package com.yuncommunity.gas.base;

import com.oldfeel.conf.BaseApplication;
import com.pgyersdk.crash.PgyCrashManager;

/**
 * Created by mini on 2017/4/8.
 */
public class MyApplication extends BaseApplication {

    @Override
    public void initBaseConstant() {
        PgyCrashManager.register(this);
    }
}
