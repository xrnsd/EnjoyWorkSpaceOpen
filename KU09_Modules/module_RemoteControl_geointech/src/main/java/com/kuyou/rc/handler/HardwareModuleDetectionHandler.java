package com.kuyou.rc.handler;

import android.content.Context;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.usb.UsbDevice;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.kuyou.rc.R;
import com.kuyou.rc.handler.hmd.UsbDeviceHandler;
import com.kuyou.rc.protocol.uwb.UwbManager;
import com.kuyou.rc.protocol.uwb.basic.IModuleInfoListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kuyou.common.bytes.ByteUtils;
import kuyou.common.camera.CameraUtil;
import kuyou.common.file.FileUtils;
import kuyou.common.file.SdUtils;
import kuyou.common.ipc.RemoteEvent;
import kuyou.common.ku09.basic.ThermalCameraControl;
import kuyou.common.ku09.event.rc.basic.EventRemoteControl;
import kuyou.common.ku09.event.rc.hardware.EventHardwareModuleStatusDetectionFinish;
import kuyou.common.ku09.event.rc.hardware.EventHardwareModuleStatusDetectionRequest;
import kuyou.common.ku09.event.rc.hardware.EventHardwareModuleStatusDetectionResult;
import kuyou.common.ku09.handler.BasicAssistHandler;
import kuyou.common.ku09.protocol.IHardwareModuleDetection;
import kuyou.common.status.StatusProcessBusCallbackImpl;
import kuyou.common.status.basic.IStatusProcessBusCallback;

/**
 * action :协处理器[硬件模块状态检测]
 * <p>
 * remarks:  <br/>
 * author: wuguoxian <br/>
 * date: 21-9-9 <br/>
 * </p>
 */
public class HardwareModuleDetectionHandler extends BasicAssistHandler implements IHardwareModuleDetection {

    protected final static String TAG = "com.kuyou.rc.handler > HardwareModuleDetectionHandler";

    protected final static int PS_DETECTION_CAMERA_INFRARED_THERMAL_TIME_OUT = 1024;
    protected final static int PS_DETECTION_LOCATION_UWB_TIME_OUT = 1025;
    protected final static int PS_DETECTION_LOCATION_UWB_SET_ID_TIME_OUT = 1026;

    private List<byte[]> mHardwareModuleInfoList;
    private List<Integer> mHardwareModuleTypeIdList;
    private Map<Integer, String> mHardwareModuleTypeInfoMap;

    public HardwareModuleDetectionHandler() {
        mHardwareModuleInfoList = new ArrayList<>();
        mHardwareModuleTypeIdList = new ArrayList<>();
        mHardwareModuleTypeInfoMap = new HashMap<>();

        mHardwareModuleTypeInfoMap.put(HM_TYPE_INPUT_CAMERA_INFRARED_THERMAL, "红外热成像");
        mHardwareModuleTypeInfoMap.put(HM_TYPE_INPUT_LOCATION_UWB, "UWB");
        mHardwareModuleTypeInfoMap.put(HM_TYPE_INPUT_CAMERA_NORMAL, "普通后摄");
        mHardwareModuleTypeInfoMap.put(HM_TYPE_OUTPUT_SCREEN_UNIQUE_OPTICAL_WAVEGUIDE, "光波导");
        mHardwareModuleTypeInfoMap.put(HM_TYPE_INPUT_VOICE_CONTROL, "语音控制");
        mHardwareModuleTypeInfoMap.put(HM_TYPE_INPUT_GAS_DETECTION_CARBON_MONOXIDE, "气体检测[一氧化碳]");
        mHardwareModuleTypeInfoMap.put(HM_TYPE_INPUT_GAS_DETECTION_METHANE, "气体检测[甲烷]");
        //mHardwareModuleTypeInfoMap.put(HM_TYPE_INPUT_TEMPERATURE_HUMIDITY,"温湿度检测");
        //mHardwareModuleTypeInfoMap.put(HM_TYPE_INPUT_GYROSCOPE,"陀螺仪");
        //mHardwareModuleTypeInfoMap.put(HM_TYPE_INPUT_BEIDOU_TWO,"北斗2");
        //mHardwareModuleTypeInfoMap.put(HM_TYPE_INPUT_BAROMETER,"气压计");
        //mHardwareModuleTypeInfoMap.put(HM_TYPE_OUTPUT_LASER_LIGHT,"激光指向灯");
        //mHardwareModuleTypeInfoMap.put(HM_TYPE_OUTPUT_FLASHLIGHT,"手电筒");
        mHardwareModuleTypeInfoMap.put(HM_TYPE_INPUT_SIM, "SIM卡");
        mHardwareModuleTypeInfoMap.put(HM_TYPE_INPUT_SD_CARD, "SD卡");
        mHardwareModuleTypeInfoMap.put(HM_TYPE_INPUT_STRONG_POWER_DETECTION, "强电靠近检测");
    }

    @Override
    protected void initReceiveProcessStatusNotices() {
        super.initReceiveProcessStatusNotices();

        for (Map.Entry<Integer, String> entry : mHardwareModuleTypeInfoMap.entrySet()) {
            mHardwareModuleTypeIdList.add(entry.getKey());
            getStatusProcessBus().registerStatusNoticeCallback(entry.getKey(),
                    new StatusProcessBusCallbackImpl(false, 0)
                            .setNoticeHandleLooperPolicy(IStatusProcessBusCallback.LOOPER_POLICY_BACKGROUND));
        }

        getStatusProcessBus().registerStatusNoticeCallback(PS_DETECTION_CAMERA_INFRARED_THERMAL_TIME_OUT,
                new StatusProcessBusCallbackImpl(false, 3000)
                        .setNoticeHandleLooperPolicy(IStatusProcessBusCallback.LOOPER_POLICY_MAIN));
        getStatusProcessBus().registerStatusNoticeCallback(PS_DETECTION_LOCATION_UWB_TIME_OUT,
                new StatusProcessBusCallbackImpl(false, 3000)
                        .setNoticeHandleLooperPolicy(IStatusProcessBusCallback.LOOPER_POLICY_MAIN));
        getStatusProcessBus().registerStatusNoticeCallback(PS_DETECTION_LOCATION_UWB_SET_ID_TIME_OUT,
                new StatusProcessBusCallbackImpl(false, 3000)
                        .setNoticeHandleLooperPolicy(IStatusProcessBusCallback.LOOPER_POLICY_MAIN));
    }

    @Override
    public void setContext(Context context) {
        super.setContext(context);
        //HM_TYPE_INPUT_CAMERA_INFRARED_THERMAL
        UsbDeviceHandler.register(context).setUsbDeviceListener(new UsbDeviceHandler.IUsbDeviceListener() {
            @Override
            public void onUsbDevice(UsbDevice device, boolean attached) {
                if (device.getProductName().replaceAll(" ", "").equals("PIR324ThermalCamera") && attached) {
                    HardwareModuleDetectionHandler.this.getStatusProcessBus().stop(PS_DETECTION_CAMERA_INFRARED_THERMAL_TIME_OUT);
                    HardwareModuleDetectionHandler.this.dispatchEvent(new EventHardwareModuleStatusDetectionResult()
                            .setStatusId(HM_STATUS_BE_EQUIPPED_NORMAL)
                            .setTypeId(HM_TYPE_INPUT_CAMERA_INFRARED_THERMAL)
                            .setRemote(false));
                    ThermalCameraControl.close();
                }
            }
        });

        UwbManager.getInstance(getContext())
                .setModuleInfoListener(new IModuleInfoListener() {
                    @Override
                    public void onGetModuleId(int devId) {
                        HardwareModuleDetectionHandler.this.getStatusProcessBus().stop(PS_DETECTION_LOCATION_UWB_TIME_OUT);
                        HardwareModuleDetectionHandler.this.dispatchEvent(new EventHardwareModuleStatusDetectionResult()
                                .setStatusId(HM_STATUS_BE_EQUIPPED_NORMAL)
                                .setTypeId(HM_TYPE_INPUT_LOCATION_UWB)
                                .setRemote(false));

                        final int uwbIdSoftware = Integer.valueOf(HardwareModuleDetectionHandler.this.getDeviceConfig().getUwbId());
                        if (uwbIdSoftware != devId) {
                            HardwareModuleDetectionHandler.this.getStatusProcessBus().start(PS_DETECTION_LOCATION_UWB_SET_ID_TIME_OUT);
                            UwbManager.getInstance(HardwareModuleDetectionHandler.this.getContext()).setId(uwbIdSoftware);
                        }
                    }

                    @Override
                    public void onSetModuleIdFinish(int devId, boolean result) {
                        UwbManager.getInstance(getContext()).closeSerialPort();
                    }
                });
    }

    @Override
    public boolean onReceiveEventNotice(RemoteEvent event) {
        switch (event.getCode()) {
            case EventRemoteControl.Code.HARDWARE_MODULE_STATUS_DETECTION_REQUEST:
                getStatusProcessBus().start(EventHardwareModuleStatusDetectionRequest.getTypeId(event));
                break;
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

    @Override
    protected void onReceiveProcessStatusNotice(int statusCode, boolean isRemove) {
        switch (statusCode) {
            case PS_DETECTION_CAMERA_INFRARED_THERMAL_TIME_OUT:
                ThermalCameraControl.close();
                dispatchEvent(new EventHardwareModuleStatusDetectionResult()
                        .setStatusId(getDeviceConfig().isHardwareModuleCarry(HM_TYPE_INPUT_CAMERA_INFRARED_THERMAL)
                                ? HM_STATUS_BE_EQUIPPED_NOT_DETECTED
                                : HM_STATUS_NOT_EQUIPPED)
                        .setTypeId(HM_TYPE_INPUT_CAMERA_INFRARED_THERMAL));
                return;
            case PS_DETECTION_LOCATION_UWB_TIME_OUT:
                UwbManager.getInstance(getContext()).closeSerialPort();
                dispatchEvent(new EventHardwareModuleStatusDetectionResult()
                        .setStatusId(getDeviceConfig().isHardwareModuleCarry(HM_TYPE_INPUT_LOCATION_UWB)
                                ? HM_STATUS_BE_EQUIPPED_NOT_DETECTED
                                : HM_STATUS_NOT_EQUIPPED)
                        .setTypeId(HM_TYPE_INPUT_LOCATION_UWB));
                return;
            case PS_DETECTION_LOCATION_UWB_SET_ID_TIME_OUT:
                UwbManager.getInstance(getContext()).closeSerialPort();
                Log.d(TAG, "onReceiveProcessStatusNotice > UWB:设备ID设置失败");
                return;
            default:
                break;
        }

        final int typeId = statusCode;
        if (!getDeviceConfig().isHardwareModuleCarry(typeId)) {
            dispatchEvent(new EventHardwareModuleStatusDetectionResult()
                    .setStatusId(HM_STATUS_NOT_EQUIPPED)
                    .setTypeId(typeId)
                    .setRemote(false));
            return;
        }
        switch (typeId) {
            case HM_TYPE_INPUT_CAMERA_INFRARED_THERMAL:
                ThermalCameraControl.open();
                getStatusProcessBus().start(PS_DETECTION_CAMERA_INFRARED_THERMAL_TIME_OUT);
                break;

            case HM_TYPE_INPUT_LOCATION_UWB:
                UwbManager.getInstance(getContext())
                        .open()
                        .performGetId();
                getStatusProcessBus().start(PS_DETECTION_LOCATION_UWB_TIME_OUT);
                break;

            case HM_TYPE_INPUT_CAMERA_NORMAL:
                final boolean isCameraAvailable = CameraUtil.getInstance(getContext()).isCameraAvailable(CameraCharacteristics.LENS_FACING_BACK);
                dispatchEvent(new EventHardwareModuleStatusDetectionResult()
                        .setStatusId(isCameraAvailable ? HM_STATUS_BE_EQUIPPED_NORMAL : HM_STATUS_BE_EQUIPPED_NOT_DETECTED)
                        .setTypeId(typeId)
                        .setRemote(false));
                break;

            case HM_TYPE_OUTPUT_SCREEN_UNIQUE_OPTICAL_WAVEGUIDE:
                final String filePathDevUniqueOptical = "/sys/class/op02220ba/op02220ba/val";
                final String resultDevUniqueOptical = FileUtils.getInstance(getContext()).readData(filePathDevUniqueOptical);
                boolean isExistDevUniqueOptical = resultDevUniqueOptical.equals("1");
                dispatchEvent(new EventHardwareModuleStatusDetectionResult()
                        .setStatusId(isExistDevUniqueOptical ? HM_STATUS_BE_EQUIPPED_NORMAL : HM_STATUS_BE_EQUIPPED_NOT_DETECTED)
                        .setTypeId(typeId)
                        .setRemote(false));
                break;

            case HM_TYPE_INPUT_GAS_DETECTION_CARBON_MONOXIDE:
            case HM_TYPE_INPUT_GAS_DETECTION_METHANE:
                final String filePathDevGasDetection = "/sys/kernel/lactl/attr/gas";
                final String resultDevGasDetection = FileUtils.getInstance(getContext()).readData(filePathDevGasDetection);
                boolean isExistDevGasDetection = resultDevGasDetection.contains("gas_pwr_on");
                dispatchEvent(new EventHardwareModuleStatusDetectionResult()
                        .setStatusId(isExistDevGasDetection ? HM_STATUS_BE_EQUIPPED_NORMAL : HM_STATUS_BE_EQUIPPED_NOT_DETECTED)
                        .setTypeId(typeId)
                        .setRemote(false));
                break;

            case HM_TYPE_INPUT_SIM:
                TelephonyManager tm = (TelephonyManager) getContext().getSystemService("phone");
                String simSerialNumber = tm.getSimSerialNumber();
                final boolean isSimInsert = simSerialNumber != null && !simSerialNumber.equals("");
                dispatchEvent(new EventHardwareModuleStatusDetectionResult()
                        .setStatusId(isSimInsert ? HM_STATUS_BE_EQUIPPED_NORMAL : HM_STATUS_BE_EQUIPPED_NOT_DETECTED)
                        .setTypeId(typeId)
                        .setRemote(false));
                break;

            case HM_TYPE_INPUT_SD_CARD:
                final boolean isSdInsert = SdUtils.isStorageMounted(getContext());
                dispatchEvent(new EventHardwareModuleStatusDetectionResult()
                        .setStatusId(isSdInsert ? HM_STATUS_BE_EQUIPPED_NORMAL : HM_STATUS_BE_EQUIPPED_NOT_DETECTED)
                        .setTypeId(typeId)
                        .setRemote(false));
                break;

            case HM_TYPE_INPUT_VOICE_CONTROL:
            case HM_TYPE_OUTPUT_FLASHLIGHT:
            case HM_TYPE_INPUT_STRONG_POWER_DETECTION:
                dispatchEvent(new EventHardwareModuleStatusDetectionResult()
                        .setStatusId(HM_STATUS_BE_EQUIPPED_NORMAL)
                        .setTypeId(typeId)
                        .setRemote(false));
                break;

            default:
                Log.e(TAG, "detectionHardwareModule > process fail : invalid typeId = " + typeId);
                break;
        }
    }

    @Override
    protected void dispatchEvent(RemoteEvent event) {
        printfResult(event);
        super.dispatchEvent(event);
    }

    protected void printfResult(RemoteEvent event) {
        if (EventRemoteControl.Code.HARDWARE_MODULE_STATUS_DETECTION_RESULT != event.getCode()) {
            return;
        }
        final int typeId = EventHardwareModuleStatusDetectionResult.getTypeId(event);
        final String hardwareModuleName = mHardwareModuleTypeInfoMap.get(Integer.valueOf(typeId));
        String hardwareModuleInfo = getContext().getString(R.string.hm_status_none);

        switch (EventHardwareModuleStatusDetectionResult.getStatusId(event)) {
            case HM_STATUS_BE_EQUIPPED_NORMAL:
                hardwareModuleInfo = getContext().getString(R.string.hm_status_be_equipped_normal);
                break;
            case HM_STATUS_BE_EQUIPPED_EXCEPTION:
                hardwareModuleInfo = getContext().getString(R.string.hm_status_be_equipped_exception);
                break;
            case HM_STATUS_BE_EQUIPPED_NOT_DETECTED:
                hardwareModuleInfo = getContext().getString(R.string.hm_status_be_equipped_not_detected);
                break;
            case HM_STATUS_BE_EQUIPPED_DISABLE:
                hardwareModuleInfo = getContext().getString(R.string.hm_status_be_equipped_disable);
                break;
            case HM_STATUS_NOT_EQUIPPED:
                hardwareModuleInfo = getContext().getString(R.string.hm_status_not_equipped);
                break;
            default:
                break;
        }
        Log.d(TAG, String.format("模块: %-15s\n -->状态:%-10s\n", hardwareModuleName, hardwareModuleInfo));
    }

    protected void addHardwareModuleInfo(int typeId, byte[] info) {
        mHardwareModuleInfoList.add(info);
        mHardwareModuleTypeIdList.remove(Integer.valueOf(typeId));
        if (0 == mHardwareModuleTypeIdList.size()) {
            dispatchEvent(new EventHardwareModuleStatusDetectionFinish()
                    .setMsg(getBody())
                    .setRemote(false));
        }
    }

    public void start() {
        for (int typeId : mHardwareModuleTypeIdList) {
            onReceiveEventNotice(new EventHardwareModuleStatusDetectionRequest()
                    .setTypeId(typeId)
                    .setRemote(false));
        }
    }

    public boolean isFinish() {
        synchronized (mHardwareModuleTypeIdList) {
            return 0 == mHardwareModuleTypeIdList.size();
        }
    }

    public byte[] getBody() {
        List<byte[]> bodyBytes = new ArrayList<>();

        byte[] headBytes = new byte[2];
        headBytes[0] = (byte) HM_ADDITIONAL_ITEM_HEAD;
        headBytes[0] = ByteUtils.int2Byte(mHardwareModuleInfoList.size());

        bodyBytes.add(headBytes);
        bodyBytes.addAll(mHardwareModuleInfoList);

        return ByteUtils.byteMergerAll(bodyBytes);
    }
}
