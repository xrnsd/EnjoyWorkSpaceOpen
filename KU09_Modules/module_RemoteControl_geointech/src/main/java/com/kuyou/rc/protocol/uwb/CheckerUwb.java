package com.kuyou.rc.protocol.uwb;

import kuyou.common.protocol.Checker;

/**
 * action :协议校验器[安全帽的UWB模块]
 * <p>
 * remarks:  <br/>
 * author: wuguoxian <br/>
 * date: 21-4-1 <br/>
 * </p>
 */
public class CheckerUwb extends Checker {

    private static CheckerUwb sMain;

    private CheckerUwb() {

    }

    public static CheckerUwb getInstance() {
        if (null == sMain) {
            sMain = new CheckerUwb();
        }
        return sMain;
    }

    @Override
    public int getMsgLengthMini() {
        return 2;
    }

    @Override
    protected int getMsgHeadFlag() {
        return 0xFF;
    }

    @Override
    protected int getMsgHeadLength() {
        return 3;
    }

    @Override
    protected int getMsgBodyLength(byte[] data) {
        if (data[0] == (byte) 0xFF
                && data[1] == (byte) 0xFF
                && data[data.length - 1] == (byte) 0xFF) {
            return data.length - 3;
        }
        return -1;
    }

    @Override
    protected boolean check(byte[] data) {
        return data[0] == (byte) 0xFF && data[data.length - 1] == (byte) 0xFF;
    }
}
