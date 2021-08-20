package com.kuyou.rc.handler.location.basic;

import android.location.Location;

import com.kuyou.rc.protocol.jt808extend.item.SicLocationAlarm;

/**
 * action :位置提供[接口]
 * <p>
 * remarks:  <br/>
 * author: wuguoxian <br/>
 * date: 21-7-8 <br/>
 * </p>
 */
public interface ILocationDispatcherCallback {

    public void dispatchLocation(Location location);
}
