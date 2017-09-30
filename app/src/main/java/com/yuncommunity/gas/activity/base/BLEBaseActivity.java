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


    private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                LogUtil.i(TAG, "蓝牙连接上了");//当设备连接上的时候
                bluetoothListener.connected();
                gatt.discoverServices();

            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                LogUtil.i(TAG, "蓝牙链接不上");
                bluetoothListener.disconnected();
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

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                LogUtil.i(TAG, "发现了服务");
//                initCharacteristic();
                try {
                    Thread.sleep(200);//延迟发送，否则第一次消息会不成功
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

//                final String uuid = "00002902-0000-1000-8000-00805f9b34fb";
//                BluetoothGattService service=  gatt.getService(UUID.fromString(uuid));
                List<BluetoothGattService> services = gatt.getServices();
//
                LogUtil.i(TAG, "服务数量" + services.size());
//                BluetoothGattCharacteristic bluetoothGattCharacteristic=   service.getCharacteristic(UUID.fromString(uuid));
                List<BluetoothGattCharacteristic> gcharacteristics = services.get(0).getCharacteristics();
//
                LogUtil.i(TAG, "特征数量" + gcharacteristics.size());
                BluetoothGattCharacteristic bluetoothGattCharacteristic = gcharacteristics.get(1);
                bluetoothGattCharacteristic.getProperties();
                bluetoothGattCharacteristic.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT);
                bluetoothGattCharacteristic.setValue(hexStringToByte("68a110a1e148540368096b6e8416"));
                bluetoothListener.onServicesDiscovered(gatt, bluetoothGattCharacteristic);
                gatt.writeCharacteristic(bluetoothGattCharacteristic);

            }

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
            bluetoothListener.onCharacteristicWrite(str);


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
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            Log.e(TAG, "gcharacteristics characteristic: " + characteristic);
//            readCharacteristic(characteristic);
        }
    };


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

    /**
     * 连接蓝牙设备
     */
    public void connectDevice(String address) {
        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);//address是mac字符串
        device.connectGatt(getContext(), false, mGattCallback);
    }






}
