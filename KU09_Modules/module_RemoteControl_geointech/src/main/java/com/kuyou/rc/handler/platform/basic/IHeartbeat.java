package com.kuyou.rc.handler.platform.basic;

/**
 * action :
 * <p>
 * remarks:  <br/>
 * author: wuguoxian <br/>
 * date: 21-8-27 <br/>
 * </p>
 */
public interface IHeartbeat {
    public boolean isHeartbeatConnected();
    public void start();
    public void stop();
}
