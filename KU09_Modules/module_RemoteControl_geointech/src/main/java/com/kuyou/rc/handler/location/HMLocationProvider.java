package com.kuyou.rc.handler.location;

import android.app.HelmetModuleManageServiceManager;
import android.app.IHelmetModuleLocationCallback;
import android.content.Context;
import android.location.Location;
import android.util.Log;

import com.kuyou.rc.ModuleApplication;
import com.kuyou.rc.handler.location.basic.ILocationProvider;
import com.kuyou.rc.protocol.jt808extend.item.SicLocationAlarm;

import kuyou.sdk.jt808.base.RemoteControlDeviceConfig;

/**
 * action :位置提供器
 * <p>
 * remarks:  <br/>
 * author: wuguoxian <br/>
 * date: 21-4-2 <br/>
 * </p>
 */
public class HMLocationProvider implements ILocationProvider {
    protected final String TAG = "com.kuyou.rc.location > " + this.getClass().getSimpleName();

    protected Location mLocation = null;
    protected Context mContext = null;

    private IOnLocationChangeListener mLocationChangeListener = null;
    private SicLocationAlarm mLocationInfo = null;

    private HelmetModuleManageServiceManager mHmmsm;
    private RemoteControlDeviceConfig mRemoteControlDeviceConfig;

    private IHelmetModuleLocationCallback.Stub mHelmetModuleLocationCallback;

    private Location mLocationFake = null;
    private Location mLocationCache = null;

    private boolean isEnableLocationCache = false;

    public HMLocationProvider(Context context) {
        mContext = context.getApplicationContext();
        init();
    }

    protected Context getContext() {
        return mContext;
    }

    protected void init() {

    }

    public void start() {
        getHelmetModuleManageServiceManager()
                .registerHelmetModuleLocationCallback(mHelmetModuleLocationCallback);
    }

    public void stop() {
        getHelmetModuleManageServiceManager()
                .unregisterHelmetModuleLocationCallback(mHelmetModuleLocationCallback);
    }

    protected HelmetModuleManageServiceManager getHelmetModuleManageServiceManager() {
        if (null != mHmmsm) {
            return mHmmsm;
        }
        mHmmsm = (HelmetModuleManageServiceManager) getContext().getSystemService("helmet_module_manage_service");
        return mHmmsm;
    }

    public HMLocationProvider setLocationChangeListener(IOnLocationChangeListener listener) {
        mLocationChangeListener = listener;
        return HMLocationProvider.this;
    }

    /**
     * action :未定位时是否使用缓存位置
     */
    public HMLocationProvider setEnableLocationCache(boolean enableLocationCache) {
        isEnableLocationCache = enableLocationCache;
        return HMLocationProvider.this;
    }

    public HMLocationProvider setRemoteControlDeviceConfig(RemoteControlDeviceConfig config) {
        mRemoteControlDeviceConfig = config;
        return HMLocationProvider.this;
    }

    protected Location getLocationFake() {
        if (null == mLocationFake) {
            mLocationFake = new Location(ILocationProvider.FAKE_PROVIDER);
            mLocationFake.setLongitude(0);
            mLocationFake.setLatitude(0);
        }
        return mLocationFake;
    }

    protected Location getLocationCache() {
        if (null == mLocationCache) {
            mLocationCache = new Location(ILocationProvider.CACHE_PROVIDER);
            mLocationCache.setLongitude(0);
            mLocationCache.setLatitude(0);
        }
        if (null == mLocationCache) {
            return getLocationFake();
        }
        return mLocationCache;
    }

    protected RemoteControlDeviceConfig getRemoteControlDeviceConfig() {
        return mRemoteControlDeviceConfig;
    }

    @Override
    public void dispatchLocation(Location location) {
        mLocation = location;
        getLocationInfo().setLocation(location);
        if (null == mLocationChangeListener) {
            Log.d(TAG, "dispatchEventLocationChange > process fail : mLocationChangeListener is null");
            return;
        }
        mLocationChangeListener.onLocationChange(location);
    }

    @Override
    public Location getLocation() {
        if (!isEffectivePositioning()) {
            if (isEnableLocationCache) {
                return getLocationCache();
            }
            getLocationFake();
        }
        return mLocation;
    }

    @Override
    public SicLocationAlarm getLocationInfo() {
        if (null == mRemoteControlDeviceConfig) {
            throw new NullPointerException("RemoteControlDeviceConfig is null ,please perform method : setRemoteControlDeviceConfig");
        }
        if (null == mLocationInfo) {
            mLocationInfo = new SicLocationAlarm();
            mLocationInfo.setConfig(mRemoteControlDeviceConfig);
        }
        mLocationInfo.setLocation(getLocation());
        return mLocationInfo;
    }

    @Override
    public boolean isEffectivePositioning() {
        return null != mLocation;
    }

    public static interface IOnLocationChangeListener {
        public void onLocationChange(Location location);
    }
}
