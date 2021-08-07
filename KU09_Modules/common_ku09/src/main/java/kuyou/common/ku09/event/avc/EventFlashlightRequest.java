package kuyou.common.ku09.event.avc;

import kuyou.common.ipc.RemoteEvent;
import kuyou.common.ku09.event.avc.base.EventRequest;

/**
 * action :事件[手电筒开关请求]
 * <p>
 * remarks:  <br/>
 * author: wuguoxian <br/>
 * date: 21-3-27 <br/>
 * </p>
 */
public class EventFlashlightRequest extends EventRequest {

    protected static final String KEY_IS_SWITCH = "keyEventData.isSwitch";

    public EventFlashlightRequest setSwitch(boolean val) {
        getData().putBoolean(KEY_IS_SWITCH, val);
        return EventFlashlightRequest.this;
    }

    @Override
    public int getCode() {
        return Code.FLASHLIGHT_REQUEST;
    }

    public static boolean isSwitch(RemoteEvent event) {
        return event.getData().getBoolean(KEY_IS_SWITCH);
    }
}