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
     * 查询卡信息
     * 68 a1 01 e1 a1 48 54 17680110010000000005000200330000000000000000000000f316
     * <p>
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
     * 6e 6b:数据
     * CS:校验域 累加和校验
     * 16:结束符 (固定)
     */
    public static GetCommand decode(String message) {
        //前面 18位没用
        //前面14位为 68 a1 01 e1 a1 48 54 //前去判断
        //取中间的数据域

        String start = message.substring(0, 14);
        if (!start.toLowerCase().equals("68a101e1a14854")) {
            return null;
        } else {
            //开始解码
            String dataDomain = message.substring(18, message.length() - 4);
            LogUtil.i("解码 数据域:" + dataDomain);
            //前2位是 命令类型
            String commandType = dataDomain.substring(0, 2);
            LogUtil.i("解码 命令类型:" + commandType);
            String data = dataDomain.substring(2, dataDomain.length());
            LogUtil.i("解码 数据:" + data);

            if (commandType.equals("01")) {
                //读卡
//                01 10010000000005 00020033 000000 000000 0000 00 00 00

                GetCardInfoCommand getCardInfoCommand=new GetCardInfoCommand();

                //区域号+表号 共7位，计费表为8位
                if (data.length() == "10010000000005000200330000000000000000000000".length()) {
                    //计量表
                    String info1 = data.substring(0, 14);//获取区域号+表号
                    String info2 = data.substring(14, 22);//卡识别，卡类型，表类型，卡状态
                    String info3 = data.substring(22, 28);//累计量
                    String info4 = data.substring(28, 34);//剩余量
                    String info5 = data.substring(34, 38);//购买次数
                    String info6 = data.substring(38, 40);//磁攻击
                    String info7 = data.substring(40, 42);//卡座攻击
                    String info8 = data.substring(42, 44);//表具状态
                    String info9 = data.substring(6, 14);//卡号
                    LogUtil.i("解码 读卡"
                            +"区域号+表号:"+info1
                            +"卡号"+info9
                            +"\n卡识别，卡类型，表类型，卡状态" +info2
                            +"\n累计量"+info3
                            +"\n剩余量"+info4
                            +"\n购买次数"+info5
                            +"\n磁攻击"+info6
                            +"\n卡座攻击"+info7
                            +"\n表具状态"+info8);

                    getCardInfoCommand.setInfo1(info1);
                    getCardInfoCommand.setInfo2(info2);
                    getCardInfoCommand.setInfo3(info3);
                    getCardInfoCommand.setInfo4(info4);

                    getCardInfoCommand.setInfo5(info5);
                    getCardInfoCommand.setInfo6(info6);
                    getCardInfoCommand.setInfo7(info7);
                    getCardInfoCommand.setInfo8(info8);
                    getCardInfoCommand.setInfo9("00"+info9);
                    getCardInfoCommand.setInfo10("计量卡");


                    getCardInfoCommand.setCommandType(GetCommand.COMMAND_TYPE_QUERY);

                    return  getCardInfoCommand;

                } else if (data.length() == "1001000000000005000200330000000000000000000000".length()) {
                    //计费表
                    String info1 = data.substring(0, 14 + 2);//获取区域号+表号
                    String info2 = data.substring(14 + 2, 22 + 2);//卡识别，卡类型，表类型，卡状态
                    String info3 = data.substring(22 + 2, 28 + 2);//累计量
                    String info4 = data.substring(28 + 2, 34 + 2);//剩余量
                    String info5 = data.substring(34 + 2, 38 + 2);//购买次数
                    String info6 = data.substring(38 + 2, 40 + 2);//磁攻击
                    String info7 = data.substring(40 + 2, 42 + 2);//卡座攻击
                    String info8 = data.substring(42 + 2, 44 + 2);//表具状态
                    String info9 = data.substring(6, 14+2);//卡号

                    LogUtil.i("解码 读卡"
                            +"区域号+表号:"+info1
                            +"\n卡识别，卡类型，表类型，卡状态" +info2
                            +"\n累计量"+info3
                            +"\n剩余量"+info4
                            +"\n购买次数"+info5
                            +"\n磁攻击"+info6
                            +"\n卡座攻击"+info7
                            +"\n表具状态"+info8);
                    getCardInfoCommand.setCommandType(GetCommand.COMMAND_TYPE_QUERY);
                    getCardInfoCommand.setInfo1(info1);
                    getCardInfoCommand.setInfo2(info2);
                    getCardInfoCommand.setInfo3(info3);
                    getCardInfoCommand.setInfo4(info4);

                    getCardInfoCommand.setInfo5(info5);
                    getCardInfoCommand.setInfo6(info6);
                    getCardInfoCommand.setInfo7(info7);
                    getCardInfoCommand.setInfo8(info8);
                    getCardInfoCommand.setInfo9(info9);
                    getCardInfoCommand.setInfo10("计费卡");
                    return  getCardInfoCommand;
                }


            }else if(commandType.equals("06")){
                //修改表号
                //data
                ChangeCommand changeCommand=new ChangeCommand();
                changeCommand.setCommandType(GetCommand.COMMAND_TYPE_WRITE);

                if(data.toLowerCase().equals("ff")){
                    changeCommand.setSuccessfull(true);
                }else {
                    changeCommand.setSuccessfull(false);
                }

                return changeCommand;

            }



//            GetCommand getCommand=new GetCommand();
//            getCommand.setCommandType(commandType);

        }


        return new GetCommand();
    }

}
