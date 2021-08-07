package com.kuyou.rc.platform;

import android.util.Log;

import kuyou.common.ipc.RemoteEvent;
import kuyou.common.ku09.event.rc.EventConnectResult;
import kuyou.common.ku09.event.rc.base.EventResult;
import kuyou.sdk.jt808.base.exceptions.SocketManagerException;
import kuyou.sdk.jt808.base.jt808bean.JTT808Bean;
import kuyou.sdk.jt808.base.jt808coding.JTT808Coding;
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
        byte[] bytes;
        try {
            bytes = JTT808Coding.check808DataThrows(data.getBodyBytes());
        } catch (SocketManagerException e) {
            Log.e(TAG, Log.getStackTraceString(e));
            return;
        }
        if (null == bytes) {
            Log.w(TAG, "onSocketReadResponse > bytes is null");
            return;
        }
        onRemote2LocalMessage(JTT808Coding.resolve808(bytes), bytes);
    }

    public abstract void onRemote2LocalMessage(JTT808Bean bean, byte[] data);

}
