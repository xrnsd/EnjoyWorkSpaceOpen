package kuyou.common.ku09.event.avc;

import kuyou.common.ipc.RemoteEvent;
import kuyou.common.ku09.event.avc.base.EventRequest;

/**
 * action :
 * <p>
 * remarks:  <br/>
 * author: wuguoxian <br/>
 * date: 21-3-27 <br/>
 * </p>
 */
public class EventLaserLightRequest extends EventRequest {

    protected static final String KEY_IS_SWITCH = "keyEventData.isSwitch";

    public EventLaserLightRequest setSwitch(boolean val) {
        getData().putBoolean(KEY_IS_SWITCH, val);
        return EventLaserLightRequest.this;
    }

    @Override
    public int getCode() {
        return Code.LASER_LIGHT_REQUEST;
    }

    public static boolean isSwitch(RemoteEvent event) {
        return event.getData().getBoolean(KEY_IS_SWITCH);
    }
}