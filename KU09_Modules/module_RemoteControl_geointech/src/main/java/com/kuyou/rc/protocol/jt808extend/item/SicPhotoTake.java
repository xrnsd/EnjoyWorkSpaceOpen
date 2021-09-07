package com.kuyou.rc.protocol.jt808extend.item;

import android.util.Log;

import com.kuyou.rc.protocol.jt808extend.basic.InstructionParserListener;
import com.kuyou.rc.protocol.jt808extend.basic.SicBasic;

import kuyou.common.bytes.ByteUtils;
import kuyou.common.ku09.event.avc.basic.EventAudioVideoCommunication;
import kuyou.common.ku09.event.rc.basic.EventRemoteControl;
import kuyou.common.ku09.protocol.IJT808ExtensionProtocol;

/**
 * action :JT808扩展的单项指令编解码器[拍照][抽象]
 * <p>
 * remarks:  <br/>
 * author: wuguoxian <br/>
 * date: 21-8-9 <br/>
 * </p>
 */
public class SicPhotoTake extends SicBasic {
    protected final String TAG = "com.kuyou.rc.protocol.jt808extend.item > SicPhotoTake";

    private static final int FILE_FORMAT_TYPE_CODE_DEF = 0; //JPEG
    private static final long MEDIA_ID_BASE = 1601485261000L;//2020/10/1 1:1:1

    private long mMediaId = 0;
    private long mTimeShootPerform = -1;
    private int mFileFormatTypeCode = 0;

    private int mEventType = SicAudioVideo.EVENT_TYPE_LOCAL_DEVICE_INITIATE;
    private int mResult = ResultCode.SUCCESS;
    private String mFileFormatType = "jpeg";

    public static interface ResultCode {
        public final static int SUCCESS = 0;
        public final static int LOCAL_DEVICE_SHOOT_FAIL = 1;
        public final static int LOCAL_DEVICE_UPLOAD_FAIL = 2;
    }

    public long getMediaId() {
        return mMediaId;
    }

    public void setMediaId(long val) {
        val -= MEDIA_ID_BASE;
        mMediaId = val;
    }

    public SicPhotoTake setEventType(int val) {
        mEventType = val;
        return SicPhotoTake.this;
    }

    public int getEventType() {
        return mEventType;
    }

    public String getFileName() {
        return new StringBuilder()
                .append("IMG_")
                .append(getMediaId()).append(".").append(mFileFormatType).toString();
    }

    public int getResult() {
        return mResult;
    }

    public SicPhotoTake setResult(int result) {
        mResult = result;
        return SicPhotoTake.this;
    }

    @Override
    public String getTitle() {
        return "拍照和照片上传";
    }

    @Override
    public int getFlag() {
        return IJT808ExtensionProtocol.S2C_REQUEST_PHOTO_TAKE_AND_PHOTO_UPLOAD;
    }

    @Override
    public boolean isMatchEventCode(int eventCode) {
        return eventCode == EventAudioVideoCommunication.Code.PHOTO_TAKE_RESULT
                || eventCode == EventRemoteControl.Code.PHOTO_UPLOAD_RESULT;
    }

    @Override
    public void parse(byte[] data, InstructionParserListener listener) {
        super.parse(data, listener);
        if (null != listener)
            listener.onRemote2LocalExpand(SicPhotoTake.this);
    }

    @Override
    public byte[] getBody(final int config) {
        byte initiateType = ByteUtils.int2Byte(getEventType());
        byte result = ByteUtils.int2Byte(getResult());

        byte[] body = ByteUtils.byteMergerAll(
                new byte[]{initiateType},
                new byte[]{result}
        );

        StringBuilder sb = new StringBuilder(1024);
        sb.append("\ngetEventType = ").append(getEventType());
        Log.d(TAG, sb.toString());
        return getPackToJt808(C2S_RESULT_PHOTO_TAKE_AND_PHOTO_UPLOAD, body);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(1024);
        sb.append("\nmFileFormatType = ").append(mFileFormatType);
        sb.append("\nmEventType = ").append(mEventType);
        sb.append("\nmMediaId = ").append(mMediaId);
        return sb.toString();
    }
}
