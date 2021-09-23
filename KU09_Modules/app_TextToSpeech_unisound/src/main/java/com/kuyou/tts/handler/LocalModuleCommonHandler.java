package com.kuyou.tts.handler;

import android.util.Log;

import kuyou.common.ipc.RemoteEvent;
import kuyou.common.ku09.event.avc.EventAVCModuleLiveExit;
import kuyou.common.ku09.event.tts.EventTextToSpeech;
import kuyou.common.ku09.handler.ModuleCommonHandler;

/**
 * action :模块通用事件处理器
 * <p>
 * remarks:  <br/>
 * author: wuguoxian <br/>
 * date: 21-8-21 <br/>
 * </p>
 */
public class LocalModuleCommonHandler extends ModuleCommonHandler {

    public LocalModuleCommonHandler() {
    }

    @Override
    public boolean onReceiveEventNotice(RemoteEvent event) {
        if (super.onReceiveEventNotice(event)) {
            return true;
        }
        switch (event.getCode()) {
            case EventTextToSpeech.Code.MODULE_EXIT:
                if (EventAVCModuleLiveExit.isReboot(event)) {
                    Log.i(TAG, "onReceiveEventNotice > 模块即将重启");
                    rebootModule(500);
                } else {
                    Log.i(TAG, "onReceiveEventNotice > 模块即将关闭");
                    android.os.Process.killProcess(android.os.Process.myPid());
                }
                return true;
            default:
                return false;
        }
    }
}
