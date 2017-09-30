package com.yuncommunity.gas.protocol;

import com.yuncommunity.gas.utils.Cfun;
import com.zuoni.zuoni_common.utils.LogUtil;

/**
 * Created by zangyi_shuai_ge on 2017/9/29
 * ic卡读卡协议
 */

public class ProtocolICRead {

    /**
     * 编码
     * 身份验证
     * [68 a1 10 a1 e1 48 54 03 68 09 6b 6e] cs-84 16
     * 68 a1 10 a1 e1 48 54 03 68 09 6b 6e 84 16
     * 68a110a1e148540368096b6e8416
     * 查询卡
     * 68 a1 10 a1 e1 48 54 02 68 01 ff a1 16
     * 写卡
     * 计费
     * 68 a1 10 a1 e1 48 54 0d 68 06 0007 000000101000 00000117 f1 16
     * 68 a1 10 a1 e1 48 54 0d 68 06 0007 000000101000 00000117 7F 16
     * 计量
     * 68 a1 10 a1 e1 48 54 0a 68 06 0002 002000 05 01 01 17 ef 16
     * 68 a1 10 a1 e1 48 54 0a 68 06 0007 002000 05 01 01 17 5B 16
     * <p>
     * 以下以身份验证为例
     * <p>
     * 68:起始符  (固定)
     * a1:系统标识 (固定)
     * 10:地址启用标识 (固定)
     * a1:目标类型 (固定)
     * e1:源类型 (固定)
     * 48 54:目标地址 (固定)
     * 03:数据域长度
     * 68:同步符 (固定)
     * 09:命令类型   其中 09 6b 6e 为数据域
     * 6b 6e:数据
     * 84:校验域 累加和校验
     * 16:结束符 (固定)
     */


    //编码
    public static String code(SendCommand sendCommand) {
        String dataDomain;
        if (sendCommand instanceof SendCommandWrite) {
            SendCommandWrite sendCommandWrite = (SendCommandWrite) sendCommand;
            dataDomain = sendCommandWrite.getCommandType() + sendCommandWrite.getData();
        } else {
            dataDomain = sendCommand.getCommandType() + sendCommand.getData();//数据域
        }

        LogUtil.i("编码 数据域:" + dataDomain);
        String dataDomainLength = Integer.toHexString(dataDomain.length() / 2);//数据域长度转成16进制
        if (dataDomainLength.length() == 1) {
            dataDomainLength = "0" + dataDomainLength;
        }
        String accSum = Cfun.accSum(Cfun.x16Str2Byte("68a110a1e14854" + dataDomainLength + "68" + dataDomain));//和校验
        String command = "68a110a1e14854" + dataDomainLength + "68" + dataDomain + accSum + "16";
        LogUtil.i("编码" + command);
        return command;
    }

    /**
     * 解码
     * 身份验证
     * 68 a1 01 e1 a1 48 54 03 68 09 6e 6b CS 16
     * 68:起始符  (固定)
     * a1:系统标识 (固定)
     * 10:地址启用标识 (固定)
     * a1:目标类型 (固定)
     * e1:源类型 (固定)
     * 48 54:目标地址 (固定)
     * 03:数据域长度
     * 68:同步符 (固定)
     * 09:命令类型   其中 09 6b 6e 为数据域
     * 6e 6b:数据
     * CS:校验域 累加和校验
     * 16:结束符 (固定)
     *
     *
     * */
    public static GetCommand decode(String message) {
        //前面 18位没用
        //前面14位为 68 a1 01 e1 a1 48 54 //前去判断
        //取中间的数据域

        String start=message.substring(0,14);
        if(!start.toLowerCase().equals("68a101e1a14854")){
            return null;
        }else {
            //开始解码
            String dataDomain=message.substring(18,message.length()-4);
            LogUtil.i("编码 数据域:" + dataDomain);
            //前2位是 命令类型
            String commandType=dataDomain.substring(0,2);
            String data=dataDomain.substring(2,dataDomain.length());

            GetCommand getCommand=new GetCommand();

        }


        return null;
    }

}
