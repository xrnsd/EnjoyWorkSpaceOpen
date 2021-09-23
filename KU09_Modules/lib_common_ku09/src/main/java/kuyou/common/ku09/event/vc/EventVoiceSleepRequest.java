package kuyou.common.ku09.event.vc;

import kuyou.common.ku09.event.vc.basic.EventRequest;

/**
 * action :事件[远程控制相关][休眠]
 * <p>
 * remarks:  <br/>
 * author: wuguoxian <br/>
 * date: 21-3-27 <br/>
 * </p>
 */
public class EventVoiceSleepRequest extends EventRequest {

    @Override
    public int getCode() {
        return Code.VOICE_SLEEP;
    }
}