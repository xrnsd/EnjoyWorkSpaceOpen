package com.kuyou.jt808.info;

import android.os.Process;
import android.util.Log;

import kuyou.common.ku09.bytes.ByteUtils;
import kuyou.sdk.jt808.base.jt808coding.JTT808Coding;
import kuyou.sdk.jt808.utils.Base64Util;

/**
 * action :鉴权消息
 * <p>
 * author: wuguoxian <br/>
 * date: 20-10-29 <br/>
 * <p>
 */
public class AuthenticationInfo extends MsgInfo {

    private static AuthenticationInfo sMain;

    private AuthenticationInfo() {
        super();
    }

    public static AuthenticationInfo getInstance() {
        Process.setThreadPriority(Process.THREAD_PRIORITY_DISPLAY);
        if (null == sMain) {
            sMain = new AuthenticationInfo();
        }
        return sMain;
    }

    public byte[] getAuthenticationMsgBytes() {
        byte[] authCode = Base64Util.encrypt(getConfig().getDevId());
        byte[] authCodeMsg = ByteUtils.byteMergerAll(new byte[]{ByteUtils.int2Byte(authCode.length)}, authCode, getItemAddition());
        //Log.i(TAG, kuyou.common.utils.ByteUtils.bytes2hex(JTT808Coding.generate808(0x0102, getConfig(), authCodeMsg)));
        Log.d(TAG, toString());
        return JTT808Coding.generate808(0x0102, getConfig(), authCodeMsg);
    }

//    public byte[] getAuthenticationMsgBytes() {
//        byte[] authCode = Base64Util.encrypt(getConfig().getDevId());
//        return JTT808Coding.generate808(0x0102, getConfig(), authCode);
//    }

    protected byte[] getItemAddition() {
        byte[] uwbIdType = new byte[2]; //UWB模块ID
        byte[] uwbIdBytes = ByteUtils.int2Bytes(getConfig().getUwbId());
        uwbIdType[0] = (byte) (0xE1);
        uwbIdType[1] = ByteUtils.int2Byte(uwbIdBytes.length);
        return ByteUtils.byteMergerAll(uwbIdType, uwbIdBytes);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(1024);
        sb.append("\nAuthenticationInfo: ");
        sb.append("\ndevId = ").append(getConfig().getDevId());
        sb.append("\nuwbId = ").append(getConfig().getUwbId());
        return sb.toString();
    }
}
