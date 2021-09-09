package com.kuyou.rc.handler.hardware;

import android.content.Context;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.usb.UsbDevice;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.kuyou.rc.protocol.uwb.UwbManager;
import com.kuyou.rc.protocol.uwb.basic.IModuleInfoListener;

import kuyou.common.camera.CameraUtil;
import kuyou.common.file.FileUtils;
import kuyou.common.file.SdUtils;
import kuyou.common.ipc.RemoteEvent;
import kuyou.common.ku09.event.avc.EventLaserLightRequest;
import kuyou.common.ku09.event.rc.basic.EventRemoteControl;
import kuyou.common.ku09.event.rc.hardware.EventHardwareModuleStatusDetectionRequest;
import kuyou.common.ku09.event.rc.hardware.EventHardwareModuleStatusDetectionResult;
import kuyou.common.ku09.handler.BasicAssistHandler;
import kuyou.common.ku09.protocol.IHardwareModule;
import kuyou.common.ku09.status.StatusProcessBusCallbackImpl;
import kuyou.common.ku09.status.basic.IStatusProcessBusCallback;

/**
 * action :
 * <p>
 * remarks:  <br/>
 * author: wuguoxian <br/>
 * date: 21-9-9 <br/>
 * </p>
 */
public class HardwareModuleDetectionHandler extends BasicAssistHandler implements IHardwareModule {

    protected final static int PS_DETECTION_CAMERA_INFRARED_THERMAL_TIME_OUT = 1024;
    protected final static int PS_DETECTION_LOCATION_UWB_TIME_OUT = 1025;

    @Override
    public void initReceiveProcessStatusNotices() {
        super.initReceiveProcessStatusNotices();

        getStatusProcessBus().registerStatusNoticeCallback(HM_TYPE_INPUT_CAMERA_INFRARED_THERMAL,
                new StatusProcessBusCallbackImpl(false, 0)
                        .setNoticeHandleLooperPolicy(IStatusProcessBusCallback.LOOPER_POLICY_BACKGROUND));
        getStatusProcessBus().registerStatusNoticeCallback(HM_TYPE_INPUT_LOCATION_UWB,
                new StatusProcessBusCallbackImpl(false, 0)
                        .setNoticeHandleLooperPolicy(IStatusProcessBusCallback.LOOPER_POLICY_BACKGROUND));
        getStatusProcessBus().registerStatusNoticeCallback(HM_TYPE_INPUT_CAMERA_NORMAL,
                new StatusProcessBusCallbackImpl(false, 0)
                        .setNoticeHandleLooperPolicy(IStatusProcessBusCallback.LOOPER_POLICY_BACKGROUND));
        getStatusProcessBus().registerStatusNoticeCallback(HM_TYPE_OUTPUT_SCREEN_UNIQUE_OPTICAL_WAVEGUIDE,
                new StatusProcessBusCallbackImpl(false, 0)
                        .setNoticeHandleLooperPolicy(IStatusProcessBusCallback.LOOPER_POLICY_BACKGROUND));
        getStatusProcessBus().registerStatusNoticeCallback(HM_TYPE_INPUT_VOICE_CONTROL,
                new StatusProcessBusCallbackImpl(false, 0)
                        .setNoticeHandleLooperPolicy(IStatusProcessBusCallback.LOOPER_POLICY_BACKGROUND));
        getStatusProcessBus().registerStatusNoticeCallback(HM_TYPE_INPUT_GAS_DETECTION_CARBON_MONOXIDE,
                new StatusProcessBusCallbackImpl(false, 0)
                        .setNoticeHandleLooperPolicy(IStatusProcessBusCallback.LOOPER_POLICY_BACKGROUND));
        getStatusProcessBus().registerStatusNoticeCallback(HM_TYPE_INPUT_GAS_DETECTION_METHANE,
                new StatusProcessBusCallbackImpl(false, 0)
                        .setNoticeHandleLooperPolicy(IStatusProcessBusCallback.LOOPER_POLICY_BACKGROUND));
        getStatusProcessBus().registerStatusNoticeCallback(HM_TYPE_INPUT_TEMPERATURE_HUMIDITY,
                new StatusProcessBusCallbackImpl(false, 0)
                        .setNoticeHandleLooperPolicy(IStatusProcessBusCallback.LOOPER_POLICY_BACKGROUND));
        getStatusProcessBus().registerStatusNoticeCallback(HM_TYPE_INPUT_GYROSCOPE,
                new StatusProcessBusCallbackImpl(false, 0)
                        .setNoticeHandleLooperPolicy(IStatusProcessBusCallback.LOOPER_POLICY_BACKGROUND));
        getStatusProcessBus().registerStatusNoticeCallback(HM_TYPE_INPUT_BEIDOU_TWO,
                new StatusProcessBusCallbackImpl(false, 0)
                        .setNoticeHandleLooperPolicy(IStatusProcessBusCallback.LOOPER_POLICY_BACKGROUND));
        getStatusProcessBus().registerStatusNoticeCallback(HM_TYPE_INPUT_BAROMETER,
                new StatusProcessBusCallbackImpl(false, 0)
                        .setNoticeHandleLooperPolicy(IStatusProcessBusCallback.LOOPER_POLICY_BACKGROUND));
        getStatusProcessBus().registerStatusNoticeCallback(HM_TYPE_OUTPUT_LASER_LIGHT,
                new StatusProcessBusCallbackImpl(false, 0)
                        .setNoticeHandleLooperPolicy(IStatusProcessBusCallback.LOOPER_POLICY_BACKGROUND));
        getStatusProcessBus().registerStatusNoticeCallback(HM_TYPE_OUTPUT_FLASHLIGHT,
                new StatusProcessBusCallbackImpl(false, 0)
                        .setNoticeHandleLooperPolicy(IStatusProcessBusCallback.LOOPER_POLICY_BACKGROUND));
        getStatusProcessBus().registerStatusNoticeCallback(HM_TYPE_INPUT_SIM,
                new StatusProcessBusCallbackImpl(false, 0)
                        .setNoticeHandleLooperPolicy(IStatusProcessBusCallback.LOOPER_POLICY_BACKGROUND));
        getStatusProcessBus().registerStatusNoticeCallback(HM_TYPE_INPUT_SD_CARD,
                new StatusProcessBusCallbackImpl(false, 0)
                        .setNoticeHandleLooperPolicy(IStatusProcessBusCallback.LOOPER_POLICY_BACKGROUND));
        getStatusProcessBus().registerStatusNoticeCallback(HM_TYPE_INPUT_STRONG_POWER_DETECTION,
                new StatusProcessBusCallbackImpl(false, 0)
                        .setNoticeHandleLooperPolicy(IStatusProcessBusCallback.LOOPER_POLICY_BACKGROUND));


        getStatusProcessBus().registerStatusNoticeCallback(PS_DETECTION_CAMERA_INFRARED_THERMAL_TIME_OUT,
                new StatusProcessBusCallbackImpl(false, 3000));
        getStatusProcessBus().registerStatusNoticeCallback(PS_DETECTION_LOCATION_UWB_TIME_OUT,
                new StatusProcessBusCallbackImpl(false, 3000));
    }

    @Override
    public void setContext(Context context) {
        super.setContext(context);
        //HM_TYPE_INPUT_CAMERA_INFRARED_THERMAL
        UsbDeviceHandler.register(context).setUsbDeviceListener(new UsbDeviceHandler.IUsbDeviceListener() {
            @Override
            public void onUsbDevice(UsbDevice device, boolean attached) {
                if (device.getProductName().equals("PIR324ThermalCamera") && attached) {
                    HardwareModuleDetectionHandler.this.getStatusProcessBus().stop(PS_DETECTION_CAMERA_INFRARED_THERMAL_TIME_OUT);
                    HardwareModuleDetectionHandler.this.dispatchEvent(new EventHardwareModuleStatusDetectionResult()
                            .setStatusId(HM_STATUS_BE_EQUIPPED_NORMAL)
                            .setTypeId(HM_TYPE_INPUT_CAMERA_INFRARED_THERMAL)
                            .setRemote(false));
                    HardwareModuleDetectionHandler.this.dispatchEvent(new EventLaserLightRequest()
                            .setSwitch(false)
                            .setRemote(true));
                }
            }
        });
    }

    @Override
    public boolean onReceiveEventNotice(RemoteEvent event) {
        switch (event.getCode()) {
            case EventRemoteControl.Code.HARDWARE_MODULE_STATUS_DETECTION_REQUEST:
                getStatusProcessBus().start(EventHardwareModuleStatusDetectionRequest.getTypeId(event));
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
                dispatchEvent(new EventLaserLightRequest().setSwitch(false).setRemote(true));
                dispatchEvent(new EventHardwareModuleStatusDetectionResult()
                        .setStatusId(HM_STATUS_BE_EQUIPPED_NOT_DETECTED)
                        .setTypeId(HM_TYPE_INPUT_CAMERA_INFRARED_THERMAL));
                return;
            case PS_DETECTION_LOCATION_UWB_TIME_OUT:
                dispatchEvent(new EventLaserLightRequest().setSwitch(false).setRemote(true));
                dispatchEvent(new EventHardwareModuleStatusDetectionResult()
                        .setStatusId(HM_STATUS_BE_EQUIPPED_NOT_DETECTED)
                        .setTypeId(HM_TYPE_INPUT_LOCATION_UWB));
                return;
            default:
                break;
        }

        final int typeId = statusCode;
        switch (typeId) {
            case HM_TYPE_INPUT_CAMERA_INFRARED_THERMAL:
                dispatchEvent(new EventLaserLightRequest()
                        .setSwitch(true)
                        .setRemote(true));
                getStatusProcessBus().start(PS_DETECTION_CAMERA_INFRARED_THERMAL_TIME_OUT);
                break;

            case HM_TYPE_INPUT_LOCATION_UWB:
                UwbManager.getInstance(getContext(), new IModuleInfoListener() {
                    @Override
                    public void onGetModuleId(int devId) {
                        HardwareModuleDetectionHandler.this.getStatusProcessBus().stop(PS_DETECTION_LOCATION_UWB_TIME_OUT);
                        HardwareModuleDetectionHandler.this.dispatchEvent(new EventHardwareModuleStatusDetectionResult()
                                .setStatusId(HM_STATUS_BE_EQUIPPED_NORMAL)
                                .setTypeId(HM_TYPE_INPUT_LOCATION_UWB)
                                .setRemote(false));
                    }

                    @Override
                    public void onSetModuleIdFinish(int devId, boolean result) {

                    }
                });
                getStatusProcessBus().start(PS_DETECTION_LOCATION_UWB_TIME_OUT);
                break;

            case HM_TYPE_INPUT_CAMERA_NORMAL:
                dispatchEvent(new EventHardwareModuleStatusDetectionResult()
                        .setStatusId(CameraUtil.getInstance(getContext()).isCameraAvailable(CameraCharacteristics.LENS_FACING_BACK)
                                ? HM_STATUS_BE_EQUIPPED_NORMAL
                                : HM_STATUS_BE_EQUIPPED_NOT_DETECTED)
                        .setTypeId(typeId)
                        .setRemote(false));
                break;

            case HM_TYPE_OUTPUT_SCREEN_UNIQUE_OPTICAL_WAVEGUIDE:
                final String filePathDevUniqueOptical = "/sys/class/op02220ba/op02220ba/val";
                final String resultDevUniqueOptical = FileUtils.getInstance(getContext()).readData(filePathDevUniqueOptical);
                boolean isExistDevUniqueOptical = resultDevUniqueOptical.equals("1");
                if (!isExistDevUniqueOptical) {
                    Log.d(TAG, "detectionHardwareModule > 硬件检测 > 光波导[单目] : 不存在 ，resultDevUniqueOptical = " + resultDevUniqueOptical);
                }
                dispatchEvent(new EventHardwareModuleStatusDetectionResult()
                        .setStatusId(isExistDevUniqueOptical ? HM_STATUS_BE_EQUIPPED_NORMAL : HM_STATUS_NOT_EQUIPPED)
                        .setTypeId(typeId)
                        .setRemote(false));
                break;

            case HM_TYPE_INPUT_GAS_DETECTION_CARBON_MONOXIDE:
            case HM_TYPE_INPUT_GAS_DETECTION_METHANE:
                final String filePathDevGasDetection = "/sys/kernel/lactl/attr/gas";
                final String resultDevGasDetection = FileUtils.getInstance(getContext()).readData(filePathDevGasDetection);
                boolean isExistDevGasDetection = resultDevGasDetection.contains("gas_pwr_on");

                dispatchEvent(new EventHardwareModuleStatusDetectionResult()
                        .setStatusId(isExistDevGasDetection ? HM_STATUS_BE_EQUIPPED_NORMAL : HM_STATUS_NOT_EQUIPPED)
                        .setTypeId(typeId)
                        .setRemote(false));
                break;

            case HM_TYPE_INPUT_SIM:
                TelephonyManager tm = (TelephonyManager) getContext().getSystemService("phone");
                String simSerialNumber = tm.getSimSerialNumber();
                boolean isSimInsert = simSerialNumber != null && !simSerialNumber.equals("");

                dispatchEvent(new EventHardwareModuleStatusDetectionResult()
                        .setStatusId(isSimInsert ? HM_STATUS_BE_EQUIPPED_NORMAL : HM_STATUS_NOT_EQUIPPED)
                        .setTypeId(typeId)
                        .setRemote(false));

                break;

            case HM_TYPE_INPUT_SD_CARD:
                dispatchEvent(new EventHardwareModuleStatusDetectionResult()
                        .setStatusId(SdUtils.isStorageMounted(getContext()) ? HM_STATUS_BE_EQUIPPED_NORMAL : HM_STATUS_NOT_EQUIPPED)
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

            case HM_TYPE_INPUT_TEMPERATURE_HUMIDITY:
            case HM_TYPE_INPUT_GYROSCOPE:
            case HM_TYPE_INPUT_BEIDOU_TWO:
            case HM_TYPE_INPUT_BAROMETER:
                dispatchEvent(new EventHardwareModuleStatusDetectionResult()
                        .setStatusId(HM_STATUS_NOT_EQUIPPED)
                        .setTypeId(typeId)
                        .setRemote(false));
                break;

            default:
                Log.e(TAG, "detectionHardwareModule > process fail : invalid typeId = " + typeId);
                break;
        }
    }
}
