package kuyou.common.ku09.event.vc;

import kuyou.common.ipc.RemoteEvent;
import kuyou.common.ku09.event.vc.basic.EventResult;

/**
 * action :事件[远程控制相关][模块唤醒结果]
 * <p>
 * remarks:  <br/>
 * author: wuguoxian <br/>
 * date: 21-3-27 <br/>
 * </p>
 */
public class EventVoiceWakeupResult extends EventResult {

    protected static final String KEY_WAKE_UP_STATUS = "keyEventData.voiceWakeupStatus";

    @Override
    public int getCode() {
        return Code.VOICE_WAKEUP_RESULT;
    }

    public EventVoiceWakeupResult setWakeUpStatus(boolean val) {
        getData().putBoolean(KEY_WAKE_UP_STATUS, val);
        return EventVoiceWakeupResult.this;
    }

    public static boolean getWakeUpStatus(RemoteEvent event) {
        return event.getData().getBoolean(KEY_WAKE_UP_STATUS);
    }


}