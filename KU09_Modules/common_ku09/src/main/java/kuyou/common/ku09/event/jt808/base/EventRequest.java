package kuyou.common.ku09.event.jt808.base;

import android.util.Log;

/**
 * action :
 * <p>
 * remarks:  <br/>
 * author: wuguoxian <br/>
 * date: 21-3-27 <br/>
 * </p>
 */
public class EventRequest extends ModuleEventJt808 {

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