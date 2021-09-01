package kuyou.common.ku09.status;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import androidx.annotation.NonNull;

import java.util.HashMap;
import java.util.Map;

/**
 * action :提供状态的物流服务
 * <p>
 * author: wuguoxian <br/>
 * date: 21-08-24 <br/>
 * <p>
 */
public class StatusProcessBusImpl extends Handler implements IStatusProcessBus {

    protected static final String TAG = "com.kuyou.rc.handler.platform > HandlerStatusGuard";

    private volatile static StatusProcessBusImpl instance;

    public static StatusProcessBusImpl getInstance() {
        if (instance == null) {
            synchronized (StatusProcessBusImpl.class) {
                if (instance == null) {
                    HandlerThread handlerThreadStatusGuard = new HandlerThread(".HandlerThread.status.guard");
                    handlerThreadStatusGuard.start();
                    instance = new StatusProcessBusImpl(handlerThreadStatusGuard.getLooper());
                }
            }
        }
        return instance;
    }

    private StatusProcessBusImpl(Looper looper) {
        super(looper);
    }

    private int mProcessFlag = 0;

    protected Map<Integer, IStatusProcessBusCallback> mStatusBusCallbackList = new HashMap<Integer, IStatusProcessBusCallback>();
    protected Map<Integer, Handler> mStatusBusCallbackHandlerList = new HashMap<Integer, Handler>();
    protected Map<Integer, Runnable> mStatusBusCallbackRunnableList = new HashMap<Integer, Runnable>();

    public int applyProcessFlag() {
        Log.d(TAG, "applyProcessFlag > basic flag = " + mProcessFlag);
        return mProcessFlag++;
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
        IStatusProcessBusCallback config = mStatusBusCallbackList.get(flag);
        if (config.isAutoNoticeReceiveCycle()) {
            sendEmptyMessageDelayed(flag, config.getNoticeReceiveFreq());
        }
        mStatusBusCallbackHandlerList.get(flag).post(mStatusBusCallbackRunnableList.get(flag));
    }

    @Override
    public int registerStatusBusProcessCallback(final IStatusProcessBusCallback callback) {
        final int flag = applyProcessFlag();
        mStatusBusCallbackList.put(flag, callback);
        mStatusBusCallbackRunnableList.put(flag, new Runnable() {
            @Override
            public void run() {
                callback.onReceiveStatusNotice(false);
            }
        });
        mStatusBusCallbackHandlerList.put(flag, new Handler(callback.getNoticeHandleLooper()));
        return flag;
    }

    @Override
    public void start(int processFlag) {
        if (!mStatusBusCallbackList.containsKey(processFlag)) {
            Log.e(TAG, "start > process fail : mStatusBusCallbackList not contains msg = " + processFlag);
            return;
        }
        removeMessages(processFlag);
        long delayed = mStatusBusCallbackList.get(processFlag).getNoticeReceiveFreq();
        if (0 < delayed) {
            sendEmptyMessageDelayed(processFlag, delayed);
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
        mStatusBusCallbackList.get(processFlag).onReceiveStatusNotice(true);
    }

    @Override
    public boolean isStart(int processFlag) {
        return hasMessages(processFlag);
    }
}
