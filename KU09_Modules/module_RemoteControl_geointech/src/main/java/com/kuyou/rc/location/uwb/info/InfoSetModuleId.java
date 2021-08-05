package com.kuyou.rc.location.uwb.info;

import android.util.Log;

import com.kuyou.rc.location.uwb.base.IModuleInfoListener;
import com.kuyou.rc.location.uwb.base.InfoUwb;

import java.util.Arrays;

import kuyou.common.bytes.ByteUtils;


/**
 * action :
 * <p>
 * remarks:  <br/>
 * author: wuguoxian <br/>
 * date: 21-4-1 <br/>
 * </p>
 */
public class InfoSetModuleId extends InfoUwb {
    private int mDevId = -1;

    @Override
    public String geTitle() {
        return "设置设备ID";
    }

    @Override
    public int getFlag() {
        return 0xB1;
    }

    @Override
    public int getCmdCode() {
        return CmdCode.SET_MODULE_ID;
    }

    @Override
    public void parse(byte[] data, IModuleInfoListener listener) {
        final int index = 4, length = 4;
        final byte[] idBytes = Arrays.copyOfRange(data, index, index + length);
        final int id = ByteUtils.bytes2Int(idBytes);
        listener.onSetModuleIdFinish(id, id == mDevId);
        if (id != mDevId) {
            Log.e(TAG, new StringBuilder("parse > set module id process fail : id = ").append(id)
                    .append("mDevId = ").append(mDevId)
                    .toString());
        }
    }

    @Override
    public byte[] getBody() {
        return getBody(mDevId);
    }

    public byte[] getBody(int devId) {
        setDevId(devId);
        byte[] devIdBytes = ByteUtils.int32ToBytes(devId);
        Log.d(TAG, "setDevId > devId = " + ByteUtils.bytes2hex(devIdBytes));
        byte[] cmd = {
                //STX
                (byte) 0xFF, (byte) 0xFF,
                //RSCTK
                (byte) 0x00,
                //CMD
                (byte) 0xA1,
                //DATA
                devIdBytes[0], devIdBytes[1], devIdBytes[2], devIdBytes[3],
                //BCC
                (byte) 0x00,
                //ETX
                (byte) 0xFF
        };
        cmd = addCheckByte(cmd);
        return cmd;
    }

    public InfoSetModuleId setDevId(int id) {
        mDevId = id;
        return InfoSetModuleId.this;
    }
}
