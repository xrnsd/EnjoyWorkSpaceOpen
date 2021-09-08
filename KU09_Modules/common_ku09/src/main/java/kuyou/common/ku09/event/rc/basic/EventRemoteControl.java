package kuyou.common.ku09.event.rc.basic;

import kuyou.common.ipc.RemoteEvent;
import kuyou.common.ku09.event.common.EventCommon;

/**
 * action :事件[远程控制相关][抽象]
 * <p>
 * remarks:  <br/>
 * author: wuguoxian <br/>
 * date: 21-3-29 <br/>
 * </p>
 */
public class EventRemoteControl extends EventCommon implements IEventCodeGroupRemoteControl {
    // implements IRemoteControl<RemoteEvent>

    protected final String TAG = "kuyou.common.ku09.event.jt808 > " + this.getClass().getSimpleName();

    @Override
    public int getCode() {
        return -1;
    }

    public RemoteEvent setPlatformType(int val) {
        getData().putInt(KEY_PLATFORM_TYPE, val);
        return EventRemoteControl.this;
    }

    public static int getPlatformType(RemoteEvent event) {
        return event.getData().getInt(KEY_PLATFORM_TYPE);
    }

    public RemoteEvent setMediaType(int val) {
        getData().putInt(KEY_MEDIA_TYPE, val);
        return EventRemoteControl.this;
    }

    public static int getMediaType(RemoteEvent event) {
        return event.getData().getInt(KEY_MEDIA_TYPE);
    }
}
