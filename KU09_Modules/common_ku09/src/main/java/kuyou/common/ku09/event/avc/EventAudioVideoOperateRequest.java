package kuyou.common.ku09.event.avc;

import android.os.Bundle;

import androidx.annotation.NonNull;

import kuyou.common.ipc.RemoteEvent;
import kuyou.common.ku09.event.avc.base.EventAudioVideoCommunication;
import kuyou.common.ku09.event.avc.base.EventRequest;

/**
 * action :事件[音视频处理请求]
 * <p>
 * remarks:  <br/>
 * author: wuguoxian <br/>
 * date: 21-3-27 <br/>
 * </p>
 */
public class EventAudioVideoOperateRequest extends EventRequest {

    protected static final String KEY_MEDIA_TYPE = "keyEventData.mediaType";
    protected static final String KEY_EVENT_TYPE = "keyEventData.eventType";
    protected static final String KEY_EVENT_DATA_FLOW_ID = "keyEventData.flowID";
    protected static final String KEY_TOKEN = "keyEventData.token";
    protected static final String KEY_CHANNEL_ID = "keyEventData.channelId";

    public static interface Action {
        public final static String OPEN = "event.action.open";
        public final static String CLOSE = "event.action.close";
    }

    @Override
    public int getCode() {
        return Code.AUDIO_VIDEO_OPERATE_REQUEST;
    }

    public EventAudioVideoOperateRequest setMediaType(int val) {
        getData().putInt(KEY_MEDIA_TYPE, val);
        return EventAudioVideoOperateRequest.this;
    }

    public static int getMediaType(RemoteEvent event) {
        return event.getData().getInt(KEY_MEDIA_TYPE);
    }

    public EventAudioVideoOperateRequest setToken(String token) {
        getData().putString(KEY_TOKEN, token);
        return EventAudioVideoOperateRequest.this;
    }

    public EventAudioVideoOperateRequest setEventType(int type) {
        getData().putInt(KEY_EVENT_TYPE, type);
        return EventAudioVideoOperateRequest.this;
    }

    public static int getEventType(RemoteEvent event) {
        return event.getData().getInt(KEY_EVENT_TYPE);
    }

    public static int getFlowId(RemoteEvent event) {
        return event.getData().getInt(KEY_EVENT_DATA_FLOW_ID);
    }

    public static int getFlowId(Bundle data) {
        return data.getInt(KEY_EVENT_DATA_FLOW_ID);
    }

    public EventAudioVideoOperateRequest setFlowId(int val) {
        getData().putInt(KEY_EVENT_DATA_FLOW_ID, val);
        return EventAudioVideoOperateRequest.this;
    }

    public static String getToken(RemoteEvent event) {
        return getToken(event.getData());
    }

    public static String getToken(Bundle data) {
        if (null == data)
            return null;
        return data.getString(KEY_TOKEN);
    }

    public EventAudioVideoOperateRequest setChannelId(String channelId) {
        getData().putString(KEY_CHANNEL_ID, channelId);
        return EventAudioVideoOperateRequest.this;
    }

    public static String getChannelId(RemoteEvent event) {
        return getChannelId(event.getData());
    }

    public static String getChannelId(Bundle data) {
        if (null == data)
            return null;
        return data.getString(KEY_CHANNEL_ID);
    }

    @NonNull
    @Override
    public String toString() {
        return new StringBuilder()
                .append("\n getMediaType = ").append(getMediaType(EventAudioVideoOperateRequest.this))
                .append("\n getEventType = ").append(getEventType(EventAudioVideoOperateRequest.this))
                .append("\n getFlowId = ").append(getFlowId(EventAudioVideoOperateRequest.this))
                .append("\n getToken = ").append(getToken(EventAudioVideoOperateRequest.this))
                .append("\n getChannelId = ").append(getChannelId(EventAudioVideoOperateRequest.this))
                .toString();
    }
}