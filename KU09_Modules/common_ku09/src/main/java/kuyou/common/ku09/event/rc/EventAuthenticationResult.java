package kuyou.common.ku09.event.rc;

import kuyou.common.ku09.event.rc.base.EventResult;

/**
 * action :鉴权
 * <p>
 * remarks:  <br/>
 * author: wuguoxian <br/>
 * date: 21-3-27 <br/>
 * </p>
 */
public class EventAuthenticationResult extends EventResult {

    @Override
    public int getCode() {
        return Code.AUTHENTICATION_RESULT;
    }
}