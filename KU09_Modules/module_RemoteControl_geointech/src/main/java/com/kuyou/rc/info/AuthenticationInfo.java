package com.kuyou.rc.info;

import android.os.Process;
import android.util.Log;

import kuyou.common.bytes.ByteUtils;
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
    protected final String TAG = "com.kuyou.rc.info > AuthenticationInfo";

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
        byte[] authCodeMsg = ByteUtils.byteMergerAll(
                new byte[]{ByteUtils.int2Byte(authCode.length)}, authCode, getItemAddition());
        Log.d(TAG, toString());
        return JTT808Coding.generate808(0x0102, getConfig(), authCodeMsg);
    }

    protected byte[] getItemAddition() {
        //UWB模块ID
        byte[] uwbIdType = new byte[2];
        //byte[] uwbIdBytes = ByteUtils.int32ToBytes(getConfig().getUwbId());//协议 V20210406
        byte[] uwbIdBytes = getConfig().getUwbId().getBytes();//协议 V20210723
        uwbIdType[0] = (byte) (0xE1);
        uwbIdType[1] = ByteUtils.int2Byte(uwbIdBytes.length);

        //peergine多端视频服务SDK配置信息：采集端ID
        byte[] pceiType = null, pceiBytes = null;
        if (null != getConfig().getCollectingEndId()) {
            pceiType = new byte[2];
            pceiBytes = getConfig().getCollectingEndId().getBytes();//协议 V20210723
            pceiType[0] = (byte) (0xE2);
            pceiType[1] = ByteUtils.int2Byte(pceiBytes.length);
        } else {
            Log.e(TAG, "getItemAddition > process fail : peergine多端视频服务SDK的采集端ID为空");
        }

        return ByteUtils.byteMergerAll(uwbIdType, uwbIdBytes, pceiType, pceiBytes);

//        byte[] hardwareModuleStatus = new byte[2];
//        byte[] hardwareModuleStatusBytes = getConfig().getCollectingEndId().getBytes();//协议 V20210723
//        hardwareModuleStatus[0] = (byte) (0xE3);
//        hardwareModuleStatus[1] = ByteUtils.int2Byte(hardwareModuleStatus.length);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(1024);
        sb.append("\nAuthenticationInfo: ");
        sb.append("\ndevId = ").append(getConfig().getDevId());
        sb.append("\nuwbId = ").append(getConfig().getUwbId());
        sb.append("\npeergine多端视频服务SDK的采集端ID = ").append(getConfig().getCollectingEndId());
        return sb.toString();
    }
}
