package kuyou.common.ku09.handler.basic;

public interface IStatusGuard {
    public boolean registerStatusGuardCallback(final IStatusGuardCallback callback, final StatusGuardRequestConfig config);

    public void start(int msgWhat);

    public void stop(int msgWhat);

    public boolean isStart(int msgWhat);
}
