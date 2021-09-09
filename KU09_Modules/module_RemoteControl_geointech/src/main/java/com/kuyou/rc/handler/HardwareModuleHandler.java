package com.kuyou.rc.handler;

import com.kuyou.rc.handler.hmd.HardwareModuleInfo;

import java.util.ArrayList;
import java.util.List;

import kuyou.common.bytes.ByteUtils;
import kuyou.common.ipc.RemoteEvent;
import kuyou.common.ku09.event.rc.basic.EventRemoteControl;
import kuyou.common.ku09.event.rc.hardware.EventHardwareModuleStatusDetectionFinish;
import kuyou.common.ku09.event.rc.hardware.EventHardwareModuleStatusDetectionRequest;
import kuyou.common.ku09.event.rc.hardware.EventHardwareModuleStatusDetectionResult;
import kuyou.common.ku09.handler.BasicAssistHandler;
import kuyou.common.ku09.protocol.IHardwareModule;

/**
 * action :
 * <p>
 * remarks:  <br/>
 * author: wuguoxian <br/>
 * date: 21-9-9 <br/>
 * </p>
 */
public class HardwareModuleHandler extends BasicAssistHandler implements IHardwareModule {

    protected static final String TAG = "com.kuyou.rc.handler > HardwareModuleHandler";

    private List<byte[]> mHardwareModuleInfoList;
    private List<Integer> mHardwareModuleTypeIdList;

    public HardwareModuleHandler() {
        mHardwareModuleInfoList = new ArrayList<>();
        mHardwareModuleTypeIdList = new ArrayList<>();

        mHardwareModuleTypeIdList.add(HM_TYPE_INPUT_CAMERA_INFRARED_THERMAL);
        mHardwareModuleTypeIdList.add(HM_TYPE_INPUT_LOCATION_UWB);
        mHardwareModuleTypeIdList.add(HM_TYPE_INPUT_CAMERA_NORMAL);
        mHardwareModuleTypeIdList.add(HM_TYPE_OUTPUT_SCREEN_UNIQUE_OPTICAL_WAVEGUIDE);
        mHardwareModuleTypeIdList.add(HM_TYPE_INPUT_VOICE_CONTROL);
        mHardwareModuleTypeIdList.add(HM_TYPE_INPUT_GAS_DETECTION_CARBON_MONOXIDE);
        mHardwareModuleTypeIdList.add(HM_TYPE_INPUT_GAS_DETECTION_METHANE);
        mHardwareModuleTypeIdList.add(HM_TYPE_INPUT_TEMPERATURE_HUMIDITY);
        mHardwareModuleTypeIdList.add(HM_TYPE_INPUT_GYROSCOPE);
        mHardwareModuleTypeIdList.add(HM_TYPE_INPUT_BEIDOU_TWO);
        mHardwareModuleTypeIdList.add(HM_TYPE_INPUT_BAROMETER);
        mHardwareModuleTypeIdList.add(HM_TYPE_OUTPUT_LASER_LIGHT);
        mHardwareModuleTypeIdList.add(HM_TYPE_OUTPUT_FLASHLIGHT);
        mHardwareModuleTypeIdList.add(HM_TYPE_INPUT_SIM);
        mHardwareModuleTypeIdList.add(HM_TYPE_INPUT_SD_CARD);
        mHardwareModuleTypeIdList.add(HM_TYPE_INPUT_STRONG_POWER_DETECTION);
    }

    @Override
    public boolean onReceiveEventNotice(RemoteEvent event) {
        switch (event.getCode()) {
            case EventRemoteControl.Code.HARDWARE_MODULE_STATUS_DETECTION_RESULT:
                addHardwareModuleInfo(
                        EventHardwareModuleStatusDetectionResult.getTypeId(event),
                        EventHardwareModuleStatusDetectionResult.getMsg(event));
                break;
            default:
                return false;
        }
        return true;
    }

    public void startDetection() {
        for (int typeId : mHardwareModuleTypeIdList) {
            dispatchEvent(new EventHardwareModuleStatusDetectionRequest()
                    .setTypeId(typeId)
                    .setRemote(false));
        }

    }

    protected void onDetectionFinish() {
        dispatchEvent(new EventHardwareModuleStatusDetectionFinish()
                .setMsg(getBody())
                .setRemote(false));
    }

    protected byte[] getBody() {
        List<byte[]> bodyBytes = new ArrayList<>();

        byte[] headBytes = new byte[2];
        headBytes[0] = (byte) HM_ADDITIONAL_ITEM_HEAD;
        headBytes[0] = ByteUtils.int2Byte(mHardwareModuleInfoList.size());

        bodyBytes.add(headBytes);
        bodyBytes.addAll(mHardwareModuleInfoList);

        return ByteUtils.byteMergerAll(bodyBytes);
    }

    protected void addHardwareModuleInfo(int typeId, byte[] info) {
        mHardwareModuleInfoList.add(info);
        mHardwareModuleTypeIdList.remove(Integer.valueOf(typeId));
        if (0 == mHardwareModuleTypeIdList.size()) {
            onDetectionFinish();
        }
    }

    public HardwareModuleHandler addHardwareModuleInfo(int typeId, int statusId) {
        mHardwareModuleInfoList.add(new HardwareModuleInfo()
                .setHMStatusId(statusId)
                .setHMTypeId(typeId)
                .getInfoBody());
        return HardwareModuleHandler.this;
    }

    public HardwareModuleHandler addHardwareModuleInfo(HardwareModuleInfo info) {
        mHardwareModuleInfoList.add(info.getInfoBody());
        return HardwareModuleHandler.this;
    }
}
