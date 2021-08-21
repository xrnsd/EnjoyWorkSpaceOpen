package com.kuyou.vc;

import com.kuyou.vc.definition.IVoiceControlCustomConfig;
import com.kuyou.vc.handler.VoiceControlHandler;
import com.kuyou.vc.protocol.base.VoiceControl;

import java.util.ArrayList;
import java.util.List;

import kuyou.common.ipc.RemoteEventBus;
import kuyou.common.ku09.BaseApplication;
import kuyou.common.ku09.event.avc.base.EventAudioVideoCommunication;
import kuyou.common.ku09.event.vc.EventVoiceWakeupRequest;
import kuyou.common.ku09.handler.KeyHandler;
import kuyou.common.ku09.key.KeyConfig;

/**
 * action :语音控制模块
 * <p>
 * author: wuguoxian <br/>
 * date: 20-11-3 <br/>
 * <p>
 */
public class ModuleApplication extends BaseApplication implements IVoiceControlCustomConfig {

    private KeyHandler mKeyHandler;
    private VoiceControlHandler mVoiceControlHandler;

    @Override
    protected String getApplicationName() {
        return "VoiceControl_unisound";
    }

    @Override
    protected void init() {
        super.init();

        registerHandler(getVoiceControlHandler(), getKeyHandler());
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
    protected RemoteEventBus.IFrameLiveListener getIpcFrameLiveListener() {
        return null;
    }

    @Override
    protected String isReady() {
        String statusSuper = super.isReady();
        StringBuilder status = new StringBuilder();
        if (null != statusSuper) {
            status.append(statusSuper);
        }
        if (!getVoiceControlHandler().isReady()) {
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

    public KeyHandler getKeyHandler() {
        if (null == mKeyHandler) {
            mKeyHandler = new KeyHandler() {
                @Override
                protected void onKeyLongClick(int keyCode) {
                    super.onKeyLongClick(keyCode);
                    if (KeyConfig.VOICE_CONTROL == keyCode) {
                        dispatchEvent(new EventVoiceWakeupRequest()
                                .setRemote(false));
                    }
                }
            };
        }
        return mKeyHandler;
    }

    public VoiceControlHandler getVoiceControlHandler() {
        if (null == mVoiceControlHandler) {
            mVoiceControlHandler = new VoiceControlHandler(getApplicationContext());
            mVoiceControlHandler.setVoiceType(VoiceControl.TYPE.HARDWARE);
            mVoiceControlHandler.setDispatchEventCallBack(ModuleApplication.this);
            mVoiceControlHandler.setModuleManager(ModuleApplication.this);
            mVoiceControlHandler.init();
        }
        return mVoiceControlHandler;
    }
}
