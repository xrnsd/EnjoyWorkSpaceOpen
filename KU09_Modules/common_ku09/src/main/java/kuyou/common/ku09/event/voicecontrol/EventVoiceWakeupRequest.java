package kuyou.common.ku09.event.voicecontrol;

import android.util.Log;

import kuyou.common.ku09.event.voicecontrol.base.EventRequest;
import kuyou.common.ku09.event.voicecontrol.base.ModuleEventVoiceControl;

/**
 * action :
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