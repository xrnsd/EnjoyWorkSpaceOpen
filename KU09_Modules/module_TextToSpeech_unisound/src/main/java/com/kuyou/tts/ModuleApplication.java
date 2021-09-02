package com.kuyou.tts;

import android.app.IHelmetModuleTTSCallback;
import android.os.RemoteException;

import com.kuyou.tts.handler.LocalModuleCommonHandler;
import com.kuyou.tts.handler.TTSHandler;

import kuyou.common.ku09.BasicModuleApplication;

/**
 * action :语音合成模块
 * <p>
 * remarks:  <br/>
 * author: wuguoxian <br/>
 * date: 20-11-2 <br/>
 * <p>
 */
public class ModuleApplication extends BasicModuleApplication {
    private final String TAG = "com.kuyou.tts > ModuleApplication";

    private LocalModuleCommonHandler mLocalModuleCommonHandler;
    private TTSHandler mTTSHandler;

    @Override
    protected String getApplicationName() {
        return "TextToSpeech_unisound";
    }

    @Override
    protected void initRegisterEventHandlers() {
        registerEventHandler(getTTSHandler());
        registerEventHandler(getModuleBasicEventHandler());
    }

    @Override
    protected void initModuleSystemServiceCallBack() {
        super.initModuleSystemServiceCallBack();
        mHelmetModuleManageServiceManager.registerHelmetModuleTTSCallback(new IHelmetModuleTTSCallback.Stub() {
            @Override
            public void onRequestTtsPlay(String text) throws RemoteException {
                ModuleApplication.this.getTTSHandler().onRequestTtsPlay(text);
            }
        });
    }

    @Override
    protected String isReady() {
        String statusSuper = super.isReady();
        StringBuilder status = new StringBuilder();
        if (null != statusSuper) {
            status.append(statusSuper);
        }
        if (!getTTSHandler().isReady()) {
            status.append(",TTS初始化异常");
        }
        return status.toString();
    }

    @Override
    public void play(String text) {
        getTTSHandler().onRequestTtsPlay(text);
    }

    public LocalModuleCommonHandler getModuleBasicEventHandler() {
        if (null == mLocalModuleCommonHandler) {
            mLocalModuleCommonHandler = new LocalModuleCommonHandler();
            mLocalModuleCommonHandler.setPowerStatusListener(getTTSHandler());
        }
        return mLocalModuleCommonHandler;
    }

    public TTSHandler getTTSHandler() {
        if (null == mTTSHandler) {
            mTTSHandler = new TTSHandler(getApplicationContext());
        }
        return mTTSHandler;
    }
}
