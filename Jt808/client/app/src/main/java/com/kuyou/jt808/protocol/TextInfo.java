package com.kuyou.jt808.protocol;

import android.app.Activity;
import android.util.Log;

import com.cuichen.jt808_sdk.sdk.jt808bean.JTT808Bean;
import com.cuichen.jt808_sdk.sdk.jt808utils.ByteUtil;

import java.util.Arrays;

/**
 * action :0x8300,文本下发指令解析
 * <p>
 * author: wuguoxian <br/>
 * date: 20-10-19 <br/>
 * <p>
 */
public class TextInfo extends MsgInfo {
    private String mText;
    private String mTextType;

    public TextInfo() {
        super();
    }

    @Override
    public boolean parse(byte[] bytes) {
        super.parse(bytes);
        bytes = getMsgContent();
        try {
            mTextType = ByteUtil.byteToBit(bytes[0]);

            mText = new String(Arrays.copyOfRange(bytes, 1, bytes.length), "GBK");

            Log.d(TAG, toString());

            //if (isTTS()) {
                Log.d(TAG, " isTTS >  onHandlerTts ");
                onHandlerTts(MSG_ID_8300, getText());
            //}
            return true;
        } catch (Exception e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }

        Log.d(TAG, toString());
        return false;
    }

    public void setText(String val) {
        mText = val;
    }

    public String getText() {
        return mText;
    }

    public void setTextType(String val) {
        mTextType = val;
    }

    public String getTextType() {
        return mTextType;
    }

    public boolean isTTS() {
        return null != getTextType()
                && getTextType().length() >= 4
                && getTextType().substring(3, 4).equals("1");
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(1024);
        sb.append("mText = ").append(mText);
        sb.append("\nmTextType = ").append(mTextType);
        return sb.toString();
    }
}
