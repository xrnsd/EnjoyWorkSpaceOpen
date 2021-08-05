package kuyou.common.ku09.event.jt808;

import kuyou.common.ipc.RemoteEvent;
import kuyou.common.ku09.event.jt808.base.EventResult;

/**
 * action :
 * <p>
 * remarks:  <br/>
 * author: wuguoxian <br/>
 * date: 21-3-27 <br/>
 * </p>
 */
public class EventAudioVideoParametersApplyResult extends EventResult {
    protected static final String KEY_MEDIA_TYPE = "keyEventData.mediaType";

    @Override
    public int getCode() {
        return Code.AUDIO_AND_VIDEO_PARAMETERS_APPLY_RESULT;
    }

    public EventAudioVideoParametersApplyResult setMediaType(int val) {
        getData().putInt(KEY_MEDIA_TYPE, val);
        return EventAudioVideoParametersApplyResult.this;
    }

    public static int getMediaType(RemoteEvent event) {
        return event.getData().getInt(KEY_MEDIA_TYPE);
    }

}