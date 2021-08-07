package kuyou.common.ku09.event.rc;

import kuyou.common.ku09.event.rc.base.EventResult;

/**
 * action :事件[照片上传结果]
 * <p>
 * remarks:  <br/>
 * author: wuguoxian <br/>
 * date: 21-3-27 <br/>
 * </p>
 */
public class EventPhotoUploadResult extends EventResult {

    @Override
    public int getCode() {
        return Code.PHOTO_UPLOAD_RESULT;
    }
}