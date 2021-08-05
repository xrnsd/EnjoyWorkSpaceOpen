package kuyou.common.ku09.event.openlive;

import androidx.annotation.NonNull;

import kuyou.common.ipc.RemoteEvent;
import kuyou.common.ku09.event.openlive.base.EventRequest;

/**
 * action :
 * <p>
 * remarks:  <br/>
 * author: wuguoxian <br/>
 * date: 21-3-27 <br/>
 * </p>
 */
public abstract class EventMediaRequest extends EventRequest {

    protected static final String KEY_TOKEN = "event.data.token";
    protected static final String KEY_CHANNEL_ID = "event.data.channel.id";
    protected static final String KEY_ACTION = "event.data.action";
    protected static final String KEY_EVENT_TYPE = "keyEventData.eventType";

    protected static final String VAL_NONE = "none";

    public static interface Action {
        public final static String OPEN = "event.action.open";
        public final static String CLOSE = "event.action.close";
    }

    private String mToken = null, mChannelId = null, mAction = null;

    public String getToken() {
        return mToken;
    }

    public EventMediaRequest setToken(String token) {
        mToken = token;
        getData().putString(KEY_TOKEN, token);
        return EventMediaRequest.this;
    }

    public EventMediaRequest setAction(String action) {
        getData().putString(KEY_ACTION, action);
        mAction = action;
        return EventMediaRequest.this;
    }

    public String getChannelId() {
        return mChannelId;
    }

    public EventMediaRequest setChannelId(String channelId) {
        mChannelId = channelId;
        getData().putString(KEY_CHANNEL_ID, channelId);
        return EventMediaRequest.this;
    }

    @NonNull
    @Override
    public String toString() {
        return new StringBuilder()
                .append("\n Token = ").append(mToken)
                .append("\n Action = ").append(mAction)
                .append("\n ChannelId = ").append(mChannelId)
                .toString();
    }

    public static String getToken(RemoteEvent event) {
        return event.getData().getString(KEY_TOKEN);
    }

    public static String getChannelId(RemoteEvent event) {
        return event.getData().getString(KEY_CHANNEL_ID);
    }

    public static boolean isCloseAction(RemoteEvent event) {
        return event.getData().getString(KEY_ACTION, VAL_NONE).equals(Action.CLOSE);
    }

    public static boolean isOpenAction(RemoteEvent event) {
        return event.getData().getString(KEY_ACTION, VAL_NONE).equals(Action.OPEN);
    }

    public EventMediaRequest setEventType(int val) {
        getData().putInt(KEY_EVENT_TYPE, val);
        return EventMediaRequest.this;
    }

    public static int getEventType(RemoteEvent event) {
        return event.getData().getInt(KEY_EVENT_TYPE);
    }
}