package com.kuyou.jt808.business;

import android.location.Location;
import android.util.Log;

import kuyou.sdk.jt808.base.Jt808Config;
import kuyou.sdk.jt808.base.socketbean.PulseData;
import com.kuyou.jt808.info.LocationInfo;

/**
 * action :位置上报作为心跳
 * <p>
 * author: wuguoxian <br/>
 * date: 20-12-8 <br/>
 * <p>
 */
public class PulseLocationData extends PulseData {
    private static final String TAG = "com.kuyou.jt808 > PulseLocationData";

    private LocationInfo mLocationInfo;

    public PulseLocationData() {
        super();
    }

    public LocationInfo getLocationInfo() {
        if (null == mLocationInfo)
            mLocationInfo = new LocationInfo();
        return mLocationInfo;
    }

    public void setConfig(Jt808Config config) {
        getLocationInfo().setConfig(config);
    }

    public void setLocation(Location location) {
        if (null == mLocationInfo) {
            mLocationInfo = new LocationInfo();
        }
        getLocationInfo().setLocation(location);
    }

    @Override
    public byte[] parse() {
        if (null == mLocationInfo)
            return null;
        Log.d(TAG, "心跳");
        return mLocationInfo.reportLocation();
    }
}
