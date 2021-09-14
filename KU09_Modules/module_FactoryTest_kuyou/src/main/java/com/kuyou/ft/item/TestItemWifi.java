package com.kuyou.ft.item;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.Settings.SettingNotFoundException;
import android.util.Log;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.List;

import com.kuyou.ft.R;
import com.kuyou.ft.basic.TestItem;

public class TestItemWifi extends TestItem {

    private int mtThreadCount = 0, mMaxLoopTimes = 20;
    private boolean mLocationEnabled = false;
    private String mStrInfo;
    private List<ScanResult> mScanResult;

    private WifiScanSubThread mWifiScanSubThread;
    private TextView mTvShow;
    private WifiManager mWifiManager;
    private static int WIFI_SCAN_PERMISSION_CODE = 1000;

    private Handler mHandler = new Handler() {
        public void handleMessage(Message paramMessage) {
            if (paramMessage.what == 0) {
                Log.i(TAG, "mHandler.threadcount==" + mtThreadCount);
                mStrInfo = (getString(R.string.wifi_open) + ":");
                showWifi();

                if (mtThreadCount >= mMaxLoopTimes)
                    mWifiScanSubThread.interrupt();
            }
        }
    };

    @Override
    public int getTestId() {
        return R.id.test_wifi;
    }

    @Override
    public String getTestTitle(Context context) {
        return context.getString(R.string.wifi_test);
    }

    @Override
    public int getSubContentId() {
        return R.layout.test_item_wifi;
    }

    @Override
    protected void initViews() {
        super.initViews();
        mTvShow = findViewById(R.id.wifi_show);
    }

    @Override
    public void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);
        requastPermission(TestItemWifi.this);
        mStrInfo = (getString(R.string.wifi_open) + "\n");
        mWifiManager = ((WifiManager) getSystemService("wifi"));
        if (!(mLocationEnabled = isLocationEnabled())) {
            android.provider.Settings.Secure.putInt(getContentResolver(), android.provider.Settings.Secure.LOCATION_MODE, android.provider.Settings.Secure.LOCATION_MODE_HIGH_ACCURACY);
        } else {
            mLocationEnabled = false;
        }
        if (mWifiManager.isWifiEnabled()) {
            mTvShow.setText(getString(R.string.wifi_open));
            showWifi();
        } else {
            mTvShow.setText(getString(R.string.wifi_opening));
            mWifiManager.setWifiEnabled(true);
            mWifiScanSubThread = new WifiScanSubThread();
            mWifiScanSubThread.start();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (!mLocationEnabled) {
            android.provider.Settings.Secure.putInt(getContentResolver(), android.provider.Settings.Secure.LOCATION_MODE, android.provider.Settings.Secure.LOCATION_MODE_OFF);
        }
        mWifiManager.setWifiEnabled(false);
    }

    @Override
    protected void onAging() {
        super.onAging();
        onAgingTestItem(() -> onResult(true), 10000);
    }

    private String intToIp(int paramInt) {
        return (paramInt & 0xFF) + "." + (0xFF & paramInt >> 8) + "." + (0xFF & paramInt >> 16) + "." + (0xFF & paramInt >> 24);
    }

    private void showWifi() {
        //add chh android9.0开启WIFI不能主动扫描需要被动开启扫描
        mWifiManager.startScan();
        //end chh
        mScanResult = mWifiManager.getScanResults();
        if (this.mScanResult != null) {

            Log.i(TAG, "this.list==" + mScanResult + " size=" + mScanResult.size());

            if (this.mScanResult.size() != 0) {
                mtThreadCount = mMaxLoopTimes;

                for (int i = 0; i < mScanResult.size(); i++) {
                    ScanResult localScanResult = (ScanResult) mScanResult.get(i);
                    mStrInfo = (this.mStrInfo + "\nSSID: " + localScanResult.SSID + ",     Level: " + localScanResult.level);
                }
                WifiInfo localWifiInfo = mWifiManager.getConnectionInfo();
                String str = intToIp(localWifiInfo.getIpAddress());
                Log.i(TAG, "this.info==" + mStrInfo);
                mTvShow.setText(this.mStrInfo + "\n" + getString(R.string.wifi_now) + " \nIP address:" + str + "\nName:" + localWifiInfo.getSSID() + "\nMAC:" + localWifiInfo.getBSSID());
            } else {
                if (mtThreadCount >= mMaxLoopTimes)
                    mTvShow.setText(getString(R.string.wifi_now) + "\n No device be searched !");
            }
        }
    }

    private boolean isLocationEnabled() {
        ContentResolver resolver = getContentResolver();
        try {
            return android.provider.Settings.Secure.getInt(resolver, android.provider.Settings.Secure.LOCATION_MODE) != android.provider.Settings.Secure.LOCATION_MODE_OFF;
        } catch (SettingNotFoundException e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }
        return false;
    }

    public class WifiScanSubThread extends Thread {
        WifiScanSubThread() {
        }

        public void run() {
            Looper.prepare();
            while (mtThreadCount < mMaxLoopTimes) {
                if (mWifiManager.isWifiEnabled()) {
                    Message localMessage = new Message();
                    localMessage.what = 0;
                    mHandler.sendMessage(localMessage);
                }
                try {
                    Thread.sleep(2000L);
                } catch (InterruptedException localInterruptedException) {
                    localInterruptedException.printStackTrace();
                }

                mtThreadCount++;
            }
        }
    }

    private void requastPermission(Activity context) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission_group.LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // 获取wifi连接需要定位权限,没有获取权限
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_WIFI_STATE,
            }, WIFI_SCAN_PERMISSION_CODE);
            return;
        }
    }
}