package com.yuncommunity.gas.protocol;

import java.io.Serializable;

/**
 * Created by zangyi_shuai_ge on 2017/10/11
 */

public class GetCardInfoCommand extends GetCommand implements Serializable{

//    String info1 = data.substring(0, 14);//获取区域号+表号
//    String info2 = data.substring(14, 22);//卡识别，卡类型，表类型，卡状态
//    String info3 = data.substring(22, 28);//累计量
//    String info4 = data.substring(28, 34);//剩余量
//    String info5 = data.substring(34, 38);//购买次数
//    String info6 = data.substring(38, 40);//磁攻击
//    String info7 = data.substring(40, 42);//卡座攻击
//    String info8 = data.substring(42, 44);//表具状态

    //获取卡信息
    private String info1;
    private String info2;
    private String info3;
    private String info4;
    private String info5;
    private String info6;
    private String info7;
    private String info8;
    private String info9;

    public String getInfo10() {
        return info10;
    }

    public void setInfo10(String info10) {
        this.info10 = info10;
    }

    private String info10;
    public String getInfo9() {
        return info9;
    }

    public void setInfo9(String info9) {
        this.info9 = info9;
    }

    public String getInfo1() {
        return info1;
    }

    public void setInfo1(String info1) {
        this.info1 = info1;
    }

    public String getInfo2() {
        return info2;
    }

    public void setInfo2(String info2) {
        this.info2 = info2;
    }

    public String getInfo3() {
        return info3;
    }

    public void setInfo3(String info3) {
        this.info3 = info3;
    }

    public String getInfo4() {
        return info4;
    }

    public void setInfo4(String info4) {
        this.info4 = info4;
    }

    public String getInfo5() {
        return info5;
    }

    public void setInfo5(String info5) {
        this.info5 = info5;
    }

    public String getInfo6() {
        return info6;
    }

    public void setInfo6(String info6) {
        this.info6 = info6;
    }

    public String getInfo7() {
        return info7;
    }

    public void setInfo7(String info7) {
        this.info7 = info7;
    }

    public String getInfo8() {
        return info8;
    }

    public void setInfo8(String info8) {
        this.info8 = info8;
    }

    public String getResult() {


        return "\n解码 读卡\n"
                +"\n====================\n"
                + "\n区域号+表号:" + info1
                +"\n卡号"+info9
                + "\n卡识别，卡类型，表类型，卡状态:" + info2
                + "\n累计量:" + info3
                + "\n剩余量:" + info4
                + "\n购买次数:" + info5
                + "\n磁攻击:" + info6
                + "\n卡座攻击:" + info7
                + "\n表具状态:" + info8
                +"\n====================\n";
    }
}
