package com.yuncommunity.gas.activity;

import android.app.Activity;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.yuncommunity.gas.R;
import com.yuncommunity.gas.activity.base.BLEBaseActivity;
import com.yuncommunity.gas.callback.BluetoothListener;
import com.zuoni.zuoni_common.utils.LogUtil;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * Created by zangyi_shuai_ge on 2017/9/1
 * 杭天燃气抄表界面
 */

public class CopyingActivity extends BLEBaseActivity implements BluetoothListener {
    @Bind(R.id.btnCopyScan)
    ImageButton btnCopyScan;
    @Bind(R.id.tvDeviceState)
    TextView tvDeviceState;
    @Bind(R.id.btnCopyingRead)
    ImageButton btnCopyingRead;
    @Bind(R.id.btnCopyingStop)
    ImageButton btnCopyingStop;
    @Bind(R.id.tvMessage)
    TextView tvMessage;

    //蓝牙 Intent请求代码
    private static final int REQUEST_CONNECT_DEVICE = 1;//蓝牙设备返回码
    private static final int REQUEST_ENABLE_BT = 2;//打开蓝牙
    //当前蓝牙设备连接状态
    private static final int DEVICE_STATE_NONE = 0;//设备未连接
    private static final int DEVICE_STATE_CONNECTING = 1;//设备正在连接
    private static final int DEVICE_STATE_CONNECTED = 2;//设备已经连接

    private int nowConnectState = 0;//当前设备连接状态

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        setTitle("蓝牙抄表");
        bluetoothListener = this;


//        SendCommand sendCommand = new SendCommand();
//        sendCommand.setCommandType(SendCommand.COMMAND_TYPE_VERIFICATION);
//
////        return getGou_mai_ci_shu()+getGou_mai_liang()+getZheng_bao_jing_liang()
////                +getTou_zhi_liang()+getDa_liu_she_ding()+getFa_men_zi_jian();
//        SendCommandWrite sendCommandWrite = new SendCommandWrite();
////        sendCommandWrite.setWriteType(SendCommandWrite.WRITE_TYPE_CHARGING);
//        sendCommandWrite.setWriteType(SendCommandWrite.WRITE_TYPE_METERING);
//        //计量
//        //0002 002000 05 01 01 17 ef 16
//        sendCommandWrite.setGou_mai_ci_shu("0002");
//        sendCommandWrite.setGou_mai_liang("002000");
//        sendCommandWrite.setZheng_bao_jing_liang("05");
//        sendCommandWrite.setTou_zhi_liang("01");
//        sendCommandWrite.setDa_liu_she_ding("01");
//        sendCommandWrite.setFa_men_zi_jian("17");
//
//        //计费
////        return getGou_mai_ci_shu()+getChong_zhi_jing_e()+getDang_qian_jia_ge();
//        // 0007 000000101000 00000117 f1 16
//        sendCommandWrite.setGou_mai_ci_shu("0007");
//        sendCommandWrite.setChong_zhi_jing_e("000000101000");
//        sendCommandWrite.setDang_qian_jia_ge("00000117");
//
//        ProtocolICRead.code(sendCommand);

//        ProtocolICRead.decode("68a101e1a148540368096e6bCS16");//身份验证
//
//        ProtocolICRead.decode("68a101e1a14854026806ffCS16");//写卡成功
    }


    @Override
    protected int setLayout() {
        return R.layout.ht_activity_copying;
    }

    /**
     * 设备扫描返回
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CONNECT_DEVICE:
                //获取一个蓝牙设备
                if (resultCode == Activity.RESULT_OK) {
                    String address = data.getExtras().getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
                    connectDevice(address);//连接蓝牙设备
                }
                break;
        }
    }


    @OnClick({R.id.btnCopyScan, R.id.btnCopyingRead, R.id.btnCopyingStop})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btnCopyScan:
                //搜索设备
                if (nowConnectState == DEVICE_STATE_CONNECTED) {
                    showToast("蓝牙设备已连接");
                } else {
                    Intent intent = new Intent(getContext(), DeviceListActivity.class);
                    startActivityForResult(intent, REQUEST_CONNECT_DEVICE);
                }
                break;
            case R.id.btnCopyingRead:
                if (nowConnectState == DEVICE_STATE_CONNECTED) {
                    //向蓝牙发送一条命令
//                    createCommand(1);
                } else {
                    showToast("请先连接蓝牙");
                }

                break;
            case R.id.btnCopyingStop:

                break;
        }
    }

    @Override
    public void connected() {
        LogUtil.i("回调","连接上了");
    }

    @Override
    public void disconnected() {
        LogUtil.i("回调","没连上");
    }

    @Override
    public void onServicesDiscovered(BluetoothGatt gatt, BluetoothGattCharacteristic bluetoothGattCharacteristic) {
        LogUtil.i("回调","发现");
    }

    @Override
    public void onCharacteristicWrite(String message) {
        LogUtil.i("回调","写数据"+message);
    }
}
