package kuyou.common.ku09.event.rc;

import kuyou.common.ipc.RemoteEvent;
import kuyou.common.ku09.event.rc.base.EventRequest;

/**
 * action :事件[音视频参数申请请求]
 * <p>
 * remarks:  <br/>
 * author: wuguoxian <br/>
 * date: 21-3-27 <br/>
 * </p>
 */
public class EventAudioVideoParametersApplyRequest extends EventRequest {

    @Override
    public int getCode() {
        return Code.AUDIO_AND_VIDEO_PARAMETERS_APPLY_REQUEST;
    }
}