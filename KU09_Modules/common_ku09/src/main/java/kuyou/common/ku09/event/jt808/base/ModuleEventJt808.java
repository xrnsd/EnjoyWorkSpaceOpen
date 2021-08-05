package kuyou.common.ku09.event.jt808.base;

import kuyou.common.ipc.RemoteEvent;

/**
 * action :
 * <p>
 * remarks:  <br/>
 * author: wuguoxian <br/>
 * date: 21-3-29 <br/>
 * </p>
 */
public abstract class ModuleEventJt808 extends RemoteEvent {

    protected final String TAG = "kuyou.common.ku09.event.jt808 > "+ this.getClass().getSimpleName();

    protected final static int FLAG_CODE = 2048;

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
}
