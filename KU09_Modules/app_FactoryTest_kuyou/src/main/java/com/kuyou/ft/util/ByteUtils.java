package com.kuyou.ft.util;

import android.util.Log;

/**
 * action :字符转换工具包
 * <p>
 * author: wuguoxian <br/>
 * date: 21-1-12 <br/>
 * <p>
 */
public class ByteUtils {
    protected final static String TAG = "com.kuyou.ft.util > ByteUtils";

    public static boolean isEmpty(byte[] array) {
        return null == array || array.length == 0;
    }

    private static StringBuilder SByte2hexCache;

    public static String byte2hex(byte[] buffer) {
        SByte2hexCache = new StringBuilder(buffer.length);
        for (int i = 0, length = buffer.length; i < length; i++) {
            SByte2hexCache.append(byte2hex(buffer[i]));
            if (i < length - 1)
                SByte2hexCache.append(" ");
        }
        return SByte2hexCache.toString();
    }

    public static String byte2hex(byte buffer) {
        return String.format("%02x", new Object[]{buffer}).toUpperCase();
    }

    /**
     * byte 转int字节
     *
     * @param value int数字
     * @return byte 字节
     */
    public static int byte2Int(byte value) {
        return value & 0xFF;
    }

    public static String string2HexString(String s) {
        String r = bytes2HexString(string2Bytes(s));
        return r;
    }

    /*
     * 字符串转字节数组
     */
    public static byte[] string2Bytes(String s) {
        byte[] r = s.getBytes();
        return r;
    }

    /*
     * 字节数组转16进制字符串
     */
    public static String bytes2HexString(byte[] b) {
        String r = "";

        for (int i = 0; i < b.length; i++) {
            String hex = Integer.toHexString(b[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            r += hex.toUpperCase();
        }

        return r;
    }

    public static byte[] hextoBytes(String str) {
        if (str == null || str.trim().equals("")) {
            return new byte[0];
        }
        byte[] bytes = new byte[str.length() / 2];
        for (int i = 0; i < str.length() / 2; i++) {
            String subStr = str.substring(i * 2, i * 2 + 2);
            bytes[i] = (byte) Integer.parseInt(subStr, 16);
        }
        return bytes;
    }

    public static byte fletcherChecksum(byte[] data) {
        byte Xor = data[0];
        for (int index = 1, length = data.length; index < length - 1; index++) {
            Xor = (byte) (Xor ^ data[index]);
        }
        return (byte) (0xFF & Xor);
    }

    public static boolean checkSum(byte[] bytes, int index) {
        if (index > bytes.length - 1) {
            return false;
        }
        byte right = bytes[index];
        int plus = 0;
        for (int i = 0; i < bytes.length; i++) {
            if (index != i) {
                plus += bytes[i];
            }
        }
        return int2Byte(plus) == right;
    }

    /**
     * int 转byte字节
     *
     * @param value int数字
     * @return byte 字节
     */
    public static byte int2Byte(int value) {
        return (byte) value;
    }

    /**
     * int 转 2 byte数组
     *
     * @param a int数字
     * @return 2 byte 数组
     */
    public static byte[] int2Bytes(int a) {
        return new byte[]{
                (byte) ((a >> 8) & 0xFF),
                (byte) (a & 0xFF)
        };
    }

    /**
     * int 转 4 byte数组
     *
     * @param a int数字
     * @return 4 byte 数组
     */
    public static byte[] int2BytesLong(int a) {
        return new byte[]{
                (byte) ((a >> 24) & 0xFF),
                (byte) ((a >> 16) & 0xFF),
                (byte) ((a >> 8) & 0xFF),
                (byte) (a & 0xFF)
        };
    }

    public static byte[] bits2Bytes(int... values) {
        final int flag = 8;
        int count = values.length;
        if (0 != count % flag) {
            Log.e(TAG, "bits2Bytes fail > parameter length is not a multiple of " + flag);
            return new byte[]{0};
        }
        byte[] bytes = new byte[count / flag];
        StringBuilder sb = new StringBuilder();
        int index = 0, byteIndex = 0, val;
        for (int i = values.length - 1; i >= 0; i--) {//二进制高低位刚好和数组相反,得倒过来
            val = values[i];
            if (0 != val && 1 != val) {
                Log.e(TAG, "bits2Bytes fail > can only be 0 or 1 ,Invalid parameter = " + val);
                return new byte[]{0};
            }
            if (index >= flag) {
                bytes[byteIndex] = Integer.valueOf(sb.toString(), 2).byteValue();
                sb = new StringBuilder();
                byteIndex += 1;
                index = 0;
            }
            sb.append(val);
            index += 1;
            count -= 1;
            if (count <= 0) {
                bytes[byteIndex] = Integer.valueOf(sb.toString(), 2).byteValue();
            }
        }
        return bytes;
    }
}
