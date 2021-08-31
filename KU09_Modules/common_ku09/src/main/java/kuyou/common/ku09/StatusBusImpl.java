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
import kuyou.common.ku09.basic.StatusBusProcessCallback;

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

    private int mProcessFlag = 0;
    private int mMarkFlag = 0;
    private final int MARK_STATUS_NONE = -1024;

    protected Map<Integer, StatusBusProcessCallback> mStatusBusCallbackList = new HashMap<Integer, StatusBusProcessCallback>();
    protected Map<Integer, Handler> mStatusBusCallbackHandlerList = new HashMap<Integer, Handler>();
    protected Map<Integer, Runnable> mStatusBusCallbackRunnableList = new HashMap<Integer, Runnable>();
    protected Map<Integer, StatusBusProcessCallback> mStatusBusConfigList = new HashMap<Integer, StatusBusProcessCallback>();

    protected Map<Integer, Integer> mStatusMarkStatusList = new HashMap<Integer, Integer>();
    protected Map<Integer, String> mStatusMarkTagList = new HashMap<Integer, String>();

    public int applyProcessFlag() {
        Log.d(TAG, "applyProcessFlag > basic flag = " + mProcessFlag);
        return mProcessFlag++;
    }

    public int applyMarkFlag() {
        Log.d(TAG, "applyMarkFlag > basic flag = " + mMarkFlag);
        return mMarkFlag++;
    }

    @Override
    public void handleMessage(@NonNull Message msg) {
        super.handleMessage(msg);
        int flag = msg.what;
        removeMessages(flag);
        if (!mStatusBusCallbackList.containsKey(flag)) {
            Log.e(TAG, "handleMessage > process fail : mStatusBusCallbackList not contains msg = " + flag);
            return;
        }
        StatusBusProcessCallback config = mStatusBusConfigList.get(flag);
        if (config.isAutoMessageReceiveCycle()) {
            sendEmptyMessageDelayed(flag, config.getMessageReceiveFreq());
        }
        mStatusBusCallbackHandlerList.get(flag).post(mStatusBusCallbackRunnableList.get(flag));
    }

    @Override
    public int registerStatusBusProcessCallback(final StatusBusProcessCallback callback) {
        final int flag = applyProcessFlag();
        mStatusBusCallbackList.put(flag, callback);
        mStatusBusCallbackRunnableList.put(flag, new Runnable() {
            @Override
            public void run() {
                callback.onReceiveMessage(false);
            }
        });
        mStatusBusCallbackHandlerList.put(flag, new Handler(callback.getMessageHandleLooper()));
        mStatusBusConfigList.put(flag, callback);
        return flag;
    }

    @Override
    public void start(int processFlag) {
        if (!mStatusBusCallbackList.containsKey(processFlag)) {
            Log.e(TAG, "start > process fail : mStatusBusCallbackList not contains msg = " + processFlag);
            return;
        }
        long delayed = mStatusBusConfigList.get(processFlag).getMessageReceiveFreq();
        if (0 < delayed) {
            sendEmptyMessageDelayed(processFlag, mStatusBusConfigList.get(processFlag).getMessageReceiveFreq());
            return;
        }
        sendEmptyMessage(processFlag);
    }

    @Override
    public void stop(int processFlag) {
        if (!mStatusBusCallbackList.containsKey(processFlag)) {
            Log.e(TAG, "stop > process fail : mStatusBusCallbackList not contains msg = " + processFlag);
            return;
        }
        if (hasMessages(processFlag)) {
            removeMessages(processFlag);
            return;
        }
        mStatusBusCallbackList.get(processFlag).onReceiveMessage(true);
    }

    @Override
    public boolean isStart(int processFlag) {
        return hasMessages(processFlag);
    }

    @Override
    public int registerStatusBusMark(String markTag) {
        final int flag = applyMarkFlag();
        mStatusMarkTagList.put(flag, markTag);
        mStatusMarkStatusList.put(flag, MARK_STATUS_NONE);
        return 0;
    }

    @Override
    public void setMarkStatus(int markFlag, int status) {
        Log.d(TAG, new StringBuilder("setMarkStatus >")
                .append("\nmarkTag = ").append(mStatusMarkTagList.get(markFlag))
                .append("\nmarkFlag = ").append(markFlag)
                .append("\nstatus = ").append(status)
                .toString());
        mStatusMarkStatusList.put(markFlag, status);
    }

    @Override
    public int getMarkStatus(int markFlag) {
        final Integer flag = Integer.valueOf(markFlag);
        final int statusLocal = mStatusMarkStatusList.get(flag);
        if (MARK_STATUS_NONE == statusLocal) {
            Log.w(TAG, "getMarkStatus > process fail : mark not exists status = " + mStatusMarkTagList.get(flag));
        }
        return statusLocal;
    }

    @Override
    public boolean isExistsMarkStatus(int markFlag, int status) {
        final Integer flag = Integer.valueOf(markFlag);
        final int statusLocal = mStatusMarkStatusList.get(flag);
        if (MARK_STATUS_NONE == statusLocal) {
            Log.w(TAG, "isExistsMarkStatus > process fail : mark not exists status = " + mStatusMarkTagList.get(flag));
            return false;
        }
        return statusLocal == status;
    }
}
