package com.kuyou.rc.platform;

import kuyou.sdk.jt808.oksocket.client.sdk.client.ConnectionInfo;
import kuyou.sdk.jt808.oksocket.client.sdk.client.action.SocketActionAdapter;
import kuyou.sdk.jt808.oksocket.core.pojo.OriginalData;

/**
 * action :
 * <p>
 * remarks:  <br/>
 * author: wuguoxian <br/>
 * date: 21-8-6 <br/>
 * </p>
 */
public abstract class RemoteCommandCallback extends SocketActionAdapter {

    protected final String TAG = "com.kuyou.rc.platform > RemoteCommandCallback";

    @Override
    public void onSocketReadResponse(ConnectionInfo info, String action, OriginalData data) {
        onRemote2LocalMessage(data);
    }

    public abstract void onRemote2LocalMessage(OriginalData data);

}
