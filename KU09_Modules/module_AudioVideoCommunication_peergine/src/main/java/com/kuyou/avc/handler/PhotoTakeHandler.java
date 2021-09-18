package com.kuyou.avc.handler;

import android.os.Bundle;
import android.util.Log;

import com.kuyou.avc.basic.photo.IAudioVideoRequestCallback;
import com.kuyou.avc.basic.photo.TakePhotoBackground;
import com.kuyou.avc.ui.basic.ITakePhotoByCameraResultListener;
import com.kuyou.avc.basic.photo.ITakePhotoByScreenshotResultCallback;

import kuyou.common.ipc.RemoteEvent;
import kuyou.common.ku09.event.avc.EventPhotoTakeRequest;
import kuyou.common.ku09.event.avc.EventPhotoTakeResult;
import kuyou.common.ku09.event.avc.basic.EventAudioVideoCommunication;
import kuyou.common.ku09.event.rc.EventPhotoUploadRequest;
import kuyou.common.ku09.event.rc.basic.EventRemoteControl;
import kuyou.common.ku09.handler.BasicAssistHandler;
import kuyou.common.ku09.protocol.basic.IJT808ExtensionProtocol;
import kuyou.common.status.StatusProcessBusCallbackImpl;
import kuyou.common.status.basic.IStatusProcessBusCallback;

public class PhotoTakeHandler extends BasicAssistHandler {

    protected final static String TAG = "com.kuyou.avc.handler > PhotoTakeHandler";

    protected final static int PS_TAKE_PHOTO_TIMEOUT = 0;

    private IAudioVideoRequestCallback mAudioVideoRequestCallback;
    private RemoteEvent mRemoteEventHandled;

    public IAudioVideoRequestCallback getAudioVideoRequestCallback() {
        return mAudioVideoRequestCallback;
    }

    public PhotoTakeHandler setAudioVideoRequestCallback(IAudioVideoRequestCallback callback) {
        this.mAudioVideoRequestCallback = callback;
        return PhotoTakeHandler.this;
    }

    protected RemoteEvent getRemoteEventHandled() {
        return mRemoteEventHandled;
    }

    protected void setRemoteEventHandled(RemoteEvent remoteEventHandled) {
        mRemoteEventHandled = remoteEventHandled;
    }

    public boolean isItInHandlerState(int handlerStatus) {
        if (null == getAudioVideoRequestCallback()) {
            Log.e(TAG, "isItInHandlerState > process fail : AudioVideoRequestCallback is null");
            return false;
        }
        return handlerStatus == getAudioVideoRequestCallback().getHandlerStatus();
    }

    public boolean isLiveOnlineByType(int typeCode) {
        if (null == getAudioVideoRequestCallback()) {
            Log.e(TAG, "isLiveOnlineByType > process fail : AudioVideoRequestCallback is null");
            return false;
        }
        if (-1 == typeCode) {
            return getAudioVideoRequestCallback().getOnlineList().size() > 0;
        }
        return getAudioVideoRequestCallback().getOnlineList().containsKey(typeCode);
    }

    @Override
    protected void initReceiveProcessStatusNotices() {
        super.initReceiveProcessStatusNotices();
        getStatusProcessBus().registerStatusNoticeCallback(PS_TAKE_PHOTO_TIMEOUT,
                new StatusProcessBusCallbackImpl(false, 3000)
                        .setNoticeHandleLooperPolicy(IStatusProcessBusCallback.LOOPER_POLICY_MAIN));
    }

    @Override
    protected void onReceiveProcessStatusNotice(int statusCode, boolean isRemove) {
        super.onReceiveProcessStatusNotice(statusCode, isRemove);
        switch (statusCode) {
            case PS_TAKE_PHOTO_TIMEOUT:
                Log.e(TAG, "onReceiveProcessStatusNotice > 拍照回调，无响应");
                handlerTakePhotoResult(false, "", getRemoteEventHandled().getData());
                break;
            default:
                break;
        }
    }

    @Override
    protected void initReceiveEventNotices() {
        registerHandleEvent(EventRemoteControl.Code.PHOTO_UPLOAD_RESULT, true);
        registerHandleEvent(EventAudioVideoCommunication.Code.PHOTO_TAKE_REQUEST, true);
    }

    @Override
    public boolean onReceiveEventNotice(RemoteEvent event) {
        switch (event.getCode()) {
            case EventAudioVideoCommunication.Code.PHOTO_TAKE_REQUEST:
                setRemoteEventHandled(event);
                Log.i(TAG, "onReceiveEventNotice > 处理拍照请求");

                if (IJT808ExtensionProtocol.EVENT_TYPE_LOCAL_DEVICE_INITIATE == EventPhotoTakeRequest.getEventType(event)) {
                    play("正在为您拍照");
                }

                getStatusProcessBus().start(PS_TAKE_PHOTO_TIMEOUT);

                //截图拍照
                if (isLiveOnlineByType(-1)) {
                    int onLineTypeCode = -1;
                    if (isLiveOnlineByType(IJT808ExtensionProtocol.MEDIA_TYPE_VIDEO)) {
                        onLineTypeCode = IJT808ExtensionProtocol.MEDIA_TYPE_VIDEO;
                    }
                    if (isLiveOnlineByType(IJT808ExtensionProtocol.MEDIA_TYPE_THERMAL)) {
                        onLineTypeCode = IJT808ExtensionProtocol.MEDIA_TYPE_THERMAL;
                    }
                    if (-1 != onLineTypeCode) {
                        int result = getAudioVideoRequestCallback().getOnlineList()
                                .get(onLineTypeCode)
                                .screenshot(new ITakePhotoByScreenshotResultCallback() {
                                    @Override
                                    public Bundle getEventData() {
                                        return event.getData();
                                    }

                                    @Override
                                    public void onTakePhotoResult(boolean result, String info, Bundle data) {
                                        PhotoTakeHandler.this.handlerTakePhotoResult(result, info, data);
                                    }
                                });
                        if (-1 != result) {//异常失败处理
                            PhotoTakeHandler.this.handlerTakePhotoResult(false, "", event.getData());
                        }
                        return true;
                    }
                }

                ////后台相机拍照
                TakePhotoBackground.perform(getContext(), event.getData(), new ITakePhotoByCameraResultListener() {
                                    @Override
                                    public void onTakePhotoResult(boolean result, String info, Bundle data) {
                                        PhotoTakeHandler.this.handlerTakePhotoResult(result,info,data);
                                    }
                                });

                //前台相机拍照
//                TakePhotoForeground.perform(getContext(), event.getData(), new ITakePhotoByCameraResultListener() {
//                    @Override
//                    public void onTakePhotoResult(boolean result, String info, Bundle data) {
//                        PhotoTakeHandler.this.handlerTakePhotoResult(result, info, data);
//                    }
//                });
                return true;
            default:
                return false;
        }
    }

    protected void handlerTakePhotoResult(boolean result, String info, Bundle data) {
        getStatusProcessBus().stop(PS_TAKE_PHOTO_TIMEOUT);

        if (result) {
            Log.d(TAG, "onResult > 拍照成功");
            if (null == data) {
                Log.e(TAG, "handlerTakePhotoResult > dispatchEvent process fail : data is null");
                return;
            }
            dispatchEvent(new EventPhotoTakeResult()
                    .setImgFilePath(info)
                    .setResult(true)
                    .setEventType(EventPhotoUploadRequest.getEventType(data))
                    .setRemote(true));
        } else {
            Log.d(TAG, "onResult > 拍照失败");
            if (null == data) {
                Log.e(TAG, "handlerTakePhotoResult > dispatchEvent process fail : data is null");
                return;
            }
            dispatchEvent(new EventPhotoTakeResult()
                    .setData(data)
                    .setRemote(true)
                    .setResult(false));
        }
    }
}