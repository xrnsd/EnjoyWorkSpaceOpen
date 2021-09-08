package kuyou.common.ku09.event.rc;

import kuyou.common.ku09.event.rc.basic.EventRemoteControlRequest;

/**
 * action :事件[鉴权请求]
 * <p>
 * remarks:  <br/>
 * author: wuguoxian <br/>
 * date: 21-3-27 <br/>
 * </p>
 */
public class EventAuthenticationRequest extends EventRemoteControlRequest {

    @Override
    public int getCode() {
        return AUTHENTICATION_REQUEST;
    }

}