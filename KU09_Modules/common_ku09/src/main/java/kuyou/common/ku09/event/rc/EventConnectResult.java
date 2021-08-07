package kuyou.common.ku09.event.rc;

import kuyou.common.ku09.event.rc.base.EventResult;

/**
 * action :事件[连接后台请求处理结果]
 * <p>
 * remarks:  <br/>
 * author: wuguoxian <br/>
 * date: 21-3-27 <br/>
 * </p>
 */
public class EventConnectResult extends EventResult {

    @Override
    public int getCode() {
        return Code.CONNECT_RESULT;
    }

}