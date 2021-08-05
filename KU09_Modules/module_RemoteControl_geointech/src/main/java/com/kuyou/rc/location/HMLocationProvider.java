package com.kuyou.rc.location;

import android.app.HelmetModuleManageServiceManager;
import android.app.IHelmetModuleLocationCallback;
import android.content.Context;
import android.location.Location;
import android.os.RemoteException;
import android.util.Log;

import com.kuyou.rc.info.LocationInfo;
import com.kuyou.rc.location.base.ILocationProvider;

import kuyou.sdk.jt808.base.RemoteControlDeviceConfig;

/**
 * action :位置提供器
 * <p>
 * remarks:  <br/>
 * author: wuguoxian <br/>
 * date: 21-4-2 <br/>
 * </p>
 */
public class HMLocationProvider implements
        LocationInfo.CONFIG,
        ILocationProvider {
    protected final String TAG = "com.kuyou.rc.location > " + this.getClass().getSimpleName();

    private IOnLocationChangeListener mLocationChangeListener = null;
    private Location mLocationFake = null;
    private LocationInfo mLocationInfo = null;

    protected Location mLocation = null;
    protected Context mContext = null;

    private RemoteControlDeviceConfig mRemoteControlDeviceConfig;

    public HMLocationProvider(Context context) {
        mContext = context.getApplicationContext();
        init();
    }

    protected Context getContext() {
        return mContext;
    }

    protected void init() {

    }

    public HMLocationProvider enableLocalBasePosition(HelmetModuleManageServiceManager hmmsm) {
        hmmsm.registerHelmetModuleLocationCallback(new IHelmetModuleLocationCallback.Stub() {
            @Override
            public void onLocationChange(Location location) throws RemoteException {
                HMLocationProvider.this.dispatchLocation(location);
            }
        });
        return HMLocationProvider.this;
    }

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
                mLocationFake = new Location(FAKE_PROVIDER);
                mLocationFake.setLongitude(113.908685D);
                mLocationFake.setLatitude(22.549927D);
            }
            return mLocationFake;
        }
        return mLocation;
    }

    public RemoteControlDeviceConfig getRemoteControlDeviceConfig() {
        return mRemoteControlDeviceConfig;
    }

    public void setRemoteControlDeviceConfig(RemoteControlDeviceConfig config) {
        mRemoteControlDeviceConfig = config;
    }

    @Override
    public LocationInfo getLocationInfo() {
        if (null == mRemoteControlDeviceConfig) {
            throw new NullPointerException("RemoteControlDeviceConfig is null ,please perform method : setRemoteControlDeviceConfig");
        }
        if (null == mLocationInfo) {
            mLocationInfo = new LocationInfo();
            mLocationInfo.setConfig(mRemoteControlDeviceConfig);
        }
        mLocationInfo.setLocation(getLocation());
        return mLocationInfo;
    }

    public boolean isValidLocation() {
        return null != mLocation;
    }

    public static interface IOnLocationChangeListener {
        public void onLocationChange(Location location);
    }
}
