package com.yuncommunity.gas.activity;

import android.app.Activity;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.yuncommunity.gas.ICGasInfo;
import com.yuncommunity.gas.R;
import com.yuncommunity.gas.activity.base.BLEBaseActivity;
import com.yuncommunity.gas.callback.BluetoothListener;
import com.yuncommunity.gas.protocol.ChangeCommand;
import com.yuncommunity.gas.protocol.GetCardInfoCommand;
import com.yuncommunity.gas.protocol.GetCommand;
import com.yuncommunity.gas.protocol.ProtocolICRead;
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

    private boolean isRead = true;//是否为读卡

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        setTitle("蓝牙抄表");
        bluetoothListener = this;

        isRead = getIntent().getBooleanExtra("isRead", true);

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
                    LogUtil.i("zzz", address);
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
                    needDiscoverServices = true;
                    //把上次的关了
                    if (bluetoothGatt != null) {
                        bluetoothGatt.close();
                    }
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
        LogUtil.i("回调", "连接上了");
        tvDeviceState.setText("连接成功");
        nowConnectState = DEVICE_STATE_CONNECTED;
    }

    @Override
    public void disconnected() {
        LogUtil.i("回调", "没连上");
        tvDeviceState.setText("设备未连接");
        nowConnectState = DEVICE_STATE_NONE;
    }

    private BluetoothGatt bluetoothGatt;
    private BluetoothGattCharacteristic bluetoothGattCharacteristic;

    @Override
    public void onServicesDiscovered(BluetoothGatt gatt, BluetoothGattCharacteristic bluetoothGattCharacteristic) {
//        LogUtil.i("回调", "发现");
        //发现蓝牙设备先把蓝牙设备保存下来
        //身份验证
        bluetoothGattCharacteristic.getProperties();
        bluetoothGattCharacteristic.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT);
        bluetoothGattCharacteristic.setValue(hexStringToByte("68a110a1e148540368096b6e8416".toUpperCase()));
        LogUtil.i("发送蓝牙命令" + "68a110a1e148540368096b6e8416");
        gatt.writeCharacteristic(bluetoothGattCharacteristic);

        bluetoothGatt = gatt;
        this.bluetoothGattCharacteristic = bluetoothGattCharacteristic;
    }

    @Override
    protected void onDestroy() {
        if (bluetoothGatt != null) {
            bluetoothGatt.disconnect();
            bluetoothGatt.close();
        }
        super.onDestroy();
    }

    private String nowMessage = "";

    @Override
    public void onCharacteristicChanged(String message) {
        LogUtil.i("回调收到数据:" + message);
        if (isAuthentication(message.toLowerCase())) {
            tvMessage.setText(tvMessage.getText().toString().trim() + "\n身份验证成功   !!!");
            //进行下一步骤
            tvMessage.setText(tvMessage.getText().toString().trim() + "\n正在查询卡片内容请稍后...");

            tvMessage.postDelayed(new Runnable() {
                @Override
                public void run() {
                    //查询卡信息
                    if (isRead) {
                        bluetoothGattCharacteristic.getProperties();
                        bluetoothGattCharacteristic.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT);
                        bluetoothGattCharacteristic.setValue(hexStringToByte("68a110a1e14854026801ffa116".toUpperCase()));
                        bluetoothGatt.writeCharacteristic(bluetoothGattCharacteristic);
                    } else {
                        //修改卡信息
                        bluetoothGattCharacteristic.getProperties();
                        bluetoothGattCharacteristic.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT);
                        bluetoothGattCharacteristic.setValue(hexStringToByte("68a110a1e148540a6806000200200005010117ef16".toUpperCase()));
                        bluetoothGatt.writeCharacteristic(bluetoothGattCharacteristic);
                    }
                }
            }, 200);

        } else {
            tvMessage.setText(tvMessage.getText().toString().trim() + "\n收到消息:" + "\n" + message);
            //判断是不是我要的命令
            nowMessage = nowMessage + message;
            if (isMyNeedMessage(nowMessage.toLowerCase())) {
                LogUtil.i("是我要的命令" + nowMessage);

                GetCommand getCommand = ProtocolICRead.decode(nowMessage);

                if (getCommand.getCommandType().equals(GetCommand.COMMAND_TYPE_QUERY)) {
                    final GetCardInfoCommand getCardInfoCommand = (GetCardInfoCommand) getCommand;
                    tvMessage.setText(tvMessage.getText().toString().trim() + "\n" + getCardInfoCommand.getResult());

                    tvMessage.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Intent intent = new Intent();
                            Bundle mBundle = new Bundle();
                            mBundle.putSerializable("GetCardInfoCommand", getCardInfoCommand);
                            intent.putExtras(mBundle);
                            setResult(ICGasInfo.RESULT_CODE_COPY_CARD_INFO, intent);
                            finish();
                        }
                    }, 200);

                } else if (getCommand.getCommandType().equals(GetCommand.COMMAND_TYPE_WRITE)) {
                    ChangeCommand changeCommand = (ChangeCommand) getCommand;
                    if (changeCommand.isSuccessfull()) {
                        showToast("写卡成功");
                    } else {
                        showToast("写卡失败");
                    }
                }

                //解析协议
                nowMessage = "";
            }

        }
    }

    private boolean isMyNeedMessage(String nowMessage) {
        if (nowMessage.substring(0, "68a101e1a14854".length()).equals("68a101e1a14854")) {
            if (nowMessage.substring(nowMessage.length() - 2, nowMessage.length()).equals("16")) {
                return true;
            }
        }
        return false;
    }

    /**
     * 是否为身份验证
     */
    private boolean isAuthentication(String message) {
        if (message.length() == "68a101e1a148540368096e6b7516".length()) {
            if (message.substring(0, "68a101e1a14854036809".length()).equals("68a101e1a14854036809")) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    public byte[] hexStringToByte(String hex) {
        int len = (hex.length() / 2);
        byte[] result = new byte[len];
        char[] achar = hex.toCharArray();
        for (int i = 0; i < len; i++) {
            int pos = i * 2;
            result[i] = (byte) (toByte(achar[pos]) << 4 | toByte(achar[pos + 1]));
        }
        return result;
    }

    private byte toByte(char c) {
        byte b = (byte) "0123456789ABCDEF".indexOf(c);
        return b;
    }
}
