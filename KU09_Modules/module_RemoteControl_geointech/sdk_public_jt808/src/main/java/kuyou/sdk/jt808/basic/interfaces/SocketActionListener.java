package kuyou.sdk.jt808.base.interfaces;

public interface SocketActionListener {

    void onSocketReadResponse(byte[] bytes);

    void onSocketWriteResponse(byte[] bytes);

    void onPulseSend(byte[] bytes); // 注意：子线程回调

    void onSocketDisconnection();

    void onSocketConnectionSuccess();

    void onSocketConnectionFailed();
}