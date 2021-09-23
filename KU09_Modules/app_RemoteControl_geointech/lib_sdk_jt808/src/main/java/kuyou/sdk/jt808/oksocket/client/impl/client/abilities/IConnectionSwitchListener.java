package kuyou.sdk.jt808.oksocket.client.impl.client.abilities;


import kuyou.sdk.jt808.oksocket.client.sdk.client.ConnectionInfo;
import kuyou.sdk.jt808.oksocket.client.sdk.client.connection.IConnectionManager;

/**
 * Created by xuhao on 2017/6/30.
 */

public interface IConnectionSwitchListener {
    void onSwitchConnectionInfo(IConnectionManager manager, ConnectionInfo oldInfo, ConnectionInfo newInfo);
}
