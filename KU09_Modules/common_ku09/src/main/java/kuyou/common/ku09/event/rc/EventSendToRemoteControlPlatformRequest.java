package kuyou.common.ku09.event.rc;

import kuyou.common.ipc.RemoteEvent;
import kuyou.common.ku09.event.rc.basic.EventRemoteControlResult;

/**
 * action :事件[发送指令到平台的请求]
 * <p>
 * remarks:  <br/>
 * author: wuguoxian <br/>
 * date: 21-3-27 <br/>
 * </p>
 */
public class EventSendToRemoteControlPlatformRequest extends EventRemoteControlResult {

    protected final static String KEY_MSG_BUFFER = "keyEventData.msgBuffer";

    public EventSendToRemoteControlPlatformRequest() {
        applyCode();
        setRemote(false);
    }

    public byte[] getMsg() {
        return getData().getByteArray(KEY_MSG_BUFFER);
    }

    public EventSendToRemoteControlPlatformRequest setMsg(byte[] val) {
        getData().putByteArray(KEY_MSG_BUFFER, val);
        return EventSendToRemoteControlPlatformRequest.this;
    }

    @Override
    public int getCode() {
        return SEND_TO_REMOTE_CONTROL_PLATFORM;
    }

    public static byte[] getMsg(RemoteEvent event) {
        return event.getData().getByteArray(KEY_MSG_BUFFER);
    }
}