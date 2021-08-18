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
public class MultiCaptureGroup extends MultiCapture {

    @Override
    protected int getContentViewResId() {
        return R.layout.main_capture;
    }

    @Override
    public int getTypeCode() {
        return IAudioVideo.MEDIA_TYPE_GROUP;
    }

    @Override
    protected void onStart() {
        super.onStart();
        liveStart();
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
        if (getTransfering()) {
            btnRefuse();
            setTransfering(false);
        }

        // For test ...
        //RecordAudioBothStop("/sdcard/Download/capture.avi", 0);
        LiveStop();
    }
}
