package kuyou.common.ku09.event.common.base;

import android.os.Bundle;

import kuyou.common.ipc.RemoteEvent;
import kuyou.common.permission.CommonDialog;

/**
 * action :
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
