package kuyou.common.ku09.event.jt808;

import kuyou.common.ku09.event.jt808.base.EventRequest;

/**
 * action :
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