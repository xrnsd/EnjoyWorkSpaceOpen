package com.kuyou.avc.ui;

import com.kuyou.avc.R;
import com.kuyou.avc.ui.basic.MultiRender;

import kuyou.common.ku09.protocol.IJT808ExtensionProtocol;

/**
 * action :音视频通信[群组通话][采集端]
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
        return IJT808ExtensionProtocol.MEDIA_TYPE_GROUP;
    }

    @Override
    protected void onStart() {
        super.onStart();
        LiveServerSet();
        LiveConnect();
    }

    @Override
    public void onDestroy() {
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

    @Override
    protected void playExit(){
        if (IJT808ExtensionProtocol.RESULT_SUCCESS == getResult()) {
            play(getString(R.string.media_request_exit_group_success));
        }
    }
}
