package com.kuyou.avc;

import com.kuyou.avc.handler.FlashlightHandler;
import com.kuyou.avc.handler.LocalKeyHandler;
import com.kuyou.avc.handler.LocalModuleCommonHandler;
import com.kuyou.avc.handler.av.PeergineAudioVideoHandler;
import com.kuyou.avc.handler.PhotoTakeHandler;
import com.kuyou.avc.handler.av.AudioVideoRequestResultHandler;

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

    private LocalKeyHandler mLocalKeyHandler;
    private PhotoTakeHandler mPhotoTakeHandler;
    private FlashlightHandler mFlashlightHandler;
    private LocalModuleCommonHandler mModuleEventHandler;
    private AudioVideoRequestResultHandler mAudioVideoRequestHandler;

    @Override
    protected void initRegisterEventHandlers() {
        registerEventHandler(getModuleBasicEventHandler());
        registerEventHandler(getLocalKeyHandler());
        registerEventHandler(getAudioVideoRequestHandler());
        registerEventHandler(getPhotoTakeHandler());
        registerEventHandler(getFlashlightHandler());
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
            mPhotoTakeHandler.setAudioVideoRequestCallback(getAudioVideoRequestHandler());
        }
        return mPhotoTakeHandler;
    }

    protected AudioVideoRequestResultHandler getAudioVideoRequestHandler() {
        if (null == mAudioVideoRequestHandler) {
            mAudioVideoRequestHandler = new PeergineAudioVideoHandler();
            registerActivityLifecycleCallbacks(mAudioVideoRequestHandler);
        }
        return mAudioVideoRequestHandler;
    }

    protected FlashlightHandler getFlashlightHandler() {
        if (null == mFlashlightHandler) {
            mFlashlightHandler = new FlashlightHandler();
        }
        return mFlashlightHandler;
    }

    protected LocalKeyHandler getLocalKeyHandler() {
        if (null == mLocalKeyHandler) {
            mLocalKeyHandler = new LocalKeyHandler();
            mLocalKeyHandler.setAudioVideoRequestResult(getAudioVideoRequestHandler());
        }
        return mLocalKeyHandler;
    }
}
