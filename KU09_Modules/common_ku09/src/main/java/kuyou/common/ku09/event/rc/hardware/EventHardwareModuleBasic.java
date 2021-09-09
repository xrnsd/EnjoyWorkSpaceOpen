package kuyou.common.ku09.event.rc.hardware;


import kuyou.common.ipc.RemoteEvent;
import kuyou.common.ku09.event.rc.EventSendToRemoteControlPlatformRequest;

/**
 * action :事件[硬件模块检测结果][全部模块检测完成]
 * <p>
 * remarks:  <br/>
 * author: wuguoxian <br/>
 * date: 21-3-27 <br/>
 * </p>
 */
public abstract class EventHardwareModuleBasic extends EventSendToRemoteControlPlatformRequest {

    protected static final String KEY_HARDWARE_MODULE_TYPE_ID = "keyEventData.hardwareModuleTypeId";

    public EventHardwareModuleBasic setTypeId(int val) {
        getData().putInt(KEY_HARDWARE_MODULE_TYPE_ID, val);
        return EventHardwareModuleBasic.this;
    }

    public static int getTypeId(RemoteEvent event) {
        return event.getData().getInt(KEY_HARDWARE_MODULE_TYPE_ID);
    }

}