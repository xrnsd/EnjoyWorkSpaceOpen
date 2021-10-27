package com.kuyou.rc.basic.wifi;

/**
 * action :
 * <p>
 * remarks:  <br/>
 * author: wuguoxian <br/>
 * date: 21-10-23 <br/>
 * </p>
 */
public interface IWifiConnectInfoListener {
    public void onReadWifiConnectInfo(WifiConnectInfo info);
    public void onReadFinish();
}
