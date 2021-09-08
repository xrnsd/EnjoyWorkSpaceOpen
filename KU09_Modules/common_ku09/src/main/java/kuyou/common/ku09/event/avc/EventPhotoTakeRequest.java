package kuyou.common.ku09.event.avc;


import android.os.Bundle;
import android.os.Environment;

import kuyou.common.ipc.RemoteEvent;
import kuyou.common.ku09.event.avc.basic.EventRequest;

/**
 * action :事件[拍照请求]
 * <p>
 * remarks:  <br/>
 * author: wuguoxian <br/>
 * date: 21-3-27 <br/>
 * </p>
 */
public class EventPhotoTakeRequest extends EventRequest {

    protected final static String KEY_IS_UPLOAD = "keyEventData.imgUpload";
    protected final static String KEY_CONFIG_FILE_NAME = "keyEventData.configFileName";
    protected final static String KEY_CONFIG_IMG_STORAGE_DIR = "keyEventData.configImgStorageDir";
    protected final static String KEY_CONFIG_IMG_FORMAT_TYPE = "keyEventData.configImgFormatType";
    protected final static String KEY_CONFIG_IMG_WIDTH = "keyEventData.configImgWidth";
    protected final static String KEY_CONFIG_IMG_HEIGHT = "keyEventData.configImgHeight";

    public EventPhotoTakeRequest setUpload(boolean val) {
        getData().putBoolean(KEY_IS_UPLOAD, val);
        return EventPhotoTakeRequest.this;
    }

    public EventPhotoTakeRequest setFileName(String val) {
        getData().putString(KEY_CONFIG_FILE_NAME, val);
        return EventPhotoTakeRequest.this;
    }

    @Override
    public int getCode() {
        return Code.PHOTO_TAKE_REQUEST;
    }

    public static boolean isUpload(RemoteEvent event) {
        return event.getData().getBoolean(KEY_IS_UPLOAD);
    }

    public static String getFileName(Bundle data) {
        String def = new StringBuilder()
                .append("IMG_")
                .append(System.currentTimeMillis()).append(".").append(getFileFormatType(data)).toString();
        return data.getString(KEY_CONFIG_FILE_NAME,def);
    }

    public static String getFileFormatType(Bundle data) {
        String def = "jpg";
        return data.getString(KEY_CONFIG_IMG_FORMAT_TYPE,def);
    }

    public static String getImgStorageDir(Bundle data) {
        String def = new StringBuilder(Environment.getExternalStorageDirectory().toString())
                .append("/DCIM")
                .toString();
        return data.getString(KEY_CONFIG_IMG_STORAGE_DIR,def);
    }

    public static int getImgWidth(Bundle data) {
        int def = 1920;
        return data.getInt(KEY_CONFIG_IMG_WIDTH, def);
    }

    public static int getImgHeight(Bundle data) {
        int def = 1080;
        return data.getInt(KEY_CONFIG_IMG_HEIGHT, def);
    }
}