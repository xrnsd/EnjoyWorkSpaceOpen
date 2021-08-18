package com.kuyou.avc.ui;

import com.kuyou.avc.R;
import com.kuyou.avc.ui.base.MultiCapture;

import kuyou.common.ku09.event.avc.base.IAudioVideo;

/**
 * action :
 * <p>
 * remarks:  <br/>
 * author: wuguoxian <br/>
 * date: 21-7-23 <br/>
 * </p>
 */
public class MultiCaptureAudio extends MultiCapture {

    @Override
    protected int getContentViewResId() {
        return R.layout.main_capture;
    }

    @Override
    public int getTypeCode() {
        return IAudioVideo.MEDIA_TYPE_AUDIO;
    }
}
