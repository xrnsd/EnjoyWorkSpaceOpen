package kuyou.common.ku09.status;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import androidx.annotation.NonNull;

import java.util.HashMap;
import java.util.Map;

import kuyou.common.ku09.status.basic.IStatusProcessBus;
import kuyou.common.ku09.status.basic.IStatusProcessBusCallback;

/**
 * action :提供状态的物流服务
 * <p>
 * author: wuguoxian <br/>
 * date: 21-08-24 <br/>
 * <p>
 */
public class StatusProcessBusImpl extends Handler implements IStatusProcessBus {

    protected static final String TAG = "ckuyou.common.ku09.status > StatusProcessBusImpl";

    private volatile static StatusProcessBusImpl instance;

    public static StatusProcessBusImpl getInstance() {
        if (instance == null) {
            synchronized (StatusProcessBusImpl.class) {
                if (instance == null) {
                    HandlerThread handlerThreadStatusProcessBus = new HandlerThread(".HandlerThread.StatusProcessBus");
                    handlerThreadStatusProcessBus.start();
                    instance = new StatusProcessBusImpl(handlerThreadStatusProcessBus.getLooper());
                }
            }
        }
        return instance;
    }

    private StatusProcessBusImpl(Looper looper) {
        super(looper);
    }

    private int mProcessFlag = 0;

    private Looper mLooper;

    protected Map<Integer, IStatusProcessBusCallback> mStatusProcessBusCallbackList = new HashMap<Integer, IStatusProcessBusCallback>();
    protected Map<Integer, Handler> mStatusProcessBusCallbackHandlerList = new HashMap<Integer, Handler>();
    protected Map<Integer, Runnable> mStatusProcessBusCallbackRunnableList = new HashMap<Integer, Runnable>();

    protected int applyProcessFlag() {
        Log.d(TAG, "applyProcessFlag > basic flag = " + mProcessFlag);
        return mProcessFlag++;
    }

    protected Looper getBackgroundHandleLooper() {
        if (null == mLooper) {
            HandlerThread handlerThreadBackgroundHandle = new HandlerThread(".HandlerThread.BackgroundHandle");
            handlerThreadBackgroundHandle.start();
            mLooper = handlerThreadBackgroundHandle.getLooper();
        }
        return mLooper;
    }

    @Override
    public void handleMessage(@NonNull Message msg) {
        super.handleMessage(msg);
        int flag = msg.what;
        removeMessages(flag);
        if (!mStatusProcessBusCallbackList.containsKey(flag)) {
            Log.e(TAG, "handleMessage > process fail : mStatusProcessBusCallbackList not contains msg = " + flag);
            return;
        }
        IStatusProcessBusCallback config = mStatusProcessBusCallbackList.get(flag);
        if (config.isAutoNoticeReceiveCycle()) {
            sendEmptyMessageDelayed(flag, config.getNoticeReceiveFreq());
        }
        mStatusProcessBusCallbackHandlerList.get(flag).post(mStatusProcessBusCallbackRunnableList.get(flag));
    }

    @Override
    public int registerStatusProcessBusCallback(final IStatusProcessBusCallback callback) {
        final int flag = applyProcessFlag();
        mStatusProcessBusCallbackList.put(flag, callback);
        mStatusProcessBusCallbackRunnableList.put(flag, new Runnable() {
            @Override
            public void run() {
                callback.onReceiveStatusProcessNotice(false);
            }
        });
        final int looperPolicy = callback.getNoticeHandleLooperPolicy();
        Looper looper = null;
        switch (looperPolicy) {
            case IStatusProcessBusCallback.LOOPER_POLICY_BACKGROUND:
                looper = getBackgroundHandleLooper();
                break;
            case IStatusProcessBusCallback.LOOPER_POLICY_MAIN:
                looper = Looper.getMainLooper();
                break;
            default:
                looper = callback.getNoticeHandleLooper();
                break;
        }
        if (null != looper) {
            mStatusProcessBusCallbackHandlerList.put(flag, new Handler(looper));
        } else {
            Log.e(TAG, "registerStatusProcessBusCallback > process fail :getNoticeHandleLooper is null ");
        }
        return flag;
    }

    @Override
    public void start(int processFlag) {
        if (!mStatusProcessBusCallbackList.containsKey(processFlag)) {
            Log.e(TAG, "start > process fail : mStatusProcessBusCallbackList not contains msg = " + processFlag);
            return;
        }
        removeMessages(processFlag);
        long delayed = mStatusProcessBusCallbackList.get(processFlag).getNoticeReceiveFreq();
        if (0 < delayed) {
            sendEmptyMessageDelayed(processFlag, delayed);
            return;
        }
        sendEmptyMessage(processFlag);
    }

    @Override
    public void stop(int processFlag) {
        if (!mStatusProcessBusCallbackList.containsKey(processFlag)) {
            Log.e(TAG, "stop > process fail : mStatusProcessBusCallbackList not contains msg = " + processFlag);
            return;
        }
        if (hasMessages(processFlag)) {
            removeMessages(processFlag);
            return;
        }
        if (!mStatusProcessBusCallbackList.get(processFlag).isEnableReceiveRemoveNotice()) {
            return;
        }
        mStatusProcessBusCallbackList.get(processFlag).onReceiveStatusProcessNotice(true);
    }

    @Override
    public boolean isStart(int processFlag) {
        return hasMessages(processFlag);
    }
}
