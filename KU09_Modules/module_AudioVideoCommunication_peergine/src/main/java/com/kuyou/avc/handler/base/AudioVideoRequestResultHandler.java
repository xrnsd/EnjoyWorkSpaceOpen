package com.kuyou.avc.handler.base;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.kuyou.avc.ui.base.BaseAVCActivity;

import java.util.HashMap;
import java.util.Map;

import kuyou.common.ku09.BaseHandler;
import kuyou.common.ku09.config.DevicesConfig;

/**
 * action :
 * <p>
 * remarks:  <br/>
 * author: wuguoxian <br/>
 * date: 21-7-23 <br/>
 * </p>
 */
public abstract class AudioVideoRequestResultHandler extends BaseHandler implements IAudioVideoRequestCallback, Application.ActivityLifecycleCallbacks {

    protected Map<Integer, BaseAVCActivity> mItemListOnline = new HashMap<>();
    protected int mHandlerStatus;

    private Handler mHandlerKeepAliveClient;
    private DevicesConfig mDevicesConfig;

    protected DevicesConfig getDevicesConfig() {
        return mDevicesConfig;
    }

    public void setDevicesConfig(DevicesConfig devicesConfig) {
        mDevicesConfig = devicesConfig;
    }

    @Override
    public Map<Integer, BaseAVCActivity> getOnlineList() {
        return mItemListOnline;
    }

    @Override
    public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {

    }

    @Override
    public void onActivityStarted(@NonNull Activity activity) {

    }

    @Override
    public void onActivityResumed(@NonNull Activity activity) {

    }

    @Override
    public void onActivityPaused(@NonNull Activity activity) {

    }

    @Override
    public void onActivityStopped(@NonNull Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(@NonNull Activity activity) {

    }

    @Override
    public int getHandlerStatus() {
        return mHandlerStatus;
    }

    public void setHandlerStatus(int handlerStatus) {
        mHandlerStatus = handlerStatus;
    }

    public boolean isItInHandlerState(int handlerStatus) {
        return handlerStatus == getHandlerStatus();
    }

    @Override
    public AudioVideoRequestResultHandler setHandlerKeepAliveClient(Handler handlerKeepAliveClient) {
        mHandlerKeepAliveClient = handlerKeepAliveClient;
        return AudioVideoRequestResultHandler.this;
    }

    protected Handler getHandlerKeepAliveClient() {
        return mHandlerKeepAliveClient;
    }
}
