package com.kuyou.vc.handler;

import android.content.Context;

import com.kuyou.vc.protocol.VoiceControlHardware;
import com.kuyou.vc.protocol.VoiceControlSoft;
import com.kuyou.vc.protocol.basic.IOnParseListener;
import com.kuyou.vc.protocol.basic.VoiceControl;
import com.kuyou.vc.protocol.info.InfoVolume;

import kuyou.common.audio.AudioMngHelper;
import kuyou.common.ipc.RemoteEvent;
import kuyou.common.ku09.event.avc.EventAudioVideoOperateRequest;
import kuyou.common.ku09.event.avc.EventFlashlightRequest;
import kuyou.common.ku09.event.avc.EventPhotoTakeRequest;
import kuyou.common.ku09.event.rc.EventAudioVideoParametersApplyRequest;
import kuyou.common.ku09.event.vc.base.EventVoiceControl;
import kuyou.common.ku09.handler.BasicEventHandler;
import kuyou.common.ku09.protocol.IJT808ExtensionProtocol;

/**
 * action :协处理器[语音控制][Unisound]
 * <p>
 * author: wuguoxian <br/>
 * date: 21-8-21 <br/>
 * 待实现:<br/>
 * 在线升级语音指令<br/>
 * 语音控制模式切换<br/>
 * 添加独立的调试模式<br/>
 * </p>
 */
public class UnisoundVoiceControlHandler extends BasicEventHandler {
    private final String TAG = "com.kuyou.vc.handler > UnisoundVoiceControlHandler";

    private AudioMngHelper mAudioMngHelper;
    private VoiceControl mVoiceControl;

    public UnisoundVoiceControlHandler(Context context) {
        setContext(context.getApplicationContext());
        mAudioMngHelper = new AudioMngHelper(getContext());
    }

    public UnisoundVoiceControlHandler init(int voiceType) {
        if (VoiceControl.TYPE.SOFT == voiceType) {
            mVoiceControl = new VoiceControlSoft();
        } else if (VoiceControl.TYPE.HARDWARE == voiceType) {
            mVoiceControl = new VoiceControlHardware();
        } else {
            throw new RuntimeException("getVoiceControlType() is invalid");
        }
        mVoiceControl.init(getContext());
        mVoiceControl.setListener(getListener());
        mVoiceControl.setCallBack(new VoiceControl.ICallBack() {
            @Override
            public void onPlay(String text) {
                UnisoundVoiceControlHandler.this.play(text);
            }
        });
        return UnisoundVoiceControlHandler.this;
    }

    public void switchType(int type) {
        if (type == mVoiceControl.getType()) {
            return;
        }
        mVoiceControl.stop();
        init(type);
        mVoiceControl.start();
    }

    @Override
    public boolean onModuleEvent(RemoteEvent event) {
        switch (event.getCode()) {
            case EventVoiceControl.Code.VOICE_WAKEUP:
                play("正在为您打开语音控制");
                getVoiceControl().start();
                return true;
            default:
                return false;
        }
    }

    public boolean isReady() {
        return null != getVoiceControl();
    }

    protected VoiceControl getVoiceControl() {
        return mVoiceControl;
    }

    protected IOnParseListener getListener() {
        return new IOnParseListener() {
            @Override
            public boolean onWakeup(boolean switchStatus) {
                if (switchStatus) {
                    getVoiceControl().onWakeup();
                } else {
                    getVoiceControl().onSleep();
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
                            .setMediaType(IJT808ExtensionProtocol.MEDIA_TYPE_THERMAL)
                            .setEventType(IJT808ExtensionProtocol.EVENT_TYPE_CLOSE)
                            .setRemote(true));
                } else {
                    dispatchEvent(new EventAudioVideoParametersApplyRequest()
                            .setMediaType(IJT808ExtensionProtocol.MEDIA_TYPE_THERMAL)
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
}
