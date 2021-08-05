package kuyou.sdk.jt808.base.socketbean;

import kuyou.sdk.jt808.oksocket.core.iocore.interfaces.IPulseSendable;
import kuyou.sdk.jt808.base.Jt808Config;
import kuyou.sdk.jt808.base.interfaces.SocketPulseListener;
import kuyou.sdk.jt808.base.jt808coding.JT808Directive;

public class PulseData implements IPulseSendable {

    private Jt808Config mConfig;

    public PulseData() {
    }

    private SocketPulseListener mSocketPulseListener;

    public PulseData(SocketPulseListener listener, Jt808Config config) {
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