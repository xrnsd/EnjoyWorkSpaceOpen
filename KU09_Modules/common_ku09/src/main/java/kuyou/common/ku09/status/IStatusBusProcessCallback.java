package kuyou.common.ku09.status;

import android.os.Looper;

public interface IStatusBusProcessCallback {

    /**
     * action:收到消息
     *
     * @param isRemove ，为true表示已主动移除
     */
    public void onReceiveMessage(boolean isRemove);

    /**
     * action:设定是否开启消息自动循环
     */
    public boolean isAutoMessageReceiveCycle();

    /**
     * action:设定消息接收频度
     */
    public long getMessageReceiveFreq();

    /**
     * action:设定消息处理线程
     */
    public Looper getMessageHandleLooper();
}
