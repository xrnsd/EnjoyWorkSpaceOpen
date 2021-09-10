package com.kuyou.rc.basic.location;

import android.location.Location;

import com.kuyou.rc.protocol.jt808extend.item.SicLocationAlarm;

/**
 * action :位置分发[接口]
 * <p>
 * remarks:  <br/>
 * author: wuguoxian <br/>
 * date: 21-7-8 <br/>
 * </p>
 */
public interface ILocationDispatcherCallback {

    public void dispatchLocation(Location location);
}
