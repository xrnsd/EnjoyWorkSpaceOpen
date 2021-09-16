package com.kuyou.ft.item;

import android.content.Context;

import com.kuyou.ft.R;
import com.kuyou.ft.basic.TestItem;

public class TestItemVoiceControl extends TestItem {

    protected final static int OPEN_TIMEING_FLAG = 5;

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
        start();
    }

    @Override
    protected int getTimingFlag() {
        return OPEN_TIMEING_FLAG;
    }

    protected void start() {
        getStatusProcessBus().start(PS_TIMING);
    }
}