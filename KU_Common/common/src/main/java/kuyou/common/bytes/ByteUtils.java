package kuyou.common.bytes;

import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.List;

/**
 * action :字符转化相关封装实现
 * <p>
 * remarks:  <br/>
 * author: wuguoxian <br/>
 * date: 21-07-16 <br/>
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
     * 多个数组合并一个
     *
     * @return
     */
    public static byte[] byteMergerAll(byte[]... bytes) {
        int allLength = 0;
        for (byte[] b : bytes) {
            if (null == b)
                continue;
            allLength += b.length;
        }
        byte[] allByte = new byte[allLength];
        int countLength = 0;
        for (byte[] b : bytes) {
            if (null == b)
                continue;
            System.arraycopy(b, 0, allByte, countLength, b.length);
            countLength += b.length;
        }
        return allByte;
    }

    /**
     * 多个数组合并一个
     *
     * @return
     */
    public static byte[] byteMergerAll(List<byte[]> bytes) {
        int allLength = 0;
        for (byte[] b : bytes) {
            if (null == b)
                continue;
            allLength += b.length;
        }
        byte[] allByte = new byte[allLength];
        int countLength = 0;
        for (byte[] b : bytes) {
            if (null == b)
                continue;
            System.arraycopy(b, 0, allByte, countLength, b.length);
            countLength += b.length;
        }
        return allByte;
    }

    /**
     * 将数据数据恢复成0
     */
    public static void resetBytes(byte[] bytes) {
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = 0;
        }
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

    /**
     * 计算校验和
     *
     * @param bytes
     * @param index 校验和结果所在的下标
     * @return 是否成功
     */
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
     * 计算CRC16 MOD BUS校验码
     *
     * @param bytes 源数据最后两位为校验码
     *              完整数据：32 00 00 4B 7B 4A 00 30 01 BD 78 5D 78 5D
     *              eg:32 00 00 4B 7B 4A 00 30 01 BD 78 5D 校验码 78 5D
     * @return
     */
    public static boolean checkCRC16(byte[] bytes) {
        byte[] data = Arrays.copyOfRange(bytes, 0, bytes.length - 2);
        byte[] code = Arrays.copyOfRange(bytes, bytes.length - 2, bytes.length);
        String checkCode = HexUtil.byte2HexStrNoSpace(code);
        String result = getCRC16(data);
        return checkCode.equalsIgnoreCase(result);
    }

    /**
     * 计算CRC16 MOD BUS校验码
     */
    public static String getCRC16(byte[] data) {
        int crc = 0x0000FFFF;
        int polynomial = 0X0000A001;
        for (byte b : data) {
            crc ^= ((int) b & 0x000000FF);
            for (int j = 0; j < 8; j++) {
                if ((crc & 0x00000001) != 0) {
                    crc >>= 1;
                    crc ^= polynomial;
                } else {
                    crc >>= 1;
                }
            }
        }
        //高低位转换，(不转换 高位在左低位在右)
        crc = ((crc & 0x0000FF00) >> 8) | ((crc & 0x000000FF) << 8);
        return Integer.toHexString(crc).toUpperCase();
    }

    /**
     * int转byte
     *
     * @param value int数字
     * @return byte 字节
     */
    public static byte int2Byte(int value) {
        return (byte) value;
    }

    /**
     * byte 转int
     *
     * @param value int数字
     * @return byte 字节
     */
    public static int byte2Int(byte value) {
        return value & 0xFF;
    }

    /**
     * 2字节bytes转int
     *
     * @param value
     * @return
     */
    public static int twoBytesToInteger(byte[] value) {
        int temp0 = value[0] & 0xFF;
        int temp1 = value[1] & 0xFF;
        return ((temp0 << 8) + temp1);
    }

//    /**
//     * 2字节bytes转int
//     *
//     * @return int
//     */
//    public static int bytes2Int(byte[] bytes) {
//        int a = ((bytes[0] & 0xf0) >> 4) * 4096;
//        int b = (bytes[0] & 0x0f) * 256;
//        int c = bytes[1] & 0xf0;
//        int d = bytes[1] & 0x0f;
//        return a + b + c + d;
//    }

    /**
     * 3字节bytes转int
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
     * 4字节bytes转int
     *
     * @param value
     * @return
     */
    public static int fourBytesToInteger(byte[] value) {
        int temp0 = value[0] & 0xFF;
        int temp1 = value[1] & 0xFF;
        int temp2 = value[2] & 0xFF;
        int temp3 = value[3] & 0xFF;
        return ((temp0 << 24) + (temp1 << 16) + (temp2 << 8) + temp3);
    }

    /**
     * 4字节bytes转int
     *
     * @return
     */
    public static int fourBytes2Int(byte[] bytes) {
        int mask = 0xff;
        int temp;
        int n = 0;
        for (byte b : bytes) {
            n <<= 8;
            temp = b & mask;
            n |= temp;
        }
        return n;
    }


    public static int bytes2Int(byte[] value) {
        if (null == value)
            return -1;
        switch (value.length) {
            case 1:
                return byte2Int(value[0]);
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

    /**
     * byte字节转Bit
     * bit位（0～8位）是从右往左数的 eg:10000011 (位0：1，位2：1，位3：0)
     *
     * @param b        字节
     * @param bitIndex 获取bit位的下标
     * @return bit
     */
    public static byte byteToBit(byte b, int bitIndex) {
        byte[] array = new byte[8];
        for (int i = 7; i >= 0; i--) {
            array[i] = (byte) (b & 1);
            b = (byte) (b >> 1);
        }
        //倒序取
        return array[8 - 1 - bitIndex];
    }

    /**
     * byte字节转Bit
     *
     * @param b 字节
     * @return bit
     */
    public static String byteToBit(byte byteVal) {
        return new StringBuffer(20)
                .append((byteVal >> 7) & 0x1)
                .append((byteVal >> 6) & 0x1)
                .append((byteVal >> 5) & 0x1)
                .append((byteVal >> 4) & 0x1)
                .append((byteVal >> 3) & 0x1)
                .append((byteVal >> 2) & 0x1)
                .append((byteVal >> 1) & 0x1)
                .append((byteVal >> 0) & 0x1)
                .toString();
    }

    /**
     * long转DWORD数据类型
     * 低位到高位
     */
    public static byte[] longToDword(long value) {
        byte[] data = new byte[4];
        for (int i = 0; i < data.length; i++) {
            data[i] = (byte) (value >> (8 * (3 - i)));
        }
        return data;
    }


    /**
     * DWORD无符号整型数据转换为java的long型
     */
    public static long dwordToLong(byte buf[], int index) {
        int firstByte = (0x000000FF & ((int) buf[index]));
        int secondByte = (0x000000FF & ((int) buf[index + 1]));
        int thirdByte = (0x000000FF & ((int) buf[index + 2]));
        int fourthByte = (0x000000FF & ((int) buf[index + 3]));
        return ((long) (firstByte | secondByte << 8 | thirdByte << 16 |
                fourthByte << 24)) & 0xFFFFFFFFL;
    }

    /**
     * 一个int转WORD
     *
     * @param value
     * @return
     */
    public static byte[] int2Word(int value) {
        return BitOperator.numToByteArray(value, 2);
    }

    /**
     * 一个int转DWORD
     *
     * @param value
     * @return
     */
    public static byte[] int2DWord(int value) {
        return BitOperator.numToByteArray(value, 4);
    }

    /**
     * 一个int[]模拟的2进制位列表转DWORD格式的byte[]
     *
     * @param value
     * @return
     */
    public static byte[] bits2DWord(int... values) {
        int count = values.length;
        if (count != 32) {
            Log.e(TAG, "bits2Bytes fail > The maximum number of parameters is 32 ");
            return new byte[]{0};
        }
        return bits2Bytes(values);
    }

    /**
     * 一个int[]模拟的2进制位列表转byte[]
     *
     * @param value
     * @return
     */
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
     * 2字节byte数组 转 short[WORD]
     */
    public static short bytes2Word(byte[] bytes) {
        return (short) bytes2Int(bytes);
    }

    /**
     * 4字节byte数组 转 int[DWORD]
     */
    public static int bytes2Dword(byte[] bytes) {
        return fourBytes2Int(bytes);
    }


    public static byte[] date2Bytes(String time) {
        time = time.trim();
        byte year = (byte) (Integer.parseInt(time.substring(0, 4)) - 2000);
        byte month = (byte) Integer.parseInt(time.substring(4, 6));
        byte day = (byte) Integer.parseInt(time.substring(6, 8));
        byte hour = (byte) Integer.parseInt(time.substring(8, 10));
        byte minute = (byte) Integer.parseInt(time.substring(10, 12));
        byte second = (byte) Integer.parseInt(time.substring(12, 14));
        return new byte[]{year, month, day, hour, minute, second};
    }

    /**
     * 将BCD码转成String
     *
     * @param b
     * @return
     */
    public static String bcd2Str(byte[] b) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < b.length; i++) {
            int h = ((b[i] & 0xff) >> 4) + 48;
            sb.append((char) h);
            int l = (b[i] & 0x0f) + 48;
            sb.append((char) l);
        }
        return sb.toString();
    }

    /**
     * 将String转成BCD码
     *
     * @param s
     * @return
     */
    public static byte[] str2Bcd(String s) {
        if (s.length() % 2 != 0) {
            s = "0" + s;
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        char[] cs = s.toCharArray();
        for (int i = 0; i < cs.length; i += 2) {
            int high = cs[i] - 48;
            int low = cs[i + 1] - 48;
            baos.write(high << 4 | low);
        }
        return baos.toByteArray();
    }

    /**
     * 获取字节指定位置的位的值
     *
     * @param s
     * @return
     */
    private static int byte2BitByPos(byte b, int pos) {
        return (b & (1 << pos)) > 0 ? 0 : 1;
    }


    /**
     * 字符转16进制字符串
     *
     * @param s
     * @return
     */
    public static String byte2Hex(Byte paramByte) {
        return String.format("%02x", new Object[]{paramByte}).toUpperCase();
    }


    /**
     * 字符数组转16进制字符串
     *
     * @param s
     * @return
     */
    public static String bytes2Hex(byte[] paramArrayOfByte) {
        StringBuilder localStringBuilder = new StringBuilder();
        int i = paramArrayOfByte.length;
        for (int j = 0; j < i; j++) {
            localStringBuilder.append(byte2Hex(Byte.valueOf(paramArrayOfByte[j])));
            localStringBuilder.append(" ");
        }
        return localStringBuilder.toString();
    }

    /**
     * 字符转2进制字符串
     *
     * @param s
     * @return
     */
    public static String byte2Heb(Byte paramByte) {
        return Integer.toBinaryString((paramByte & 0xFF) + 0x100).substring(1);
    }


    /**
     * 字符数组转2进制字符串
     *
     * @param s
     * @return
     */
    public static String bytes2Heb(byte[] paramArrayOfByte) {
        StringBuilder localStringBuilder = new StringBuilder();
        int i = paramArrayOfByte.length;
        for (int j = 0; j < i; j++) {
            localStringBuilder.append(byte2Heb(Byte.valueOf(paramArrayOfByte[j])));
            localStringBuilder.append(" ");
        }
        return localStringBuilder.toString();
    }

    public static byte[] hex2Bytes(String str) {
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
}
