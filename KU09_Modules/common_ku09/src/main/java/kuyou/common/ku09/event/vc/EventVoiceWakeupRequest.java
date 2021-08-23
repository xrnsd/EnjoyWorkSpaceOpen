package kuyou.common.ku09.event.vc;

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

    @Override
    public int getCode() {
        return Code.VOICE_WAKEUP;
    }
}