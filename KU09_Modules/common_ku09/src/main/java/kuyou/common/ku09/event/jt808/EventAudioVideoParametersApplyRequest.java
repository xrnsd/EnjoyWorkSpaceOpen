package kuyou.common.ku09.event.jt808;

import kuyou.common.ipc.RemoteEvent;
import kuyou.common.ku09.event.jt808.base.EventRequest;

/**
 * action :
 * <p>
 * remarks:  <br/>
 * author: wuguoxian <br/>
 * date: 21-3-27 <br/>
 * </p>
 */
public class EventAudioVideoParametersApplyRequest extends EventRequest {

    protected static final String KEY_MEDIA_TYPE = "keyEventData.mediaType";
    protected static final String KEY_EVENT_TYPE = "keyEventData.eventType";
    protected static final String KEY_IS_SWITCH = "keyEventData.isSwitch";

    @Override
    public int getCode() {
        return Code.AUDIO_AND_VIDEO_PARAMETERS_APPLY_REQUEST;
    }

    public EventAudioVideoParametersApplyRequest setSwitch(boolean val) {
        getData().putBoolean(KEY_IS_SWITCH, val);
        return EventAudioVideoParametersApplyRequest.this;
    }

    public static boolean isSwitch(RemoteEvent event) {
        return event.getData().getBoolean(KEY_IS_SWITCH);
    }

    public EventAudioVideoParametersApplyRequest setMediaType(int val) {
        getData().putInt(KEY_MEDIA_TYPE, val);
        return EventAudioVideoParametersApplyRequest.this;
    }

    public static int getMediaType(RemoteEvent event) {
        return event.getData().getInt(KEY_MEDIA_TYPE);
    }

    public EventAudioVideoParametersApplyRequest setEventType(int val) {
        getData().putInt(KEY_EVENT_TYPE, val);
        return EventAudioVideoParametersApplyRequest.this;
    }

    public static int getEventType(RemoteEvent event) {
        return event.getData().getInt(KEY_EVENT_TYPE);
    }
}