package kuyou.common.ku09;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import androidx.annotation.NonNull;

import java.util.HashMap;
import java.util.Map;

import kuyou.common.ku09.basic.IStatusBus;
import kuyou.common.ku09.basic.IStatusBusCallback;
import kuyou.common.ku09.basic.StatusBusRequestConfig;

/**
 * action :提供模块状态的物流服务
 * <p>
 * author: wuguoxian <br/>
 * date: 21-08-24 <br/>
 * <p>
 */
public class StatusBusImpl extends Handler implements IStatusBus {

    protected static final String TAG = "com.kuyou.rc.handler.platform > HandlerStatusGuard";

    private volatile static StatusBusImpl instance;
    public static StatusBusImpl getInstance() {
        if (instance == null) {
            synchronized (StatusBusImpl.class) {
                if (instance == null) {
                    HandlerThread handlerThreadStatusGuard = new HandlerThread(".HandlerThread.status.guard");
                    handlerThreadStatusGuard.start();
                    instance = new StatusBusImpl(handlerThreadStatusGuard.getLooper());
                }
            }
        }
        return instance;
    }

    private StatusBusImpl(Looper looper) {
        super(looper);
    }

    private int mMessageFlag = 0;

    protected Map<Integer, IStatusBusCallback> mStatusGuardCallbackList = new HashMap<Integer, IStatusBusCallback>();
    protected Map<Integer, Handler> mStatusGuardCallbackHandlerList = new HashMap<Integer, Handler>();
    protected Map<Integer, Runnable> mStatusGuardCallbackRunnableList = new HashMap<Integer, Runnable>();
    protected Map<Integer, StatusBusRequestConfig> mStatusGuardConfigList = new HashMap<Integer, StatusBusRequestConfig>();

    public int applyMessageFlag() {
        Log.d(TAG, "applyMessageFlag > basic flag = " + mMessageFlag);
        return mMessageFlag++;
    }

    @Override
    public int registerStatusBusCallback(final IStatusBusCallback callback, final StatusBusRequestConfig config) {
        final int msgWhat = applyMessageFlag();
        mStatusGuardCallbackList.put(msgWhat, callback);
        mStatusGuardCallbackRunnableList.put(msgWhat, new Runnable() {
            @Override
            public void run() {
                callback.onReceiveMessage(false);
            }
        });
        mStatusGuardCallbackHandlerList.put(msgWhat, new Handler(config.getMessageHandleLooper()));
        mStatusGuardConfigList.put(msgWhat, config);
        return msgWhat;
    }

    @Override
    public void start(int msgWhat) {
        if (!mStatusGuardCallbackList.containsKey(msgWhat)) {
            Log.e(TAG, "start > process fail : mStatusGuardCallbackList not contains msg = " + msgWhat);
            return;
        }
        long delayed = mStatusGuardConfigList.get(msgWhat).getMessageReceiveFreq();
        if (0 < delayed) {
            sendEmptyMessageDelayed(msgWhat, mStatusGuardConfigList.get(msgWhat).getMessageReceiveFreq());
            return;
        }
        sendEmptyMessage(msgWhat);
    }

    @Override
    public void stop(int msgWhat) {
        if (!mStatusGuardCallbackList.containsKey(msgWhat)) {
            Log.e(TAG, "stop > process fail : mStatusGuardCallbackList not contains msg = " + msgWhat);
            return;
        }
        if (hasMessages(msgWhat)) {
            removeMessages(msgWhat);
            return;
        }
        mStatusGuardCallbackList.get(msgWhat).onReceiveMessage(true);
    }

    @Override
    public boolean isStart(int msgWhat) {
        return hasMessages(msgWhat);
    }

    @Override
    public void handleMessage(@NonNull Message msg) {
        super.handleMessage(msg);
        int msgWhat = msg.what;
        removeMessages(msgWhat);
        if (!mStatusGuardCallbackList.containsKey(msgWhat)) {
            Log.e(TAG, "handleMessage > process fail : mStatusGuardCallbackList not contains msg = " + msgWhat);
            return;
        }
        StatusBusRequestConfig config = mStatusGuardConfigList.get(msgWhat);
        if (config.isAutoMessageReceiveCycle()) {
            sendEmptyMessageDelayed(msgWhat, config.getMessageReceiveFreq());
        }
        mStatusGuardCallbackHandlerList.get(msgWhat).post(mStatusGuardCallbackRunnableList.get(msgWhat));
    }
}
