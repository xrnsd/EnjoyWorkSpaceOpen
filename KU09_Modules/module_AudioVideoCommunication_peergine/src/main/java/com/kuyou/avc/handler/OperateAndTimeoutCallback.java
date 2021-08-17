package com.kuyou.avc.handler;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import androidx.annotation.NonNull;

import java.util.HashMap;
import java.util.Map;

/**
 * action :
 * <p>
 * remarks:  <br/>
 * author: wuguoxian <br/>
 * date: 21-8-13 <br/>
 * </p>
 */
public class OperateAndTimeoutCallback extends Handler {
    protected final String TAG = "com.kuyou.rc.platform > OperateTimeoutCallback";

    private static OperateAndTimeoutCallback sMain;

    private OperateAndTimeoutCallback() {
        super(Looper.getMainLooper());
    }

    public static OperateAndTimeoutCallback getInstance() {
        if (null == sMain) {
            sMain = new OperateAndTimeoutCallback();
        }
        return sMain;
    }

    protected Map<Integer, Runnable> mOperateCallBackList = new HashMap<Integer, Runnable>();
    protected Map<Integer, Runnable> mTimeOutCallBackList = new HashMap<Integer, Runnable>();

    @Override
    public void handleMessage(@NonNull Message msg) {
        super.handleMessage(msg);
        final int msgCode = msg.what;
        removeMessages(msgCode);
        if (!mTimeOutCallBackList.containsKey(msgCode)) {
            Log.e(TAG, "handleMessage > process fail : mTimeOutCallBackList not contains operateCode = " + msgCode);
            return;
        }
        mTimeOutCallBackList.get(msgCode).run();
    }

    public OperateAndTimeoutCallback register(int operateCode, Runnable callback, Runnable timeOutCallback) {
        mOperateCallBackList.put(operateCode, callback);
        mTimeOutCallBackList.put(operateCode, timeOutCallback);
        return OperateAndTimeoutCallback.this;
    }

    public void start(int operateCode, int timeOutFlag) {
        if (!mOperateCallBackList.containsKey(operateCode)) {
            Log.e(TAG, "start > process fail : mOperateCallBackList not contains operateCode = " + operateCode);
            return;
        }
        mOperateCallBackList.get(operateCode).run();
        sendEmptyMessageDelayed(operateCode, timeOutFlag);
    }

    public void stop(int operateCode) {
        removeMessages(operateCode);
    }
}
