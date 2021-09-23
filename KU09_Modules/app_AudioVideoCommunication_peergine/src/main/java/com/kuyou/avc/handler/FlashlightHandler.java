package com.kuyou.avc.handler;

import android.util.Log;

import kuyou.common.ku09.handler.CameraLightControl;

import kuyou.common.ipc.RemoteEvent;
import kuyou.common.ku09.event.avc.EventFlashlightRequest;
import kuyou.common.ku09.event.avc.EventFlashlightResult;
import kuyou.common.ku09.event.avc.EventLaserLightRequest;
import kuyou.common.ku09.event.avc.EventLaserLightResult;
import kuyou.common.ku09.event.avc.basic.EventAudioVideoCommunication;
import kuyou.common.ku09.handler.BasicAssistHandler;

/**
 * action :协处理器[手电筒]
 * <p>
 * remarks:  <br/>
 * author: wuguoxian <br/>
 * date: 21-7-26 <br/>
 * </p>
 */
public class FlashlightHandler extends BasicAssistHandler {
    protected final String TAG = "com.kuyou.avc.handler > FlashlightHandler";

    @Override
    protected void initReceiveEventNotices() {
        registerHandleEvent(EventAudioVideoCommunication.Code.FLASHLIGHT_REQUEST, true);
        registerHandleEvent(EventAudioVideoCommunication.Code.LASER_LIGHT_REQUEST, true);
    }

    @Override
    public boolean onReceiveEventNotice(RemoteEvent event) {

        boolean result = false;
        switch (event.getCode()) {
            case EventAudioVideoCommunication.Code.FLASHLIGHT_REQUEST:
                Log.i(TAG, "onReceiveEventNotice > 处理手电筒申请");
                result = CameraLightControl.getInstance(getContext())
                        .switchFlashLight(EventFlashlightRequest.isSwitch(event));
                dispatchEvent(new EventFlashlightResult().setResult(result));
                if (!result) {
                    Log.e(TAG, "onReceiveEventNotice > process fail : 无法打开手电筒");
                    play("无法打开手电筒");
                }
                break;

            case EventAudioVideoCommunication.Code.LASER_LIGHT_REQUEST:
                Log.i(TAG, "onReceiveEventNotice > 处理激光灯申请");
                result = CameraLightControl.getInstance(
                        getContext()).switchLaserLight(EventLaserLightRequest.isSwitch(event));
                dispatchEvent(new EventLaserLightResult().setResult(result));
                if (!result) {
                    Log.e(TAG, "onReceiveEventNotice > process fail : 无法打开激光灯");
                    play("无法打开激光灯");
                }
                break;
            default:
                return false;
        }
        return true;
    }
}
