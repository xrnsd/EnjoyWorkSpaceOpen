package kuyou.common.ku09.status;

import android.os.Looper;

public abstract class StatusProcessBusCallbackImpl implements IStatusProcessBusCallback {

    private boolean isAutoNoticeReceiveCycle = false;
    private boolean isEnableReceiveRemoveNotice = false;
    private long mNoticeReceiveFreq = -1;
    private Looper mNoticeHandleLooper = null;

    /**
     * action:自动循环收到消息
     *
     * @param val1 ，为true表示开启，自动循环收到消息
     * @param val2 ，val1为true时，自动循环收到消息的周期
     * @param val3 ，设定消息处理线程
     */
    public StatusProcessBusCallbackImpl(boolean val1, long val2, Looper val3) {
        isAutoNoticeReceiveCycle = val1;
        mNoticeReceiveFreq = val2;
        mNoticeHandleLooper = val3;
    }

    public boolean isAutoNoticeReceiveCycle() {
        return isAutoNoticeReceiveCycle;
    }

    public long getNoticeReceiveFreq() {
        return mNoticeReceiveFreq;
    }

    public Looper getNoticeHandleLooper() {
        return mNoticeHandleLooper;
    }

    public boolean isEnableReceiveRemoveNotice() {
        return isEnableReceiveRemoveNotice;
    }


    /**
     * action:是否接收状态移除通知
     *
     * @param val ，为true表示已主动移除
     */
    public StatusProcessBusCallbackImpl setEnableReceiveRemoveNotice(boolean val) {
        isEnableReceiveRemoveNotice = val;
        return StatusProcessBusCallbackImpl.this;
    }
}
