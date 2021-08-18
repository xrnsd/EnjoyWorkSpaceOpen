package com.kuyou.avc.ui;

import com.kuyou.avc.R;
import com.kuyou.avc.handler.PeergineAudioVideoHandler;
import com.kuyou.avc.ui.base.MultiRender;

import kuyou.common.ku09.event.avc.base.IAudioVideo;

/**
 * action :
 * <p>
 * remarks:  <br/>
 * author: wuguoxian <br/>
 * date: 21-7-23 <br/>
 * </p>
 */
public class MultiRenderGroup extends MultiRender {

    @Override
    protected int getContentViewResId() {
        return R.layout.main_render;
    }

    @Override
    public int getTypeCode() {
        return IAudioVideo.MEDIA_TYPE_GROUP;
    }

    @Override
    protected void onStart() {
        super.onStart();
        LiveServerSet();
        LiveConnect();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void finish() {
        exitLive();
        super.finish();
    }

    private void exitLive(){
        LiveDisconnect();
    }

    @Override
    protected void playExit(){
        if (IAudioVideo.RESULT_SUCCESS == getResult()) {
            play(getString(R.string.media_request_exit_group_success));
        }
    }
}
