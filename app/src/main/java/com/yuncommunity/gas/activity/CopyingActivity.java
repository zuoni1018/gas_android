package com.yuncommunity.gas.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.yuncommunity.gas.R;
import com.yuncommunity.gas.activity.base.BaseTitleActivity;
import com.yuncommunity.gas.bluetooth.BluetoothChatService;
import com.zuoni.zuoni_common.utils.LogUtil;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.yuncommunity.gas.bluetooth.BluetoothChat.DEVICE_NAME;


/**
 * Created by zangyi_shuai_ge on 2017/9/1
 * 杭天燃气抄表界面
 */

public class CopyingActivity extends BaseTitleActivity {
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
    // 从BluetoothChatService处理程序发送的消息类型
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;
//
//    // 从BluetoothChatService处理程序接收到的密钥名
//    public static final String DEVICE_NAME = "device_name";
//    private static SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


    //蓝牙
    private BluetoothAdapter mBluetoothAdapter;// 本地蓝牙适配器
    private BluetoothChatService mChatService; // 蓝牙服务
    private String mConnectedDeviceName;// 连接设备的名称


    // 从BluetoothChatService处理程序获得信息返回
    @SuppressLint("HandlerLeak")
    private final Handler mHandler = new Handler() {

        @SuppressLint("HandlerLeak")
        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                //蓝牙连接状态(改变的时候会收到这边的指令)
                case MESSAGE_STATE_CHANGE:
                    switch (msg.arg1) {
                        case BluetoothChatService.STATE_CONNECTED:
                            LogUtil.i("蓝牙设备", "蓝牙链接上了");
                            setBluetoothState(DEVICE_STATE_CONNECTED);
                            break;

                        case BluetoothChatService.STATE_CONNECTING:
                            LogUtil.i("蓝牙设备", "蓝牙正在连接...");
                            setBluetoothState(DEVICE_STATE_CONNECTING);
                            break;
                        case BluetoothChatService.STATE_LISTEN:
                        case BluetoothChatService.STATE_NONE:
                            LogUtil.i("蓝牙设备", "蓝牙未连接...");
                            setBluetoothState(DEVICE_STATE_NONE);
                            break;
                    }
                    break;
                case MESSAGE_READ:
                    //收到蓝牙结果
                    byte[] readBuf = (byte[]) msg.obj;
                    // 构建一个字符串有效字节的缓冲区
                    String readMessage = new String(readBuf, 0, msg.arg1);
//                    LogUtil.i("蓝牙得到结果", readMessage);
//                    readMessage(readMessage);

                    break;

                case MESSAGE_DEVICE_NAME:
                    // 保存连接设备的名字
                    mConnectedDeviceName = msg.getData().getString(DEVICE_NAME);
                    LogUtil.i("连接闪"+mConnectedDeviceName);
                    tvDeviceState.setText("已连接  :" + mConnectedDeviceName + "");
                    break;


            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        setTitle("蓝牙抄表");

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();// 获取本地蓝牙适配器
        if (mBluetoothAdapter == null) {
            showToast("当前设备不支持蓝牙");
            finish();//退出当前界面并不执行下面代码
            return;
        }

        mChatService = new BluetoothChatService(this, mHandler);//蓝牙可用则开启一个蓝牙服务
    }

    @Override
    protected void onStart() {
        super.onStart();
        // 如果蓝牙没开启，则开启蓝牙.
        // setupChat在onActivityResult()将被调用
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
            // 否则，设置通信会话
        } else {
            if (mChatService == null)
                // setupChat();
                Toast.makeText(this, "开启蓝牙会话", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        mChatService.stop();
        super.onDestroy();
    }

    @Override
    protected int setLayout() {
        return R.layout.ht_activity_copying;
    }

    // 发送蓝牙消息
    private void sendMessage(String message) {
        if (mChatService.getState() != BluetoothChatService.STATE_CONNECTED) {
            return;
        }
        //发送message
        if (message.length() > 0) {
            byte[] send = message.getBytes();
            mChatService.write(send);
        }
    }

    private void connectDevice(String address, boolean secure) {
        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        mChatService.connect(device);
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

                    connectDevice(address, true);//连接蓝牙设备
                }
                break;
            case REQUEST_ENABLE_BT:
                // 当请求启用蓝牙返回
                if (resultCode == Activity.RESULT_OK) {
                    showToast("蓝牙开启成功！");
                } else {
                    showToast("蓝牙未启用。程序退出");
                    finish();
                }
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

    /**
     * 根据蓝牙链接状态设置对应的UI
     */
    private void setBluetoothState(int deviceState) {
        nowConnectState = deviceState;
        switch (deviceState) {
            case DEVICE_STATE_NONE:
                tvDeviceState.setText("蓝牙设备未连接");
                break;
            case DEVICE_STATE_CONNECTING:
                tvDeviceState.setText("蓝牙设备正在连接...");
                break;
            case DEVICE_STATE_CONNECTED:
                tvDeviceState.setText("已连接  :" + mConnectedDeviceName + "");
                break;
        }
    }
}
