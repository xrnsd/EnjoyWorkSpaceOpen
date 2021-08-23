package com.kuyou.avc.handler;

import android.content.Context;
import android.util.Log;

import com.kuyou.avc.handler.basic.IAudioVideoRequestCallback;
import com.kuyou.avc.handler.basic.CameraLightControl;

import kuyou.common.ku09.event.avc.EventFlashlightRequest;
import kuyou.common.ku09.event.avc.EventPhotoTakeRequest;
import kuyou.common.ku09.handler.KeyHandler;
import kuyou.common.ku09.key.KeyConfig;
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
        Log.d(TAG, "onKeyClick > keyCode = " + keyCode);
        switch (keyCode) {
            case KeyConfig.CALL:
                mAudioVideoHandler.performOperate();
                break;
            case KeyConfig.FLASHLIGHT:
                dispatchEvent(new EventFlashlightRequest()
                        .setSwitch(!CameraLightControl.getInstance(mContext).isFlashLightOn())
                        .setEventType(IJT808ExtensionProtocol.EVENT_TYPE_LOCAL_DEVICE_INITIATE));
                break;
            case KeyConfig.CAMERA:
                dispatchEvent(new EventPhotoTakeRequest()
                        .setEventType(IJT808ExtensionProtocol.EVENT_TYPE_LOCAL_DEVICE_INITIATE)
                        .setRemote(false));
                break;
            default:
                break;
        }
    }

    @Override
    public void onKeyDoubleClick(int keyCode) {
        switch (keyCode) {
            case KeyConfig.VOICE_CONTROL:
                mAudioVideoHandler.switchMediaType();
                break;
            default:
                break;
        }
    }

    @Override
    public void onKeyLongClick(int keyCode) {

    }
}