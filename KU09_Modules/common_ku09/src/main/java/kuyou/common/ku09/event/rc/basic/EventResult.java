package kuyou.common.ku09.event.rc.basic;

import android.util.Log;

import kuyou.common.ipc.RemoteEvent;

/**
 * action :事件[远程控制相关][请求处理结果][抽象]
 * <p>
 * remarks:  <br/>
 * author: wuguoxian <br/>
 * date: 21-3-27 <br/>
 * </p>
 */
public class EventResult extends EventRemoteControl {

    public static final String KEY_RESULT_CODE = "result.code";

    public static interface ResultCode {
        public final static int FAIL = 0;
        public final static int SUCCESS = 1;
        public final static int DIS = 2;
    }

    public EventResult setResultCode(int resultCode) {
        getData().putInt(KEY_RESULT_CODE, resultCode);
        return EventResult.this;
    }

    public EventResult setResult(boolean isSuccess) {
        return setResultCode(isSuccess ? ResultCode.SUCCESS : ResultCode.FAIL);
    }

    @Override
    public int getCode() {
        if (-1 == getCode()) {
            Log.e(TAG, "getCode > process fail : eventCode is invalid");
        }
        return getCode();
    }

    public static int getResultCode(RemoteEvent event) {
        return event.getData().getInt(KEY_RESULT_CODE, -1);
    }

    public static boolean isResultSuccess(RemoteEvent event) {
        return EventResult.ResultCode.SUCCESS == getResultCode(event);
    }
}