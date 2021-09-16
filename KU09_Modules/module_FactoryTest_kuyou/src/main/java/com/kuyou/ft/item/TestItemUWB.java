package com.kuyou.ft.item;

import android.content.Context;

import com.kuyou.ft.R;
import com.kuyou.ft.basic.TestItem;

import kuyou.common.ipc.RemoteEvent;
import kuyou.common.ku09.event.rc.basic.EventRemoteControl;
import kuyou.common.ku09.event.rc.hmd.EventHardwareModuleStatusDetectionRequest;
import kuyou.common.ku09.event.rc.hmd.EventHardwareModuleStatusDetectionResult;
import kuyou.common.ku09.protocol.basic.IHardwareModuleDetection;
import kuyou.common.status.StatusProcessBusCallbackImpl;
import kuyou.common.status.basic.IStatusProcessBusCallback;

public class TestItemUWB extends TestItem {

    protected final static int OPEN_TIMEING_FLAG = 8;

    protected final static int PS_MODULE_DETECTION_TIME_OUT = 0;

    @Override
    public int getTestId() {
        return R.id.test_uwb;
    }

    @Override
    protected int getHardwareModuleTypeId() {
        return IHardwareModuleDetection.HM_TYPE_INPUT_LOCATION_UWB;
    }

    @Override
    public String getTestTitle(Context context) {
        return context.getString(R.string.title_uwb);
    }

    @Override
    public int getSubContentId() {
        return R.layout.test_item_uwb;
    }

    @Override
    protected void initViews() {
        super.initViews();
        mTvTitle = findViewById(R.id.tv_test_title);
        mTvTitle.setText(getString(R.string.title_uwb_normal, OPEN_TIMEING_FLAG));
        mTvTiming = findViewById(R.id.tv_time);
        start();
    }

    @Override
    protected int getTimingFlag() {
        return OPEN_TIMEING_FLAG;
    }

    @Override
    protected void initReceiveProcessStatusNotices() {
        super.initReceiveProcessStatusNotices();
        getStatusProcessBus().registerStatusNoticeCallback(PS_MODULE_DETECTION_TIME_OUT,
                new StatusProcessBusCallbackImpl(false, OPEN_TIMEING_FLAG * 1000)
                        .setNoticeHandleLooperPolicy(IStatusProcessBusCallback.LOOPER_POLICY_MAIN)
                        .setEnableReceiveRemoveNotice(true));
    }

    @Override
    public void onReceiveEventNotice(RemoteEvent event) {
        super.onReceiveEventNotice(event);
        switch (event.getCode()) {
            case EventRemoteControl.Code.HARDWARE_MODULE_STATUS_DETECTION_RESULT:
                getStatusProcessBus().stop(PS_MODULE_DETECTION_TIME_OUT);
                mTvTitle.setText(EventHardwareModuleStatusDetectionResult.getStatusConnect(event));
                mBtnSuccess.setEnabled(IHardwareModuleDetection.HM_STATUS_BE_EQUIPPED_NORMAL
                        == EventHardwareModuleStatusDetectionResult.getStatusId(event));
                break;
            default:
                break;
        }
    }

    @Override
    protected void onReceiveProcessStatusNotice(int statusCode, boolean isRemove) {
        super.onReceiveProcessStatusNotice(statusCode, isRemove);
        switch (statusCode) {
            case PS_MODULE_DETECTION_TIME_OUT:
                getStatusProcessBus().stop(PS_TIMING);
                if (isRemove) {
                    return;
                }
                int statusId = getDeviceConfig().isHardwareModuleCarry(IHardwareModuleDetection.HM_TYPE_INPUT_LOCATION_UWB) ?
                        IHardwareModuleDetection.HM_STATUS_BE_EQUIPPED_NOT_DETECTED : IHardwareModuleDetection.HM_STATUS_NOT_EQUIPPED;
                mTvTitle.setText(EventHardwareModuleStatusDetectionResult.getHardwareModuleStatusConnectById(statusId));
                mBtnSuccess.setEnabled(false);
                break;
            default:
                break;
        }
    }

    protected void start() {
        getStatusProcessBus().start(PS_MODULE_DETECTION_TIME_OUT);
        dispatchEvent(new EventHardwareModuleStatusDetectionRequest()
                .setTypeId(IHardwareModuleDetection.HM_TYPE_INPUT_LOCATION_UWB)
                .setRemote(true));
        getStatusProcessBus().start(PS_TIMING);
    }
}