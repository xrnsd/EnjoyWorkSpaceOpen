package kuyou.common.ku09.basic;

public interface IStatusBus {
    public int registerStatusBusCallback(final IStatusBusCallback callback, final StatusBusRequestConfig config);

    public void start(int msgWhat);

    public void stop(int msgWhat);

    public boolean isStart(int msgWhat);
}
