package com.kuyou.voicecontrol.protocol;

import android.util.Log;

import kuyou.common.ku09.bytes.BitOperator;

import java.util.Arrays;

import kuyou.common.protocol.Checker;
import kuyou.common.utils.ByteUtils;
import kuyou.common.utils.CRC16;

/**
 * action :
 * <p>
 * remarks:  <br/>
 * author: wuguoxian <br/>
 * date: 21-1-27 <br/>
 * </p>
 */
public class CheckerVoice extends Checker {
    private final static byte[] MSG_HEADER = {(byte) 0x75, (byte) 0x41, (byte) 0x72, (byte) 0x54, (byte) 0x63, (byte) 0x50, (byte) 0x5E};

    private CheckerVoice() {
    }

    private static CheckerVoice sMain;

    public static CheckerVoice getInstance() {
        if (null == sMain) {
            sMain = new CheckerVoice();
        }
        return sMain;
    }

    byte[] mCheckBytes = new byte[2];

    @Override
    public int getMsgLengthMini() {
        return 6;
    }

    @Override
    protected int getMsgHeadFlag() {
        return 0x75;
    }

    @Override
    protected int getMsgHeadLength() {
        return 16;
    }

    @Override
    protected int getMsgBodyLength(byte[] data) {
        if (data.length < getMsgHeadLength())
            return -1;
        return BitOperator.twoBytesToInteger(Arrays.copyOfRange(data, 12, 14));
    }

    @Override
    protected boolean check(byte[] data) {
        if (data.length < getMsgHeadLength()) {
            return false;
        }
        try {
            final int checkSum = BitOperator.twoBytesToInteger(Arrays.copyOfRange(data, 10, 12));
            mCheckBytes[0] = data[10];
            mCheckBytes[1] = data[11];
            data[10] = 0x00;
            data[11] = 0x00;
            if (checkSum == CRC16.CRC16_XMODEM(data)) {
                data[10] = mCheckBytes[0];
                data[11] = mCheckBytes[1];
                return true;
            }
        } catch (Exception e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }
        return false;
    }

    private byte[] getMsgByHeader(byte[] msg) {
        Log.d(TAG, "getMsgByHeader > msg = " + ByteUtils.bytes2hex(msg));
        byte flag = (byte) 0x75;
        int index2 = 0, count2 = MSG_HEADER.length;
        boolean isExits = true;

        try {
            for (int index = 0, count = msg.length; index < count; index++) {
                if (msg[index] != flag) {
                    continue;
                }
                isExits = true;
                for (index2 = 0; index2 < count2; index2++) {
                    if (MSG_HEADER[index2] != msg[index + index2]) {
                        isExits = false;
                        break;
                    }
                }
                if (isExits) {
                    msg = Arrays.copyOfRange(msg, index, count + 1 - index);
                    if (null == msg || msg.length < 0)
                        mCheckCache = MSG_HEADER;
                    return msg;
                }
            }
        } catch (Exception e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }
        return msg;
    }

    @Override
    protected byte[] handler(byte[] data) {
        Log.d(TAG, "handler > " + ByteUtils.bytes2hex(data));
        if (null == data) {
            Log.d(TAG, "handler > checksum fail: data is null");
            return null;
        }
        if (data.length < getMsgLengthMini()) {
            Log.d(TAG, "handler > data.length is invalid");
            return null;
        }

        if (ByteUtils.byte2Int(data[0]) == getMsgHeadFlag()) {
            final int validLength = getMsgBodyLength(data);
            if (-1 == validLength) {
                mCheckCache = data;
                Log.d(TAG, "handler > checksum fail: validLength == -1");
                return null;
            }
            if (validLength < data.length - getMsgHeadLength()) { //出现两条指令拼接
                int length = validLength + getMsgHeadLength();
                mCheckCache = Arrays.copyOfRange(data, length, data.length);

                if (null == mCheckCache
                        || mCheckCache.length <= 0
                        || ByteUtils.byte2Int(mCheckCache[0]) != getMsgHeadFlag()) {
                    mCheckCache = null;
                }
                return handler(Arrays.copyOfRange(data, 0, length));
            }
            if (validLength == data.length - getMsgHeadLength()) {
                if (!check(data)) {
                    Log.d(TAG, "handler > checksum fail: data = " + ByteUtils.bytes2hex(data));
                    return null;
                }
                Log.d(TAG, "handler > data = " + ByteUtils.bytes2hex(data));
                return data;
            }
            mCheckCache = data;
        } else if (null != mCheckCache) {
            byte[] dataNew = new byte[mCheckCache.length + data.length];
            System.arraycopy(mCheckCache, 0, dataNew, 0, mCheckCache.length);
            System.arraycopy(data, 0, dataNew, mCheckCache.length, data.length);
            return handler(dataNew);
        }
        return null;
    }
}
