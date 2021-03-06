package com.yuncommunity.gas.protocol;

/**
 * Created by zangyi_shuai_ge on 2017/9/29
 */

public class SendCommand {




    public static final String COMMAND_TYPE_VERIFICATION = "09";//身份验证
    public static final String COMMAND_TYPE_QUERY = "01";//查询卡
    public static final String COMMAND_TYPE_WRITE = "06";//写卡

    //
    //起始符  系统标识 地址启用标识

    private String commandType = "00";//命令类型
    private String data;//数据


    public String getCommandType() {
        return commandType;
    }

    public void setCommandType(String commandType) {
        this.commandType = commandType;
    }

    public String getData() {
        if(commandType.equals(COMMAND_TYPE_VERIFICATION)){
            return "6b6e";//身份验证的是固定的
        }else if(commandType.equals(COMMAND_TYPE_QUERY)){
            return "ff";//查卡
        }
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
