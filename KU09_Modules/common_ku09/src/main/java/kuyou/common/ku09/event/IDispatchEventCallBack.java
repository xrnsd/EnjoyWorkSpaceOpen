package kuyou.common.ku09.event;

import kuyou.common.ipc.RemoteEvent;

/**
 * action :
 * <p>
 * remarks:  <br/>
 * author: wuguoxian <br/>
 * date: 21-3-29 <br/>
 * </p>
 */
public interface IDispatchEventCallBack {
    public void dispatchEvent(RemoteEvent event);
}
