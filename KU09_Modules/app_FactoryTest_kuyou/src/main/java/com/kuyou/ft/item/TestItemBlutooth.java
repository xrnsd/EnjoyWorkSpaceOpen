package com.kuyou.ft.item;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.TextView;

import com.kuyou.ft.R;
import com.kuyou.ft.basic.TestItemBasic;

public class TestItemBlutooth extends TestItemBasic {

    private int threadcount = 0,
            mBluetoothStatus = 0,
            Maxlooptimes = 30;
    private boolean mScanFlag = false;
    private StringBuilder mStrBluetoothInfo = new StringBuilder("===== Bluetooth device list =====\n");

    private TextView mTvShow, mTvAddress, mTvName;
    private BtSearchThread mBtSearchThread;
    private BluetoothAdapter mBluetoothAdapter;
    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        public void onReceive(Context paramContext, Intent paramIntent) {
            String action = paramIntent.getAction();
            if ("android.bluetooth.device.action.FOUND".equals(action)) {
                BluetoothDevice localBluetoothDevice = (BluetoothDevice) paramIntent.getParcelableExtra("android.bluetooth.device.extra.DEVICE");
                mBluetoothStatus = 2;
                mStrBluetoothInfo.append("\nAddress:").append(localBluetoothDevice.getAddress());
                mStrBluetoothInfo.append("\nName:").append(localBluetoothDevice.getName());
                mStrBluetoothInfo.append("\n");

                mTvShow.setText(mStrBluetoothInfo.toString());
            } else if ("android.bluetooth.adapter.action.DISCOVERY_FINISHED".equals(action)) {
                mStrBluetoothInfo.append("BT search Finished\n");
                mTvShow.setText(mStrBluetoothInfo.toString());
                mBluetoothStatus = 3;
                if (isTestModeAging()) {
                    finish();
                }
            }
        }
    };

    private Handler mHandler = new Handler() {
        public void handleMessage(Message paramMessage) {
            if (paramMessage.what == 0) {
                Log.i(TAG, "mHandler threadcount==" + threadcount);
                showBluetooth();
            }
        }
    };

    @Override
    public int getTestId() {
        return R.id.test_bluetooth;
    }

    @Override
    public int getSubContentId() {
        return R.layout.test_item_bluetooth;
    }

    @Override
    public String getTestTitle(Context context) {
        return context.getString(R.string.test_bluetooth);
    }

    @Override
    public void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);

        mScanFlag = false;
        mBluetoothStatus = 0;

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter != null) {
            if (mBluetoothAdapter.isEnabled()) {
                mBluetoothStatus = 1;
                showBluetooth();
            } else {
                mTvShow.setText(getString(R.string.blue_opening));
                mBluetoothAdapter.enable();

                mBtSearchThread = new BtSearchThread();
                mBtSearchThread.start();
            }
        }
    }

    @Override
    protected void initViews() {
        super.initViews();
        mTvShow = findViewById(R.id.b_show);
        mTvAddress = findViewById(R.id.b_address);
        mTvName = findViewById(R.id.b_name);
    }

    @Override
    protected void initWindowConfig() {
        super.initWindowConfig();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mBluetoothAdapter.disable();
        try {
            unregisterReceiver(mBroadcastReceiver);
        } catch (Exception e) {
            Log.e("123456", Log.getStackTraceString(e));
        }
    }

    protected void showBluetooth() {
        if (threadcount >= (Maxlooptimes - 1)) {
            if (mBluetoothStatus < 2) {
                mTvShow.setText("No BT device be searched");
            }
            if (mBtSearchThread != null) {
                mBtSearchThread.interrupt();
            }
        } else {
            if (mBluetoothAdapter.isEnabled()) {
                if (mScanFlag == false) {
                    mScanFlag = true;
                    mBluetoothStatus = 1;
                    try {
                        IntentFilter intentFilter = new IntentFilter("android.bluetooth.device.action.FOUND");
                        intentFilter.addAction("android.bluetooth.adapter.action.DISCOVERY_FINISHED");
                        registerReceiver(mBroadcastReceiver, intentFilter);
                    } catch (Exception e) {
                        Log.e("123456", Log.getStackTraceString(e));
                        unregisterReceiver(mBroadcastReceiver);
                    }
                    mBluetoothAdapter.startDiscovery();
                }
            }
            if (mBluetoothStatus == 0) {
                mTvShow.setText(R.string.blue_opening);
            } else if (mBluetoothStatus == 1) {
                mTvShow.setText(R.string.blue_open);
            } else {
                mTvShow.setText(mStrBluetoothInfo);
            }
            mTvShow.invalidate();
        }
    }

    @Override
    protected void onAging() {
        super.onAging();
        onAgingTestItem(() -> onResult(true), 8000);
    }

    public class BtSearchThread extends Thread {
        BtSearchThread() {
        }

        public void run() {
            Looper.prepare();
            while (threadcount < Maxlooptimes) {
                {
                    Message localMessage = new Message();
                    localMessage.what = 0;
                    Log.i(TAG, "run threadcount==" + threadcount);
                    mHandler.sendMessage(localMessage);
                }
                try {
                    Thread.sleep(2000L);
                } catch (InterruptedException localInterruptedException) {
                    localInterruptedException.printStackTrace();
                }
                threadcount++;
            }
        }
    }
}