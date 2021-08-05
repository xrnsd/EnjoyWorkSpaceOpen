package com.kuyou.rc.location.base;

import android.location.Location;

import com.kuyou.rc.info.LocationInfo;

/**
 * action :
 * <p>
 * remarks:  <br/>
 * author: wuguoxian <br/>
 * date: 21-7-8 <br/>
 * </p>
 */
public interface ILocationProvider {

    public Location getLocation();

    public LocationInfo getLocationInfo();
}
