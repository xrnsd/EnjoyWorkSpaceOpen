package kuyou.common.ku09.handler;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import androidx.annotation.NonNull;

import java.util.HashMap;
import java.util.Map;

import kuyou.common.ku09.handler.basic.IStatusGuard;
import kuyou.common.ku09.handler.basic.IStatusGuardCallback;
import kuyou.common.ku09.handler.basic.StatusGuardRequestConfig;

/**
 * action :模块状态守护
 * <p>
 * author: wuguoxian <br/>
 * date: 20-11-4 <br/>
 * 已实现列表：<br/>
 * 1 IPC框架配置 <br/>
 * 2 log保存 <br/>
 * 3 模块活动保持 <br/>
 * 4 设备基础配置 <br/>
 * 5 设备部分状态监听 <br/>
 * 6 按键监听分发 <br/>
 * <p>
 */
public class HandlerStatusGuard extends Handler implements IStatusGuard {
    //单例
    private volatile static HandlerStatusGuard instance; //声明成 volatile

    public static HandlerStatusGuard getSingleton() {
        if (instance == null) {
            synchronized (HandlerStatusGuard.class) {
                if (instance == null) {
                    HandlerThread handlerThreadStatusGuard = new HandlerThread(".HandlerThread.status.guard");
                    handlerThreadStatusGuard.start();
                    instance = new HandlerStatusGuard(handlerThreadStatusGuard.getLooper());
                }
            }
        }
        return instance;
    }

    private HandlerStatusGuard(Looper looper) {
        super(looper);
    }

    protected static final String TAG = "com.kuyou.rc.handler.platform > HandlerStatusGuard";
    private static int sMessageFlag = 0;

    protected Map<Integer, IStatusGuardCallback> mStatusGuardCallbackList = new HashMap<Integer, IStatusGuardCallback>();
    protected Map<Integer, Handler> mStatusGuardCallbackHandlerList = new HashMap<Integer, Handler>();
    protected Map<Integer, Runnable> mStatusGuardCallbackRunnableList = new HashMap<Integer, Runnable>();
    protected Map<Integer, StatusGuardRequestConfig> mStatusGuardConfigList = new HashMap<Integer, StatusGuardRequestConfig>();

    @Override
    public boolean registerStatusGuardCallback(final IStatusGuardCallback callback, final StatusGuardRequestConfig config) {
        int msgWhat = applyMessageFlag();
        callback.setReceiveMessage(msgWhat);
        mStatusGuardCallbackList.put(msgWhat, callback);
        mStatusGuardCallbackRunnableList.put(msgWhat, new Runnable() {
            @Override
            public void run() {
                callback.onReceiveMessage();
            }
        });
        mStatusGuardCallbackHandlerList.put(msgWhat, new Handler(config.getMessageHandleLooper()));
        return true;
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
        removeMessages(msgWhat);
        mStatusGuardCallbackList.get(msgWhat).onRemoveMessage();
    }

    @Override
    public boolean isStart(int msgWhat) {
        return hasMessages(msgWhat);
    }

    public static int applyMessageFlag() {
        return sMessageFlag++;
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
        StatusGuardRequestConfig config = mStatusGuardConfigList.get(msgWhat);
        if (config.isAutoMessageReceiveCycle()) {
            sendEmptyMessageDelayed(msgWhat, config.getMessageReceiveFreq());
        }
        mStatusGuardCallbackHandlerList.get(msgWhat).post(mStatusGuardCallbackRunnableList.get(msgWhat));
    }
}
