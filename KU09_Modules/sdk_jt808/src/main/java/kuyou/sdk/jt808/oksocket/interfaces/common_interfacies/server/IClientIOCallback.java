package kuyou.sdk.jt808.oksocket.interfaces.common_interfacies.server;


import kuyou.sdk.jt808.oksocket.core.iocore.interfaces.ISendable;
import kuyou.sdk.jt808.oksocket.core.pojo.OriginalData;

public interface IClientIOCallback {

    void onClientRead(OriginalData originalData, IClient client, IClientPool<IClient, String> clientPool);

    void onClientWrite(ISendable sendable, IClient client, IClientPool<IClient, String> clientPool);

}
