package kuyou.common.ku09.event.openlive;

import kuyou.common.ipc.RemoteEvent;

/**
 * action :
 * <p>
 * remarks:  <br/>
 * author: wuguoxian <br/>
 * date: 21-3-27 <br/>
 * </p>
 */
public class EventInfearedVideoRequest extends EventMediaRequest {

    @Override
    public int getCode() {
        return Code.INFEARED_VIDEO_REQUEST;
    }
}