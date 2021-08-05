package kuyou.common.ku09.event.voicecontrol.base;

import android.util.Log;

import kuyou.common.ku09.event.openlive.base.ModuleEventOpenLive;

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