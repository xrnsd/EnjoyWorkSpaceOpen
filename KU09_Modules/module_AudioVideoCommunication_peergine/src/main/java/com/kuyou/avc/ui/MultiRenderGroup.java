package com.kuyou.avc.ui;

import android.os.Handler;

import com.kuyou.avc.R;
import com.kuyou.avc.ui.base.MultiRender;
import com.kuyou.avc.ui.custom.MultiCapture;

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
        exitLive();
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
}
