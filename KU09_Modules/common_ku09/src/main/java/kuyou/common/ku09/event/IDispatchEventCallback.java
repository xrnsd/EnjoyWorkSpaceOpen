package kuyou.common.ku09.event;

import kuyou.common.ipc.RemoteEvent;

/**
 * action :接口[事件分发器]
 * <p>
 * remarks:  <br/>
 * author: wuguoxian <br/>
 * date: 21-3-29 <br/>
 * </p>
 */
public interface IDispatchEventCallback {
    public void dispatchEvent(RemoteEvent event);
}
