package com.yuncommunity.gas.callback;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;

/**
 * Created by zangyi_shuai_ge on 2017/9/30
 */

public interface BluetoothListener {

    void connected();//连接上

    void disconnected();//断开连接

    void onServicesDiscovered(BluetoothGatt gatt, BluetoothGattCharacteristic  bluetoothGattCharacteristic);//发现了服务

    void onCharacteristicWrite(String message);
}
