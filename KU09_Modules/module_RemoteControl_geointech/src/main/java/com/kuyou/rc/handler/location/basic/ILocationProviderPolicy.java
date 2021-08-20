package com.kuyou.rc.handler.location.basic;

/**
 * action :位置提供策略
 * <p>
 * remarks:  <br/>
 * author: wuguoxian <br/>
 * date: 21-08-20 <br/>
 * </p>
 */
public interface ILocationProviderPolicy {
    /**
     * action :未定位时使用缓存位置
     */
    public static final int POLICY_PROVIDER_CACHE_LOCATION = (1 << 0);
    /**
     * action :使用原生定位位置 <br/>
     * remark :<br/>
     *  01 互斥策略：POLICY_PROVIDER_AMAP
     */
    public static final int POLICY_PROVIDER_NORMAL_LOCAL = (1 << 1);
    /**
     * action :使用高德定位位置 <br/>
     * remark :<br/>
     *  01 互斥策略：POLICY_PROVIDER_NORMAL_LOCAL
     */
    public static final int POLICY_PROVIDER_AMAP = (1 << 2);
    /**
     * action :使用轨迹滤波器
     */
    public static final int POLICY_FILER = (1 << 3);
}
