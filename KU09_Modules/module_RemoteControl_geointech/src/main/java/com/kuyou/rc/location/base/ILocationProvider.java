package com.kuyou.rc.location.base;

import android.location.Location;

import com.kuyou.rc.protocol.item.SicLocationAlarm;

/**
 * action :
 * <p>
 * remarks:  <br/>
 * author: wuguoxian <br/>
 * date: 21-7-8 <br/>
 * </p>
 */
public interface ILocationProvider {
    
    public boolean isValidLocation();

    public Location getLocation();

    public SicLocationAlarm getLocationInfo();

    public void dispatchLocation(Location location);
}
