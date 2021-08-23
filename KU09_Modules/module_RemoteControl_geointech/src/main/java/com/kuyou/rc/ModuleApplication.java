package com.kuyou.rc;

import android.content.Context;

import com.kuyou.rc.handler.AlarmHandler;
import com.kuyou.rc.handler.LocalKeyHandler;
import com.kuyou.rc.handler.LocationHandler;
import com.kuyou.rc.handler.PlatformInteractiveHandler;
import com.kuyou.rc.handler.location.basic.ILocationProviderPolicy;

import java.util.ArrayList;
import java.util.List;

import kuyou.common.ku09.BasicModuleApplication;
import kuyou.common.ku09.event.avc.basic.EventAudioVideoCommunication;
import kuyou.common.ku09.event.rc.basic.EventRemoteControl;
import kuyou.common.ku09.handler.ModuleCommonHandler;
import kuyou.sdk.jt808.basic.RemoteControlDeviceConfig;

/**
 * action :远程控制模块
 * <p>
 * author: wuguoxian <br/>
 * date: 20-10-24 <br/>
 * <p>
 */
public class ModuleApplication extends BasicModuleApplication {
    private final String TAG = "com.kuyou.rc > ModuleApplication";

    private RemoteControlDeviceConfig mConfig;

    private ModuleCommonHandler mModuleCommonHandler;
    private PlatformInteractiveHandler mPlatformInteractiveHandler;
    private LocationHandler mLocationHandler;
    private AlarmHandler mAlarmHandler;
    private LocalKeyHandler mLocalKeyHandler;

    @Override
    protected String getApplicationName() {
        return "RemoteControl_geointech";
    }

    @Override
    protected List<Integer> getEventDispatchList() {
        List<Integer> list = new ArrayList<>();

        list.add(EventAudioVideoCommunication.Code.PHOTO_TAKE_RESULT);
        list.add(EventRemoteControl.Code.AUDIO_VIDEO_PARAMETERS_APPLY_REQUEST);
        list.add(EventRemoteControl.Code.PHOTO_UPLOAD_REQUEST);

        return list;
    }

    @Override
    protected void init() {
        super.init();

        registerEventHandler(
                getModuleBasicEventHandler(),
                getLocalKeyHandler(),
                getPlatformInteractiveHandler(),
                getAlarmHandler(),
                getLocationHandler());

        getPlatformInteractiveHandler().connect();
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

    public RemoteControlDeviceConfig getConfig() {
        if (null == mConfig) {
            mConfig = new RemoteControlDeviceConfig() {
                @Override
                public String getDevId() {
                    return ModuleApplication.this.getDevicesConfig().getDevId();
                }

                @Override
                public String getUwbId() {
                    return ModuleApplication.this.getDevicesConfig().getUwbId();
                }

                @Override
                public String getCollectingEndId() {
                    return ModuleApplication.this.getDevicesConfig().getCollectingEndId();
                }

                @Override
                public int getHeartbeatInterval() {
                    return ModuleApplication.this.getDevicesConfig().getHeartbeatInterval();
                }

                @Override
                public String getRemoteControlServerAddress() {
                    return ModuleApplication.this.getDevicesConfig().getRemoteControlServerAddress();
                }

                @Override
                public int getRemoteControlServerPort() {
                    return ModuleApplication.this.getDevicesConfig().getRemoteControlServerPort();
                }

                @Override
                public String getAuthenticationCode() {
                    return ModuleApplication.this.getDevicesConfig().getAuthenticationCode();
                }

                @Override
                public String getRemotePhotoServerAddress() {
                    return ModuleApplication.this.getDevicesConfig().getRemotePhotoServerAddress();
                }

                @Override
                public String getDirPathStoragePhoto() {
                    return ModuleApplication.this.getDevicesConfig().getDirPathStoragePhoto();
                }
            };
        }
        return mConfig;
    }

    public ModuleCommonHandler getModuleBasicEventHandler() {
        if (null == mModuleCommonHandler) {
            mModuleCommonHandler = new ModuleCommonHandler()
                    .setPowerStatusListener(getLocalKeyHandler());
        }
        return mModuleCommonHandler;
    }

    public LocationHandler getLocationHandler() {
        if (null == mLocationHandler) {
            int policy = 0;
            policy |= ILocationProviderPolicy.POLICY_PROVIDER_CACHE_LOCATION;
            policy |= ILocationProviderPolicy.POLICY_PROVIDER_AMAP;
            //policy |= ILocationProviderPolicy.POLICY_FILER;
            mLocationHandler = new LocationHandler()
                    .setLocationProviderPolicy(policy)
                    .initProviderFilter(ModuleApplication.this, getHandlerKeepAliveClient().getLooper(), getConfig());
        }
        return mLocationHandler;
    }

    public LocalKeyHandler getLocalKeyHandler() {
        if (null == mLocalKeyHandler) {
            mLocalKeyHandler = new LocalKeyHandler();
        }
        return mLocalKeyHandler;
    }

    public AlarmHandler getAlarmHandler() {
        if (null == mAlarmHandler) {
            mAlarmHandler = new AlarmHandler()
                    .setLocationProvider(getLocationHandler().getLocationProvider());
        }
        return mAlarmHandler;
    }

    protected PlatformInteractiveHandler getPlatformInteractiveHandler() {
        if (null == mPlatformInteractiveHandler) {
            mPlatformInteractiveHandler = new PlatformInteractiveHandler(new PlatformInteractiveHandler.IControlHandlerCallback() {
                @Override
                public Context getContext() {
                    return ModuleApplication.this;
                }

                @Override
                public RemoteControlDeviceConfig getConfig() {
                    return ModuleApplication.this.getConfig();
                }
            });
        }
        return mPlatformInteractiveHandler;
    }
}
