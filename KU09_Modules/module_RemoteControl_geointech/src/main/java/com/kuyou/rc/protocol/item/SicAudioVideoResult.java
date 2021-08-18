package com.kuyou.rc.protocol.item;

import kuyou.common.ku09.event.rc.base.EventRemoteControl;

/**
 * action :
 * <p>
 * remarks:  <br/>
 * author: wuguoxian <br/>
 * date: 21-8-18 <br/>
 * </p>
 */
public class SicAudioVideoResult extends SicAudioVideo {
    protected final String TAG = "com.kuyou.rc.protocol.item > SicAudioVideoResult";

    @Override
    public int getMatchEventCode() {
        return EventRemoteControl.Code.AUDIO_VIDEO_PARAMETERS_APPLY_RESULT;
    }
}
