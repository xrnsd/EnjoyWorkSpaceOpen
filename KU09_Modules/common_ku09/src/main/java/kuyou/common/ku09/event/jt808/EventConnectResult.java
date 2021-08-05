package kuyou.common.ku09.event.jt808;

import kuyou.common.ku09.event.jt808.base.EventResult;

/**
 * action :
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