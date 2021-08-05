package com.kuyou.openlive.activities.base;

import com.kuyou.openlive.R;
import com.kuyou.openlive.ui.VideoGridContainer;

import static kuyou.common.ku09.JT808ExtensionProtocol.IAudioVideo.EVENT_TYPE_LOCAL_INITIATED;
import static kuyou.common.ku09.JT808ExtensionProtocol.IAudioVideo.EVENT_TYPE_PLATFORM_INITIATED;
import static kuyou.common.ku09.JT808ExtensionProtocol.IAudioVideo.KEY_EVENT_TYPE;

/**
 * action :普通推流界面，默认使用后摄
 * <p>
 * author: wuguoxian <br/>
 * date: 20-10-26 <br/>
 * </p>
 */
public abstract class OrdinaryPushActivity extends AgoraActivity {

    protected abstract String getItemTypeName();

    @Override
    protected int getContentViewResId() {
        return R.layout.activity_live_room;
    }

    protected boolean isLocalInitiated() {
        return EVENT_TYPE_LOCAL_INITIATED == getIntent().getIntExtra(KEY_EVENT_TYPE, EVENT_TYPE_PLATFORM_INITIATED);
    }

    @Override
    protected VideoGridContainer getVideoGridContainer() {
        return findViewById(R.id.live_video_grid_layout);
    }

    @Override
    protected void initLiveConfig() {
        super.initLiveConfig();
        play("已为您打开" + getItemTypeName());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        play("已为您关闭" + getItemTypeName());
    }
}
