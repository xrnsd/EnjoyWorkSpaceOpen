package com.kuyou.rc.protocol.jt808extend.item;

import kuyou.common.ku09.event.rc.basic.EventRemoteControl;

/**
 * action :协议编解码项[语音控制][硬件实现][打开视频通话]
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
