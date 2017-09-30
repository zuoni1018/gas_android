package com.yuncommunity.gas.protocol;

/**
 * Created by zangyi_shuai_ge on 2017/9/29
 * 接收命令
 */

public class GetCommand {
    public static final String COMMAND_TYPE_VERIFICATION = "09";//身份验证
    public static final String COMMAND_TYPE_QUERY = "01";//查询卡
    public static final String COMMAND_TYPE_WRITE = "06";//写卡

    private String commandType = "00";//命令类型
    private String data;//数据

    public String getCommandType() {
        return commandType;
    }

    public void setCommandType(String commandType) {
        this.commandType = commandType;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
