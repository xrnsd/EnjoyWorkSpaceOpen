package kuyou.common.ku09.event.rc.hmd;

import kuyou.common.ipc.RemoteEvent;

/**
 * action :事件[硬件模块检测结果][单模块]
 * <p>
 * remarks:  <br/>
 * author: wuguoxian <br/>
 * date: 21-3-27 <br/>
 * </p>
 */
public class EventHardwareModuleStatusDetectionResult extends EventHardwareModuleBasic {

    protected static final String KEY_HARDWARE_MODULE_STATUS_ID = "keyEventData.hardwareModuleStatusId";
    protected static final String KEY_HARDWARE_MODULE_STATUS_CONNECT = "keyEventData.hardwareModuleStatusConnect";

    @Override
    public int getCode() {
        return Code.HARDWARE_MODULE_STATUS_DETECTION_RESULT;
    }

    @Override
    public EventHardwareModuleBasic setTypeId(int val) {
        return super.setTypeId(val);
    }

    public EventHardwareModuleStatusDetectionResult setStatusId(int val) {
        getData().putInt(KEY_HARDWARE_MODULE_STATUS_ID, val);
        getData().putString(KEY_HARDWARE_MODULE_STATUS_CONNECT, getHardwareModuleStatusConnectById(val));
        return EventHardwareModuleStatusDetectionResult.this;
    }

    public static int getStatusId(RemoteEvent event) {
        return event.getData().getInt(KEY_HARDWARE_MODULE_STATUS_ID);
    }

    public static String getStatusConnect(RemoteEvent event) {
        return event.getData().getString(KEY_HARDWARE_MODULE_STATUS_CONNECT);
    }

    public static String getHardwareModuleStatusConnectById(int statusId) {
        String result = "未知状态:" + statusId;
        switch (statusId) {
            case HM_STATUS_BE_EQUIPPED_NORMAL:
                result = "功能已开启,硬件工作正常";
                break;
            case HM_STATUS_BE_EQUIPPED_EXCEPTION:
                result = "功能已开启,但硬件工作异常";
                break;
            case HM_STATUS_BE_EQUIPPED_NOT_DETECTED:
                result = "功能已开启,但硬件未检测到";
                break;
            case HM_STATUS_BE_EQUIPPED_DISABLE:
                result = "功能已开启,但硬件已禁用";
                break;
            case HM_STATUS_NOT_EQUIPPED:
                result = "功能未开启";
                break;
            default:
                break;
        }
        return result;
    }

}