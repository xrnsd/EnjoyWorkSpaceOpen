package kuyou.common.ku09.status;

import android.os.Looper;

public interface IStatusProcessBusCallback {

    /**
     * action:收到状态通知 <br/>
     * remarks:  isEnableReceiveRemoveNotice 返回false时isRemove为空 <br/>
     *
     * @param isRemove ，为true表示状态已被主动移除 <br/>
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

    /**
     * action:是否接收状态移除通知
     *
     * @return 为true表示接收
     */
    public boolean isEnableReceiveRemoveNotice();
}
