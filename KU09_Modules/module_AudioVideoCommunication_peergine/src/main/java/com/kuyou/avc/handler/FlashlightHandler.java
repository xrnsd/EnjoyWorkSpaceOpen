package com.kuyou.avc.handler;

import android.content.Context;
import android.util.Log;

import com.kuyou.avc.handler.basic.CameraLightControl;

import kuyou.common.ipc.RemoteEvent;
import kuyou.common.ku09.handler.BasicEventHandler;
import kuyou.common.ku09.event.avc.EventFlashlightRequest;
import kuyou.common.ku09.event.avc.EventFlashlightResult;
import kuyou.common.ku09.event.avc.EventLaserLightRequest;
import kuyou.common.ku09.event.avc.EventLaserLightResult;
import kuyou.common.ku09.event.avc.base.EventAudioVideoCommunication;

/**
 * action :协处理器[手电筒]
 * <p>
 * remarks:  <br/>
 * author: wuguoxian <br/>
 * date: 21-7-26 <br/>
 * </p>
 */
public class FlashlightHandler extends BasicEventHandler {
    protected final String TAG = "com.kuyou.avc.handle > FlashlightHandler";

    public FlashlightHandler(Context context) {
        setContext(context.getApplicationContext());
    }

    @Override
    public boolean onModuleEvent(RemoteEvent event) {

        boolean result = false;
        switch (event.getCode()) {
            case EventAudioVideoCommunication.Code.FLASHLIGHT_REQUEST:
                Log.i(TAG, "onModuleEvent > 处理手电筒申请");
                result = CameraLightControl.getInstance(getContext())
                        .switchFlashLight(EventFlashlightRequest.isSwitch(event));
                dispatchEvent(new EventFlashlightResult().setResult(result));
                if (!result) {
                    Log.e(TAG, "onModuleEvent > process fail : 无法打开手电筒");
                    play("无法打开手电筒");
                }
                return true;

            case EventAudioVideoCommunication.Code.LASER_LIGHT_REQUEST:
                Log.d(TAG, "onModuleEvent > 处理激光灯申请");
                result = CameraLightControl.getInstance(
                        getContext()).switchLaserLight(EventLaserLightRequest.isSwitch(event));
                dispatchEvent(new EventLaserLightResult().setResult(result));
                if (!result) {
                    Log.e(TAG, "onModuleEvent > process fail : 无法打开激光灯");
                    play("无法打开激光灯");
                }
                return true;
            default:
                return false;
        }
    }
}
