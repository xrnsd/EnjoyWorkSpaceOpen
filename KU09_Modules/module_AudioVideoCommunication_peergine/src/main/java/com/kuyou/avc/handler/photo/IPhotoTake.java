package com.kuyou.avc.handler.photo;

public interface IPhotoTake {
    /**
     * action :未定位时使用缓存位置
     */
    public static final int POLICY_FOREGROUND_TAKE  = (1 << 0);
    /**
     * action :使用原生定位位置 <br/>
     * remark :<br/>
     *  01 互斥策略：POLICY_PROVIDER_AMAP
     */
    public static final int POLICY_BACKGROUND_TAKE = (1 << 1);
    /**
     * action :使用原生定位位置 <br/>
     * remark :<br/>
     *  01 互斥策略：POLICY_PROVIDER_AMAP
     */
    public static final int POLICY_SCREENSHOT = (1 << 1);
}
