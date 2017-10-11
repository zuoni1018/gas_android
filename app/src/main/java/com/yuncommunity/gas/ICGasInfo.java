package com.yuncommunity.gas;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.oldfeel.utils.ETUtil;
import com.yuncommunity.gas.activity.CopyingActivity;
import com.yuncommunity.gas.base.MyActivity;
import com.yuncommunity.gas.base.Net;
import com.yuncommunity.gas.bean.gson.GetICUserList;
import com.yuncommunity.gas.conf.JsonApi;
import com.yuncommunity.gas.dialog.NoWriteRecord;
import com.yuncommunity.gas.protocol.GetCardInfoCommand;
import com.yuncommunity.gas.utils.MyUtils;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;
import com.zuoni.zuoni_common.utils.LogUtil;
import com.zuoni.zuoni_common.utils.ToastUtils;

import org.ksoap2.serialization.SoapObject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;

import static com.yuncommunity.gas.utils.Xml2Json.xml2JSON;


public class ICGasInfo extends MyActivity {
    private static final int REQUEST_ENABLE_BT = 124;
    private static final int REQUEST_CALCULATE = 125;
    @Bind(R.id.btn_back)
    TextView btnBack;
    @Bind(R.id.toolbar_title)
    TextView toolbarTitle;
    @Bind(R.id.pay_recording)
    TextView payRecording;
    @Bind(R.id.setting)
    ImageView setting;
    @Bind(R.id.user_num)
    TextView userNum;
    @Bind(R.id.user_name)
    TextView userName;
    @Bind(R.id.user_adr)
    TextView userAdr;
    @Bind(R.id.gas_image)
    ImageView gasImage;
    @Bind(R.id.gas_image_ll)
    LinearLayout gasImageLl;
    @Bind(R.id.gas_money)
    TextView gasMoney;
    @Bind(R.id.gas_unit)
    TextView gasUnit;
    @Bind(R.id.btn_payment)
    Button btnPayment;
    @Bind(R.id.du_ka_pay_num)
    EditText duKaPayNum;
    @Bind(R.id.calculate)
    ImageButton calculate;


    @Bind(R.id.du_ka_pay)
    LinearLayout duKaPay;//读卡
    @Bind(R.id.du_ka_pay_money)
    TextView duKaPayMoney;
    @Bind(R.id.du_ka_pay_submit)
    ImageButton duKaPaySubmit;
    @Bind(R.id.du_ka_recharge)
    LinearLayout duKaRecharge;


    @Bind(R.id.customerNo)
    TextView customerNo;
    @Bind(R.id.customerName)
    TextView customerName;
    @Bind(R.id.customerType)
    TextView customerType;
    @Bind(R.id.telNo)
    TextView telNo;
    @Bind(R.id.mobileNo)
    TextView mobileNo;
    @Bind(R.id.certNo)
    TextView certNo;
    @Bind(R.id.meterTypeNo)
    TextView meterTypeNo;
    @Bind(R.id.factoryNo)
    TextView factoryNo;
    @Bind(R.id.Address)
    TextView Address;
    @Bind(R.id.layoutIcUserInfo)
    LinearLayout layoutIcUserInfo;


    @Bind(R.id.info1)
    TextView info1;
    @Bind(R.id.info9)
    TextView info9;
    @Bind(R.id.info2)
    TextView info2;
    @Bind(R.id.info3)
    TextView info3;
    @Bind(R.id.info4)
    TextView info4;
    @Bind(R.id.info5)
    TextView info5;
    @Bind(R.id.info6)
    TextView info6;
    @Bind(R.id.info7)
    TextView info7;
    @Bind(R.id.info8)
    TextView info8;
    @Bind(R.id.layoutReadCardInfo)
    LinearLayout layoutReadCardInfo;
    @Bind(R.id.tvMessage)
    TextView tvMessage;
    @Bind(R.id.tvGetICUserList)
    TextView tvGetICUserList;
    @Bind(R.id.type)
    TextView type;


    //    private String no;
    private String money;


    private boolean isGetmeterNo = false;//是否已近拿到表号
    private String meterNo = "";//表号

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gas_info_duka);
        ButterKnife.bind(this);
        toolbarTitle.setText("XX燃气IC卡自助充值");

        initState();


//        no = getIntent().getStringExtra("no");


        // 获取未写卡的充值记录
//        Net net2 = new Net(this, JsonApi.GetICRecord);
//        net2.setParams("meterNo", "");
//        net2.sendRequest("", new Net.Callback() {
//            @Override
//            public void success(SoapObject soapObject) {
//                soapObject = MyUtils.getSoapObject(soapObject, "ICChargeRecord");
//                if (soapObject != null) {
//                    showNoWriteRecord(soapObject);
//                }
//            }
//        });

//        gasMoney.setText(Html.fromHtml("卡内金额:<font color='#ee941a'>" + 1200
//                + "</font>&nbsp;&nbsp;卡内气量:<font color='#ee941a'>" + 512
//                + "</font>&nbsp;&nbsp;充值次数:<font color='#ee941a'>" + 6 + "</font>"));

        ETUtil.hideSoftKeyboard(this);
    }

    private void initState() {
        tvMessage.setText("请先通过蓝牙获取卡号，才可以进行充值及查询卡信息等操作");
        layoutIcUserInfo.setVisibility(View.GONE);
        layoutReadCardInfo.setVisibility(View.GONE);
        duKaRecharge.setVisibility(View.GONE);
        duKaPay.setVisibility(View.GONE);
    }

    private void showNoWriteRecord(SoapObject soapObject) {
        NoWriteRecord noWriteRecord = NoWriteRecord.newInstance(MyUtils.getString(soapObject, "Id"));
        noWriteRecord.show(getSupportFragmentManager(), "no_write_record");
    }

    private String getInfo(String key, SoapObject soapObject) {
        if (soapObject.getProperty(key).toString().equals("anyType{}")) {
            return "";
        }
        return soapObject.getProperty(key).toString();
    }

    @OnClick(R.id.btn_payment)
    public void btnPayment() {
        Intent mIntent = new Intent(ICGasInfo.this, CopyingActivity.class);
        mIntent.putExtra("isRead",true);
        startActivityForResult(mIntent, COPY_REQUEST);
    }

    public static int COPY_REQUEST = 10086;
    public static int RESULT_CODE_COPY_CARD_INFO = 1000;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // 启动蓝牙
        if (resultCode == RESULT_OK && requestCode == REQUEST_ENABLE_BT) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    btnPayment();
                }
            }, 400);
        }

        // 计算金额
        if (resultCode == RESULT_OK && requestCode == REQUEST_CALCULATE) {
            money = data.getStringExtra("money");
            String num = data.getStringExtra("num");
            duKaPayNum.setText(num);
            duKaPayMoney.setText(money + "元");

            duKaRecharge.setVisibility(View.VISIBLE);
            calculate.setImageResource(R.drawable.cx_jisuan);
        }

        if (requestCode == COPY_REQUEST) {
            if (resultCode == RESULT_CODE_COPY_CARD_INFO) {

                GetCardInfoCommand getCardInfoCommand = (GetCardInfoCommand) data.getSerializableExtra("GetCardInfoCommand");
                meterNo = getCardInfoCommand.getInfo9();
                LogUtil.i("拿到表号", meterNo);

                info1.setText(getCardInfoCommand.getInfo1());
                info2.setText(getCardInfoCommand.getInfo2());
                info3.setText(getCardInfoCommand.getInfo3());
                info4.setText(getCardInfoCommand.getInfo4());

                info5.setText(getCardInfoCommand.getInfo5());
                info6.setText(getCardInfoCommand.getInfo6());
                info7.setText(getCardInfoCommand.getInfo7());
                info8.setText(getCardInfoCommand.getInfo8());

                info9.setText(getCardInfoCommand.getInfo9());
                type.setText(getCardInfoCommand.getInfo10());

                tvMessage.setText("获取取卡号成功!!!可以进行充值及查询卡信息");

                layoutIcUserInfo.setVisibility(View.VISIBLE);
                layoutReadCardInfo.setVisibility(View.VISIBLE);

                duKaRecharge.setVisibility(View.VISIBLE);
                duKaPay.setVisibility(View.VISIBLE);

                //查询
                GetICUserList(meterNo);
                GetICRecord(meterNo);//这个干嘛
            }

        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void GetICRecord(String meterNo) {
        Net net2 = new Net(this, JsonApi.GetICRecord);
        net2.setParams("meterNo", meterNo);
        net2.sendRequest("", new Net.Callback() {
            @Override
            public void success(SoapObject soapObject) {
                soapObject = MyUtils.getSoapObject(soapObject, "ICChargeRecord");
                if (soapObject != null) {
                    showNoWriteRecord(soapObject);
                }
            }
        });
    }

    /**
     * <预付费>通过表计编号查询用户基本信息
     */
    private void GetICUserList(String meterNo) {
        OkHttpUtils
                .post()
                .url(AppUrl.BASE_URL + JsonApi.GETICUSERLIST)
                .addParams("meterNo", meterNo)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        LogUtil.i("GetICUserList", e.toString());
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        LogUtil.i("GetICUserList", response);
                        LogUtil.i("GetICUserList2" + xml2JSON(response));
                        Gson gson = new Gson();
                        GetICUserList info = gson.fromJson(xml2JSON(response), GetICUserList.class);
                        if (info.getArrayOfUsersList().getUsersList() != null) {

                            ToastUtils.showToast(ICGasInfo.this, "查询卡信息成功");

                            customerNo.setText(info.getArrayOfUsersList().getUsersList().getCustomerNo());
                            customerName.setText(info.getArrayOfUsersList().getUsersList().getCustomerName());
                            customerType.setText(info.getArrayOfUsersList().getUsersList().getCustomerType());
                            telNo.setText(info.getArrayOfUsersList().getUsersList().getTelNo());

                            mobileNo.setText(info.getArrayOfUsersList().getUsersList().getMobileNo());
                            certNo.setText(info.getArrayOfUsersList().getUsersList().getCertNo());
                            meterTypeNo.setText(info.getArrayOfUsersList().getUsersList().getMeterTypeNo());
                            factoryNo.setText(info.getArrayOfUsersList().getUsersList().getFactoryNo());
                            Address.setText(info.getArrayOfUsersList().getUsersList().getAddress());
                        }else {
                            ToastUtils.showToast(ICGasInfo.this, "查询卡信息失败");

                            customerNo.setText("");
                            customerName.setText("");
                            customerType.setText("");
                            telNo.setText("");

                            mobileNo.setText("");
                            certNo.setText("");
                            meterTypeNo.setText("");
                            factoryNo.setText("");
                            Address.setText("");
                        }

                    }
                });
    }


    @OnClick(R.id.btn_back)
    public void btnBack() {
        onBackPressed();
    }

    @OnClick(R.id.pay_recording)
    public void payRecording() {
        Intent intent = new Intent(this, ICPayRecording.class);
        intent.putExtra("meterNo", meterNo);
        startActivity(intent);
    }


    //计算金额
    @OnClick(R.id.calculate)
    public void calculate() {
        Intent intent = new Intent(this, ICLvInfo.class);
        intent.putExtra("meterNo", "0000000001");
        intent.putExtra("num", duKaPayNum.getText().toString());
        startActivityForResult(intent, REQUEST_CALCULATE);
        calculate.setImageResource(R.drawable.cx_jisuan);
    }

    @OnClick(R.id.du_ka_pay_submit)
    public void dukaPaySubmit() {
//        Net net = new Net(this, JsonApi.ICRECHARGE);
//        net.setParams("meterNo", no);
//        net.setParams("buyVolume", duKaPayNum);
//        net.setParams("buyMoney", duKaPayMoney);
//        net.setParams("type", "");
//        net.setParams("payId", "");
//        net.sendRequest("", new Net.Callback() {
//            @Override
//            public void success(SoapObject soapObject) {
//                duKaRecharge.setVisibility(View.VISIBLE);
//            }
//        });

        Intent intent = new Intent(this, Payment.class);
        intent.putExtra("communicateNo", meterNo);
        intent.putExtra("payableAmount", money);
        intent.putExtra("from", Payment.FROM_IC);
        startActivity(intent);
    }

    public void showPay() {
        duKaPay.setVisibility(View.VISIBLE);
    }


    @OnClick(R.id.tvGetICUserList)
    public void onViewClicked() {
        GetICUserList(meterNo);
    }

}
