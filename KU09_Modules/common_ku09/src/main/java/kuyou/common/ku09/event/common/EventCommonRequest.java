package kuyou.common.ku09.event.common;

import kuyou.common.ipc.RemoteEvent;

/**
 * action :事件[远程控制相关请求][抽象]
 * <p>
 * remarks:  <br/>
 * author: wuguoxian <br/>
 * date: 21-3-27 <br/>
 * </p>
 */
public abstract class EventCommonRequest extends EventCommon {

    public static interface RequestCode {
        public final static int OPEN = 0;
        public final static int CLOSE = 1;
        public final static int REOPEN = 2;
    }

    public EventCommonRequest setRequestCode(int val) {
        getData().putInt(KEY_REQUEST_CODE, val);
        return EventCommonRequest.this;
    }

    public static int getRequestCode(RemoteEvent event) {
        return event.getData().getInt(KEY_REQUEST_CODE);
    }
}