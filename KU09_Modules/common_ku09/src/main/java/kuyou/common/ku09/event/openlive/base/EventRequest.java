package kuyou.common.ku09.event.openlive.base;

/**
 * action :
 * <p>
 * remarks:  <br/>
 * author: wuguoxian <br/>
 * date: 21-3-27 <br/>
 * </p>
 */
public abstract class EventRequest extends ModuleEventOpenLive {

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