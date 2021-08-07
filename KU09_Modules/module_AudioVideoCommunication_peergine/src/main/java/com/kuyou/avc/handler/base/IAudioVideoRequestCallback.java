package com.kuyou.avc.handler.base;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;

import com.kuyou.avc.ui.base.BaseAVCActivity;

import java.util.Map;

import kuyou.common.ipc.RemoteEvent;

public interface IAudioVideoRequestCallback {


    /**
     * action:通话状态：待机
     * */
    public final static int HS_NORMAL = 0;
    
    /**
     * action:通话状态：申请打开中
     * */
    public final static int HS_OPEN_REQUEST_BE_EXECUTING = 1;

    /**
     * action:通话状态：申请成功打开中
     * */
    public final static int HS_OPEN_HANDLE_BE_EXECUTING = 2;
    
    /**
     * action:通话状态：开启中
     * */
    public final static int HS_OPEN = 2;

    /**
     * action:通话状态：关闭中
     * */
    public final static int HS_CLOSE_BE_EXECUTING = 3;

    public IAudioVideoRequestCallback setHandlerKeepAliveClient(Handler handlerKeepAliveClient);

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
