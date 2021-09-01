package kuyou.common.ku09.status;

import android.os.Looper;

public abstract class StatusProcessBusCallbackImpl implements IStatusProcessBusCallback {

    private boolean isAutoNoticeReceiveCycle = false;
    private long mNoticeReceiveFreq = -1;
    private Looper mNoticeHandleLooper = null;

    /**
     * action:自动循环收到消息
     *
     * @param val1 ，为true表示开启，自动循环收到消息
     * @param val2 ，val1为true时，自动循环收到消息的周期
     * @param val3 ，设定消息处理线程
     * */
    public StatusProcessBusCallbackImpl(boolean val1, long val2, Looper val3) {
        isAutoNoticeReceiveCycle = val1;
        mNoticeReceiveFreq = val2;
        mNoticeHandleLooper = val3;
    }

    /**
     * action:自动循环收到消息
     */
    public boolean isAutoNoticeReceiveCycle() {
        return isAutoNoticeReceiveCycle;
    }

    /**
     * action:消息接收频度
     */
    public long getNoticeReceiveFreq() {
        return mNoticeReceiveFreq;
    }

    /**
     * action:处理消息时处的循环
     */
    public Looper getNoticeHandleLooper() {
        return mNoticeHandleLooper;
    }
}
