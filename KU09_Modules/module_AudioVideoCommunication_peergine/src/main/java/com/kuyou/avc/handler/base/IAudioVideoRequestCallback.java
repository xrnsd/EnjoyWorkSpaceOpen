package com.kuyou.avc.handler.base;

import android.app.Activity;
import android.content.Context;

import com.kuyou.avc.ui.base.BaseAVCActivity;

import java.util.Map;

import kuyou.common.ipc.RemoteEvent;

public interface IAudioVideoRequestCallback {
    
    public final static int HS_NORMAL = 0;
    public final static int HS_OPEN_BE_EXECUTING = 1;
    public final static int HS_OPEN = 2;
    public final static int HS_CLOSE_BE_EXECUTING = 3;

    /**
     * action:开启的模式列表
     */
    public Map<Integer, BaseAVCActivity> getOnlineList();

    /**
     * action:对应模式的通信是否开启
     */
    public abstract boolean isLiveByTypeCode(final int typeCode);

    /**
     * action:获取处理器状态
     */
    public abstract int getHandlerStatus();

    /**
     * action:解析事件项，打开对应模式的通信
     */
    public abstract String openItem(Context context, RemoteEvent event);

    /**
     * action:关闭所有模式当前打开的通信
     */
    public abstract void exitAllLiveActivity();

    /**
     * action:执行多媒体操作[向平台申请]
     */
    public abstract void performOperate(final int typeCode);

    /**
     * action:根据模式code返回模式名称
     */
    public abstract String getTitleByMediaType(final int typeCode, int combinationStrResId);

}
