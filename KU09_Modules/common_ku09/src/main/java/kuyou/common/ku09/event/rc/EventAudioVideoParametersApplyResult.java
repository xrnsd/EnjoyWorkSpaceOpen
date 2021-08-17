package kuyou.common.ku09.event.rc;

import kuyou.common.ku09.event.rc.base.EventResult;

/**
 * action :事件[向平台发出音视频请求处理结果]
 * <p>
 * remarks:  <br/>
 * author: wuguoxian <br/>
 * date: 21-3-27 <br/>
 * </p>
 */
public class EventAudioVideoParametersApplyResult extends EventResult {

    @Override
    public int getCode() {
        return Code.AUDIO_AND_VIDEO_PARAMETERS_APPLY_RESULT;
    }

}