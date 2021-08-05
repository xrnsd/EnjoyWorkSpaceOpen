package com.kuyou.voicecontrol.protocol.info;

import android.util.Log;

import com.kuyou.voicecontrol.protocol.CodecVoice;
import com.kuyou.voicecontrol.protocol.base.IOnParseListener;

import java.util.Arrays;

import kuyou.common.protocol.Info;

/**
 * action :
 * <p>
 * remarks:  <br/>
 * author: wuguoxian <br/>
 * date: 21-1-26 <br/>
 * </p>
 */
public abstract class InfoBase extends Info<IOnParseListener> {

    public static interface IParseFinishListener {
        public void onParseFinish(String text);
    }

    protected final static String HEAD_UAR_TCP = "uArTcP";

    protected byte mSeq = 0;
    protected byte mCtrl = 0;
    protected byte[] mCmd = new byte[2];
    protected byte[] mCrc16 = new byte[2];
    protected int mLen = 0;
    protected int mCsLen = 0;
    protected String mPayload = null;
    protected String[] mCmdContexts;

    private IParseFinishListener mParseFinishListener;

    public IParseFinishListener getParseFinishListener() {
        return mParseFinishListener;
    }

    public void setParseFinishListener(IParseFinishListener parseFinishListener) {
        mParseFinishListener = parseFinishListener;
    }

    public byte getSeq() {
        return mSeq;
    }

    public void setSeq(byte seq) {
        mSeq = seq;
    }

    public byte getCtrl() {
        return mCtrl;
    }

    public void setCtrl(byte ctrl) {
        mCtrl = ctrl;
    }

    public byte[] getCmd() {
        return mCmd;
    }

    public void setCmd(byte[] cmd) {
        mCmd = cmd;
    }

    public byte[] getCrc16() {
        return mCrc16;
    }

    public void setCrc16(byte[] crc16) {
        mCrc16 = crc16;
    }

    public int getLen() {
        return mLen;
    }

    public void setLen(int len) {
        mLen = len;
    }

    public int getCsLen() {
        return mCsLen;
    }

    public void setCsLen(int csLen) {
        mCsLen = csLen;
    }

    public String getPayload() {
        return mPayload;
    }

    public void setPayload(String payload) {
        mPayload = payload;
    }

    @Override
    public int getFlag() {
        return 0x03;
    }

    @Override
    public boolean isSuccess() {
        return true;
    }

    @Override
    public byte[] getBody() {
        throw new RuntimeException("此接口已禁用");
    }

    @Override
    public void parse(byte[] bytes, IOnParseListener listener) {
        try {
            setSeq(bytes[6]);
            setCtrl(bytes[7]);
            int indexFlag = 8;
            setCmd(Arrays.copyOfRange(bytes, indexFlag, indexFlag + getCmd().length));

            indexFlag = 10;
            setCrc16(Arrays.copyOfRange(bytes, indexFlag, indexFlag + getCrc16().length));

            setLen(CodecVoice.getLenByBytes(bytes));

            setCsLen(CodecVoice.getCsLenByBytes(bytes));

            setPayload(CodecVoice.getPayloadByBytes(bytes));
        } catch (Exception e) {
            Log.e(TAG, Log.getStackTraceString(e));
            return;
        }
        perform(listener);
    }

    @Override
    public void reset() {
        mSeq = 0;
        mCtrl = 0;
        mCmd = new byte[2];
        mCrc16 = new byte[2];
        mLen = 0;
        mCsLen = 0;
        mPayload = null;
    }

    public boolean perform(IOnParseListener listener) {
        if (null == geTitle() || geTitle().length() <= 0) {
            return false;
        }
        getParseFinishListener().onParseFinish(geTitle());
        return false;
    }

    public String[] getCmdContext() {
        if (null == mCmdContexts)
            mCmdContexts = getCmdContexts();
        return mCmdContexts;
    }

    public abstract String getPayloadDef();

    protected abstract String[] getCmdContexts();
}
