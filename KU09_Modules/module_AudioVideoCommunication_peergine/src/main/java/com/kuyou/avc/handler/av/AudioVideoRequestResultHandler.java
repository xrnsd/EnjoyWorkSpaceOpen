package com.kuyou.avc.handler.av;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.kuyou.avc.basic.photo.IAudioVideoRequestCallback;
import com.kuyou.avc.ui.basic.AVCActivity;

import java.util.HashMap;
import java.util.Map;

import kuyou.common.ipc.RemoteEvent;
import kuyou.common.ipc.RemoteEventBus;
import kuyou.common.ku09.handler.BasicAssistHandler;

/**
 * action :协处理器[音视频][抽象]
 * <p>
 * remarks:  <br/>
 * author: wuguoxian <br/>
 * date: 21-7-23 <br/>
 * </p>
 */
public abstract class AudioVideoRequestResultHandler extends BasicAssistHandler implements IAudioVideoRequestCallback,
        Application.ActivityLifecycleCallbacks,
        RemoteEventBus.IFrameLiveListener {

    protected final String TAG = "com.kuyou.avc.handler.av > AudioVideoRequestResultHandler";

    protected Map<Integer, AVCActivity> mItemListOnline = new HashMap<>();
    protected int mHandlerStatus;

    @Override
    public Map<Integer, AVCActivity> getOnlineList() {
        return mItemListOnline;
    }

    /**
     * action:解析事件项，打开对应模式的通信
     */
    protected abstract String openItem(Context context, RemoteEvent event);

    /**
     * action:解析事件项，打开对应模式的通信
     */
    protected abstract void exitAllLiveItem(int eventType);

    /**
     * action:对应模式的通信是否开启
     */
    protected abstract boolean isLiveOnlineByType(int typeCode);

    /**
     * action:对应模式的通信是否开启
     */
    @Override
    public int getHandlerStatus() {
        return mHandlerStatus;
    }

    /**
     * action:对应模式模块状态
     */
    public void setHandleStatus(int handlerStatus) {
        mHandlerStatus = handlerStatus;
    }

    public boolean isItInHandlerState(int handlerStatus) {
        return handlerStatus == getHandlerStatus();
    }

    @Override
    public void onIpcFrameResisterSuccess() {

    }

    @Override
    public void onIpcFrameUnResister() {

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
}
