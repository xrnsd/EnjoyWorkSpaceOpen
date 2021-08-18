package com.kuyou.rc.protocol.item;

import android.util.Log;

import com.kuyou.rc.protocol.InstructionParserListener;
import com.kuyou.rc.protocol.base.SicBasic;

import java.util.Arrays;

import kuyou.common.bytes.ByteUtils;

/**
 * action :
 * <p>
 * remarks:  <br/>
 * author: wuguoxian <br/>
 * date: 21-8-9 <br/>
 * </p>
 */
public class SicTextMessage extends SicBasic {
    protected final String TAG = "com.kuyou.rc.protocol > SicTextMessage";

    private String mText;
    private String mTextType;

    @Override
    public String getTitle() {
        return "语音短信";
    }

    @Override
    public int getFlag() {
        return S2C_REQUEST_TEXT_MESSAGE;
    }

    @Override
    public void parse(byte[] data, InstructionParserListener listener) {
        byte[] bytes = getMsgContentAndParseMsgHeader(data);

        try {
            mTextType = ByteUtils.byteToBit(bytes[0]);

            mText = new String(Arrays.copyOfRange(bytes, 1, bytes.length), "GBK");

            Log.d(TAG, toString());
        } catch (Exception e) {
            if (null != listener)
                listener.onRemote2LocalExpandFail(e);
        }
        if (null != listener)
            listener.onRemote2LocalExpand(SicTextMessage.this);
    }

    @Override
    public byte[] getBody(final int config) {
        return null;
    }

    public String getText() {
        return mText;
    }

    public String getTextType() {
        return mTextType;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(1024);
        sb.append("mText = ").append(mText);
        sb.append("\nmTextType = ").append(mTextType);
        return sb.toString();
    }
}