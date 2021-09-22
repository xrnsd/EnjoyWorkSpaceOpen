package com.kuyou.ft.item;

import android.content.Context;
import android.util.Log;
import android.widget.TextView;

import com.kuyou.ft.R;
import com.kuyou.ft.basic.TestItemBasic;

import kuyou.common.ipc.RemoteEvent;
import kuyou.common.ku09.event.vc.EventVoiceWakeupRequest;
import kuyou.common.ku09.event.vc.EventVoiceWakeupResult;
import kuyou.common.ku09.event.vc.basic.EventVoiceControl;

public class TestItemVoiceControl extends TestItemBasic {

    protected final static int OPEN_TIMEING_FLAG = 5;

    protected TextView mTvOption;

    @Override
    public int getTestId() {
        return R.id.test_voice_control;
    }

    @Override
    public String getTestTitle(Context context) {
        return context.getString(R.string.title_voice_control);
    }

    @Override
    public int getSubContentId() {
        return R.layout.test_item_voice_control;
    }

    @Override
    protected void initViews() {
        super.initViews();
        mTvTitle = findViewById(R.id.tv_test_title);
        mTvTitle.setText(getString(R.string.title_voice_control_normal, OPEN_TIMEING_FLAG));
        mTvTiming = findViewById(R.id.tv_time);
        mTvOption = findViewById(R.id.tv_test_option);
        start();
    }

    @Override
    public void onReceiveEventNotice(RemoteEvent event) {
        if (EventVoiceControl.Code.VOICE_WAKEUP_RESULT != event.getCode()) {
            return;
        }
        if (!EventVoiceWakeupResult.getWakeUpStatus(event)) {
            Log.e(TAG, "onReceiveEventNotice > process fail : 模块唤醒状态:非唤醒");
            return;
        }
        getStatusProcessBus().stop(PS_TIMING);
        mTvOption.setText("测试成功");
    }

    @Override
    protected int getTimingFlag() {
        return OPEN_TIMEING_FLAG;
    }

    protected void start() {
        dispatchEvent(new EventVoiceWakeupRequest()
                .setWakeUpRequestType(EventVoiceWakeupRequest.TypeCode.FactoryTest)
                .setRemote(true));
        getStatusProcessBus().start(PS_TIMING);
    }


}