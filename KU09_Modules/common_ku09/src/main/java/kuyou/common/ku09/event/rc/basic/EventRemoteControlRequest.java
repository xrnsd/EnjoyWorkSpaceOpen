package kuyou.common.ku09.event.rc.basic;

import kuyou.common.ipc.RemoteEvent;
import kuyou.common.ku09.event.common.EventCommonRequest;

/**
 * action :
 * <p>
 * remarks:  <br/>
 * author: wuguoxian <br/>
 * date: 21-9-8 <br/>
 * </p>
 */
public abstract class EventRemoteControlRequest extends EventCommonRequest implements IEventCodeGroupRemoteControl{

    public EventRemoteControlRequest setPlatformType(int val) {
        getData().putInt(KEY_PLATFORM_TYPE, val);
        return EventRemoteControlRequest.this;
    }

    public static int getPlatformType(RemoteEvent event) {
        return event.getData().getInt(KEY_PLATFORM_TYPE);
    }

    public EventRemoteControlRequest setMediaType(int val) {
        getData().putInt(KEY_MEDIA_TYPE, val);
        return EventRemoteControlRequest.this;
    }

    public static int getMediaType(RemoteEvent event) {
        return event.getData().getInt(KEY_MEDIA_TYPE);
    }
}
