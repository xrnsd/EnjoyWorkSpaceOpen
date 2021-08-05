package com.kuyou.voicecontrol;

import com.kuyou.voicecontrol.definition.IVoiceControlCustomConfig;
import com.kuyou.voicecontrol.protocol.VoiceControlHardware;
import com.kuyou.voicecontrol.protocol.VoiceControlSoft;
import com.kuyou.voicecontrol.protocol.base.IOnParseListener;
import com.kuyou.voicecontrol.protocol.base.VoiceControl;
import com.kuyou.voicecontrol.protocol.info.InfoVolume;

import kuyou.common.ipc.RemoteEvent;
import kuyou.common.ku09.BaseApplication;
import kuyou.common.ku09.audio.AudioMngHelper;
import kuyou.common.ku09.event.jt808.EventAudioVideoParametersApplyRequest;
import kuyou.common.ku09.event.openlive.EventAudioRequest;
import kuyou.common.ku09.event.openlive.EventFlashlightRequest;
import kuyou.common.ku09.event.openlive.EventInfearedVideoRequest;
import kuyou.common.ku09.event.openlive.EventMediaRequest;
import kuyou.common.ku09.event.openlive.EventPhotoTakeRequest;
import kuyou.common.ku09.event.openlive.EventVideoRequest;
import kuyou.common.ku09.event.voicecontrol.EventVoiceWakeupRequest;
import kuyou.common.ku09.event.voicecontrol.base.ModuleEventVoiceControl;
import kuyou.common.ku09.key.KeyConfig;
import kuyou.common.ku09.JT808ExtensionProtocol.IAudioVideo;

/**
 * action :语音控制相关实现封装
 * <p>
 * author: wuguoxian <br/>
 * date: 20-11-3 <br/>
 * <p>
 */
public class ModuleApplication extends BaseApplication implements IVoiceControlCustomConfig, IAudioVideo {

    private static ModuleApplication sApp;
    private AudioMngHelper mAudioMngHelper;
    private VoiceControl mVoiceControl;

    @Override
    protected void init() {
        super.init();
        sApp = this;
        mAudioMngHelper = new AudioMngHelper(getApplicationContext());
    }

    public static ModuleApplication getInstance() {
        return sApp;
    }

    @Override
    protected String getApplicationName() {
        return "VoiceControl";
    }

    @Override
    protected boolean isReady() {
        return getVoiceControl() != null;
    }

    @Override
    protected long getFeedTimeLong() {
        if (!isReady())
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
                        .setFileName(new StringBuilder()
                                .append("IMG_")
                                .append(System.currentTimeMillis())
                                .append(".jpg").toString())
                        .setUpload(true)
                        .setRemote(true));
                return super.onShoot();
            }

            @Override
            public boolean onCallEg() {
                dispatchEvent(new EventAudioVideoParametersApplyRequest()
                        .setEventType(IAudioVideo.EVENT_TYPE_LOCAL_INITIATED)
                        .setMediaType(IAudioVideo.MEDIA_TYPE_AUDIO)
                        .setRemote(true));
                return super.onCallEg();
            }

            @Override
            public boolean onCallHome() {
                dispatchEvent(new EventAudioVideoParametersApplyRequest()
                        .setEventType(IAudioVideo.EVENT_TYPE_LOCAL_INITIATED)
                        .setMediaType(IAudioVideo.MEDIA_TYPE_AUDIO)
                        .setRemote(true));
                return super.onCallHome();
            }

            @Override
            public boolean onCallEnd() {
                dispatchEvent(new EventAudioRequest()
                        .setEventType(IAudioVideo.EVENT_TYPE_LOCAL_INITIATED)
                        .setAction(EventMediaRequest.Action.CLOSE)
                        .setRemote(true));
                dispatchEvent(new EventAudioVideoParametersApplyRequest()
                        .setEventType(IAudioVideo.EVENT_TYPE_LOCAL_INITIATED)
                        .setMediaType(IAudioVideo.MEDIA_TYPE_AUDIO)
                        .setSwitch(false)
                        .setRemote(true));
                return super.onCallEnd();
            }

            @Override
            public boolean onVideo(boolean switchStatus) {
                boolean result = super.onVideo(switchStatus);
                if (!switchStatus) {
                    dispatchEvent(new EventVideoRequest()
                            .setEventType(IAudioVideo.EVENT_TYPE_LOCAL_INITIATED)
                            .setAction(EventMediaRequest.Action.CLOSE)
                            .setRemote(true));
                }
                dispatchEvent(new EventAudioVideoParametersApplyRequest()
                        .setEventType(IAudioVideo.EVENT_TYPE_LOCAL_INITIATED)
                        .setMediaType(IAudioVideo.MEDIA_TYPE_VIDEO)
                        .setSwitch(switchStatus)
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
                    dispatchEvent(new EventInfearedVideoRequest()
                            .setEventType(IAudioVideo.EVENT_TYPE_LOCAL_INITIATED)
                            .setAction(EventMediaRequest.Action.CLOSE)
                            .setRemote(true));
                }
                dispatchEvent(new EventAudioVideoParametersApplyRequest()
                        .setEventType(IAudioVideo.EVENT_TYPE_LOCAL_INITIATED)
                        .setMediaType(IAudioVideo.MEDIA_TYPE_INFEARED)
                        .setSwitch(switchStatus)
                        .setRemote(true));
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
            case ModuleEventVoiceControl.Code.VOICE_WAKEUP:
                play("正在为您打开语音");
                getVoiceControl().start();
                break;
            default:
                break;
        }
    }

    @Override
    protected void onKeyClick(int keyCode) {
        if (KeyConfig.VOICE_CONTROL == keyCode) {
            dispatchEvent(new EventVoiceWakeupRequest()
                    .setRemote(false));
            return;
        }
    }
}
