package com.kuyou.avc;

import com.kuyou.avc.handler.FlashlightHandler;
import com.kuyou.avc.handler.LocalKeyHandler;
import com.kuyou.avc.handler.LocalModuleCommonHandler;
import com.kuyou.avc.handler.PeergineAudioVideoHandler;
import com.kuyou.avc.handler.PhotoTakeHandler;
import com.kuyou.avc.handler.AudioVideoRequestResultHandler;

import kuyou.common.ipc.RemoteEventBus;
import kuyou.common.ku09.BasicModuleApplication;

/**
 * action :音视频服务模块
 * <p>
 * remarks:  <br/>
 * author: wuguoxian <br/>
 * date: 21-7-12 <br/>
 * </p>
 */
public class ModuleApplication extends BasicModuleApplication {
    private static final String TAG = "com.kuyou.avc > ModuleApplication";

    private LocalModuleCommonHandler mModuleEventHandler;
    private FlashlightHandler mFlashlightHandler;
    private LocalKeyHandler mLocalKeyHandler;
    private AudioVideoRequestResultHandler mAudioVideoRequestHandler;
    private PhotoTakeHandler mPhotoTakeHandler;

    @Override
    protected String getApplicationName() {
        return "AudioVideoCommunication_peergine";
    }

    @Override
    protected void initRegisterEventHandlers() {
        registerEventHandler(getModuleBasicEventHandler());
        registerEventHandler(getLocalKeyHandler());
        registerEventHandler(getAudioVideoRequestHandler());
        registerEventHandler(getPhotoTakeHandler());
        registerEventHandler(getFlashlightHandler());
    }

    @Override
    protected long getFeedTimeLong() {
        return 30 * 1000;
    }

    @Override
    protected RemoteEventBus.IFrameLiveListener getIpcFrameLiveListener() {
        return getAudioVideoRequestHandler();
    }

    protected LocalModuleCommonHandler getModuleBasicEventHandler() {
        if (null == mModuleEventHandler) {
            mModuleEventHandler = new LocalModuleCommonHandler();
        }
        return mModuleEventHandler;
    }

    protected PhotoTakeHandler getPhotoTakeHandler() {
        if (null == mPhotoTakeHandler) {
            mPhotoTakeHandler = new PhotoTakeHandler();
            mPhotoTakeHandler.setContext(getApplicationContext());
            mPhotoTakeHandler.setAudioVideoRequestCallback(getAudioVideoRequestHandler());
        }
        return mPhotoTakeHandler;
    }

    protected AudioVideoRequestResultHandler getAudioVideoRequestHandler() {
        if (null == mAudioVideoRequestHandler) {
            mAudioVideoRequestHandler = new PeergineAudioVideoHandler(getApplicationContext());
            mAudioVideoRequestHandler.setDevicesConfig(getDeviceConfig());
            registerActivityLifecycleCallbacks(mAudioVideoRequestHandler);
        }
        return mAudioVideoRequestHandler;
    }

    protected FlashlightHandler getFlashlightHandler() {
        if (null == mFlashlightHandler) {
            mFlashlightHandler = new FlashlightHandler(getApplicationContext());
        }
        return mFlashlightHandler;
    }

    protected LocalKeyHandler getLocalKeyHandler() {
        if (null == mLocalKeyHandler) {
            mLocalKeyHandler = new LocalKeyHandler(getApplicationContext());
            mLocalKeyHandler.setAudioVideoRequestResult(getAudioVideoRequestHandler());
        }
        return mLocalKeyHandler;
    }
}
