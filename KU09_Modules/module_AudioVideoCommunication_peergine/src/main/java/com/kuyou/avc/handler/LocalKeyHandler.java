package com.kuyou.avc.handler;

import android.util.Log;

import kuyou.common.ku09.handler.CameraLightControl;

import com.kuyou.avc.basic.photo.IAudioVideoRequestCallback;

import kuyou.common.ku09.event.avc.EventFlashlightRequest;
import kuyou.common.ku09.event.avc.EventPhotoTakeRequest;
import kuyou.common.ku09.handler.KeyHandler;
import kuyou.common.ku09.protocol.basic.IKeyConfig;
import kuyou.common.ku09.protocol.basic.IJT808ExtensionProtocol;

/**
 * action :实体按键事件协处理器
 * <p>
 * remarks:  <br/>
 * author: wuguoxian <br/>
 * date: 21-7-23 <br/>
 * </p>
 */
public class LocalKeyHandler extends KeyHandler {

    protected final String TAG = "com.kuyou.avc.handler > KeyHandler";

    private IAudioVideoRequestCallback mAudioVideoHandler;

    @Override
    public void onKeyClick(int keyCode) {
        switch (keyCode) {
            case IKeyConfig.CALL:
                mAudioVideoHandler.performOperate();
                break;
            case IKeyConfig.FLASHLIGHT:
                dispatchEvent(new EventFlashlightRequest()
                        .setSwitch(!CameraLightControl.getInstance(getContext()).isFlashLightOn())
                        .setEventType(IJT808ExtensionProtocol.EVENT_TYPE_LOCAL_DEVICE_INITIATE));
                break;
            case IKeyConfig.CAMERA:
                dispatchEvent(new EventPhotoTakeRequest()
                        .setEventType(IJT808ExtensionProtocol.EVENT_TYPE_LOCAL_DEVICE_INITIATE)
                        .setRemote(false));
                break;
            default:
                return;
        }
        Log.i(TAG, "onKeyClick > keyCode = " + keyCode);
    }

    @Override
    public void onKeyDoubleClick(int keyCode) {
        switch (keyCode) {
            case IKeyConfig.VOICE_CONTROL:
                mAudioVideoHandler.switchMediaType();
                break;
            default:
                return;
        }
        Log.i(TAG, "onKeyDoubleClick > keyCode = " + keyCode);
    }

    @Override
    public void onKeyLongClick(int keyCode) {

    }

    public LocalKeyHandler setAudioVideoRequestResult(IAudioVideoRequestCallback mAudioVideoRequestResult) {
        this.mAudioVideoHandler = mAudioVideoRequestResult;
        return LocalKeyHandler.this;
    }
}
