package com.kuyou.ft.item;

import android.content.Context;

import com.kuyou.ft.R;
import com.kuyou.ft.basic.TestItemBasic;

import kuyou.common.ipc.RemoteEvent;
import kuyou.common.ku09.event.common.basic.EventKey;
import kuyou.common.ku09.protocol.basic.IHardwareControlDetectionV1_1;
import kuyou.common.ku09.protocol.basic.IKeyConfig;

public class TestItemNearElectricity extends TestItemBasic {

    protected final static int OPEN_TIMEING_FLAG = 15;
    private boolean isDetection = false;

    @Override
    public int getTestPolicy() {
        int policy = 0;
        policy |= POLICY_TEST;
        //policy |= POLICY_TEST_AUTO;
        //policy |= POLICY_TEST_AGING;
        return policy;
    }

    @Override
    public int getTestId() {
        return R.id.test_near_electricity;
    }

    @Override
    public int getSubContentId() {
        return R.layout.test_item_near_electricity;
    }

    @Override
    public String getTestTitle(Context context) {
        return context.getString(R.string.test_near_electricity);
    }

    @Override
    protected void initViews() {
        super.initViews();

        mTvTitle = findViewById(R.id.tv_test_title);
        mTvTitle.setText(getString(R.string.title_near_electricity_normal, OPEN_TIMEING_FLAG));
        mTvTiming = findViewById(R.id.tv_time);

        getStatusProcessBus().start(PS_TIMING);
    }

    @Override
    public void onReceiveEventNotice(RemoteEvent event) {
        if (isDetection
                || EventKey.Code.KEY_CLICK != event.getCode()
                || IKeyConfig.ALARM_NEAR_POWER != EventKey.getKeyCode(event)) {
            return;
        }
        getStatusProcessBus().stop(PS_TIMING);
        mTvTitle.setText("模块已搭载");

    }
    
    protected void detection(){
        final String filePathDevGasDetection = IHardwareControlDetectionV1_1.DEV_PTAH_PRESSURE;
    }
}