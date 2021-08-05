package com.kuyou.avc.ui;

import com.kuyou.avc.R;
import com.kuyou.avc.ui.custom.MultiCapture;

import kuyou.common.ipc.RemoteEvent;
import kuyou.common.ku09.event.avc.EventPhotoTakeRequest;
import kuyou.common.ku09.event.avc.base.EventAudioVideoCommunication;
import kuyou.common.ku09.event.avc.base.IAudioVideo;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

/**
 * action :
 * <p>
 * remarks:  <br/>
 * author: wuguoxian <br/>
 * date: 21-7-23 <br/>
 * </p>
 */
public class MultiCaptureVideo extends MultiCapture {

    @Override
    protected void initViews() {
        EventBus.getDefault().register(MultiCaptureVideo.this);
        super.initViews();
    }

    @Override
    protected int getContentViewResId() {
        return R.layout.main_capture;
    }

    @Override
    public int getTypeCode() {
        return IAudioVideo.MEDIA_TYPE_VIDEO;
    }

    //本地事件
    @Subscribe
    public void onModuleEvent(RemoteEvent event) {
        switch (event.getCode()) {
            case EventAudioVideoCommunication.Code.PHOTO_TAKE_REQUEST:
                String storageDirPath = EventPhotoTakeRequest.getImgStorageDir(event.getData());
                String fileName = EventPhotoTakeRequest.getFileName(event.getData());
                String imageSaveFilePath= new StringBuilder(storageDirPath).append("/").append(fileName).toString();
                m_Live.VideoCamera(0,imageSaveFilePath);
                break;
            default:
                break;
        }
    }

    @Override
    protected void onPeerginEvent(String sAct, String sData, String sRenID) {
        super.onPeerginEvent(sAct, sData, sRenID);
//        if (sAct.equals("VideoCamera"){
//            send
//        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(MultiCaptureVideo.this);
    }
}
