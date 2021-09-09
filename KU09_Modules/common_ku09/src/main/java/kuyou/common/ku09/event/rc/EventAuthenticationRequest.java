package kuyou.common.ku09.event.rc;

import kuyou.common.ku09.event.rc.basic.EventRequest;

/**
 * action :事件[鉴权请求]
 * <p>
 * remarks:  <br/>
 * author: wuguoxian <br/>
 * date: 21-3-27 <br/>
 * </p>
 */
public class EventAuthenticationRequest extends EventRequest {

    @Override
    public int getCode() {
        return Code.AUTHENTICATION_REQUEST;
    }

}