package kuyou.common.ku09.event.vc.base;

import android.util.Log;

/**
 * action :
 * <p>
 * remarks:  <br/>
 * author: wuguoxian <br/>
 * date: 21-3-27 <br/>
 * </p>
 */
public class EventRequest extends ModuleEventVoiceControl {

    public static final String KEY_REQUEST_CODE = "request.code";

    public EventRequest(){

    }

    public EventRequest(int requestCode) {
        getData().putInt(KEY_REQUEST_CODE, requestCode);
    }

    @Override
    public int getCode() {
        if (-1 == getCode()) {
            Log.e(TAG, "getCode > process fail : eventCode is invalid");
        }
        return getCode();
    }
}