package kuyou.sdk.jt808.oksocket.interfaces.common_interfacies.server;

import kuyou.sdk.jt808.oksocket.interfaces.common_interfacies.client.IDisConnectable;
import kuyou.sdk.jt808.oksocket.interfaces.common_interfacies.client.ISender;
import kuyou.sdk.jt808.oksocket.core.protocol.IReaderProtocol;

import java.io.Serializable;

public interface IClient extends IDisConnectable, ISender<IClient>, Serializable {

    String getHostIp();

    String getHostName();

    String getUniqueTag();

    void setReaderProtocol(IReaderProtocol protocol);

    void addIOCallback(IClientIOCallback clientIOCallback);

    void removeIOCallback(IClientIOCallback clientIOCallback);

    void removeAllIOCallback();

}
