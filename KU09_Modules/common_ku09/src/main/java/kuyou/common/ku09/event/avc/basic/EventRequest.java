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

    public static final String KEY_REQUEST_CODE = "request.code";

    public static interface RequestCode {
        public final static int OPEN = 0;
        public final static int CLOSE = 1;
        public final static int SHOOT = 2;
        public final static int UPLOAD = 3;
        public final static int SHOOT_UPLOAD = 4;
    }

    public EventRequest() {

    }

    public EventRequest(int requestCode) {
        getData().putInt(KEY_REQUEST_CODE, requestCode);
    }
}