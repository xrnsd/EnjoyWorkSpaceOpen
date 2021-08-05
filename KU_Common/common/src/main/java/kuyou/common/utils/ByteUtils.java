package kuyou.common.utils;

import android.util.Log;

/**
 * action :
 * <p>
 * author: wuguoxian <br/>
 * date: 21-1-12 <br/>
 * <p>
 */
public class ByteUtils {
    protected static final String TAG = "kuyou.common.utils > ByteUtils";

    public static boolean isEmpty(byte[] array) {
        return null == array || array.length == 0;
    }

    public static String bytes2hex(byte[] buffer) {
        StringBuilder hexStrBuffer = new StringBuilder(buffer.length);
        for (int i = 0, length = buffer.length; i < length; i++) {
            hexStrBuffer.append(byte2hex(buffer[i]));
            if (i < length - 1)
                hexStrBuffer.append(" ");
        }
        return hexStrBuffer.toString();
    }

    public static String byte2hex(byte buffer) {
        return String.format("%02x", new Object[]{buffer}).toUpperCase();
        //return Integer.toHexString(buffer & 0xFF).toUpperCase();
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
        return fletcherChecksum(data, data.length);
    }

    public static byte fletcherChecksum(byte[] data, int size) {
        byte Xor = data[0];
        for (int index = 1; index < size; index++) {
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
    public static byte[] int16ToBytes(int a) {
        return new byte[]{
                (byte) ((a >> 8) & 0xFF),
                (byte) (a & 0xFF)
        };
    }

    /**
     * int 转 3 byte数组
     *
     * @param a int数字
     * @return 4 byte 数组
     */
    public static byte[] int24ToBytes(int a) {
        return new byte[]{
                (byte) ((a >> 16) & 0xFF),
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
    public static byte[] int32ToBytes(int a) {
        return new byte[]{
                (byte) ((a >> 24) & 0xFF),
                (byte) ((a >> 16) & 0xFF),
                (byte) ((a >> 8) & 0xFF),
                (byte) (a & 0xFF)
        };
    }

    public static int bytes2Int(byte[] value) {
        if (null == value)
            return -1;
        switch (value.length) {
            case 1:
                return oneByteToInteger(value[0]);
            case 2:
                return twoBytesToInteger(value);
            case 3:
                return threeBytesToInteger(value);
            case 4:
                return fourBytesToInteger(value);
            default:
                return -1;
        }
    }

    /**
     * 把一个byte转化位整形,通常为指令用
     *
     * @param value
     * @return
     */
    public static int oneByteToInteger(byte value) {
        return (int) value & 0xFF;
    }

    /**
     * 把一个2位的数组转化位整形
     *
     * @param value
     * @return
     */
    public static int twoBytesToInteger(byte[] value) {
        // if (value.length < 2) {
        // throw new Exception("Byte array too short!");
        // }
        int temp0 = value[0] & 0xFF;
        int temp1 = value[1] & 0xFF;
        return ((temp0 << 8) + temp1);
    }

    /**
     * 把一个3位的数组转化位整形
     *
     * @param value
     * @return
     */
    public static int threeBytesToInteger(byte[] value) {
        int temp0 = value[0] & 0xFF;
        int temp1 = value[1] & 0xFF;
        int temp2 = value[2] & 0xFF;
        return ((temp0 << 16) + (temp1 << 8) + temp2);
    }

    /**
     * 把一个4位的数组转化位整形,通常为指令用
     *
     * @param value
     * @return
     */
    public static int fourBytesToInteger(byte[] value) {
        // if (value.length < 4) {
        // throw new Exception("Byte array too short!");
        // }
        int temp0 = value[0] & 0xFF;
        int temp1 = value[1] & 0xFF;
        int temp2 = value[2] & 0xFF;
        int temp3 = value[3] & 0xFF;
        return ((temp0 << 24) + (temp1 << 16) + (temp2 << 8) + temp3);
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

    /**
     * 多个数组合并一个
     *
     * @return
     */
    public static byte[] byteMergerAll(byte[]... bytes) {
        int allLength = 0;
        for (byte[] b : bytes) {
            allLength += b.length;
        }
        byte[] allByte = new byte[allLength];
        int countLength = 0;
        for (byte[] b : bytes) {
            System.arraycopy(b, 0, allByte, countLength, b.length);
            countLength += b.length;
        }
        return allByte;
    }
}
