package kuyou.common.ku09.handler.basic;

import android.os.Looper;

public class StatusGuardRequestConfig {

    boolean isAutoMessageReceiveCycle = false;

    long mMessageReceiveFreq = -1;

    Looper mMessageHandleLooper = null;

    public StatusGuardRequestConfig(boolean val1, long val2, Looper val3) {
        isAutoMessageReceiveCycle = val1;
        mMessageReceiveFreq = val2;
        mMessageHandleLooper = val3;
    }

    /**
     * action:自动循环收到消息
     */
    public boolean isAutoMessageReceiveCycle() {
        return isAutoMessageReceiveCycle;
    }

    /**
     * action:消息接收频度
     */
    public long getMessageReceiveFreq() {
        return mMessageReceiveFreq;
    }

    /**
     * action:处理消息时处的循环
     */
    public Looper getMessageHandleLooper() {
        return mMessageHandleLooper;
    }
}
