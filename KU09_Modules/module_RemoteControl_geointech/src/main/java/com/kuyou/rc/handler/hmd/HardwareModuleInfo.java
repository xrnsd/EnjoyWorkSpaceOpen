package com.kuyou.rc.handler.hmd;

import kuyou.common.bytes.ByteUtils;

/**
 * action :
 * <p>
 * remarks:  <br/>
 * author: wuguoxian <br/>
 * date: 21-9-9 <br/>
 * </p>
 */
public class HardwareModuleInfo {
    private int mHMTypeId = -1;
    private int mHMStatusId = -1;

    protected int getHMTypeId() {
        return mHMTypeId;
    }

    public HardwareModuleInfo setHMTypeId(int HMTypeId) {
        mHMTypeId = HMTypeId;
        return HardwareModuleInfo.this;
    }

    protected int getHMStatusId() {
        return mHMStatusId;
    }

    public HardwareModuleInfo setHMStatusId(int HMStatusId) {
        mHMStatusId = HMStatusId;
        return HardwareModuleInfo.this;
    }

    public byte[] getInfoBody() {
        return new byte[]{ByteUtils.int2Byte(getHMTypeId()), ByteUtils.int2Byte(getHMStatusId())};
    }
}
