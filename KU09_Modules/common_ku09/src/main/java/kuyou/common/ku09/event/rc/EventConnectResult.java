package kuyou.common.ku09.event.rc;

import kuyou.common.ku09.event.rc.basic.EventRemoteControlResult;

/**
 * action :事件[连接后台请求处理结果]
 * <p>
 * remarks:  <br/>
 * author: wuguoxian <br/>
 * date: 21-3-27 <br/>
 * </p>
 */
public class EventConnectResult extends EventRemoteControlResult {

    @Override
    public int getCode() {
        return CONNECT_RESULT;
    }

}