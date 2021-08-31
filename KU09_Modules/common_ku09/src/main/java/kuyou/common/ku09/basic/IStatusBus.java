package kuyou.common.ku09.basic;

public interface IStatusBus {
    public int registerStatusBusProcessCallback(final StatusBusProcessCallback config);

    public void start(int processFlag);

    public void stop(int processFlag);

    public boolean isStart(int processFlag);

    public int registerStatusBusMark(String markTag);

    public void setMarkStatus(int markFlag,int status);

    public int getMarkStatus(int markFlag);

    public boolean isExistsMarkStatus(int markFlag,int status);
}
