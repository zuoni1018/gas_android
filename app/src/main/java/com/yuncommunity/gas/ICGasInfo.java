package com.yuncommunity.gas;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.oldfeel.utils.ETUtil;
import com.oldfeel.utils.LogUtil;
import com.yuncommunity.gas.activity.CopyingActivity;
import com.yuncommunity.gas.base.MyActivity;
import com.yuncommunity.gas.base.Net;
import com.yuncommunity.gas.conf.JsonApi;
import com.yuncommunity.gas.dialog.BTDiscovery;
import com.yuncommunity.gas.dialog.NoWriteRecord;
import com.yuncommunity.gas.utils.MyUtils;

import org.ksoap2.serialization.SoapObject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by oldfeel on 17-4-18.
 */

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
    LinearLayout duKaPay;
    @Bind(R.id.du_ka_pay_money)
    TextView duKaPayMoney;
    @Bind(R.id.du_ka_pay_submit)
    ImageButton duKaPaySubmit;
    @Bind(R.id.du_ka_recharge)
    LinearLayout duKaRecharge;
    private String no;
    private String money;

    private BTDiscovery btDiscovery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gas_info_duka);
        ButterKnife.bind(this);
        toolbarTitle.setText("XX燃气IC卡自助充值");
        no = getIntent().getStringExtra("no");

        Net net1 = new Net(this, JsonApi.GETICUSERLIST);
        net1.setParams("meterNo", no);
        net1.sendRequest("", new Net.Callback() {
            @Override
            public void success(SoapObject soapObject) {
                soapObject = MyUtils.getSoapObject(soapObject, "UsersList");
                if (soapObject != null) {
                    userInfo.addNo(no);
                    userNum.setText("用户编号 : " + getInfo("customerNo", soapObject));
                    userName.setText("用户名称 : " + getInfo("customerName", soapObject));
                    userAdr.setText("用户地址 : " + getInfo("address", soapObject));
                } else {
                    userNum.setText("用户编号 : 暂无数据");
                    userName.setText("用户名称 : 暂无数据");
                    userAdr.setText("用户地址 : 暂无数据");
                }
            }
        });

        // 获取未写卡的充值记录
        Net net2 = new Net(this, JsonApi.GetICRecord);
        net2.setParams("meterNo", no);
        net2.sendRequest("", new Net.Callback() {
            @Override
            public void success(SoapObject soapObject) {
                soapObject = MyUtils.getSoapObject(soapObject, "ICChargeRecord");
                if (soapObject != null) {
                    showNoWriteRecord(soapObject);
                }
            }
        });

        gasMoney.setText(Html.fromHtml("卡内金额:<font color='#ee941a'>" + 1200
                + "</font>&nbsp;&nbsp;卡内气量:<font color='#ee941a'>" + 512
                + "</font>&nbsp;&nbsp;充值次数:<font color='#ee941a'>" + 6 + "</font>"));

        ETUtil.hideSoftKeyboard(this);
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
//        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
//        if (mBluetoothAdapter == null) {
//            showSimpleDialog("您的设备不支持蓝牙连接");
//            return;
//        }
//
//        if (!mBluetoothAdapter.isEnabled()) {
//            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
//            return;
//        }
//
//        btDiscovery = new BTDiscovery();
//        btDiscovery.show(getSupportFragmentManager(), "bt_discovery");
//
//        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
//        // If there are paired devices
//        if (pairedDevices.size() > 0) {
//            // Loop through paired devices
//            for (BluetoothDevice device : pairedDevices) {
//                // Add the name and address to an array adapter to show in a ListView
//                if (btDiscovery != null && btDiscovery.isAdded())
//                    btDiscovery.add(device.getName() + "\n" + device.getAddress());
//            }
//        }
//        mBluetoothAdapter.startDiscovery();

        Intent mIntent=new Intent(ICGasInfo.this, CopyingActivity.class);
        startActivity(mIntent);


    }

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
        super.onActivityResult(requestCode, resultCode, data);
    }

    // Create a BroadcastReceiver for ACTION_FOUND
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            // When discovery finds a device
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Get the BluetoothDevice object from the Intent
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // Add the name and address to an array adapter to show in a ListView
                LogUtil.showLog("xxxx"+"搜索到蓝牙设备"+device.getName());

                if (btDiscovery != null && btDiscovery.isAdded()) {
                    btDiscovery.add(device.getName() + "\n" + device.getAddress());
                }
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        // Register the BroadcastReceiver
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(mReceiver, filter); // Don't forget to unregister during onDestroy
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(mReceiver);
        super.onDestroy();
    }

    @OnClick(R.id.btn_back)
    public void btnBack() {
        onBackPressed();
    }

    @OnClick(R.id.pay_recording)
    public void payRecording() {
        Intent intent = new Intent(this, ICPayRecording.class);
        intent.putExtra("meterNo", no);
        startActivity(intent);
    }

    @OnClick(R.id.calculate)
    public void calculate() {
        Intent intent = new Intent(this, ICLvInfo.class);
        intent.putExtra("meterNo", no);
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
        intent.putExtra("communicateNo", no);
        intent.putExtra("payableAmount", money);
        intent.putExtra("from", Payment.FROM_IC);
        startActivity(intent);
    }

    public void showPay() {
        duKaPay.setVisibility(View.VISIBLE);
    }

    public void chooseDevice(){

    }
}
