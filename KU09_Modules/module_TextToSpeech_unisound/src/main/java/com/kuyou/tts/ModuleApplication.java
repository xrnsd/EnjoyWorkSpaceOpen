package com.kuyou.tts;

import android.app.IHelmetModuleTTSCallback;
import android.os.RemoteException;

import com.kuyou.tts.handler.LocalModuleCommonHandler;
import com.kuyou.tts.handler.TtsHandler;

import kuyou.common.ku09.BasicModuleApplication;

/**
 * action :语音合成模块
 * <p>
 * author: wuguoxian <br/>
 * date: 20-11-2 <br/>
 * <p>
 */
public class ModuleApplication extends BasicModuleApplication {
    private final String TAG = "com.kuyou.tts > ModuleApplication";

    private LocalModuleCommonHandler mLocalModuleCommonHandler;
    private TtsHandler mTtsHandler;

    @Override
    protected String getApplicationName() {
        return "TextToSpeech_unisound";
    }

    @Override
    protected void initRegisterEventHandlers() {
        registerEventHandler(getTtsHandler());
        registerEventHandler(getModuleBasicEventHandler());
    }

    @Override
    protected void initCallBack() {
        super.initCallBack();
        mHelmetModuleManageServiceManager.registerHelmetModuleTTSCallback(new IHelmetModuleTTSCallback.Stub() {
            @Override
            public void onRequestTtsPlay(String text) throws RemoteException {
                ModuleApplication.this.getTtsHandler().onRequestTtsPlay(text);
            }
        });
    }

    @Override
    protected long getFeedTimeLong() {
        return 60 * 1000;
    }

    @Override
    protected String isReady() {
        String statusSuper = super.isReady();
        StringBuilder status = new StringBuilder();
        if (null != statusSuper) {
            status.append(statusSuper);
        }
        if (!getTtsHandler().isReady()) {
            status.append(",TTS初始化异常");
        }
        return status.toString();
    }

    @Override
    public void play(String text) {
        getTtsHandler().onRequestTtsPlay(text);
    }

    public LocalModuleCommonHandler getModuleBasicEventHandler() {
        if (null == mLocalModuleCommonHandler) {
            mLocalModuleCommonHandler = new LocalModuleCommonHandler();
            mLocalModuleCommonHandler.setPowerStatusListener(getTtsHandler());
        }
        return mLocalModuleCommonHandler;
    }

    public TtsHandler getTtsHandler() {
        if (null == mTtsHandler) {
            mTtsHandler = new TtsHandler(getApplicationContext());
        }
        return mTtsHandler;
    }
}
