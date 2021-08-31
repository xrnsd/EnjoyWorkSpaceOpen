package kuyou.common.ku09.status;

import android.os.Looper;

public abstract class StatusBusProcessCallbackImpl implements IStatusBusProcessCallback {

    private boolean isAutoMessageReceiveCycle = false;
    private long mMessageReceiveFreq = -1;
    private Looper mMessageHandleLooper = null;

    /**
     * action:自动循环收到消息
     *
     * @param val1 ，为true表示开启，自动循环收到消息
     * @param val2 ，val1为true时，自动循环收到消息的周期
     * @param val3 ，设定消息处理线程
     * */
    public StatusBusProcessCallbackImpl(boolean val1, long val2, Looper val3) {
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
