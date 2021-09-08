package kuyou.common.ku09.event.avc;

import android.os.Bundle;
import android.util.Log;

import kuyou.common.ipc.RemoteEvent;
import kuyou.common.ku09.event.avc.basic.EventResult;

/**
 * action :事件[拍照结果]
 * <p>
 * remarks:  <br/>
 * author: wuguoxian <br/>
 * date: 21-3-27 <br/>
 * </p>
 */
public class EventPhotoTakeResult extends EventResult {

    protected final static String KEY_ERROR_INFO = "keyEventData.photoTakeErrorInfo";
    protected final static String KEY_IS_UPLOAD = "keyEventData.imgUpload";
    protected final static String KEY_IMG_PATH = "keyEventData.imgFilePath";

    public EventPhotoTakeResult setImgFilePath(String filePath) {
        getData().putString(KEY_IMG_PATH, filePath);
        return EventPhotoTakeResult.this;
    }

    public EventPhotoTakeResult setErrorInfo(String info) {
        getData().putString(KEY_ERROR_INFO, info);
        return EventPhotoTakeResult.this;
    }

    public EventPhotoTakeResult setUpload(boolean val) {
        getData().putBoolean(KEY_IS_UPLOAD, val);
        return EventPhotoTakeResult.this;
    }

    @Override
    public int getCode() {
        return Code.PHOTO_TAKE_RESULT;
    }

    @Override
    public EventPhotoTakeResult setData(Bundle data) {
        super.setData(data);
        return EventPhotoTakeResult.this;
    }

    @Override
    public EventPhotoTakeResult setRemote(boolean val) {
        super.setRemote(val);
        return EventPhotoTakeResult.this;
    }

    public static boolean isUpload(RemoteEvent event) {
        return event.getData().getBoolean(KEY_IS_UPLOAD);
    }

    public static String getImgFilePath(RemoteEvent event) {
        if (event.getCode() != Code.PHOTO_TAKE_RESULT) {
            Log.e("kuyou.common.ku09 > EventPhotoTakeResult", "parse > process fail : event is invalid");
            return null;
        }
        return event.getData().getString(KEY_IMG_PATH);
    }

    public static String getError(RemoteEvent event) {
        if (event.getCode() != Code.PHOTO_TAKE_RESULT) {
            Log.e("kuyou.common.ku09 > EventPhotoTakeResult", "getError > process fail : event is invalid");
            return null;
        }
        return event.getData().getString(KEY_ERROR_INFO);
    }
}