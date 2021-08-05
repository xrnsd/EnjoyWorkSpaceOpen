package com.kuyou.jt808.location.base;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;

import com.kuyou.jt808.business.PulseLocationData;
import com.kuyou.jt808.info.LocationInfo;

import kuyou.sdk.jt808.base.Jt808Config;

/**
 * action :位置提供器
 * <p>
 * remarks:  <br/>
 * author: wuguoxian <br/>
 * date: 21-4-2 <br/>
 * </p>
 */
public abstract class HMLocationProvider implements
        LocationInfo.CONFIG,
        ILocationProvider {
    protected final String TAG = "com.kuyou.jt808.location > " + this.getClass().getSimpleName();

    private IOnLocationChangeListener mLocationChangeListener = null;
    private Location mLocationFake = null;
    private PulseLocationData mPulseLocationData = null;

    protected Location mLocation = null;
    protected Context mContext = null;
    
    protected Jt808Config mJt808Config;

    public HMLocationProvider(Context context) {
        mContext = context.getApplicationContext();
        init();
    }

    protected abstract void init();

    public void setLocationChangeListener(IOnLocationChangeListener listener) {
        mLocationChangeListener = listener;
    }

    public void dispatchLocation(Location location) {
        mLocation = location;
        getLocationInfo().setLocation(location);
        if (null == mLocationChangeListener) {
            Log.w(TAG, "dispatchEventLocationChange > process fail : mLocationChangeListener is null");
            return;
        }
        mLocationChangeListener.onLocationChange(location);
    }

    @Override
    public Location getLocation() {
        if (!isValidLocation()) {
            if (null == mLocationFake) {
                mLocationFake = new Location(LocationManager.NETWORK_PROVIDER);
                //未实际定位前，模拟定位到北京天安门
                mLocationFake.setLongitude(116.38D);
                mLocationFake.setLatitude(39.90D);
                mLocationFake.setProvider(FAKE_PROVIDER);
            }
            return mLocationFake;
        }
        return mLocation;
    }

    public Jt808Config getJt808Config() {
        return mJt808Config;
    }

    public void setJt808Config(Jt808Config jt808Config) {
        mJt808Config = jt808Config;
    }

    public LocationInfo getLocationInfo() {
        if(null == mJt808Config){
            throw new NullPointerException("mJt808Config is null,please perform method : setJt808Config");
        }
        if (null == mPulseLocationData) {
            mPulseLocationData = new PulseLocationData();
            mPulseLocationData.setLocation(getLocation());
            mPulseLocationData.getLocationInfo().setConfig(mJt808Config);
        }
        mPulseLocationData.setLocation(mLocation);
        return mPulseLocationData.getLocationInfo();
    }

    public boolean isValidLocation() {
        return null != mLocation;
    }

    public static interface IOnLocationChangeListener {
        public void onLocationChange(Location location);
    }
}
