package com.kuyou.avc.handler;

import android.content.Context;
import android.util.Log;

import com.kuyou.avc.R;
import com.kuyou.avc.handler.base.IAudioVideoRequestCallback;
import com.kuyou.avc.util.CameraLightControl;

import kuyou.common.ipc.RemoteEvent;
import kuyou.common.ku09.BaseHandler;
import kuyou.common.ku09.event.avc.EventAudioVideoOperateRequest;
import kuyou.common.ku09.event.avc.EventFlashlightRequest;
import kuyou.common.ku09.event.avc.EventPhotoTakeRequest;
import kuyou.common.ku09.event.avc.base.EventAudioVideoCommunication;
import kuyou.common.ku09.event.avc.base.IAudioVideo;
import kuyou.common.ku09.key.IKeyEventListener;
import kuyou.common.ku09.key.KeyConfig;

/**
 * action :
 * <p>
 * remarks:  <br/>
 * author: wuguoxian <br/>
 * date: 21-7-23 <br/>
 * </p>
 */
public class KeyHandler extends BaseHandler implements IKeyEventListener, IAudioVideo {
    protected final String TAG = "com.kuyou.avc.handle > KeyHandler";

    private static KeyHandler sMain;

    private KeyHandler() {

    }

    public static KeyHandler getInstance(Context context) {
        if (null == sMain) {
            sMain = new KeyHandler();
            sMain.mContext = context.getApplicationContext();
        }
        return sMain;
    }

    private Context mContext;
    private IAudioVideoRequestCallback mAudioVideoHandler;

    private int mAudioVideoType = MEDIA_TYPE_DEFAULT;

    public KeyHandler setAudioVideoRequestResult(IAudioVideoRequestCallback mAudioVideoRequestResult) {
        this.mAudioVideoHandler = mAudioVideoRequestResult;
        return KeyHandler.this;
    }

    @Override
    public boolean onModuleEvent(RemoteEvent event) {
        switch (event.getCode()) {
            case EventAudioVideoCommunication.Code.AUDIO_VIDEO_OPERATE_REQUEST:
                if (EVENT_TYPE_CLOSE != EventAudioVideoOperateRequest.getEventType(event)) {
                    mAudioVideoType = EventAudioVideoOperateRequest.getMediaType(event);
                }
                break;
            default:
                break;
        }
        return false;
    }

    @Override
    public void onKeyClick(int keyCode) {
        Log.i(TAG, "onKeyClick > keyCode =" + keyCode);
        switch (keyCode) {
            case KeyConfig.CALL:
                mAudioVideoHandler.performOperate(mAudioVideoType);
                break;
            case KeyConfig.FLASHLIGHT:
                dispatchEvent(new EventFlashlightRequest()
                        .setSwitch(!CameraLightControl.getInstance(mContext).isFlashLightOn())
                        .setEventType(IAudioVideo.EVENT_TYPE_LOCAL_DEVICE_INITIATE));
                break;
            case KeyConfig.CAMERA:
                dispatchEvent(new EventPhotoTakeRequest()
                        .setEventType(IAudioVideo.EVENT_TYPE_LOCAL_DEVICE_INITIATE)
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
                if (mAudioVideoHandler.getHandlerStatus() != IAudioVideoRequestCallback.HS_NORMAL) {
                    String content = null;
                    mAudioVideoHandler.getTitleByMediaType(mAudioVideoType, R.string.key_switch_mode_cancel_title);
                    if (null != content) {
                        play(content);
                        return;
                    }
                }
                mAudioVideoType = mAudioVideoType >= MEDIA_TYPE_GROUP
                        ? mAudioVideoType = MEDIA_TYPE_AUDIO
                        : mAudioVideoType + 1;
                String content = null;
                content = mAudioVideoHandler.getTitleByMediaType(mAudioVideoType, R.string.key_switch_mode_success_title);
                play(null != content ? content : "模式切换失败，请重新尝试");
                break;
            default:
                break;
        }
    }

    @Override
    public void onKeyLongClick(int keyCode) {

    }
}
