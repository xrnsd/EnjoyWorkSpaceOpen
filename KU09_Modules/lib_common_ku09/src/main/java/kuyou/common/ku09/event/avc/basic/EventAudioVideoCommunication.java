package kuyou.common.ku09.event.avc.basic;

import kuyou.common.ku09.event.common.basic.RemoteEventCommon;

/**
 * action :模块事件基础配置
 * <p>
 * remarks:  <br/>
 * author: wuguoxian <br/>
 * date: 21-3-29 <br/>
 * </p>
 */
public abstract class EventAudioVideoCommunication extends RemoteEventCommon {

    protected final String TAG = "kuyou.common.ku09.event.avc > EventAudioVideoCommunication";

    //6144~8191
    protected final static int FLAG_CODE = 6144;

    //FLAG_CODE+0 ~ FLAG_CODE+2047
    public static interface Code {
        //模块状态相关 0~127
        public final static int MODULE_INIT_FINISH = FLAG_CODE + 0;
        public final static int MODULE_EXIT = FLAG_CODE + 1;
        public final static int REQUEST = FLAG_CODE + 2;
        public final static int RESULT = FLAG_CODE + 3;

        //业务需求相关 128 ~ 2047
        public final static int PHOTO_TAKE_REQUEST = FLAG_CODE + 128;
        public final static int PHOTO_TAKE_RESULT = FLAG_CODE + 129;

        public final static int FLASHLIGHT_REQUEST = FLAG_CODE + 130;
        public final static int FLASHLIGHT_RESULT = FLAG_CODE + 131;

        public final static int LASER_LIGHT_REQUEST = FLAG_CODE + 132;
        public final static int LASER_LIGHT_RESULT = FLAG_CODE + 133;

        public final static int AUDIO_VIDEO_OPERATE_REQUEST = FLAG_CODE + 134;
        public final static int AUDIO_VIDEO_OPERATE_RESULT = FLAG_CODE + 135;
    }
}
