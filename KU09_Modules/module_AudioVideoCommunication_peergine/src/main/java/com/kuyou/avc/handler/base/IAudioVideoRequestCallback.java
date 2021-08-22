package com.kuyou.avc.handler.base;

public interface IAudioVideoRequestCallback {

    /**
     * action:通话状态：待机
     * */
    public final static int HS_NORMAL = 0;
    
    /**
     * action:通话状态：请求打开，正在处理
     * */
    public final static int HS_OPEN_REQUEST_BE_EXECUTING = 1;

    /**
     * action:通话状态：请求打开 > 请求成功，正在打开
     * */
    public final static int HS_OPEN_HANDLE_BE_EXECUTING = 2;
    
    /**
     * action:通话状态：打开成功，开启中
     * */
    public final static int HS_OPEN = 3;

    /**
     * action:通话状态：请求关闭，正在处理
     * */
    public final static int HS_CLOSE_BE_EXECUTING = 4;

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

}
