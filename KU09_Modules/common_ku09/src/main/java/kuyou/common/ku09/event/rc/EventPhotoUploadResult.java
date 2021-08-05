package kuyou.common.ku09.event.rc;

import kuyou.common.ipc.RemoteEvent;
import kuyou.common.ku09.event.rc.base.EventResult;

/**
 * action :
 * <p>
 * remarks:  <br/>
 * author: wuguoxian <br/>
 * date: 21-3-27 <br/>
 * </p>
 */
public class EventPhotoUploadResult extends EventResult {


    protected final static String KEY_MSG_BUFFER = "key.msg.buffer";
    

    public byte[] getMsg() {
        return getData().getByteArray(KEY_MSG_BUFFER);
    }

    public EventPhotoUploadResult setMsg(byte[] val) {
        getData().putByteArray(KEY_MSG_BUFFER, val);
        return EventPhotoUploadResult.this;
    }

    @Override
    public int getCode() {
        return Code.PHOTO_UPLOAD_RESULT;
    }
    
    public static byte[] getMsg(RemoteEvent event){
        return event.getData().getByteArray(KEY_MSG_BUFFER);
    }
}