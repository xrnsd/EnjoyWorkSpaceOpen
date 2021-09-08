package kuyou.common.ku09.event.rc;

import kuyou.common.ku09.event.rc.basic.EventRemoteControlRequest;

/**
 * action :事件[向平台发出音视频请求]
 * <p>
 * remarks:  <br/>
 * author: wuguoxian <br/>
 * date: 21-3-27 <br/>
 * </p>
 */
public class EventAudioVideoParametersApplyRequest extends EventRemoteControlRequest {

    @Override
    public int getCode() {
        return AUDIO_VIDEO_PARAMETERS_APPLY_REQUEST;
    }
}