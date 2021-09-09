package kuyou.common.ku09.event.rc.hardware;

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

    @Override
    public int getCode() {
        return Code.HARDWARE_MODULE_STATUS_DETECTION_RESULT;
    }

    public EventHardwareModuleStatusDetectionResult setStatusId(int val) {
        getData().putInt(KEY_HARDWARE_MODULE_STATUS_ID, val);
        return EventHardwareModuleStatusDetectionResult.this;
    }

    public static int getStatusId(RemoteEvent event) {
        return event.getData().getInt(KEY_HARDWARE_MODULE_STATUS_ID);
    }

}