package com.kuyou.rc.location;

import android.app.HelmetModuleManageServiceManager;
import android.app.IHelmetModuleLocationCallback;
import android.content.Context;
import android.location.Location;
import android.os.RemoteException;
import android.util.Log;

import com.kuyou.rc.ModuleApplication;
import com.kuyou.rc.location.base.ILocationProvider;
import com.kuyou.rc.protocol.item.SicLocationAlarm;

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
        SicLocationAlarm.CONFIG,
        ILocationProvider {
    protected final String TAG = "com.kuyou.rc.location > " + this.getClass().getSimpleName();

    private IOnLocationChangeListener mLocationChangeListener = null;
    private Location mLocationFake = null;
    private SicLocationAlarm mLocationInfo = null;

    protected Location mLocation = null;
    protected Context mContext = null;

    private HelmetModuleManageServiceManager mHmmsm;
    private RemoteControlDeviceConfig mRemoteControlDeviceConfig;

    private IHelmetModuleLocationCallback.Stub mHelmetModuleLocationCallback;
    private boolean isStartPositioning = false;

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
        isStartPositioning = true;
    }

    public void stop() {
        getHelmetModuleManageServiceManager()
                .unregisterHelmetModuleLocationCallback(mHelmetModuleLocationCallback);
        isStartPositioning = false;
    }

    protected HelmetModuleManageServiceManager getHelmetModuleManageServiceManager() {
        if (null != mHmmsm) {
            return mHmmsm;
        }
        try {
            mHmmsm = ModuleApplication.getInstance()
                    .getHelmetModuleManageServiceManager();
        } catch (Exception e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }
        if (null == mHmmsm) {
            mHmmsm = (HelmetModuleManageServiceManager) getContext().getSystemService("helmet_module_manage_service");
        }
        return mHmmsm;
    }

    public boolean isStart() {
        return isStartPositioning;
    }

    public IHelmetModuleLocationCallback.Stub getHelmetModuleLocationCallback() {
        if (null == mHelmetModuleLocationCallback) {
            try {
                mHelmetModuleLocationCallback = new IHelmetModuleLocationCallback.Stub() {
                    @Override
                    public void onLocationChange(Location location) throws RemoteException {
                        HMLocationProvider.this.dispatchLocation(location);
                    }
                };
            } catch (Exception e) {
                Log.e(TAG, Log.getStackTraceString(e));
            }
        }
        return mHelmetModuleLocationCallback;
    }

    public void setLocationChangeListener(IOnLocationChangeListener listener) {
        mLocationChangeListener = listener;
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

    public HMLocationProvider setRemoteControlDeviceConfig(RemoteControlDeviceConfig config) {
        mRemoteControlDeviceConfig = config;
        return HMLocationProvider.this;
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
