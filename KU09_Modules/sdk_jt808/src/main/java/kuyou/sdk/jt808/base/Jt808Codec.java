package kuyou.sdk.jt808.base;

import android.util.Log;

import kuyou.common.ipc.RemoteEvent;
import kuyou.common.ku09.bytes.ByteUtils;
import kuyou.common.ku09.event.base.IDispatchEventCallBack;
import kuyou.common.ku09.event.jt808.EventConnectResult;
import kuyou.common.ku09.event.jt808.base.EventResult;
import kuyou.sdk.jt808.base.exceptions.SocketManagerException;
import kuyou.sdk.jt808.base.jt808bean.JTT808Bean;
import kuyou.sdk.jt808.base.jt808coding.JTT808Coding;
import kuyou.sdk.jt808.oksocket.client.sdk.client.ConnectionInfo;
import kuyou.sdk.jt808.oksocket.client.sdk.client.action.SocketActionAdapter;
import kuyou.sdk.jt808.oksocket.core.pojo.OriginalData;

/**
 * action :基础JT808编解码器，只处理分发[鉴权,连接]事件，打印状态log，其他事情交给子类去做
 * <p>
 * remarks:  <br/>
 * author: wuguoxian <br/>
 * date: 21-3-29 <br/>
 * </p>
 */
public abstract class Jt808Codec extends SocketActionAdapter {
    protected final String TAG = "kuyou.sdk.jt808 > " + this.getClass().getSimpleName();

    private IDispatchEventCallBack mEventCallBack;

    public Jt808Codec(IDispatchEventCallBack callBack) {
        mEventCallBack = callBack;
    }

    protected void dispatchEvent(RemoteEvent event) {
        if (null == mEventCallBack) {
            Log.e(TAG, "dispatchEvent > process fail : mEventCallBack is null");
            return;
        }
        mEventCallBack.dispatchEvent(event);
    }

    public abstract void onMessage(final JTT808Bean bean, byte[] data);

    @Override
    public void onSocketReadResponse(ConnectionInfo info, String action, OriginalData data) {
        byte[] bytes;
        byte[] body = ByteUtils.byteMergerAll(data.getBodyBytes());
        try {
            bytes = JTT808Coding.check808DataThrows(body);
        } catch (SocketManagerException e) {
            Log.e(TAG, Log.getStackTraceString(e));
            return;
        }
        if (null == bytes) {
            Log.w(TAG, "onSocketReadResponse > bytes is null");
            return;
        }
        onMessage(JTT808Coding.resolve808(bytes), bytes);

    }

    @Override
    public void onSocketDisconnection(ConnectionInfo info, String action, Exception e) {
        super.onSocketDisconnection(info, action, e);
        dispatchEvent(new EventConnectResult()
                .setResultCode(EventResult.ResultCode.DIS));
    }

    @Override
    public void onSocketConnectionSuccess(ConnectionInfo info, String action) {
        super.onSocketConnectionSuccess(info, action);
        dispatchEvent(new EventConnectResult()
                .setResultCode(EventResult.ResultCode.SUCCESS));
    }

    @Override
    public void onSocketConnectionFailed(ConnectionInfo info, String action, Exception e) {
        super.onSocketConnectionFailed(info, action, e);
        dispatchEvent(new EventConnectResult()
                .setResultCode(EventResult.ResultCode.FAIL));
    }
}
