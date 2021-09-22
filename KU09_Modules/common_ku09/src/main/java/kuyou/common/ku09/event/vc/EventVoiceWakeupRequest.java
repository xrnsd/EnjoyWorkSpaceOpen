package kuyou.common.ku09.event.vc;

import kuyou.common.ipc.RemoteEvent;
import kuyou.common.ku09.event.vc.basic.EventRequest;

/**
 * action :事件[远程控制相关][唤醒]
 * <p>
 * remarks:  <br/>
 * author: wuguoxian <br/>
 * date: 21-3-27 <br/>
 * </p>
 */
public class EventVoiceWakeupRequest extends EventRequest {

    protected static final String KEY_WAKE_UP_REQUEST_TYPE = "keyEventData.voiceWakeupRequestType";

    public static interface TypeCode {
        public final static int Normal = 0;
        public final static int FactoryTest = 1;
    }

    @Override
    public int getCode() {
        return Code.VOICE_WAKEUP_REQUEST;
    }

    public EventVoiceWakeupRequest setWakeUpRequestType(int val) {
        getData().putInt(KEY_WAKE_UP_REQUEST_TYPE, val);
        return EventVoiceWakeupRequest.this;
    }

    public static int getWakeUpRequestType(RemoteEvent event) {
        return event.getData().getInt(KEY_WAKE_UP_REQUEST_TYPE, TypeCode.Normal);
    }
}