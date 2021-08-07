package kuyou.common.ku09.event.avc.base;

/**
 * action :协议定义[peergine音视频相关][部分常量]
 * <p>
 * remarks:  <br/>
 * author: wuguoxian <br/>
 * date: 21-7-16 <br/>
 * </p>
 */
public interface IAudioVideo {

    public static final int PLATFORM_TYPE_AGORA = 1;
    public static final int PLATFORM_TYPE_PEERGIN =2;

    public static final int MEDIA_TYPE_AUDIO = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;
    public static final int MEDIA_TYPE_INFEARED = 3;
    public static final int MEDIA_TYPE_GROUP = 4;
    public static final int MEDIA_TYPE_DEFAULT = MEDIA_TYPE_GROUP;

    public static final int EVENT_TYPE_REMOTE_PLATFORM_INITIATE = 0;
    public final static int EVENT_TYPE_REMOTE_PLATFORM_REFUSE = 1;
    public final static int EVENT_TYPE_LOCAL_DEVICE_INITIATE = 2;
    public final static int EVENT_TYPE_REMOTE_PLATFORM_NO_RESPONSE = 3;
    public static final int EVENT_TYPE_CLOSE = 255;

    public static final int STATUS_PARAMETER_DEF = 0;
    public static final int STATUS_PARAMETER_APPLYING = 1;
    
    //0x0F03
    public final static int RESULT_SUCCESS = 0;
    public final static int RESULT_FAIL = 1;

    public final static int RESULT_FAIL_FAILURE_AUDIO_VIDEO_SERVER_EXCEPTION = 2;
    public final static int RESULT_FAIL_FAILURE_AUDIO_VIDEO_PARAMETER_PARSE_FAIL = 3;
    public final static int RESULT_FAIL_FAILURE_LOCAL_DEVICE_HARDWARE_EXCEPTION = 4;
    public final static int RESULT_FAIL_FAILURE_LOCAL_DEVICE_HANDLE_TIME_OUT = 5;
    public final static int RESULT_FAIL_FAILURE_OTHER = 6;
}
