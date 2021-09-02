package com.kuyou.rc.handler;

import android.app.Application;
import android.content.Context;
import android.location.Location;
import android.util.Log;

import com.kuyou.rc.handler.location.AMapLocationProvider;
import com.kuyou.rc.handler.location.HMLocationProvider;
import com.kuyou.rc.handler.location.NormalFilterLocationProvider;
import com.kuyou.rc.handler.location.basic.ILocationProvider;
import com.kuyou.rc.handler.location.basic.ILocationProviderPolicy;
import com.kuyou.rc.handler.location.filter.FilterController;
import com.kuyou.rc.handler.location.filter.basic.IFilterCallBack;
import com.kuyou.rc.protocol.jt808extend.item.SicLocationAlarm;

import kuyou.common.ipc.RemoteEvent;
import kuyou.common.ku09.event.rc.EventSendToRemoteControlPlatformRequest;
import kuyou.common.ku09.event.rc.basic.EventRemoteControl;
import kuyou.common.ku09.handler.BasicEventHandler;

/**
 * action :协处理器[位置]
 * <p>
 * remarks:  <br/>
 * author: wuguoxian <br/>
 * date: 21-8-5 <br/>
 * </p>
 */
public class LocationHandler extends BasicEventHandler implements ILocationProviderPolicy {

    protected static final String TAG = "com.kuyou.rc.handler > LocationHandler";

    protected static final boolean IS_ENABLE_FAKE_LOCATION = true;

    private HMLocationProvider mLocationProvider;
    private HMLocationProvider mLocationProviderFilter;

    private int mLocationProviderPolicy = 0;

    public LocationHandler initProviderFilter(Application application) {
        if (null != mLocationProvider) {
            return LocationHandler.this;
        }

        Context context = application.getApplicationContext();

        //位置提供器
        if (isEnableFilterByPolicy(ILocationProviderPolicy.POLICY_PROVIDER_AMAP)) {
            AMapLocationProvider locationProvider = new AMapLocationProvider(context);
            application.registerActivityLifecycleCallbacks(locationProvider);
            mLocationProvider = locationProvider;
        } else if (isEnableFilterByPolicy(ILocationProviderPolicy.POLICY_PROVIDER_NORMAL_LOCAL)) {
            mLocationProvider = new HMLocationProvider(context);
        }
        mLocationProvider.setDeviceConfig(getDeviceConfig());
        mLocationProvider.setLocationChangeListener(new HMLocationProvider.IOnLocationChangeListener() {
            @Override
            public void onLocationChange(Location location) {
                if (!isEnableFilterByPolicy(ILocationProviderPolicy.POLICY_FILER)) {
                    return;
                }
                LocationHandler.this.mLocationProviderFilter.dispatchLocation(location);
            }
        });
        mLocationProvider.setEnableLocationCache(isEnableFilterByPolicy(ILocationProviderPolicy.POLICY_PROVIDER_CACHE_LOCATION));

        //位置过滤器
        mLocationProviderFilter = NormalFilterLocationProvider.getInstance(context)
                .setFilter(new FilterController.IFilterPolicyCallBack() {
                    @Override
                    public int getFilterPolicy() {
                        int policy = 0;
                        policy |= IFilterCallBack.POLICY_FILTER_FLUCTUATION;
                        //policy |= IFilterCallBack.POLICY_FILTER_KALMAN;
                        return policy;
                    }
                }).setDeviceConfig(getDeviceConfig());
        mLocationProviderFilter.setLocationChangeListener(new HMLocationProvider.IOnLocationChangeListener() {
            @Override
            public void onLocationChange(Location location) {
                Log.d(TAG, "过滤后:\n" + LocationHandler.this.mLocationProviderFilter.getLocationInfo().toString());
            }
        });

        mLocationProvider.start();
        return LocationHandler.this;
    }

    public boolean isEffectivePositioning() {
        if (IS_ENABLE_FAKE_LOCATION) {
            return true;
        }
        return getLocationProvider().isEffectivePositioning();
    }

    public ILocationProvider getLocationProvider() {
        if (isEnableFilterByPolicy(ILocationProviderPolicy.POLICY_FILER)) {
            return mLocationProviderFilter;
        }
        return mLocationProvider;
    }

    public SicLocationAlarm getLocationInfo() {
        return getLocationProvider().getLocationInfo();
    }

    private boolean isEnableFilterByPolicy(final int policyFlag) {
        mLocationProviderPolicy = getLocationProviderPolicy();
        if (-1 != mLocationProviderPolicy)
            return (mLocationProviderPolicy & policyFlag) != 0;
        return false;
    }

    public int getLocationProviderPolicy() {
        return mLocationProviderPolicy;
    }

    public LocationHandler setLocationProviderPolicy(final int policy) {
        this.mLocationProviderPolicy = policy;
        return LocationHandler.this;
    }

    protected void delLocationProviderPolicy(final int policy) {
        mLocationProviderPolicy &= ~policy;
    }

    protected void addLocationProviderPolicy(final int policy) {
        if (POLICY_PROVIDER_NORMAL_LOCAL == policy) {
            delLocationProviderPolicy(POLICY_PROVIDER_AMAP);
        } else if (POLICY_PROVIDER_AMAP == policy) {
            delLocationProviderPolicy(POLICY_PROVIDER_NORMAL_LOCAL);
        }
        mLocationProviderPolicy |= policy;
    }

    @Override
    protected void initHandleEventCodeList() {
        registerHandleEvent(EventRemoteControl.Code.HEARTBEAT_REPORT, false);

        registerHandleEvent(EventRemoteControl.Code.LOCATION_REPORT_START_REQUEST, false);
        registerHandleEvent(EventRemoteControl.Code.LOCATION_REPORT_STOP_REQUEST, false);
    }

    @Override
    public boolean onReceiveEventNotice(RemoteEvent event) {
        switch (event.getCode()) {
//            case EventRemoteControl.Code.LOCATION_REPORT_START_REQUEST:
//                Log.i(TAG, "onReceiveEventNotice > 开始上报位置 ");
//                break;
//            case EventRemoteControl.Code.LOCATION_REPORT_STOP_REQUEST:
//                Log.i(TAG, "onReceiveEventNotice > 停止上报位置 ");
//                break;
            case EventRemoteControl.Code.HEARTBEAT_REPORT:
//                Log.d(TAG, "onReceiveEventNotice > 心跳");
                LocationHandler.this.dispatchEvent(new EventSendToRemoteControlPlatformRequest()
                        .setMsg(LocationHandler.this.getLocationInfo().getBody()));
                break;
            default:
                return false;
        }
        return true;
    }
}
