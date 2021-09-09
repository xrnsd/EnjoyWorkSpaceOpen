package kuyou.common.ku09.event.common.basic;

import android.os.Bundle;

import kuyou.common.ipc.RemoteEvent;

/**
 * action :事件[远程事件扩展，添加事件类型][抽象]
 * <p>
 * remarks:  <br/>
 * author: wuguoxian <br/>
 * date: 21-7-27 <br/>
 * </p>
 */
public abstract class RemoteEventCommon extends RemoteEvent {

    protected static final String KEY_INITIATE_TYPE = "keyEventData.initiateType";

    protected static final String VAL_NONE = "none";

    public static int getEventType(RemoteEvent event) {
        return event.getData().getInt(KEY_INITIATE_TYPE);
    }

    public static int getEventType(Bundle data) {
        return data.getInt(KEY_INITIATE_TYPE);
    }

    public RemoteEventCommon setEventType(int type) {
        getData().putInt(KEY_INITIATE_TYPE, type);
        return RemoteEventCommon.this;
    }
}
