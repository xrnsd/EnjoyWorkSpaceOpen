package kuyou.common.ku09.event.common;

import kuyou.common.ipc.RemoteEvent;

/**
 * action :事件[远程控制相关][请求处理结果][抽象]
 * <p>
 * remarks:  <br/>
 * author: wuguoxian <br/>
 * date: 21-3-27 <br/>
 * </p>
 */
public abstract class EventCommonResult extends EventCommon {

    public static interface ResultCode {
        public final static int FAIL = 0;
        public final static int SUCCESS = 1;
        public final static int DIS = 2;
    }

    public EventCommonResult setResultCode(int resultCode) {
        getData().putInt(KEY_RESULT_CODE, resultCode);
        return EventCommonResult.this;
    }

    public EventCommonResult setResult(boolean isSuccess) {
        return setResultCode(isSuccess ? ResultCode.SUCCESS : ResultCode.FAIL);
    }

    public static int getResultCode(RemoteEvent event) {
        return event.getData().getInt(KEY_RESULT_CODE, -1);
    }

    public static boolean isResultSuccess(RemoteEvent event) {
        return ResultCode.SUCCESS == getResultCode(event);
    }
}