package com.kuyou.rc.protocol.uwb.info;

import com.kuyou.rc.protocol.uwb.basic.IModuleInfoListener;
import com.kuyou.rc.protocol.uwb.basic.InfoUwb;

import java.util.Arrays;

import kuyou.common.bytes.ByteUtils;

/**
 * action :单项指令编解码器[UWB][获取ID]
 * <p>
 * remarks:  <br/>
 * author: wuguoxian <br/>
 * date: 21-4-1 <br/>
 * </p>
 */
public class InfoGetModuleId extends InfoUwb {
    private int mDevId = -1;

    @Override
    public String getTitle() {
        return "读取设备ID";
    }

    @Override
    public int getFlag() {
        return 0xB0;
    }

    @Override
    public int getCmdCode() {
        return CmdCode.GET_MODULE_ID;
    }

    @Override
    public void parse(byte[] data, IModuleInfoListener listener) {
        final int index = 4, length = 4;
        final byte[] idBytes = Arrays.copyOfRange(data, index, index + length);
        final int id = ByteUtils.bytes2Int(idBytes);
        mDevId = id;
        listener.onGetModuleId(mDevId);
    }

    @Override
    public byte[] getBody() {
        byte[] cmd = {
                //STX
                (byte) 0xFF, (byte) 0xFF,
                //RSCTK
                (byte) 0x00,
                //CMD
                (byte) 0xA0,
                //DATA
                //BCC
                (byte) 0x00,
                //ETX
                (byte) 0xFF
        };
        return addCheckByte(cmd);
    }

    public int getDevId() {
        return mDevId;
    }
}
