package kuyou.common.ku09.event.rc.hmd;

/**
 * action :事件[硬件模块检测结果][单模块]
 * <p>
 * remarks:  <br/>
 * author: wuguoxian <br/>
 * date: 21-3-27 <br/>
 * </p>
 */
public class EventHardwareModuleStatusDetectionRequest extends EventHardwareModuleBasic {

    @Override
    public int getCode() {
        return Code.HARDWARE_MODULE_STATUS_DETECTION_REQUEST;
    }

}