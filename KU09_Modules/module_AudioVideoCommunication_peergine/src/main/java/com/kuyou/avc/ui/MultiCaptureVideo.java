package com.kuyou.avc.ui;

import com.kuyou.avc.R;
import com.kuyou.avc.handler.photo.ITakePhotoByScreenshotResultCallback;
import com.kuyou.avc.ui.basic.MultiCapture;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import kuyou.common.ipc.RemoteEvent;
import kuyou.common.ku09.event.avc.EventPhotoTakeRequest;
import kuyou.common.ku09.event.avc.basic.EventAudioVideoCommunication;
import kuyou.common.ku09.protocol.IJT808ExtensionProtocol;

/**
 * action :视频通话[基于Peergine]
 * <p>
 * remarks:  <br/>
 * author: wuguoxian <br/>
 * date: 21-7-23 <br/>
 * </p>
 */
public class MultiCaptureVideo extends MultiCapture {

    @Override
    protected int getContentViewResId() {
        return R.layout.main_capture;
    }

    @Override
    public int getTypeCode() {
        return IJT808ExtensionProtocol.MEDIA_TYPE_VIDEO;
    }

    //本地事件
    @Subscribe
    public void onModuleEvent(RemoteEvent event) {
        switch (event.getCode()) {
            case EventAudioVideoCommunication.Code.PHOTO_TAKE_REQUEST:
                break;
            default:
                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(MultiCaptureVideo.this);
    }

    @Override
    public int screenshot(ITakePhotoByScreenshotResultCallback callback) {
        super.screenshot(callback);
        String storageDirPath = EventPhotoTakeRequest.getImgStorageDir(callback.getEventData());
        String fileName = EventPhotoTakeRequest.getFileName(callback.getEventData());
        String imageSaveFilePath = new StringBuilder(storageDirPath).append("/").append(fileName).toString();
        return m_Live.VideoCamera(0, imageSaveFilePath);
    }
}
