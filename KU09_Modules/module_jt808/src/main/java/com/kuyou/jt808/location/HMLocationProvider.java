package com.kuyou.jt808.location;

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
public abstract class HMLocationProvider implements LocationInfo.CONFIG, ILiveCallBack {
    protected final String TAG = "com.kuyou.jt808.location > " + this.getClass().getSimpleName();

    protected abstract int getLocationFreq();

    private IOnLocationChangeListener mLocationChangeListener = null;
    private Location mLocationFake = null;
    private PulseLocationData mPulseLocationData = null;

    protected Location mLocation = null;
    protected Context mContext = null;

    public HMLocationProvider(Context context) {
        mContext = context.getApplicationContext();
        init();
    }

    protected abstract void init();

    public void setLocationChangeListener(IOnLocationChangeListener locationChangeListener) {
        mLocationChangeListener = locationChangeListener;
    }

    public static interface IOnLocationChangeListener {
        public void onLocationChange(Location location);
    }

    public void dispatchEventLocationChange(Location location) {
        mLocation = location;
        if (null == mLocationChangeListener) {
            Log.w(TAG, "dispatchEventLocationChange > process fail : mLocationChangeListener is null");
            return;
        }
        mLocationChangeListener.onLocationChange(location);
    }

    protected Location getLocation() {
        if (!isValidLocation()) {
            android.util.Log.d(TAG, "getLocationFake > 未正常定位,将使用模拟位置信息");
            if (null == mLocationFake) {
                mLocationFake = new Location(LocationManager.NETWORK_PROVIDER);
                mLocationFake.setLongitude(113.907817D);
                mLocationFake.setLatitude(22.548229D);
                mLocationFake.setProvider(FAKE_PROVIDER);
            }
            return mLocationFake;
        }
        return mLocation;
    }

    public LocationInfo getLocationInfo(Jt808Config config) {
        if (null == mPulseLocationData) {
            mPulseLocationData = new PulseLocationData();
            mPulseLocationData.setLocation(getLocation());
            mPulseLocationData.getLocationInfo().setConfig(config);
        }
        mPulseLocationData.setLocation(mLocation);
        return mPulseLocationData.getLocationInfo();
    }

    public boolean isValidLocation() {
        return null != mLocation;
    }

    public ILiveCallBack getLiveCallBack() {
        return HMLocationProvider.this;
    }

    @Override
    public void onResume() {

    }

    @Override
    public void onStop() {

    }

    public abstract void startLocation();

    public abstract void startLocation(Context context);
}
