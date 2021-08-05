package kuyou.common.ku09.event.avc;

import kuyou.common.ipc.RemoteEvent;
import kuyou.common.ku09.event.avc.base.EventResult;

/**
 * action :
 * <p>
 * remarks:  <br/>
 * author: wuguoxian <br/>
 * date: 21-3-27 <br/>
 * </p>
 */
public class EventAudioVideoOperateResult extends EventResult {

    protected static final String KEY_EVENT_DATA_TOKEN = "keyEventData.token";
    protected static final String KEY_EVENT_DATA_FLOW_ID = "keyEventData.flowID";

    @Override
    public int getCode() {
        return Code.AUDIO_VIDEO_OPERATE_RESULT;
    }

    public static String getToken(RemoteEvent event) {
        return event.getData().getString(KEY_EVENT_DATA_TOKEN);
    }

    public EventAudioVideoOperateResult setToken(String val) {
        getData().putString(KEY_EVENT_DATA_TOKEN, val);
        return EventAudioVideoOperateResult.this;
    }

    public static int getFlowId(RemoteEvent event) {
        return event.getData().getInt(KEY_EVENT_DATA_FLOW_ID);
    }

    public EventAudioVideoOperateResult setFlowId(int val) {
        getData().putInt(KEY_EVENT_DATA_FLOW_ID, val);
        return EventAudioVideoOperateResult.this;
    }
}