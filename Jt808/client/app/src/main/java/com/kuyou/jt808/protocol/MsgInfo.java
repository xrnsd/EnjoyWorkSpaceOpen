package com.kuyou.jt808.protocol;

import android.app.TtsServiceManager;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.kuyou.jt808.Jt808Application;
import com.cuichen.jt808_sdk.sdk.jt808bean.JTT808Bean;
import com.cuichen.jt808_sdk.sdk.jt808utils.ByteUtil;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * action :
 * <p>
 * author: wuguoxian <br/>
 * date: 20-10-20 <br/>
 * <p>
 */
public abstract class MsgInfo {
    //====================== 服务器下发 =========================
    /**
     * action : 文本信息下发
     */
    public static final int MSG_ID_8300 = 0x8F03;
    /**
     * action : 应答照片上传
     */
    public static final int MSG_ID_8F01 = 0x8F01;
    /**
     * action : 拍照,上传
     */
    public static final int MSG_ID_8F02 = 0x8F02;
    /**
     * action : 音视频参数
     */
    public static final int MSG_ID_8F03 = 0x8F03;

    //====================== 终端上传 =========================

    /**
     * action : 照片上传
     */
    public static final int MSG_ID_0F01 = 0x0F01;
    /**
     * action : 请求音视频
     */
    public static final int MSG_ID_0F02 = 0x0F02;
    /**
     * action : 应答音视频
     */
    public static final int MSG_ID_0F03 = 0x0F03;

    // ========================  协议解析 ===============================================================

    /**
     * action : 成功
     */
    public static final int RESULT_CODE_SUCCESS = 1;

    /**
     * action : 失败
     */
    public static final int RESULT_CODE_FAIL = 0;

    protected static String TAG;
    private byte[] msgContent;

    public MsgInfo() {
        TAG = "123456_" + getClass().getSimpleName();
    }

    public boolean parse(byte[] bytes) {
        msgContent = parseMsgHeader(Arrays.copyOfRange(bytes, 0, bytes.length));
        Log.d(TAG, " parse >  bytes2Hex= " + ByteUtil.bytes2Hex(bytes));
        Log.d(TAG, " parse >  bytes2Heb= " + ByteUtil.bytes2Heb(bytes));
        return true;
    }

    public abstract String toString();

    protected byte[] getMsgContent() {
        return msgContent;
    }

    protected JTT808Bean mMsgHeader;

    private byte[] parseMsgHeader(byte[] bytes) {

        mMsgHeader = new JTT808Bean();
        byte[] msgId = Arrays.copyOfRange(bytes, 0, 2);
        byte[] msgBodyAttributes = Arrays.copyOfRange(bytes, 2, 4);
        byte[] phone = Arrays.copyOfRange(bytes, 4, 10);
        byte[] msgFlowNum = Arrays.copyOfRange(bytes, 10, 12);
        mMsgHeader.setMsgId(msgId);
        mMsgHeader.setMsgBodyAttributes(msgBodyAttributes);
        mMsgHeader.setPhoneNumber(phone);
        mMsgHeader.setMsgFlowNumber(msgFlowNum);

        return Arrays.copyOfRange(bytes, 12, bytes.length);
    }

    // =======================自定义事件处理 ==============================================================

    protected Map<Integer, onMsgHandler> mOnMsgHandlerMap = new HashMap<Integer, onMsgHandler>();
    protected Map<Integer, onMsgHandlerTts> mOnMsgHandlerTtsMap = new HashMap<Integer, onMsgHandlerTts>();
    protected Handler mHandlerMain = new Handler(Looper.getMainLooper());

    public static interface onMsgHandler {
        public void onHandler(int resultCode);
    }

    public static interface onMsgHandlerTts {
        public void onHandlerTts(String text);
    }

    protected void onHandler(int msgCode, int resultCode) {
        if (null != mOnMsgHandlerMap && mOnMsgHandlerMap.containsKey(msgCode)) {
            mHandlerMain.post(() -> mOnMsgHandlerMap.get(msgCode).onHandler(resultCode));
        }
    }

    protected TtsServiceManager getTtsServiceManager(){
        return Jt808Application.getInstance().getTtsServiceManager();
    }

    protected void onHandlerTts(int msgCode, String text) {
        if (null == mOnMsgHandlerTtsMap) {
            Log.e(TAG, "onHandlerTts fail > mOnMsgHandlerTtsMap is null");
            return;
        }
        if (!mOnMsgHandlerTtsMap.containsKey(msgCode)) {
            Log.w(TAG, "onHandlerTts fail > mOnMsgHandlerTtsMap not containsKey msgCode:" + String.format("0x%04x", msgCode));
            return;
        }
        Log.d(TAG, "onHandlerTts > text = " + text);
        mHandlerMain.post(() -> mOnMsgHandlerTtsMap.get(msgCode).onHandlerTts(text));
    }

    public void setMsgHandler(int msgCode, onMsgHandler handler) {
        mOnMsgHandlerMap.put(msgCode, handler);
    }

    public void setMsgHandler(int msgCode, onMsgHandlerTts handler) {
        mOnMsgHandlerTtsMap.put(msgCode, handler);

        for (Map.Entry<Integer, onMsgHandlerTts> entry : mOnMsgHandlerTtsMap.entrySet()) {
            Log.d(TAG, "Key = " + String.format("0x%04x", entry.getKey()) + ", onMsgHandlerTts = " + entry.getValue());
        }
    }
}
