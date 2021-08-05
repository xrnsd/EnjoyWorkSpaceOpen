package com.kuyou.rc.info;

import android.net.Uri;
import android.util.Log;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import kuyou.common.bytes.ByteUtils;
import kuyou.sdk.jt808.base.RemoteControlDeviceConfig;
import kuyou.sdk.jt808.base.jt808coding.JTT808Coding;

/**
 * action :0x8F01,0x8F02,0xOF01
 * <p>
 * author: wuguoxian <br/>
 * date: 20-10-19 <br/>
 * <p>
 */
public class ImageInfo extends MsgInfo {

    private static final int FILE_FORMAT_TYPE_CODE_DEF = 0; //JPEG
    private static final long MEDIA_ID_BASE = 1601485261000L;//2020/10/1 1:1:1

    private int mWidth = 0;
    private int mHeight = 0;
    private int mResolutionSize = 0;
    private int mFilmingTime = 0;
    private long mMediaId = 0;
    private long mTimeShootPerform = -1;
    private int mFileFormatTypeCode = 0;
    private String mFileFormatType = "jpeg";
    private boolean isShootSuccess = false;

    private Uri mImgUriLocal;
    private String mImgUriRemote = "mImgUriRemote";
    private File mImgFile;

    protected Map<Integer, String> mFileFormatTypeMap;

    public ImageInfo(RemoteControlDeviceConfig config) {
        setConfig(config);
        initFileFormatType();
        setFileFormatTypeCode(FILE_FORMAT_TYPE_CODE_DEF);
    }

    private void initFileFormatType() {
        mFileFormatTypeMap = new HashMap<Integer, String>();
        mFileFormatTypeMap.put(0, "jpeg");
        mFileFormatTypeMap.put(1, "tif");
        mFileFormatTypeMap.put(2, "png");
    }

    public static interface ResultCode {
        public final static int SUCCESS = 0;
        public final static int LOCAL_DEVICE_SHOOT_FAIL = 1;
        public final static int LOCAL_DEVICE_UPLOAD_FAIL = 2;
    }

    private int mEventType = AudioVideoInfo.EVENT_TYPE_LOCAL_DEVICE_INITIATE;

    private int mResult = ResultCode.SUCCESS;

    /**
     * 0x8F01 , 0x8F02
     */
    @Override
    public boolean parse(byte[] bytes) {
        super.parse(bytes);
        bytes = getMsgContent();
        try {
            switch (mMsgHeader.getMsgId()) {
                case MSG_ID_8F01:
                    int flowNumResult = ByteUtils.bytes2Word(Arrays.copyOfRange(bytes, 0, 2));
                    int mediaId = ByteUtils.bytes2Dword(Arrays.copyOfRange(bytes, 2, 6));
                    int result = ByteUtils.byte2Int(bytes[6]);
                    onHandlerTts(MSG_ID_8F01, 0 == result ? "照片上传成功" : "照片上传失败");

                    StringBuilder sb = new StringBuilder(1024);
                    sb.append("flowNumResult = ").append(flowNumResult);
                    sb.append("\nmediaId = ").append(mediaId);
                    sb.append("\nresult = ").append(result);
                    Log.d(TAG, sb.toString());
                    return true;
                case MSG_ID_8F02:
                    shoot("开始拍照");
                    return true;
                default:
                    break;
            }
        } catch (Exception e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }
        Log.d(TAG, toString());
        return false;
    }

    public void shoot(String text) {
        Log.d(TAG, toString());
        onHandlerTts(MSG_ID_8F02, text);
    }

    /**
     * 0x0F01
     */
    public byte[] getResultMsgBytes() {
        byte initiateType = ByteUtils.int2Byte(getEventType());
        byte result = ByteUtils.int2Byte(getResult());

        byte[] body = ByteUtils.byteMergerAll(
                new byte[]{initiateType},
                new byte[]{result}
        );

        StringBuilder sb = new StringBuilder(1024);
        sb.append("\ngetEventType = ").append(getEventType());
        Log.d(TAG, sb.toString());
        return JTT808Coding.generate808(MSG_ID_0F01, getConfig(), body);
    }

    public long getMediaId() {
        return mMediaId;
    }

    public void setMediaId(long val) {
        val -= MEDIA_ID_BASE;
        mMediaId = val;
    }

    public int getResult() {
        return mResult;
    }

    public ImageInfo setResult(int result) {
        mResult = result;
        return ImageInfo.this;
    }

    public void setWidth(int val) {
        mWidth = val;
    }

    public int getWidth() {
        return mWidth;
    }

    public void setHeight(int val) {
        mHeight = val;
    }

    public int getHeight() {
        return mHeight;
    }

    public String getFileFormatType() {
        if (mFileFormatTypeMap.containsKey(getFileFormatTypeCode()))
            return mFileFormatTypeMap.get(getFileFormatTypeCode());
        return null;
    }

    public int getFileFormatTypeCode() {
        return mFileFormatTypeCode;
    }

    public void setFileFormatTypeCode(int val) {
        mFileFormatTypeCode = val;
    }

    public ImageInfo setEventType(int val) {
        mEventType = val;
        return ImageInfo.this;
    }

    public int getEventType() {
        return mEventType;
    }

    public void setResolutionSize(int val) {
        mResolutionSize = val;
    }

    public int getResolutionSize() {
        return mResolutionSize;
    }

    public void setFilmingTime(int val) {
        mFilmingTime = val;
    }

    public int getFilmingTime() {
        return mFilmingTime;
    }

    public void setImgUriLocal(Uri val) {
        mImgUriLocal = val;
    }

    public Uri getImgUriLocal() {
        return mImgUriLocal;
    }

    public long getID() {
        return System.currentTimeMillis();
    }

    public boolean isShootSuccess() {
        return isShootSuccess;
    }

    public String getImgUriRemote() {
        return mImgUriRemote;
    }

    public void setImgUriRemote(String val) {
        mImgUriRemote = val;
    }

    private static final String EXTRA_QUICK_CAPTURE = "android.intent.extra.quickCapture";

    public String getFileName() {
        return new StringBuilder()
                .append("IMG_")
                .append(getMediaId()).append(".").append(getFileFormatType()).toString();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(1024);
        sb.append("mWidth = ").append(mWidth);
        sb.append("\nmHeight = ").append(mHeight);
        sb.append("\nmFileFormatType = ").append(mFileFormatType);
        sb.append("\nmEventType = ").append(mEventType);
        sb.append("\nmResolutionSize = ").append(mResolutionSize);
        sb.append("\nmFilmingTime = ").append(mFilmingTime);
        sb.append("\nmMediaId = ").append(mMediaId);
        sb.append("\nisShootSuccess = ").append(isShootSuccess);
        sb.append("\nmImgUriLocal = ").append(mImgUriLocal);
        sb.append("\nmImgUriRemote = ").append(mImgUriRemote);
        return sb.toString();
    }
}
