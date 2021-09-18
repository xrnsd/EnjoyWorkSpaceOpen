package com.kuyou.rc.basic.location;

import android.location.Location;

import com.kuyou.rc.basic.jt808extend.item.SicLocationAlarm;

/**
 * action :位置提供[接口]
 * <p>
 * remarks:  <br/>
 * author: wuguoxian <br/>
 * date: 21-7-8 <br/>
 * </p>
 */
public interface ILocationProvider extends ILocationDispatcherCallback, ILocationProviderPolicy {
    public static final String FAKE_PROVIDER = "fake";
    public static final String CACHE_PROVIDER = "cache";

    public boolean isEffectivePositioning();

    public Location getLocation();

    public SicLocationAlarm getLocationInfo();
}
