package com.kuyou.avc.handler;

import android.content.Context;
import android.util.Log;

import com.kuyou.avc.handler.basic.CameraLightControl;
import com.kuyou.avc.handler.basic.IAudioVideoRequestCallback;

import kuyou.common.ku09.event.avc.EventFlashlightRequest;
import kuyou.common.ku09.event.avc.EventPhotoTakeRequest;
import kuyou.common.ku09.handler.KeyHandler;
import kuyou.common.ku09.config.IKeyConfig;
import kuyou.common.ku09.protocol.IJT808ExtensionProtocol;

/**
 * action :实体按键事件协处理器
 * <p>
 * remarks:  <br/>
 * author: wuguoxian <br/>
 * date: 21-7-23 <br/>
 * </p>
 */
public class LocalKeyHandler extends KeyHandler {
    protected final String TAG = "com.kuyou.avc.handle > KeyHandler";

    private Context mContext;
    private IAudioVideoRequestCallback mAudioVideoHandler;

    public LocalKeyHandler(Context context) {
        setContext(context.getApplicationContext());
    }

    public LocalKeyHandler setAudioVideoRequestResult(IAudioVideoRequestCallback mAudioVideoRequestResult) {
        this.mAudioVideoHandler = mAudioVideoRequestResult;
        return LocalKeyHandler.this;
    }

    @Override
    public void setContext(Context context) {
        mContext = context;
    }

    @Override
    public void onKeyClick(int keyCode) {
        switch (keyCode) {
            case IKeyConfig.CALL:
                mAudioVideoHandler.performOperate();
                break;
            case IKeyConfig.FLASHLIGHT:
                dispatchEvent(new EventFlashlightRequest()
                        .setSwitch(!CameraLightControl.getInstance(mContext).isFlashLightOn())
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
}
