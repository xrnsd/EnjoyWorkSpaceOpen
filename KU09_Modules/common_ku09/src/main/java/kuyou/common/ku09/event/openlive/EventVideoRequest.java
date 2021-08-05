package kuyou.common.ku09.event.openlive;

/**
 * action :
 * <p>
 * remarks:  <br/>
 * author: wuguoxian <br/>
 * date: 21-3-27 <br/>
 * </p>
 */
public class EventVideoRequest extends EventMediaRequest {

    @Override
    public int getCode() {
        return Code.VIDEO_REQUEST;
    }
}