package kuyou.common.ku09.event.avc.basic;

/**
 * action :事件[音视频相关请求][抽象]
 * <p>
 * remarks:  <br/>
 * author: wuguoxian <br/>
 * date: 21-3-27 <br/>
 * </p>
 */
public abstract class EventRequest extends EventAudioVideoCommunication {
    
    public EventRequest() {

    }

    public EventRequest(int requestCode) {
        getData().putInt(KEY_REQUEST_CODE, requestCode);
    }
}