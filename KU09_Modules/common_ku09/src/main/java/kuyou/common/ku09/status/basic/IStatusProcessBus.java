package kuyou.common.ku09.status.basic;

import android.os.Looper;

public interface IStatusProcessBus {
    public int registerStatusProcessBusCallback(final IStatusProcessBusCallback callback);

    public void start(int processFlag);

    public void stop(int processFlag);

    public boolean isStart(int processFlag);
}
