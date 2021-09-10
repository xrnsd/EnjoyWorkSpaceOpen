package com.kuyou.avc.ui;

import com.kuyou.avc.R;
import com.kuyou.avc.ui.basic.MultiCapture;

import kuyou.common.ku09.protocol.basic.IJT808ExtensionProtocol;

/**
 * action :语音通话[基于Peergine，采集端]
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
        return IJT808ExtensionProtocol.MEDIA_TYPE_AUDIO;
    }
}
