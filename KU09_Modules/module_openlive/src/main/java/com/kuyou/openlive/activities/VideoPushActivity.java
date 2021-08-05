package com.kuyou.openlive.activities;

import com.kuyou.openlive.activities.base.OrdinaryPushActivity;

import kuyou.common.ku09.event.openlive.EventLaserLightRequest;
import kuyou.common.ku09.JT808ExtensionProtocol.IAudioVideo;

/**
 * action :普通推流界面，默认使用后摄
 * <p>
 * author: wuguoxian <br/>
 * date: 20-10-26 <br/>
 * </p>
 */
public class VideoPushActivity extends OrdinaryPushActivity {

    @Override
    public int getTypeCode() {
        return IAudioVideo.MEDIA_TYPE_VIDEO;
    }

    @Override
    protected String getItemTypeName() {
        return isLocalInitiated() ? "视频通话" : "普通视频";
    }

    @Override
    protected void onResume() {
        super.onResume();
        dispatchEvent(new EventLaserLightRequest()
                .setSwitch(true)
                .setRemote(false));
    }

    @Override
    protected void onDestroy() {
        dispatchEvent(new EventLaserLightRequest()
                .setSwitch(false)
                .setRemote(false));
        super.onDestroy();
    }
}
