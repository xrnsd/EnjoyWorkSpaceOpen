package com.kuyou.rc;

import com.kuyou.rc.handler.AlarmHandler;
import com.kuyou.rc.handler.LocalKeyHandler;
import com.kuyou.rc.handler.LocationHandler;
import com.kuyou.rc.handler.photo.PhotoUploadHandler;
import com.kuyou.rc.handler.PlatformInteractiveHandler;
import com.kuyou.rc.handler.HardwareModuleDetectionHandler;
import com.kuyou.rc.basic.location.ILocationProviderPolicy;

import kuyou.common.ipc.RemoteEvent;
import kuyou.common.ku09.BasicModuleApplication;
import kuyou.common.ku09.event.common.basic.EventCommon;
import kuyou.common.ku09.handler.ModuleCommonHandler;

/**
 * action :远程控制模块
 * <p>
 * remarks:  <br/>
 * author: wuguoxian <br/>
 * date: 20-10-24 <br/>
 * </p>
 */
public class ModuleApplication extends BasicModuleApplication {

    protected final static String TAG = "com.kuyou.rc > ModuleApplication";

    private AlarmHandler mAlarmHandler;
    private LocalKeyHandler mLocalKeyHandler;
    private LocationHandler mLocationHandler;
    private PhotoUploadHandler mPhotoUploadHandler;
    private ModuleCommonHandler mModuleCommonHandler;
    private PlatformInteractiveHandler mPlatformInteractiveHandler;
    private HardwareModuleDetectionHandler mHardwareModuleDetectionHandler;

    @Override
    protected void initRegisterEventHandlers() {
        registerEventHandler(getModuleBasicEventHandler());
        registerEventHandler(getLocalKeyHandler());
        registerEventHandler(getPlatformInteractiveHandler());
        registerEventHandler(getPhotoUploadHandler());
        registerEventHandler(getAlarmHandler());
        registerEventHandler(getLocationHandler());
        registerEventHandler(getHardwareModuleDetectionHandler());
    }

    @Override
    protected void init() {
        super.init();
        getHardwareModuleDetectionHandler().start();
        getPlatformInteractiveHandler().start();
    }

    @Override
    protected String isReady() {
        String status = super.isReady();
        if (null != status) {
            return status;
        }

        //平台连接是否正常
        status = getPlatformInteractiveHandler().isReady();
        if (null != status) {
            return status;
        }

        //设备定位是否正常
        if (!getLocationHandler().isEffectivePositioning()) {
            return "未正常定位,尝试复位";
        }
        return null;
    }

    protected ModuleCommonHandler getModuleBasicEventHandler() {
        if (null == mModuleCommonHandler) {
            mModuleCommonHandler = new ModuleCommonHandler() {

                @Override
                public boolean onReceiveEventNotice(RemoteEvent event) {
                    super.onReceiveEventNotice(event);
                    return false;
                }

                @Override
                protected void initReceiveEventNotices() {
                    super.initReceiveEventNotices();
                    unRegisterHandleEvent(EventCommon.Code.NETWORK_CONNECTED);
                    unRegisterHandleEvent(EventCommon.Code.NETWORK_DISCONNECT);

                    registerHandleEvent(EventCommon.Code.NETWORK_CONNECTED, false);
                    registerHandleEvent(EventCommon.Code.NETWORK_DISCONNECT, false);
                }
            };
        }
        return mModuleCommonHandler;
    }

    protected LocationHandler getLocationHandler() {
        if (null == mLocationHandler) {
            int policy = 0;
            policy |= ILocationProviderPolicy.POLICY_PROVIDER_CACHE_LOCATION;
            policy |= ILocationProviderPolicy.POLICY_PROVIDER_AMAP;
            //policy |= ILocationProviderPolicy.POLICY_FILER;
            mLocationHandler = new LocationHandler()
                    .setLocationProviderPolicy(policy);
            mLocationHandler.setDevicesConfig(getDeviceConfig());
            mLocationHandler.initProviderFilter(ModuleApplication.this);

        }
        return mLocationHandler;
    }

    protected LocalKeyHandler getLocalKeyHandler() {
        if (null == mLocalKeyHandler) {
            mLocalKeyHandler = new LocalKeyHandler();
        }
        return mLocalKeyHandler;
    }

    protected AlarmHandler getAlarmHandler() {
        if (null == mAlarmHandler) {
            mAlarmHandler = new AlarmHandler()
                    .setLocationProvider(getLocationHandler().getLocationProvider());
        }
        return mAlarmHandler;
    }

    protected PlatformInteractiveHandler getPlatformInteractiveHandler() {
        if (null == mPlatformInteractiveHandler) {
            mPlatformInteractiveHandler = new PlatformInteractiveHandler();
        }
        return mPlatformInteractiveHandler;
    }

    protected PhotoUploadHandler getPhotoUploadHandler() {
        if (null == mPhotoUploadHandler) {
            mPhotoUploadHandler = new PhotoUploadHandler();
        }
        return mPhotoUploadHandler;
    }

    protected HardwareModuleDetectionHandler getHardwareModuleDetectionHandler() {
        if (null == mHardwareModuleDetectionHandler) {
            mHardwareModuleDetectionHandler = new HardwareModuleDetectionHandler();
        }
        return mHardwareModuleDetectionHandler;
    }
}
