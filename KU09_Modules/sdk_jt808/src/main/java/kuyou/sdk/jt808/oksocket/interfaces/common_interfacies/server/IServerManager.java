package kuyou.sdk.jt808.oksocket.interfaces.common_interfacies.server;

import kuyou.sdk.jt808.oksocket.core.iocore.interfaces.IIOCoreOptions;

public interface IServerManager<E extends IIOCoreOptions> extends IServerShutdown {

    void listen();

    void listen(E options);

    boolean isLive();

    IClientPool<String, IClient> getClientPool();
}
