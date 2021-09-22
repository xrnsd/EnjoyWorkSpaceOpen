package com.kuyou.rc.basic.jt808extend.item;

import com.kuyou.rc.basic.jt808extend.InstructionParserListener;

import kuyou.common.bytes.ByteUtils;

/**
 * action :JT808扩展的单项指令编解码器[语音短信]
 * <p>
 * remarks:  <br/>
 * author: wuguoxian <br/>
 * date: 21-8-9 <br/>
 * </p>
 */
public class SicGeneralReply extends SicBasic {
    protected final static String TAG = "com.kuyou.rc.basic.jt808extend.item > SicGeneralReply";

    private int mMsgId;
    private int mResultCode;

    public static interface ResultCode {
        public final static int SUCCESS = 0;
        public final static int FAIL = 1;
        public final static int INVALID = 2;
        public final static int NOT_SUPPORT = 3;
    }

    @Override
    public String getTitle() {
        return "通用应答";
    }

    @Override
    public int getFlag() {
        return C2S_REPLY;
    }

    @Override
    public void parse(byte[] data, InstructionParserListener listener) {

    }

    @Override
    public byte[] getBody(final int config) {
        byte[] flowNumber = ByteUtils.longToDword(getFlowNumber());
        byte[] msgId = ByteUtils.int2Word(getMsgId());
        byte result = ByteUtils.int2Byte(getResultCode());


        byte[] body = ByteUtils.byteMergerAll(
                flowNumber,
                msgId,
                new byte[]{result}
        );
        return getPackToJt808(C2S_REPLY, body);
    }

    protected int getMsgId() {
        return mMsgId;
    }

    public void setMsgId(int msgId) {
        mMsgId = msgId;
    }

    protected int getResultCode() {
        return mResultCode;
    }

    public void setResultCode(int resultCode) {
        mResultCode = resultCode;
    }
}
