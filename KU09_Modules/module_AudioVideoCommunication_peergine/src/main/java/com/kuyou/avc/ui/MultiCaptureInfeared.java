package com.kuyou.avc.ui;

import com.kuyou.avc.R;
import com.kuyou.avc.ui.base.MultiCapExter;

import kuyou.common.ku09.event.avc.base.IAudioVideo;

/**
 * action :
 * <p>
 * remarks:  <br/>
 * author: wuguoxian <br/>
 * date: 21-7-23 <br/>
 * </p>
 */
public class MultiCaptureInfeared extends MultiCapExter {

    @Override
    protected int getContentViewResId() {
        return R.layout.main_capexter;
    }

    @Override
    public int getTypeCode() {
        return IAudioVideo.MEDIA_TYPE_INFEARED;
    }

    @Override
    protected void onStart() {
        super.onStart();
        //liveStart();
    }

    @Override
    protected void onStop() {
        super.onStop();

//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                if (getTransfering()) {
//                    btnRefuse();
//                    setTransfering(false);
//                }
//
//                // For test ...
//                //RecordAudioBothStop("/sdcard/Download/capture.avi", 0);
//                LiveStop();
//            }
//        },1500);
    }
}
