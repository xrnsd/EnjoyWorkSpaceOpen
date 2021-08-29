package kuyou.common.ku09.handler.basic;

public interface IStatusGuardCallback {
    /**
     * action:收到消息
     */
    public void onReceiveMessage();

    /**
     * action:消息被移除
     */
    public void onRemoveMessage();

    /**
     * action:回调对应的消息ID
     */
    public void setReceiveMessage(int what);
}
