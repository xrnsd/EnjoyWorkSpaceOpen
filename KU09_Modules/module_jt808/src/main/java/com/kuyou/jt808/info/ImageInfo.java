package com.kuyou.jt808.info;

import android.net.Uri;
import android.util.Log;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import kuyou.common.ku09.bytes.ByteUtils;
import kuyou.sdk.jt808.base.Jt808Config;
import kuyou.sdk.jt808.base.jt808coding.JTT808Coding;
import kuyou.sdk.jt808.utils.UploadUtil;

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
    private int mEventType = 0;
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

    public ImageInfo(Jt808Config config) {
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
                    shoot("开始执行后台拍照指令");
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
        byte[] mediaId = ByteUtils.longToDword(getMediaId());
        byte fileFormatType = ByteUtils.int2Byte(getFileFormatTypeCode());
        byte eventType = ByteUtils.int2Byte(getEventType());

        byte[] body = ByteUtils.byteMergerAll(
                mediaId,
                new byte[]{fileFormatType, eventType}
        );

        StringBuilder sb = new StringBuilder(1024);
        sb.append("getMediaId = ").append(getMediaId());
        sb.append("\ngetFileFormatTypeCode = ").append(getFileFormatTypeCode());
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

    public void setEventType(int val) {
        mEventType = val;
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
    
    public String getFileName(){
        return new StringBuilder()
                .append("IMG_")
                .append(getMediaId()).append(".").append(getFileFormatType()).toString();
    }
    
    public void upload(String filePath,UploadUtil.OnUploadImageListener listener){
        File imgFile = new File(filePath);
        if(!imgFile.exists()){
            Log.e(TAG, "upload > process fail : img is`not exists = "+filePath);
            return;
        }
        UploadUtil.uploadImage(getConfig().getRemoteServerAddressPhoto(),imgFile, getConfig().getDevId(),listener);
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
