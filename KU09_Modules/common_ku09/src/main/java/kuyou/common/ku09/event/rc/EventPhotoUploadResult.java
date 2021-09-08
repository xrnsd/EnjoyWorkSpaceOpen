package kuyou.common.ku09.event.rc;

import kuyou.common.ku09.event.rc.basic.EventRemoteControlResult;

/**
 * action :事件[照片上传结果]
 * <p>
 * remarks:  <br/>
 * author: wuguoxian <br/>
 * date: 21-3-27 <br/>
 * </p>
 */
public class EventPhotoUploadResult extends EventRemoteControlResult {

    @Override
    public int getCode() {
        return PHOTO_UPLOAD_RESULT;
    }
}