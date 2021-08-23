package kuyou.sdk.jt808.basic.interfaces;

public interface SocketPulseListener {
// 每次心跳发送成功的回调
    void parse(byte[] bytes);

}