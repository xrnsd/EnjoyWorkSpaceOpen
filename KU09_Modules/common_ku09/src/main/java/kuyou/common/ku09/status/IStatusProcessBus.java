package kuyou.common.ku09.status;

public interface IStatusProcessBus {
    public int registerStatusProcessBusProcessCallback(final IStatusProcessBusCallback callback);

    public void start(int processFlag);

    public void stop(int processFlag);

    public boolean isStart(int processFlag);
}
