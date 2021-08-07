package kuyou.common.ku09.event.common;

import kuyou.common.ku09.event.common.base.EventCommon;

/**
 * action :事件[网络连接]
 * <p>
 * remarks:  <br/>
 * author: wuguoxian <br/>
 * date: 21-3-23 <br/>
 * </p>
 */
public class EventNetworkConnect extends EventCommon {
    @Override
    public int getCode() {
        return Code.NETWORK_CONNECTED;
    }
}
