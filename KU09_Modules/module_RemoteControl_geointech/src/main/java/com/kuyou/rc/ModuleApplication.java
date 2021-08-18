package com.kuyou.rc;

import android.content.Context;

import com.kuyou.rc.alarm.AlarmHandler;
import com.kuyou.rc.key.KeyHandler;
import com.kuyou.rc.location.LocationHandler;
import com.kuyou.rc.platform.PlatformInteractiveHandler;

import java.util.ArrayList;
import java.util.List;

import kuyou.common.ipc.RemoteEvent;
import kuyou.common.ku09.BaseApplication;
import kuyou.common.ku09.IPowerStatusListener;
import kuyou.common.ku09.event.avc.base.EventAudioVideoCommunication;
import kuyou.common.ku09.event.rc.base.EventRemoteControl;
import kuyou.common.ku09.key.IKeyEventListener;
import kuyou.sdk.jt808.base.RemoteControlDeviceConfig;

/**
 * action :远程控制模块
 * <p>
 * author: wuguoxian <br/>
 * date: 20-10-24 <br/>
 * <p>
 */
public class ModuleApplication extends BaseApplication {

    private static ModuleApplication sApplication;

    public static ModuleApplication getInstance() {
        return sApplication;
    }

    protected RemoteControlDeviceConfig mConfig;

    private PlatformInteractiveHandler mPlatformInteractiveHandler;
    private LocationHandler mLocationHandler;
    private AlarmHandler mAlarmHandler;
    private KeyHandler mKeyHandler;

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
        sApplication = ModuleApplication.this;
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

    public LocationHandler getLocationHandler() {
        if (null == mLocationHandler) {
            mLocationHandler = LocationHandler.getInstance()
                    .init(getApplicationContext(), getHandlerKeepAliveClient().getLooper(), getConfig());
            mLocationHandler.setDispatchEventCallBack(ModuleApplication.this);
        }
        return mLocationHandler;
    }

    public KeyHandler getKeyHandler() {
        if (null == mKeyHandler) {
            mKeyHandler = KeyHandler.getInstance();
            mKeyHandler.setDispatchEventCallBack(ModuleApplication.this);
        }
        return mKeyHandler;
    }

    public AlarmHandler getAlarmHandler() {
        if (null == mAlarmHandler) {
            mAlarmHandler = new AlarmHandler();
            mAlarmHandler.setLocationProvider(getLocationHandler().getLocationProvider());
            mAlarmHandler.setDispatchEventCallBack(ModuleApplication.this);
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
            mPlatformInteractiveHandler.setEventCallBack(ModuleApplication.this);
        }
        return mPlatformInteractiveHandler;
    }

    @Override
    protected IPowerStatusListener getPowerStatusListener() {
        return getKeyHandler();
    }

    @Override
    protected IKeyEventListener getKeyListener() {
        return getKeyHandler();
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

    @Override
    public void onModuleEvent(RemoteEvent event) {
        super.onModuleEvent(event);
        getPlatformInteractiveHandler().onModuleEvent(event);
        getAlarmHandler().onModuleEvent(event);
        getLocationHandler().onModuleEvent(event);
    }
}
