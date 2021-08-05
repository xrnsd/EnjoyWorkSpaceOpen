package kuyou.common.ku09.event.rc;

import kuyou.common.ipc.RemoteEvent;
import kuyou.common.ku09.event.rc.base.EventRequest;

/**
 * action :
 * <p>
 * remarks:  <br/>
 * author: wuguoxian <br/>
 * date: 21-3-27 <br/>
 * </p>
 */
public class EventPhotoUploadRequest extends EventRequest {
    protected final static String KEY_IMG_PATH = "key.img.file.path";

    public String getImgFilePath() {
        return getData().getString(KEY_IMG_PATH);
    }

    public EventPhotoUploadRequest setImgFilePath(String val) {
        getData().putString(KEY_IMG_PATH, val);
        return EventPhotoUploadRequest.this;
    }

    @Override
    public int getCode() {
        return Code.PHOTO_UPLOAD_REQUEST;
    }

    public static String getImgFilePath(RemoteEvent event) {
        return event.getData().getString(KEY_IMG_PATH);
    }
}