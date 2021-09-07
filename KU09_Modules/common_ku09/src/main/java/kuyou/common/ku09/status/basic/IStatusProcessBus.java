package kuyou.common.ku09.status.basic;

public interface IStatusProcessBus {
    public void registerStatusNoticeCallback(int statusCode, IStatusProcessBusCallback callback);

    public int registerStatusNoticeCallback(final IStatusProcessBusCallback callback);

    public void start(int processFlag);

    public void start(int processFlag,long delayed);

    public void stop(int processFlag);

    public boolean isStart(int processFlag);
}
