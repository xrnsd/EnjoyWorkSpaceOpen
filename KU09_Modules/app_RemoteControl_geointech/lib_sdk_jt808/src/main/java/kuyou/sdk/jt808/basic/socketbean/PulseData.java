package kuyou.sdk.jt808.basic.socketbean;

import kuyou.sdk.jt808.oksocket.core.iocore.interfaces.IPulseSendable;
import kuyou.sdk.jt808.basic.RemoteControlDeviceConfig;
import kuyou.sdk.jt808.basic.interfaces.SocketPulseListener;
import kuyou.sdk.jt808.basic.jt808coding.JT808Directive;

public class PulseData implements IPulseSendable {

    private RemoteControlDeviceConfig mConfig;

    public PulseData() {
    }

    private SocketPulseListener mSocketPulseListener;

    public PulseData(SocketPulseListener listener, RemoteControlDeviceConfig config) {
        mSocketPulseListener = listener;
        mConfig = config;
    }

    @Override
    public byte[] parse() {
        byte[] body = JT808Directive.heartPkg(mConfig);
        if (mSocketPulseListener != null)
            mSocketPulseListener.parse(body);
        return body;
    }
}