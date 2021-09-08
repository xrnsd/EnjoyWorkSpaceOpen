package kuyou.common.ku09.event.rc.basic;

/**
 * action :
 * <p>
 * remarks:  <br/>
 * author: wuguoxian <br/>
 * date: 21-9-8 <br/>
 * </p>
 */
public interface IEventCodeGroupRemoteControl {

    //4096~6143
    public final static int FLAG_CODE = 4096;
    
    //===================  FLAG_CODE+0 ~ FLAG_CODE+2047 ==================================

    //模块状态相关 0~127
    public final static int MODULE_INIT_FINISH = FLAG_CODE + 0;
    public final static int MODULE_EXIT = FLAG_CODE + 1;

    //808协议相关 128 ~ 511
    public final static int CONNECT_REQUEST = FLAG_CODE + 128;
    public final static int CONNECT_RESULT = FLAG_CODE + 129;

    public final static int AUTHENTICATION_REQUEST = FLAG_CODE + 130;
    public final static int AUTHENTICATION_RESULT = FLAG_CODE + 131;

    public final static int HEARTBEAT_REPORT_REQUEST = FLAG_CODE + 132;
    public final static int HEARTBEAT_REPORT = FLAG_CODE + 133;
    public final static int HEARTBEAT_REPLY = FLAG_CODE + 134;

    public final static int LOCATION_REPORT_START_REQUEST = FLAG_CODE + 135;
    public final static int LOCATION_REPORT_STOP_REQUEST = FLAG_CODE + 136;
    public final static int LOCATION_REPORT_REQUEST = FLAG_CODE + 137;
    public final static int LOCATION_CHANGE = FLAG_CODE + 138;

    public final static int SEND_TO_REMOTE_CONTROL_PLATFORM = FLAG_CODE + 139;

    //业务需求相关 512 ~ 2047
    public final static int LOCAL_DEVICE_STATUS = FLAG_CODE + 512;

    public final static int PHOTO_UPLOAD_REQUEST = FLAG_CODE + 513;
    public final static int PHOTO_UPLOAD_RESULT = FLAG_CODE + 514;

    public final static int AUDIO_VIDEO_PARAMETERS_APPLY_REQUEST = FLAG_CODE + 515;
    public final static int AUDIO_VIDEO_PARAMETERS_APPLY_RESULT = FLAG_CODE + 516;
}
