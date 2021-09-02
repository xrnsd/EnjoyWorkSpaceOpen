package kuyou.common.ku09.event.rc.basic;

import android.util.Log;

import kuyou.common.ipc.RemoteEvent;

/**
 * action :事件[远程控制相关请求][抽象]
 * <p>
 * remarks:  <br/>
 * author: wuguoxian <br/>
 * date: 21-3-27 <br/>
 * </p>
 */
public class EventRequest extends EventRemoteControl {

    public static final String KEY_REQUEST_CODE = "request.code";

    public static interface RequestCode {
        public final static int OPEN = 0;
        public final static int CLOSE = 1;
        public final static int REOPEN = 2;
    }

    @Override
    public int getCode() {
        if (-1 == getCode()) {
            Log.e(TAG, "getCode > process fail : eventCode is invalid");
        }
        return getCode();
    }

    public EventRequest setRequestCode(int val) {
        getData().putInt(KEY_REQUEST_CODE, val);
        return EventRequest.this;
    }

    public static int getRequestCode(RemoteEvent event) {
        return event.getData().getInt(KEY_REQUEST_CODE);
    }


}