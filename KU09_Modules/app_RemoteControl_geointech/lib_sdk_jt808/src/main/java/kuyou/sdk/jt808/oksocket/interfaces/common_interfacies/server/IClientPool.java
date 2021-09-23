package kuyou.sdk.jt808.oksocket.interfaces.common_interfacies.server;


import kuyou.sdk.jt808.oksocket.core.iocore.interfaces.ISendable;

public interface IClientPool<T, K> {

    void cache(T t);

    T findByUniqueTag(K key);

    int size();

    void sendToAll(ISendable sendable);
}
