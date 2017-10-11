package com.yuncommunity.gas.activity.base;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.yuncommunity.gas.callback.BluetoothListener;
import com.zuoni.zuoni_common.utils.LogUtil;

import java.util.List;

/**
 * Created by zangyi_shuai_ge on 2017/9/30
 * 低功耗蓝牙界面基类
 */

public abstract class BLEBaseActivity extends BaseTitleActivity {

    private String TAG = "低功耗蓝牙";
    public BluetoothListener bluetoothListener;//这个需要初始化
    public BluetoothAdapter mBluetoothAdapter;

    private final int GET_MESSAGE_TAG = 1000;

    private int nowState = BluetoothProfile.STATE_DISCONNECTED;


    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case BluetoothProfile.STATE_CONNECTED:
                    LogUtil.i(TAG, "蓝牙连接上了");//当设备连接上的时候
                    bluetoothListener.connected();
                    break;
                case BluetoothProfile.STATE_DISCONNECTED:
                    LogUtil.i(TAG, "蓝牙链接不上");
                    bluetoothListener.disconnected();
                    break;
                case GET_MESSAGE_TAG:
                    String message = (String) msg.obj;

//                    if(!lastMessage.equals(message)){
                    lastMessage = message;
                    bluetoothListener.onCharacteristicChanged((String) msg.obj);
//                    }
                    break;
            }
        }
    };

    public boolean needDiscoverServices = true;


    private BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            LogUtil.i("当前蓝牙状态:" + newState + "原来蓝牙状态" + nowState);
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                Message message = Message.obtain();
                message.what = BluetoothProfile.STATE_CONNECTED;
                handler.sendMessage(message);
                if (needDiscoverServices) {
                    LogUtil.i("当前蓝牙状态 搜索服务");
                    gatt.discoverServices();
                    needDiscoverServices = false;
                }

            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                Message message = Message.obtain();
                message.what = BluetoothProfile.STATE_DISCONNECTED;
                handler.sendMessage(message);
            }
        }


        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                LogUtil.i(TAG, "发现了服务");

                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                List<BluetoothGattService> services = gatt.getServices();

//                for (int i = 0; i < services.size(); i++) {
//                    List<BluetoothGattCharacteristic> gcharacteristics = services.get(0).getCharacteristics();
//                    for (int j = 0; j < gcharacteristics.size(); j++) {
//                        BluetoothGattCharacteristic bluetoothGattCharacteristic = gcharacteristics.get(j);
//                        UUID uuid = bluetoothGattCharacteristic.getUuid();
//                        LogUtil.i(TAG, uuid.toString());
//                    }
//                }
                //可以得到3个UUID
                //49535343-1e4d-4bd9-ba61-23c647249616  接收
                //49535343-8841-43f4-a8d4-ecbe34729bb3  上传
                //49535343-6daa-4d02-abf6-19569aca69fe  没用


                List<BluetoothGattCharacteristic> gcharacteristics = services.get(0).getCharacteristics();
//                LogUtil.i(TAG, "特征数量" + gcharacteristics.size());
                BluetoothGattCharacteristic bluetoothGattCharacteristic = gcharacteristics.get(1);//发送特征
//                gatt.readCharacteristic( gcharacteristics.get(0));
                gatt.setCharacteristicNotification(gcharacteristics.get(0), true);//接收特征
//                gatt.setCharacteristicNotification(gcharacteristics.get(1),true);
//                gatt.setCharacteristicNotification(gcharacteristics.get(2),true);
//                gatt.readCharacteristic(gcharacteristics.get(0));
//                gatt.readCharacteristic(gcharacteristics.get(1));
//                gatt.readCharacteristic(gcharacteristics.get(2));


                bluetoothListener.onServicesDiscovered(gatt, bluetoothGattCharacteristic);
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

        public String bytesToHexString(byte[] src) {
            StringBuilder stringBuilder = new StringBuilder("");
            if (src == null || src.length <= 0) {
                return null;
            }
            for (int i = 0; i < src.length; i++) {
                int v = src[i] & 0xFF;
                String hv = Integer.toHexString(v);
                if (hv.length() < 2) {
                    stringBuilder.append(0);
                }
                stringBuilder.append(hv);
            }
            return stringBuilder.toString();
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicWrite(gatt, characteristic, status);
            Log.e(TAG, "onCharacteristicWrite status: " + status);
            byte[] bytes = characteristic.getValue();
            String str = bytesToHexString(bytes);
            Log.i(TAG, "onCharacteristicWrite status: " + str);

        }

        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            super.onDescriptorWrite(gatt, descriptor, status);
            Log.e(TAG, "onDescriptorWrite status: " + status);
        }

        @Override
        public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            super.onDescriptorRead(gatt, descriptor, status);
            Log.e(TAG, "onDescriptorRead status: " + status);
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            Log.e(TAG, "onCharacteristicRead status: " + status);
//            characteristic.
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {

            byte[] bytes = characteristic.getValue();
            String str = bytesToHexString(bytes);
            LogUtil.i("onCharacteristicChanged====" + str);
            Message message = Message.obtain();
            message.what = GET_MESSAGE_TAG;
            message.obj = str;
            handler.sendMessage(message);
        }
    };

    private String lastMessage = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

        if (mBluetoothAdapter == null) {
            showToast("该设备不支持蓝牙");
            finishActivity();
            return;
        }

        //打开蓝牙
        if (!mBluetoothAdapter.isEnabled()) {
            mBluetoothAdapter.enable();
        }


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        mBluetoothAdapter.
    }

    /**
     * 连接蓝牙设备
     */
    public void connectDevice(String address) {
        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);//address是mac字符串
        device.connectGatt(getContext(), false, mGattCallback);
    }

}
