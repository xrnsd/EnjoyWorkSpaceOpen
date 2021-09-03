package kuyou.common.ku09.status;

import android.util.Log;

import java.util.HashMap;
import java.util.Map;

import kuyou.common.ku09.status.basic.IStatusProcessBus;
import kuyou.common.ku09.status.basic.IStatusProcessBusCallback;
import kuyou.common.ku09.status.basic.IStatusProcessBusProxy;

/**
 * action :提供状态的物流服务
 * <p>
 * author: wuguoxian <br/>
 * date: 21-08-24 <br/>
 * <p>
 */
public abstract class StatusProcessBusProxy implements IStatusProcessBusProxy {
    protected final String TAG = "kuyou.common.ku09.status > StatusProcessBusProxy";

    private IStatusProcessBus mStatusProcessBus;
    private Map<Integer, Integer> mStatusProcessBusProxyStatusCodeList;
    private Map<Integer, Integer> mStatusProcessBusProxyFlagList;

    public StatusProcessBusProxy(IStatusProcessBus spb) {
        mStatusProcessBus = spb;

        mStatusProcessBusProxyStatusCodeList = new HashMap<Integer, Integer>();
        mStatusProcessBusProxyFlagList = new HashMap<Integer, Integer>();
    }

    protected abstract void onReceiveStatusProcessNotice(int statusCode, boolean isRemove);

    @Override
    public void registerStatusProcessBusCallback(int statusCode, IStatusProcessBusCallback callback) {
        if (mStatusProcessBusProxyStatusCodeList.containsKey(Integer.valueOf(statusCode))) {
            Log.w(TAG, "registerStatusProcessBusCallback > process fail : statusCode is registered");
            return;
        }
        StatusProcessBusCallbackImpl callbackProxy = new StatusProcessBusCallbackImpl(callback) {
            @Override
            public void onReceiveStatusProcessNotice(boolean isRemove) {
                StatusProcessBusProxy.this.onReceiveStatusProcessNotice(
                        mStatusProcessBusProxyFlagList.get(Integer.valueOf(getStatusProcessFlag())), isRemove);
            }
        };
        final int flag = mStatusProcessBus.registerStatusProcessBusCallback(callbackProxy);
        callbackProxy.setStatusProcessFlag(flag);

        mStatusProcessBusProxyStatusCodeList.put(statusCode, flag);
        mStatusProcessBusProxyFlagList.put(flag, statusCode);
    }

    @Override
    public int registerStatusProcessBusCallback(IStatusProcessBusCallback callback) {
        throw new RuntimeException("this api is disable");
    }

    @Override
    public void start(int statusCode) {
        if (!mStatusProcessBusProxyStatusCodeList.containsKey(Integer.valueOf(statusCode))) {
            Log.w(TAG, "start > process fail : statusCode is not registered");
            return;
        }
        mStatusProcessBus
                .start(mStatusProcessBusProxyStatusCodeList.get(Integer.valueOf(statusCode)));
    }

    @Override
    public void stop(int statusCode) {
        if (!mStatusProcessBusProxyStatusCodeList.containsKey(Integer.valueOf(statusCode))) {
            Log.w(TAG, "stop > process fail : statusCode is not registered");
            return;
        }
        mStatusProcessBus
                .stop(mStatusProcessBusProxyStatusCodeList.get(Integer.valueOf(statusCode)));
    }

    @Override
    public boolean isStart(int statusCode) {
        if (!mStatusProcessBusProxyStatusCodeList.containsKey(Integer.valueOf(statusCode))) {
            Log.w(TAG, "isStart > process fail : statusCode is not registered");
            return false;
        }
        return mStatusProcessBus
                .isStart(mStatusProcessBusProxyStatusCodeList.get(Integer.valueOf(statusCode)));
    }
}
