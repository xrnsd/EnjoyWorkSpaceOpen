package kuyou.common.ku09.event.rc;

import kuyou.common.ku09.event.rc.base.EventRequest;

/**
 * action :事件[连接后台请求]
 * <p>
 * remarks:  <br/>
 * author: wuguoxian <br/>
 * date: 21-3-27 <br/>
 * </p>
 */
public class EventConnectRequest extends EventRequest {

    @Override
    public int getCode() {
        return Code.CONNECT_REQUEST;
    }

}