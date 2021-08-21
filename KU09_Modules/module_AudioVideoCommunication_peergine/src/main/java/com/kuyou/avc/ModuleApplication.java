package com.kuyou.avc;

import com.kuyou.avc.handler.FlashlightHandler;
import com.kuyou.avc.handler.LocalKeyHandler;
import com.kuyou.avc.handler.LocalModuleCommonHandler;
import com.kuyou.avc.handler.PeergineAudioVideoHandler;
import com.kuyou.avc.handler.base.AudioVideoRequestResultHandler;

import java.util.ArrayList;
import java.util.List;

import kuyou.common.ipc.RemoteEventBus;
import kuyou.common.ku09.BaseApplication;
import kuyou.common.ku09.event.avc.base.EventAudioVideoCommunication;
import kuyou.common.ku09.event.common.base.EventCommon;
import kuyou.common.ku09.event.rc.base.EventRemoteControl;

/**
 * action :音视频服务模块
 * <p>
 * remarks:  <br/>
 * author: wuguoxian <br/>
 * date: 21-7-12 <br/>
 * </p>
 */
public class ModuleApplication extends BaseApplication {

    private static final String TAG = "com.kuyou.avc > ModuleApplication";

    private LocalModuleCommonHandler mModuleEventHandler;
    private AudioVideoRequestResultHandler mAudioVideoRequestHandler;
    private FlashlightHandler mFlashlightHandler;
    private LocalKeyHandler mLocalKeyHandler;

    @Override
    protected String getApplicationName() {
        return "AudioVideoCommunication_peergine";
    }

    @Override
    protected List<Integer> getEventDispatchList() {
        List<Integer> list = new ArrayList<>();

        list.add(EventCommon.Code.NETWORK_CONNECTED);
        list.add(EventCommon.Code.NETWORK_DISCONNECT);

        list.add(EventRemoteControl.Code.PHOTO_UPLOAD_RESULT);
        list.add(EventRemoteControl.Code.AUDIO_VIDEO_PARAMETERS_APPLY_RESULT);

        list.add(EventAudioVideoCommunication.Code.AUDIO_VIDEO_OPERATE_REQUEST);
        list.add(EventAudioVideoCommunication.Code.PHOTO_TAKE_REQUEST);
        list.add(EventAudioVideoCommunication.Code.FLASHLIGHT_REQUEST);

        return list;
    }

    @Override
    protected void init() {
        super.init();
        registerHandler(
                getModuleEventHandler(),
                getLocalKeyHandler(),
                getAudioVideoRequestHandler(),
                getFlashlightHandler());
    }

    @Override
    protected long getFeedTimeLong() {
        return 30 * 1000;
    }

    @Override
    protected RemoteEventBus.IFrameLiveListener getIpcFrameLiveListener() {
        return getAudioVideoRequestHandler();
    }

    public LocalModuleCommonHandler getModuleEventHandler() {
        if (null == mModuleEventHandler) {
            mModuleEventHandler = new LocalModuleCommonHandler();
        }
        return mModuleEventHandler;
    }

    public AudioVideoRequestResultHandler getAudioVideoRequestHandler() {
        if (null == mAudioVideoRequestHandler) {
            mAudioVideoRequestHandler = new PeergineAudioVideoHandler(getApplicationContext());
            mAudioVideoRequestHandler.setDispatchEventCallBack(ModuleApplication.this)
                    .setModuleManager(ModuleApplication.this);
            mAudioVideoRequestHandler.setHandlerKeepAliveClient(getHandlerKeepAliveClient())
                    .setDevicesConfig(getDevicesConfig());
            registerActivityLifecycleCallbacks(mAudioVideoRequestHandler);
        }
        return mAudioVideoRequestHandler;
    }

    public FlashlightHandler getFlashlightHandler() {
        if (null == mFlashlightHandler) {
            mFlashlightHandler = new FlashlightHandler(getApplicationContext());
        }
        return mFlashlightHandler;
    }

    public LocalKeyHandler getLocalKeyHandler() {
        if (null == mLocalKeyHandler) {
            mLocalKeyHandler = new LocalKeyHandler(getApplicationContext());
            mLocalKeyHandler.setAudioVideoRequestResult(getAudioVideoRequestHandler())
                    .setDispatchEventCallBack(ModuleApplication.this)
                    .setModuleManager(ModuleApplication.this);
        }
        return mLocalKeyHandler;
    }
}
