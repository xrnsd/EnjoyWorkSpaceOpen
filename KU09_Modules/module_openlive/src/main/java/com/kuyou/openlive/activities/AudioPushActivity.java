package com.kuyou.openlive.activities;

import com.kuyou.openlive.activities.base.OrdinaryPushActivity;

import kuyou.common.ku09.JT808ExtensionProtocol.IAudioVideo;

/**
 * action :普通推流界面，默认使用后摄
 * <p>
 * author: wuguoxian <br/>
 * date: 20-10-26 <br/>
 * </p>
 */
public class AudioPushActivity extends OrdinaryPushActivity {

    @Override
    public int getTypeCode() {
        return IAudioVideo.MEDIA_TYPE_AUDIO;
    }

    @Override
    protected String getItemTypeName() {
        return "语音通话";
    }
}
