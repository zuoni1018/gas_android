package com.yuncommunity.gas.protocol;

/**
 * Created by zangyi_shuai_ge on 2017/9/29
 */

public class SendCommandWrite extends SendCommand {


    public final static String WRITE_TYPE_CHARGING = "01";//计费
    public final static String WRITE_TYPE_METERING = "02";//计量

    /**
     * 计量: 68 a1 10 a1 e1 48 54 0a 68 06       00 02 00 20 00 05 01 01 17 ef 16
     * 数据域:
     * 00 02   00 20 00   05  01  01  17
     * <p>
     * 00 02: 购买次数   [21,22]
     * 00 20 00:购买量 [23,24,25]
     * 05:正报警量 [26]
     * 01 :透支量 [27]
     * 01: 大流设定 [28]
     * 17: 阀门自检 [29]
     * <p>
     * : 计费: 68 a1 10 a1 e1 48 54 0d 68 06      0007 000000101000 00000117 f1 16
     * 数据域:
     * 00 07 000000101000 00000117
     * 00 07:充值次数 [21,22]
     * 00 00 00 10 10 00:充值金额 [23,24,25,26,27,28]
     * 00 00 01 17:当前价格 [2f,30,31,32]
     */
    public SendCommandWrite() {
        setCommandType(SendCommand.COMMAND_TYPE_WRITE);
    }
    private String writeType = "01";//写卡类型

    //计量
    private String gou_mai_ci_shu = "0000";
    private String gou_mai_liang = "000000";
    private String zheng_bao_jing_liang = "00";
    private String tou_zhi_liang = "00";
    private String da_liu_she_ding = "00";
    private String fa_men_zi_jian = "00";

    //计费
    private String chong_zhi_ci_shu = "0000";
    private String chong_zhi_jing_e = "000000000000";
    private String dang_qian_jia_ge = "00000000";

    public String getWriteType() {
        return writeType;
    }

    public void setWriteType(String writeType) {
        this.writeType = writeType;
    }

    public String getGou_mai_ci_shu() {
        return gou_mai_ci_shu;
    }

    public void setGou_mai_ci_shu(String gou_mai_ci_shu) {
        this.gou_mai_ci_shu = gou_mai_ci_shu;
    }

    public String getGou_mai_liang() {
        return gou_mai_liang;
    }

    public void setGou_mai_liang(String gou_mai_liang) {
        this.gou_mai_liang = gou_mai_liang;
    }

    public String getZheng_bao_jing_liang() {
        return zheng_bao_jing_liang;
    }

    public void setZheng_bao_jing_liang(String zheng_bao_jing_liang) {
        this.zheng_bao_jing_liang = zheng_bao_jing_liang;
    }

    public String getTou_zhi_liang() {
        return tou_zhi_liang;
    }

    public void setTou_zhi_liang(String tou_zhi_liang) {
        this.tou_zhi_liang = tou_zhi_liang;
    }

    public String getDa_liu_she_ding() {
        return da_liu_she_ding;
    }

    public void setDa_liu_she_ding(String da_liu_she_ding) {
        this.da_liu_she_ding = da_liu_she_ding;
    }

    public String getFa_men_zi_jian() {
        return fa_men_zi_jian;
    }

    public void setFa_men_zi_jian(String fa_men_zi_jian) {
        this.fa_men_zi_jian = fa_men_zi_jian;
    }

    public String getChong_zhi_ci_shu() {
        return chong_zhi_ci_shu;
    }

    public void setChong_zhi_ci_shu(String chong_zhi_ci_shu) {
        this.chong_zhi_ci_shu = chong_zhi_ci_shu;
    }

    public String getChong_zhi_jing_e() {
        return chong_zhi_jing_e;
    }

    public void setChong_zhi_jing_e(String chong_zhi_jing_e) {
        this.chong_zhi_jing_e = chong_zhi_jing_e;
    }

    public String getDang_qian_jia_ge() {
        return dang_qian_jia_ge;
    }

    public void setDang_qian_jia_ge(String dang_qian_jia_ge) {
        this.dang_qian_jia_ge = dang_qian_jia_ge;
    }

    @Override
    public String getData() {
        if(writeType.equals(WRITE_TYPE_CHARGING)){
            //计费
            return getGou_mai_ci_shu()+getChong_zhi_jing_e()+getDang_qian_jia_ge();
        }else {
            //计量
            return getGou_mai_ci_shu()+getGou_mai_liang()+getZheng_bao_jing_liang()
                    +getTou_zhi_liang()+getDa_liu_she_ding()+getFa_men_zi_jian();
        }
    }
}
