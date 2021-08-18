package com.kuyou.rc.location;

import android.content.Context;
import android.location.Location;
import android.os.Looper;
import android.util.Log;

import com.kuyou.rc.location.base.ILocationProvider;
import com.kuyou.rc.location.filter.FilterController;
import com.kuyou.rc.location.filter.base.IFilterCallBack;
import com.kuyou.rc.protocol.item.SicLocationAlarm;

import kuyou.common.ipc.RemoteEvent;
import kuyou.common.ku09.BaseHandler;
import kuyou.common.ku09.event.rc.EventSendToRemoteControlPlatformRequest;
import kuyou.common.ku09.event.rc.base.EventRemoteControl;
import kuyou.sdk.jt808.base.RemoteControlDeviceConfig;

/**
 * action :
 * <p>
 * remarks:  <br/>
 * author: wuguoxian <br/>
 * date: 21-8-5 <br/>
 * </p>
 */
public class LocationHandler extends BaseHandler {

    protected final String TAG = "com.kuyou.rc.handler > LocationHandler";

    public static final boolean IS_ENABLE_FAKE_LOCATION = true;

    private static LocationHandler sMain;

    private LocationHandler() {

    }

    public static LocationHandler getInstance() {
        if (null == sMain) {
            sMain = new LocationHandler();
        }
        return sMain;
    }

    private LocationReportHandler mLocationReportHandler;
    private HMLocationProvider mLocationProvider, mLocationProviderFilter;

    public LocationHandler init(Context context, Looper looper, RemoteControlDeviceConfig config) {

        if (null != mLocationProvider) {
            return LocationHandler.this;
        }

        //位置上报事件发生器，定期发出位置上报请求
        mLocationReportHandler = LocationReportHandler.getInstance(looper);
        mLocationReportHandler.setReportLocationFreq(config.getHeartbeatInterval());
        mLocationReportHandler.setLocationReportCallBack(new LocationReportHandler.IOnLocationReportCallBack() {
            @Override
            public void onLocationReport() {
                LocationHandler.this.dispatchEvent(new EventSendToRemoteControlPlatformRequest()
                        .setMsg(LocationHandler.this.getLocationInfo().getBody()));
            }
        });

        //位置提供器
        //mLocationProvider = new HMLocationProvider(context);
        mLocationProvider = new AMapLocationProvider(context);
        mLocationProvider.setRemoteControlDeviceConfig(config);
        mLocationProvider.setLocationChangeListener(new HMLocationProvider.IOnLocationChangeListener() {
            @Override
            public void onLocationChange(Location location) {
                //LocationHandler.this.mLocationProviderFilter.dispatchLocation(location);
            }
        });

        mLocationProviderFilter = NormalFilterLocationProvider.getInstance(context)
                .setFilter(new FilterController.IFilterPolicyCallBack() {
                    @Override
                    public int getFilterPolicy() {
                        int policy = 0;
                        policy |= IFilterCallBack.POLICY_FILTER_FLUCTUATION;
                        //policy |= IFilterCallBack.POLICY_FILTER_KALMAN;
                        return policy;
                    }
                }).setRemoteControlDeviceConfig(config);
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
        return mLocationProvider;
        //return mLocationProviderFilter;
    }

    public SicLocationAlarm getLocationInfo() {
        return getLocationProvider().getLocationInfo();
    }

    @Override
    public boolean onModuleEvent(RemoteEvent event) {
        switch (event.getCode()) {
            case EventRemoteControl.Code.LOCATION_REPORT_START_REQUEST:
                Log.d(TAG, "onModuleEvent > 开始上报位置 ");
                mLocationReportHandler.start();
                return true;
            case EventRemoteControl.Code.LOCATION_REPORT_STOP_REQUEST:
                Log.d(TAG, "onModuleEvent > 停止上报位置 ");
                mLocationReportHandler.stop();
                return true;
            default:
                break;
        }
        return false;
    }
}
