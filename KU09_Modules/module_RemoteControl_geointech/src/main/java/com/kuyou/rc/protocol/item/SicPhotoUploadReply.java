package com.kuyou.rc.protocol.item;

import android.util.Log;

import com.kuyou.rc.protocol.InstructionParserListener;

import java.util.Arrays;

import kuyou.common.bytes.ByteUtils;
import kuyou.common.ku09.event.rc.base.EventRemoteControl;
import com.kuyou.rc.protocol.base.JT808ExtensionProtocol;

/**
 * action :
 * <p>
 * remarks:  <br/>
 * author: wuguoxian <br/>
 * date: 21-8-9 <br/>
 * </p>
 */
public class SicPhotoUploadReply extends SicPhoto {
    protected final String TAG = "com.kuyou.rc.protocol > SicPhotoUploadReply";

    private int mResult = -1;

    @Override
    public String getTitle() {
        return "拍照上传的回复";
    }

    @Override
    public int getFlag() {
        return JT808ExtensionProtocol.S2C_RESULT_PHOTO_UPLOAD_REPLY;
    }

    @Override
    public int getMatchEventCode() {
        return EventRemoteControl.Code.PHOTO_UPLOAD_RESULT;
    }

    @Override
    public void parse(byte[] data, InstructionParserListener listener) {
        super.parse(data,listener);
        byte[] bytes = getMsgContentAndParseMsgHeader(data);
        try {
            int flowNumResult = ByteUtils.bytes2Word(Arrays.copyOfRange(bytes, 0, 2));
            int mediaId = ByteUtils.bytes2Dword(Arrays.copyOfRange(bytes, 2, 6));
            int result = ByteUtils.byte2Int(bytes[6]);

            setMediaId(mediaId);
            setResult(result);

            StringBuilder sb = new StringBuilder(1024);
            sb.append("flowNumResult = ").append(flowNumResult);
            sb.append("\nmediaId = ").append(mediaId);
            sb.append("\nresult = ").append(result);
            Log.d(TAG, sb.toString());
        } catch (Exception e) {
            if (null != listener)
                listener.onRemote2LocalExpandFail(e);
        }
        if (null != listener)
            listener.onRemote2LocalExpand(SicPhotoUploadReply.this);
    }

    public int getResult() {
        return mResult;
    }

    public boolean isResultSuccess() {
        return ResultCode.SUCCESS == mResult;
    }

    public SicPhotoUploadReply setResult(int result) {
        mResult = result;
        return SicPhotoUploadReply.this;
    }
}
