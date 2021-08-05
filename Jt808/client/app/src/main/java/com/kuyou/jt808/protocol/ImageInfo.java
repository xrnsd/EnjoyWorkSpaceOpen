package com.kuyou.jt808.protocol;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import com.kuyou.jt808.utils.CommitUtils;
import com.kuyou.jt808.utils.UploadUtil;
import com.cuichen.jt808_sdk.sdk.SocketConfig;
import com.cuichen.jt808_sdk.sdk.jt808coding.JTT808Coding;
import com.cuichen.jt808_sdk.sdk.jt808utils.ByteUtil;

import org.json.JSONObject;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * action :0x8F01,0x8F02,0xOF01
 * <p>
 * author: wuguoxian <br/>
 * date: 20-10-19 <br/>
 * <p>
 */
public class ImageInfo extends MsgInfo {

    public static final String SERVER_URL = "https://centos40.geointech.cn:8019/smart-cap/api/aqm/photo/savePhotoToCos";
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

    public ImageInfo() {
        super();
        initFileFormatType();
        setFileFormatTypeCode(FILE_FORMAT_TYPE_CODE_DEF);
    }

    private void initFileFormatType() {
        mFileFormatTypeMap = new HashMap<Integer, String>();
        mFileFormatTypeMap.put(0, "jpeg");
        mFileFormatTypeMap.put(1, "tif");
        mFileFormatTypeMap.put(2, "png");
    }

    public static void performShoot(){
        ImageInfo info = new ImageInfo();
        info.setMediaId(MSG_ID_8F02);
        info.shoot();
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
                    int flowNumResult = ByteUtil.bytes2Word(Arrays.copyOfRange(bytes, 0, 2));
                    int mediaId = ByteUtil.bytes2Dword(Arrays.copyOfRange(bytes, 2, 6));
                    int result = ByteUtil.byte2Int(bytes[6]);
                    onHandlerTts(MSG_ID_8F01, 0 == result ? "照片上传成功" : "照片上传失败");

                    StringBuilder sb = new StringBuilder(1024);
                    sb.append("flowNumResult = ").append(flowNumResult);
                    sb.append("\nmediaId = ").append(mediaId);
                    sb.append("\nresult = ").append(result);
                    Log.d(TAG, sb.toString());
                    return true;
                case MSG_ID_8F02:
                    shoot();
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

    private void shoot(){
        Log.d(TAG, toString());
        onHandlerTts(MSG_ID_8F02, "开始执行后台拍照指令");
    }

    /**
     * 0x0F01
     */
    public byte[] getResultMsgBytes() {
        byte[] mediaId = ByteUtil.longToDword(getMediaId());
        byte fileFormatType = ByteUtil.int2Byte(getFileFormatTypeCode());
        byte eventType = ByteUtil.int2Byte(getEventType());

        byte[] body = ByteUtil.byteMergerAll(
                mediaId,
                new byte[]{fileFormatType, eventType}
        );

        StringBuilder sb = new StringBuilder(1024);
        sb.append("getMediaId = ").append(getMediaId());
        sb.append("\ngetFileFormatTypeCode = ").append(getFileFormatTypeCode());
        sb.append("\ngetEventType = ").append(getEventType());
        Log.d(TAG, sb.toString());
        return JTT808Coding.generate808(MSG_ID_0F01, SocketConfig.getmPhont(), body);
    }

    public long getMediaId() {
        return mMediaId;
    }

    public void setMediaId(long val) {
        val -= MEDIA_ID_BASE;//
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

    public void shootAndUpload(Activity context, int requestCode) {
        shoot(context, requestCode);
    }

    private void shoot(Activity context, int requestCode) {
        setMediaId(System.currentTimeMillis());
        mTimeShootPerform = getMediaId();//防止重复上传

        final String fileName = new StringBuilder()
                .append(getMediaId()).append(".").append(getFileFormatType()).toString();
        final String dirPathStorage = "/sdcard/kuyou/img/";
        mImgFile = new File(dirPathStorage + fileName);
        mImgUriLocal = Uri.fromFile(mImgFile);

        Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, mImgUriLocal);
        context.startActivityForResult(captureIntent, requestCode);
    }

    public void setShootState(boolean val) {
        isShootSuccess = val;
        if (-1 == mTimeShootPerform
                || !isShootSuccess
                || null == mImgFile
                || !mImgFile.exists())
            return;
        mTimeShootPerform = -1;

        Log.d("123456", " 已拍照--------------------- ");
        UploadUtil.uploadImage(SERVER_URL, mImgFile, SocketConfig.getmPhont(),
                new UploadUtil.OnUploadImageListener(){
                    @Override
                    public void onUploadFinish(JSONObject jsonResult) {
                        if (null == jsonResult) {
                            return;
                        }
                        try{
                            Log.d("123456"," mContext.runOnUiThread(()-> onHandler(MSG_ID_0F01, RESULT_CODE_SUCCESS)) ");
                            if(0 == jsonResult.getInt("code")) {
                                Log.d("123456"," mContext.runOnUiThread(()-> onHandler(MSG_ID_0F01, RESULT_CODE_SUCCESS)) 222222");
                                onHandler(MSG_ID_0F01, RESULT_CODE_SUCCESS);
                            }
                        }catch(Exception e){
                            Log.e("123456", Log.getStackTraceString(e));
                        }
                    }
                });
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
