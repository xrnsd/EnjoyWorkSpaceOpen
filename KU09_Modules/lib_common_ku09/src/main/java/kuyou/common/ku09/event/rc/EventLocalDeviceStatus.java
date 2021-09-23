package kuyou.common.ku09.event.rc;

import android.os.Bundle;

import kuyou.common.ipc.RemoteEvent;
import kuyou.common.ku09.event.rc.basic.EventRequest;

/**
 * action :事件[连接后台请求]
 * <p>
 * remarks:  <br/>
 * author: wuguoxian <br/>
 * date: 21-3-27 <br/>
 * </p>
 */
public class EventLocalDeviceStatus extends EventRequest {

    protected static final String KEY_EVENT_DATA_DEVICE_STATUS = "keyEventData.localDeviceStatus";

    public static interface Status {
        public final static int ON_LINE = 0;
        public final static int OFF_LINE = 1;
        public final static int SHUTDOWN = 2;
    }

    @Override
    public int getCode() {
        return Code.LOCAL_DEVICE_STATUS;
    }

    public static int getDeviceStatus(RemoteEvent event) {
        return event.getData().getInt(KEY_EVENT_DATA_DEVICE_STATUS);
    }

    public EventLocalDeviceStatus setDeviceStatus(int val) {
        getData().putInt(KEY_EVENT_DATA_DEVICE_STATUS, val);
        return EventLocalDeviceStatus.this;
    }

}