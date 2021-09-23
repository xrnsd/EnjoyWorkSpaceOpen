package kuyou.common.ku09.event.avc;

import kuyou.common.ipc.RemoteEvent;
import kuyou.common.ku09.event.avc.basic.EventResult;

/**
 * action :事件[音视频处理结果]
 * <p>
 * remarks:  <br/>
 * author: wuguoxian <br/>
 * date: 21-3-27 <br/>
 * </p>
 */
public class EventAudioVideoOperateResult extends EventResult {

    protected static final String KEY_EVENT_DATA_TOKEN = "keyEventData.token";
    protected static final String KEY_EVENT_DATA_FLOW_NUMBER = "keyEventData.flowNumber";

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

    public static long getFlowNumber(RemoteEvent event) {
        return event.getData().getLong(KEY_EVENT_DATA_FLOW_NUMBER);
    }

    public EventAudioVideoOperateResult setFlowNumber(long val) {
        getData().putLong(KEY_EVENT_DATA_FLOW_NUMBER, val);
        return EventAudioVideoOperateResult.this;
    }
}