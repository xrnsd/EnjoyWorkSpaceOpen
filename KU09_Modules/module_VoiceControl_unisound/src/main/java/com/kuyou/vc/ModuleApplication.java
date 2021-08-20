package com.kuyou.vc;

import com.kuyou.vc.definition.IVoiceControlCustomConfig;
import com.kuyou.vc.protocol.VoiceControlHardware;
import com.kuyou.vc.protocol.VoiceControlSoft;
import com.kuyou.vc.protocol.base.IOnParseListener;
import com.kuyou.vc.protocol.base.VoiceControl;
import com.kuyou.vc.protocol.info.InfoVolume;

import java.util.ArrayList;
import java.util.List;

import kuyou.common.audio.AudioMngHelper;
import kuyou.common.ipc.RemoteEvent;
import kuyou.common.ku09.BaseApplication;
import kuyou.common.ku09.event.avc.EventAudioVideoOperateRequest;
import kuyou.common.ku09.event.avc.EventFlashlightRequest;
import kuyou.common.ku09.event.avc.EventPhotoTakeRequest;
import kuyou.common.ku09.event.avc.base.EventAudioVideoCommunication;
import kuyou.common.ku09.event.rc.EventAudioVideoParametersApplyRequest;
import kuyou.common.ku09.event.vc.EventVoiceWakeupRequest;
import kuyou.common.ku09.event.vc.base.EventVoiceControl;
import kuyou.common.ku09.key.KeyConfig;
import kuyou.common.ku09.protocol.IJT808ExtensionProtocol;

/**
 * action :语音控制模块
 * <p>
 * author: wuguoxian <br/>
 * date: 20-11-3 <br/>
 * <p>
 */
public class ModuleApplication extends BaseApplication implements IVoiceControlCustomConfig {

    private static ModuleApplication sApp;
    private AudioMngHelper mAudioMngHelper;
    private VoiceControl mVoiceControl;

    public static ModuleApplication getInstance() {
        return sApp;
    }

    @Override
    protected void init() {
        super.init();
        sApp = this;
        mAudioMngHelper = new AudioMngHelper(getApplicationContext());
    }

    @Override
    protected List<Integer> getEventDispatchList() {
        List<Integer> list = new ArrayList<>();

        list.add(EventAudioVideoCommunication.Code.AUDIO_VIDEO_OPERATE_RESULT);
        list.add(EventAudioVideoCommunication.Code.PHOTO_TAKE_RESULT);

        list.add(EventAudioVideoCommunication.Code.FLASHLIGHT_RESULT);
        list.add(EventAudioVideoCommunication.Code.LASER_LIGHT_RESULT);

        return list;
    }

    @Override
    protected String getApplicationName() {
        return "VoiceControl_unisound";
    }

    @Override
    protected String isReady() {
        String statusSuper = super.isReady();
        StringBuilder status = new StringBuilder();
        if (null != statusSuper) {
            status.append(statusSuper);
        }
        if (null == getVoiceControl()) {
            status.append(",VoiceControl初始化异常");
        }
        return status.toString();
    }

    @Override
    protected long getFeedTimeLong() {
        if (null != isReady())
            return INIT_CHECK_COUNT_MAX * INIT_CHECK_FREQ;
        return super.getFeedTimeLong();
    }

    @Override
    protected void initCallBack() {
        super.initCallBack();

        if (VoiceControl.TYPE.SOFT == getVoiceType()) {
            mVoiceControl = new VoiceControlSoft();
        } else if (VoiceControl.TYPE.HARDWARE == getVoiceType()) {
            mVoiceControl = new VoiceControlHardware();
        } else {
            throw new RuntimeException("getVoiceType() is invalid");
        }
        mVoiceControl.init(getApplicationContext());
        mVoiceControl.setListener(getListener());
        mVoiceControl.setCallBack(new VoiceControl.ICallBack() {
            @Override
            public void onPlay(String text) {
                ModuleApplication.this.play(text);
            }
        });
    }

    protected int getVoiceType() {
        return VoiceControl.TYPE.HARDWARE;
    }

    public VoiceControl getVoiceControl() {
        return mVoiceControl;
    }

    private String mNearPowerAlarmStatus;

    protected IOnParseListener getListener() {
        return new IOnParseListener() {

            @Override
            public boolean onWakeup(boolean switchStatus) {
                if (switchStatus) {
                    getVoiceControl().onWakeup();

                } else {
                    getVoiceControl().onSleep();

                    if (null != mNearPowerAlarmStatus) {
                        //NearPowerAlarm.open(mNearPowerAlarmStatus);
                        mNearPowerAlarmStatus = null;
                    }
                }
                return super.onWakeup(switchStatus);
            }

            @Override
            public boolean onShoot() {
                dispatchEvent(new EventPhotoTakeRequest()
                        .setFileName(new StringBuilder().append("IMG_").append(System.currentTimeMillis()).append(".jpg").toString())
                        .setUpload(true)
                        .setRemote(true));
                return super.onShoot();
            }

            @Override
            public boolean onCallEg() {
                dispatchEvent(new EventAudioVideoParametersApplyRequest()
                        .setPlatformType(IJT808ExtensionProtocol.PLATFORM_TYPE_PEERGIN)
                        .setMediaType(IJT808ExtensionProtocol.MEDIA_TYPE_AUDIO)
                        .setEventType(IJT808ExtensionProtocol.EVENT_TYPE_LOCAL_DEVICE_INITIATE)
                        .setRemote(true));
                return super.onCallEg();
            }

            @Override
            public boolean onCallHome() {
                dispatchEvent(new EventAudioVideoParametersApplyRequest()
                        .setPlatformType(IJT808ExtensionProtocol.PLATFORM_TYPE_PEERGIN)
                        .setMediaType(IJT808ExtensionProtocol.MEDIA_TYPE_AUDIO)
                        .setEventType(IJT808ExtensionProtocol.EVENT_TYPE_LOCAL_DEVICE_INITIATE)
                        .setRemote(true));
                return super.onCallHome();
            }

            @Override
            public boolean onCallEnd() {
                dispatchEvent(new EventAudioVideoOperateRequest()
                        .setMediaType(IJT808ExtensionProtocol.MEDIA_TYPE_AUDIO)
                        .setEventType(IJT808ExtensionProtocol.EVENT_TYPE_CLOSE)
                        .setRemote(true));
                return super.onCallEnd();
            }

            @Override
            public boolean onVideo(boolean switchStatus) {
                boolean result = super.onVideo(switchStatus);
                if (!switchStatus) {
                    dispatchEvent(new EventAudioVideoOperateRequest()
                            .setMediaType(IJT808ExtensionProtocol.MEDIA_TYPE_VIDEO)
                            .setEventType(IJT808ExtensionProtocol.EVENT_TYPE_CLOSE)
                            .setRemote(true));
                }
                dispatchEvent(new EventAudioVideoParametersApplyRequest()
                        .setMediaType(IJT808ExtensionProtocol.MEDIA_TYPE_VIDEO)
                        .setPlatformType(IJT808ExtensionProtocol.PLATFORM_TYPE_PEERGIN)
                        .setEventType(IJT808ExtensionProtocol.EVENT_TYPE_LOCAL_DEVICE_INITIATE)
                        .setRemote(true));
                return result;
            }

            @Override
            public boolean onFlashlight(boolean switchStatus) {
                dispatchEvent(new EventFlashlightRequest()
                        .setSwitch(switchStatus)
                        .setRemote(true));
                return true;
            }

            @Override
            public boolean onThermalCamera(boolean switchStatus) {
                boolean result = super.onThermalCamera(switchStatus);
                if (!switchStatus) {
                    dispatchEvent(new EventAudioVideoOperateRequest()
                            .setMediaType(IJT808ExtensionProtocol.MEDIA_TYPE_INFEARED)
                            .setEventType(IJT808ExtensionProtocol.EVENT_TYPE_CLOSE)
                            .setRemote(true));
                }else{
                    dispatchEvent(new EventAudioVideoParametersApplyRequest()
                            .setMediaType(IJT808ExtensionProtocol.MEDIA_TYPE_INFEARED)
                            .setPlatformType(IJT808ExtensionProtocol.PLATFORM_TYPE_PEERGIN)
                            .setEventType(IJT808ExtensionProtocol.EVENT_TYPE_LOCAL_DEVICE_INITIATE)
                            .setRemote(true));
                }
                return result;
            }

            @Override
            public boolean onVolumeChange(int configCode) {
                switch (configCode) {
                    case InfoVolume.Config.TRUN_UP:
                        mAudioMngHelper.addVoice100();
                        return true;
                    case InfoVolume.Config.TRUN_DOWN:
                        mAudioMngHelper.subVoice100();
                        return true;
                }
                return false;
            }
        };
    }

    @Override
    public void onModuleEvent(RemoteEvent event) {
        super.onModuleEvent(event);
        switch (event.getCode()) {
            case EventVoiceControl.Code.VOICE_WAKEUP:
                play("正在为您打开语音控制");
                getVoiceControl().start();
                break;
            default:
                break;
        }
    }

    @Override
    public void onKeyLongClick(int keyCode) {
        super.onKeyLongClick(keyCode);
        if (KeyConfig.VOICE_CONTROL == keyCode) {
            dispatchEvent(new EventVoiceWakeupRequest()
                    .setRemote(false));
            return;
        }
    }
}
