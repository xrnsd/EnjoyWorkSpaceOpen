package kuyou.common.ku09.status;

import android.os.Looper;

public interface IStatusProcessBusCallback {

    /**
     * action:收到状态通知
     *
     * @param isRemove ，为true表示已主动移除
     */
    public void onReceiveStatusNotice(boolean isRemove);

    /**
     * action:设定是否开启状态通知自动循环
     */
    public boolean isAutoNoticeReceiveCycle();

    /**
     * action:设定状态通知接收频度
     */
    public long getNoticeReceiveFreq();

    /**
     * action:设定状态通知处理线程
     */
    public Looper getNoticeHandleLooper();
}
