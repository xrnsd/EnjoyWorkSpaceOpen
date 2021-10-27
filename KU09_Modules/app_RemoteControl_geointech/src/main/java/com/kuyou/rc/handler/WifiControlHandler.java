package com.kuyou.rc.handler;

import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.kuyou.rc.basic.wifi.IWifiConnectInfoListener;
import com.kuyou.rc.basic.wifi.WifiConnectInfo;
import com.kuyou.rc.basic.wifi.WifiConnectInfoParser;

import java.io.File;

import kuyou.common.file.AssetsCopy;
import kuyou.common.file.FileUtils;
import kuyou.common.ku09.handler.BasicAssistHandler;
import kuyou.common.status.StatusProcessBusCallbackImpl;
import kuyou.common.status.basic.IStatusProcessBusCallback;

/**
 * action :wifi自动连接实现
 * <p>
 * remarks:  <br/>
 * author: wuguoxian <br/>
 * date: 21-10-22 <br/>
 * </p>
 */
public class WifiControlHandler extends BasicAssistHandler {

    protected final String TAG = "com.kuyou.rc.handler > WifiControlHandler";

    protected final String ACTION_WIFI_AUTO_CONNECT = "kuyou.action.wifi.AUTO_CONNECT";
    protected final String ACTION_WIFI_SAVE = "kuyou.action.wifi.SAVE";
    protected final String ACTION_WIFI_CLEAR = "kuyou.action.wifi.CLEAR";

    protected final String KEY_SSID = "kuyou.config.wifi.ssid";
    protected final String KEY_SSID_PASSWORD = "kuyou.config.wifi.ssidPassword";

    protected final static int PS_WIFI_CONNECT = 2048;

    private String mFilePathWifiConfig = null;
    private WifiConnectInfoParser mWifiConnectInfoParser;

    @Override
    public void start() {
        super.start();
        if (!getDeviceConfig().isAutoEnableWifi()) {
            Log.w(TAG, "start > process : cancel auto enable wifi");
            return;
        }
        mFilePathWifiConfig = getDeviceConfig().getWifiConfigPath();
        if (TextUtils.isEmpty(mFilePathWifiConfig)) {
            Log.e(TAG, "start > process fail : invalid mFilePathWifiConfig = " + mFilePathWifiConfig);
            return;
        }
        if (!FileUtils.isExists(mFilePathWifiConfig)) {
            FileUtils.getInstance(getContext()).createDirPath(FileUtils.getParentByPath(mFilePathWifiConfig));
            FileUtils.getInstance(getContext()).createFile(new File(mFilePathWifiConfig));
            AssetsCopy.copy(getContext(),
                    FileUtils.getFileNameByPath(mFilePathWifiConfig),
                    FileUtils.getParentByPath(mFilePathWifiConfig));
        }
        if (!FileUtils.isExists(mFilePathWifiConfig)) {
            Log.e(TAG, "start > process fail : not exist mFilePathWifiConfig = " + mFilePathWifiConfig);
            return;
        }
        mWifiConnectInfoParser = new WifiConnectInfoParser()
                .setWifiConnectInfoListener(new IWifiConnectInfoListener() {
                    @Override
                    public void onReadWifiConnectInfo(WifiConnectInfo info) {
                        changeWifi(ACTION_WIFI_AUTO_CONNECT, info);
                    }

                    @Override
                    public void onReadFinish() {

                    }
                });
        getStatusProcessBus().start(PS_WIFI_CONNECT);
    }

    protected void changeWifi(String action, WifiConnectInfo info){
        Intent wifiSettings = new Intent(action);
        wifiSettings.setPackage("com.android.settings");
        wifiSettings.putExtra(KEY_SSID, info.getSsid());
        wifiSettings.putExtra(KEY_SSID_PASSWORD, info.getPassword());
        wifiSettings.addFlags(Intent.FLAG_RECEIVER_FOREGROUND);
        try{
            getContext().sendBroadcast(wifiSettings);
        }catch(Exception e){
            Log.e(TAG, Log.getStackTraceString(e));
        }
    }

    @Override
    protected void initReceiveProcessStatusNotices() {
        super.initReceiveProcessStatusNotices();
        getStatusProcessBus().registerStatusNoticeCallback(PS_WIFI_CONNECT, new StatusProcessBusCallbackImpl()
                .setNoticeReceiveFreq(1000)
                .setNoticeHandleLooperPolicy(IStatusProcessBusCallback.LOOPER_POLICY_BACKGROUND));
    }

    @Override
    protected void onReceiveProcessStatusNotice(int statusCode, boolean isRemove) {
        super.onReceiveProcessStatusNotice(statusCode, isRemove);
        switch (statusCode) {
            case PS_WIFI_CONNECT:
                Log.d(TAG, "onReceiveProcessStatusNotice > PS_WIFI_CONNECT");
                try {
                    mWifiConnectInfoParser.parse(mFilePathWifiConfig);
                } catch (Exception e) {
                    Log.e(TAG, Log.getStackTraceString(e));
                }
                break;
            default:
                break;
        }
    }
}
