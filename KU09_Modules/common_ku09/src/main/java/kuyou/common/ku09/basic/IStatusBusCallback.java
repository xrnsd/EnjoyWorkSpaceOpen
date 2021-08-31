package kuyou.common.ku09.basic;

public interface IStatusBusCallback {
    /**
     * action:收到消息
     *
     * @param isRemove ，为true表示已主动移除
     */
    public void onReceiveMessage(boolean isRemove);
}
