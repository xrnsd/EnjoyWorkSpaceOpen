package kuyou.common.ku09.event.rc.base;

import android.util.Log;

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
    }

    @Override
    public int getCode() {
        if (-1 == getCode()) {
            Log.e(TAG, "getCode > process fail : eventCode is invalid");
        }
        return getCode();
    }
}