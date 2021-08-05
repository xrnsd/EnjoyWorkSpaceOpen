package kuyou.common.ku09.event.openlive;

/**
 * action :
 * <p>
 * remarks:  <br/>
 * author: wuguoxian <br/>
 * date: 21-3-27 <br/>
 * </p>
 */
public class EventAudioRequest extends EventMediaRequest {
    
    @Override
    public int getCode() {
        return Code.AUDIO_REQUEST;
    }
}