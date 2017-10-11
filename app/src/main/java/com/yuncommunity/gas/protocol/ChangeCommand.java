package com.yuncommunity.gas.protocol;

/**
 * Created by zangyi_shuai_ge on 2017/10/11
 */

public class ChangeCommand extends GetCommand {
    private boolean isSuccessfull;

    public boolean isSuccessfull() {
        return isSuccessfull;
    }

    public void setSuccessfull(boolean successfull) {
        isSuccessfull = successfull;
    }
}
