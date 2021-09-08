package com.kuyou.rc.protocol.jt808extend.item;

import android.util.Log;

import com.kuyou.rc.protocol.jt808extend.basic.InstructionParserListener;
import com.kuyou.rc.protocol.jt808extend.basic.SicBasic;

import kuyou.common.bytes.ByteUtils;
import kuyou.common.ku09.event.rc.basic.EventRemoteControl;
import kuyou.sdk.jt808.utils.Base64Util;

/**
 * action :JT808扩展的单项指令编解码器[鉴权，设备配置信息]
 * <p>
 * remarks:  <br/>
 * author: wuguoxian <br/>
 * date: 21-8-9 <br/>
 * </p>
 */
public class SicAuthentication extends SicBasic {
    protected final String TAG = "com.kuyou.rc.protocol.jt808extend.item > SicAuthentication";

    @Override
    public String getTitle() {
        return "设备鉴权_设备软硬件配置";
    }

    @Override
    public int getFlag() {
        return -1;//JT808ExtensionProtocol.S2C_RESULT_AUTHENTICATION_REPLY;
    }

    @Override
    public boolean isMatchEventCode(int eventCode) {
        return eventCode == EventRemoteControl.AUTHENTICATION_REQUEST;
    }

    @Override
    public void parse(byte[] data, InstructionParserListener listener) {
        super.parse(data,listener);
    }

    @Override
    public byte[] getBody(int config) {
        byte[] authCode = Base64Util.encrypt(getDeviceConfig().getDevId());
        byte[] authCodeMsg = ByteUtils.byteMergerAll(
                new byte[]{ByteUtils.int2Byte(authCode.length)}, authCode, getItemAddition());
        Log.d(TAG, toString());
        return getPackToJt808(0x0102, authCodeMsg);
    }

    protected byte[] getItemAddition() {
        //UWB模块ID
        byte[] uwbIdType = new byte[2];
        //byte[] uwbIdBytes = ByteUtils.int32ToBytes(getConfig().getUwbId());//协议 V20210406
        byte[] uwbIdBytes = getDeviceConfig().getUwbId().getBytes();//协议 V20210723
        uwbIdType[0] = (byte) (0xE1);
        uwbIdType[1] = ByteUtils.int2Byte(uwbIdBytes.length);

        //peergine多端视频服务SDK配置信息：采集端ID
        byte[] pceiType = null, pceiBytes = null;
        if (null != getDeviceConfig().getCollectingEndId()) {
            pceiType = new byte[2];
            pceiBytes = getDeviceConfig().getCollectingEndId().getBytes();//协议 V20210723
            pceiType[0] = (byte) (0xE2);
            pceiType[1] = ByteUtils.int2Byte(pceiBytes.length);
        } else {
            Log.e(TAG, "getItemAddition > process fail : peergine 多端视频服务SDK的采集端ID为空");
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
        sb.append("\ndevId = ").append(getDeviceConfig().getDevId());
        sb.append("\nuwbId = ").append(getDeviceConfig().getUwbId());
        sb.append("\npeergine 多端视频服务SDK的采集端ID = ").append(getDeviceConfig().getCollectingEndId());
        return sb.toString();
    }
    
    
}
