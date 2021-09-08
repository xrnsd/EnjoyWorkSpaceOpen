package kuyou.common.ku09.event.rc.basic;

import kuyou.common.ipc.RemoteEvent;
import kuyou.common.ku09.event.common.EventCommonResult;

/**
 * action :
 * <p>
 * remarks:  <br/>
 * author: wuguoxian <br/>
 * date: 21-9-8 <br/>
 * </p>
 */
public abstract class EventRemoteControlResult extends EventCommonResult implements IEventCodeGroupRemoteControl {


    public EventRemoteControlResult setMediaType(int val) {
        getData().putInt(KEY_MEDIA_TYPE, val);
        return EventRemoteControlResult.this;
    }

    public static int getMediaType(RemoteEvent event) {
        return event.getData().getInt(KEY_MEDIA_TYPE);
    }
}
