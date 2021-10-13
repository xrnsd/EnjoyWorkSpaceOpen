package kuyou.common.ku09.event.rc.alarm;

import kuyou.common.ipc.RemoteEvent;
import kuyou.common.ku09.event.common.basic.EventCommon;

/**
 * action :事件[报警相关][抽象]
 * <p>
 * remarks:  <br/>
 * author: wuguoxian <br/>
 * date: 21-3-23 <br/>
 * </p>
 */
public class EventAlarmReply extends EventAlarm {

    protected final static String KEY_ALARM_TYPE = "keyEventData.alarmReplyTypeCode";
    protected final static String KEY_EVENT_TYPE = "keyEventData.alarmReplyEventType";

    @Override
    public int getCode() {
        return EventCommon.Code.ALARM_REPLY;
    }

    public EventAlarmReply setAlarmType(int val) {
        getData().putInt(KEY_ALARM_TYPE, val);
        return EventAlarmReply.this;
    }

    public EventAlarmReply setEventType(int val) {
        getData().putInt(KEY_EVENT_TYPE, val);
        return EventAlarmReply.this;
    }

    public static int getAlarmType(RemoteEvent event) {
        return event.getData().getInt(KEY_ALARM_TYPE);
    }

    public static int getEventType(RemoteEvent event) {
        return event.getData().getInt(KEY_EVENT_TYPE);
    }
}
