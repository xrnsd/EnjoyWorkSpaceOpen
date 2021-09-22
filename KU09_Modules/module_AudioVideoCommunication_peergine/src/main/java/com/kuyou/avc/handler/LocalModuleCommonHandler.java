package com.kuyou.avc.handler;

import android.util.Log;

import kuyou.common.ipc.RemoteEvent;
import kuyou.common.ku09.event.avc.EventAVCModuleLiveExit;
import kuyou.common.ku09.event.avc.basic.EventAudioVideoCommunication;
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

    protected final String TAG = "com.kuyou.avc.handler > LocalModuleCommonHandler";

    @Override
    public boolean onReceiveEventNotice(RemoteEvent event) {
        super.onReceiveEventNotice(event);
        switch (event.getCode()) {
            case EventAudioVideoCommunication.Code.MODULE_EXIT:
                if (EventAVCModuleLiveExit.isReboot(event)) {
                    Log.i(TAG, "onReceiveEventNotice > 模块即将重启");
                    rebootModule(200);
                } else {
                    Log.i(TAG, "onReceiveEventNotice > 模块即将关闭");
                    android.os.Process.killProcess(android.os.Process.myPid());
                }
                break;
            default:
                return false;
        }
        return true;
    }
}
