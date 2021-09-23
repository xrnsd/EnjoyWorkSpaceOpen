package kuyou.sdk.jt808.oksocket.interfaces.common_interfacies.server;


import kuyou.sdk.jt808.oksocket.core.iocore.interfaces.IIOCoreOptions;


public interface IServerManagerPrivate<E extends IIOCoreOptions> extends IServerManager<E> {
    void initServerPrivate(int serverPort);
}
