package kuyou.common.ku09.event.common;

import android.os.Bundle;

import kuyou.common.ipc.RemoteEvent;
import kuyou.common.ku09.event.common.basic.IEventCodeGroupCommon;
import kuyou.common.ku09.event.common.basic.IEventDataKey;

/**
 * action :事件[远程事件扩展，添加事件类型][抽象]
 * <p>
 * remarks:  <br/>
 * author: wuguoxian <br/>
 * date: 21-7-27 <br/>
 * </p>
 */
public abstract class EventCommon extends RemoteEvent implements IEventDataKey, IEventCodeGroupCommon {

    public static int getEventType(RemoteEvent val) {
        return DataReaderCommon.getEventType(val);
    }

    public static int getEventType(Bundle val) {
        return DataReaderCommon.getEventType(val);
    }

    public EventCommon setEventType(int val) {
        DataReaderCommon.getInstance().setEventType(EventCommon.this, val);
        return EventCommon.this;
    }
}
