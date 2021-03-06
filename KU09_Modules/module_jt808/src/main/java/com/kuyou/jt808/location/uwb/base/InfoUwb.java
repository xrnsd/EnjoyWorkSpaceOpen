package com.kuyou.jt808.location.uwb.base;

import java.util.Arrays;

import kuyou.common.protocol.Info;
import kuyou.common.utils.ByteUtils;

/**
 * action :
 * <p>
 * remarks:  <br/>
 * author: wuguoxian <br/>
 * date: 21-4-1 <br/>
 * </p>
 */
public abstract class InfoUwb extends Info<IModuleInfoListener> {
    protected final String TAG = "kuyou.sdk.jt808 > " + this.getClass().getSimpleName();

    @Override
    public boolean isSuccess() {
        return false;
    }

    @Override
    public void reset() {

    }

    protected byte[] addCheckByte(byte[] cmd) {
        final int index = 2, length = cmd.length - 4;
        cmd[cmd.length - 2] = ByteUtils.fletcherChecksum(Arrays.copyOfRange(cmd, index, index + length));
        return cmd;
    }

    public static interface CmdCode{
        public final static int GET_MODULE_ID = 0;
        public final static int SET_MODULE_ID = 1;
    }
}
