package kuyou.sdk.jt808.basic.socketbean;

import kuyou.sdk.jt808.oksocket.core.iocore.interfaces.ISendable;


public class SendDataBean implements ISendable {
    private byte[] body;

    public SendDataBean(byte[] body) {
        this.body = body;
    }

    @Override
    public byte[] parse() {
       return body;
    }
}