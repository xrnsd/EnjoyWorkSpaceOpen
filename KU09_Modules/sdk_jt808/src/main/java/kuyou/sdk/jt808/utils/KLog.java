package kuyou.sdk.jt808.utils;

import android.util.Log;

import kuyou.sdk.jt808.base.jt808coding.JTT808Coding;
import kuyou.common.ku09.bytes.HexUtil;

import java.util.Arrays;

public class KLog {
    public static void d(String msg) {
        d("jt808_sdk", msg);
    }

    public static void d(String tag, String msg) {
        Log.i(tag, msg);
    }

    public static void i(byte[] bytes) {
        Log.i("jt808_sdk", new StringBuilder("↓\nReadHeadBean(消息头实例化):").append(JTT808Coding.resolve808ToHeader(bytes))
                .append("\nReadHead(消息头):").append(HexUtil.byte2HexStr(Arrays.copyOfRange(bytes, 0, 12)))
                .append("\nReadBody(消息体):").append(HexUtil.byte2HexStr(Arrays.copyOfRange(bytes, 12, bytes.length)))
                .toString());
    }

}