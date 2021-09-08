package kuyou.common.ku09.event.rc;

import android.os.Bundle;

import kuyou.common.ipc.RemoteEvent;
import kuyou.common.ku09.event.rc.basic.EventRemoteControlResult;

/**
 * action :事件[连接后台请求处理结果]
 * <p>
 * remarks:  <br/>
 * author: wuguoxian <br/>
 * date: 21-3-27 <br/>
 * </p>
 */
public class EventHeartbeatReply extends EventRemoteControlResult {

    @Override
    public int getCode() {
        return HEARTBEAT_REPLY;
    }

    public static long getFlowNumber(RemoteEvent event) {
        return event.getData().getLong(KEY_EVENT_DATA_FLOW_NUMBER);
    }

    public static long getFlowNumber(Bundle data) {
        if (null == data) {
            return -1;
        }
        return data.getLong(KEY_EVENT_DATA_FLOW_NUMBER);
    }

    public EventHeartbeatReply setFlowNumber(long val) {
        getData().putLong(KEY_EVENT_DATA_FLOW_NUMBER, val);
        return EventHeartbeatReply.this;
    }

}