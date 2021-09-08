package kuyou.common.ku09.event.common;

/**
 * action :事件[网络断开]
 * <p>
 * remarks:  <br/>
 * author: wuguoxian <br/>
 * date: 21-3-23 <br/>
 * </p>
 */
public class EventNetworkDisconnect extends EventCommon {
    @Override
    public int getCode() {
        return NETWORK_DISCONNECT;
    }
}
