package kuyou.common.ku09.event.common.basic;

import kuyou.common.ipc.RemoteEvent;

/**
 * action :接口[事件分发器]
 * <p>
 * remarks:  <br/>
 * author: wuguoxian <br/>
 * date: 21-3-29 <br/>
 * </p>
 */
public interface IEventBusDispatchCallback {
    public void dispatchEvent(RemoteEvent event);
}
