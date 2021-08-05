package kuyou.common.ku09.event.rc.base;

import kuyou.common.ipc.RemoteEvent;
import kuyou.common.ku09.event.common.base.RemoteEventCommon;

/**
 * action :
 * <p>
 * remarks:  <br/>
 * author: wuguoxian <br/>
 * date: 21-3-29 <br/>
 * </p>
 */
public abstract class EventRemoteControl extends RemoteEventCommon {

    protected final String TAG = "kuyou.common.ku09.event.jt808 > " + this.getClass().getSimpleName();

    protected static final String KEY_PLATFORM_TYPE = "keyEventData.platformType";
    protected static final String KEY_MEDIA_TYPE = "keyEventData.mediaType";

    //4096~6143
    protected final static int FLAG_CODE = 4096;

    //FLAG_CODE+0 ~ FLAG_CODE+2047
    public static interface Code {
        //模块状态相关 0~127
        public final static int MODULE_INIT_FINISH = FLAG_CODE + 0;
        public final static int MODULE_EXIT = FLAG_CODE + 1;

        //808协议相关 128 ~ 511
        public final static int CONNECT_REQUEST = FLAG_CODE + 128;
        public final static int CONNECT_RESULT = FLAG_CODE + 129;

        public final static int AUTHENTICATION_REQUEST = FLAG_CODE + 130;
        public final static int AUTHENTICATION_RESULT = FLAG_CODE + 131;

        public final static int LOCATION_START_REPORT_REQUEST = FLAG_CODE + 132;
        public final static int LOCATION_REPORT_REQUEST = FLAG_CODE + 133;
        public final static int LOCATION_CHANGE = FLAG_CODE + 134;
        //public final static int HEARTBEAT = FLAG_CODE + 134;

        //业务需求相关 512 ~ 2047
        public final static int PHOTO_UPLOAD_REQUEST = FLAG_CODE + 512;
        public final static int PHOTO_UPLOAD_RESULT = FLAG_CODE + 513;

        public final static int AUDIO_AND_VIDEO_PARAMETERS_APPLY_REQUEST = FLAG_CODE + 514;
        public final static int AUDIO_AND_VIDEO_PARAMETERS_APPLY_RESULT = FLAG_CODE + 515;
    }

    public EventRemoteControl setPlatformType(int val) {
        getData().putInt(KEY_PLATFORM_TYPE, val);
        return EventRemoteControl.this;
    }

    public static int getPlatformType(RemoteEvent event) {
        return event.getData().getInt(KEY_PLATFORM_TYPE);
    }

    public EventRemoteControl setMediaType(int val) {
        getData().putInt(KEY_MEDIA_TYPE, val);
        return EventRemoteControl.this;
    }

    public static int getMediaType(RemoteEvent event) {
        return event.getData().getInt(KEY_MEDIA_TYPE);
    }
}