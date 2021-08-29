package com.kuyou.avc.handler.basic;

import com.kuyou.avc.ui.basic.AVCActivity;

import java.util.Map;

public interface IAudioVideoRequestCallback {

    /**
     * action:[开/关/取消]音视频
     */
    public void performOperate();

    /**
     * action:切换音视频模式
     */
    public void switchMediaType();

    /**
     * action:根据模式code返回模式名称
     */
    public String getTitleByMediaType(final int typeCode, int combinationStrResId);

    /**
     * action:协处理器当前状态
     */
    public int getHandlerStatus();

    /**
     * action:协处理器当前打开的项列表
     */
    public Map<Integer, AVCActivity> getOnlineList();

}
