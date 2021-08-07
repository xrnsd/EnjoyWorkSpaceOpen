package com.kuyou.rc.location;

import android.content.Context;
import android.os.Looper;
import android.util.Log;

import com.kuyou.rc.info.LocationInfo;
import com.kuyou.rc.location.base.ILocationProvider;

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
    private HMLocationProvider mLocationProvider;

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
                //dispatchEvent(new EventLocationReportRequest());
                dispatchEvent(new EventSendToRemoteControlPlatformRequest()
                        .setMsg(getLocationInfo().getReportLocationMsgBody()));
            }
        });

        //位置提供器
        //mLocationProvider = NormalFilterLocationProvider.getInstance(getApplicationContext());
        mLocationProvider = AMapLocationProvider.getInstance(context);
        //mLocationProvider = new HMLocationProvider(context).enableLocalBasePosition(mHelmetModuleManageServiceManager);
        mLocationProvider.setRemoteControlDeviceConfig(config);

        return LocationHandler.this;
    }

    public boolean isValidLocation() {
        return getLocationProvider().isValidLocation();
    }

    public ILocationProvider getLocationProvider() {
        return mLocationProvider;
    }

    public LocationInfo getLocationInfo() {
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
//            case EventRemoteControl.Code.LOCATION_REPORT_REQUEST:
//                dispatchEvent(new EventSendToRemoteControlPlatformRequest()
//                        .setMsg(getLocationInfo().getReportLocationMsgBody()));
//                break;
            default:
                break;
        }
        return false;
    }
}
