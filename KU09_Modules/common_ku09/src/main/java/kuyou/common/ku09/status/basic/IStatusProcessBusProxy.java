package kuyou.common.ku09.status.basic;

public interface IStatusProcessBusProxy extends IStatusProcessBus {
    public void registerStatusProcessBusCallback(int statusCode, IStatusProcessBusCallback callback);
}
