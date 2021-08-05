package kuyou.common.ku09.event.voicecontrol;

import kuyou.common.ku09.event.voicecontrol.base.EventRequest;

/**
 * action :
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