package kuyou.common.ku09.protocol.basic;

/**
 * action :KU09
 * <p>
 * remarks:  <br/>
 * author: wuguoxian <br/>
 * date: 21-3-23 <br/>
 * 部分扩展协议指令名称项定义说明
 * S2C : 服务器发送客户端接收
 * C2S : 客户端发送服务器接收
 * </p>
 */
public interface IJT808ExtensionProtocol extends IJT808BasicProtocol {

    // ========================  CMD HEADER ================================

    //  SERVER_CMD
    /**
     * action : 文本信息
     * flow：server > client
     */
    public final static int S2C_REQUEST_TEXT_MESSAGE = 0x8300;
    /**
     * action : 拍照和照片上传
     * flow：server > client
     */
    public final static int S2C_REQUEST_PHOTO_TAKE_AND_PHOTO_UPLOAD = 0x8F02;
    /**
     * action : 音视频操作
     * flow：server > client
     */
    public final static int S2C_REQUEST_AUDIO_VIDEO_PARAMETERS = 0x8F03;

    // S2C_RESULT
    /**
     * action : 连接回复
     * flow：server > client
     */
    public final static int S2C_RESULT_CONNECT_REPLY = 0x8001;
    /**
     * action : 鉴权回复
     * flow：server > client
     */
    public final static int S2C_RESULT_AUTHENTICATION_REPLY = 0x8fff;
    /**
     * action : 服务器接收照片的回复
     * flow：server > client
     */
    public final static int S2C_RESULT_PHOTO_UPLOAD_REPLY = 0x8F01;

    // C2S_REQUEST
    /**
     * action : 请求音视频参数
     * flow：client > server
     */
    public final static int C2S_REQUEST_PHOTO_UPLOAD = 0x0F02;
    /**
     * action : 位置汇报
     * flow：client > server
     */
    public final static int C2S_REQUEST_LOCATION_REPORT = 0x0200;
    /**
     * action : 位置汇报[批量]
     * flow：client > server
     */
    public final static int C2S_REQUEST_LOCATION_BATCH_REPORT = 0x0704;


    // C2S_RESULT
    /**
     * action : 终端对"拍照和照片上传"的处理回复
     * flow：client > server
     */
    public final static int C2S_RESULT_PHOTO_TAKE_AND_PHOTO_UPLOAD = 0x0F01;
    /**
     * action : 终端对服务器音视频请求处理回复
     * flow：client > server
     */
    public final static int C2S_RESULT_AUDIO_VIDEO_PARAMETERS = 0x0F03;

    // ========================  CMD CONNECT ================================

    //AudioVideoGroup
    public static final int PLATFORM_TYPE_AGORA = 1;
    public static final int PLATFORM_TYPE_PEERGIN = 2;

    public static final int MEDIA_TYPE_AUDIO = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;
    public static final int MEDIA_TYPE_THERMAL = 3;
    public static final int MEDIA_TYPE_GROUP = 4;
    public static final int MEDIA_TYPE_DEFAULT = MEDIA_TYPE_GROUP;

    public static final int EVENT_TYPE_REMOTE_PLATFORM_INITIATE = 0;
    public final static int EVENT_TYPE_REMOTE_PLATFORM_REFUSE = 1;
    public final static int EVENT_TYPE_LOCAL_DEVICE_INITIATE = 2;
    public final static int EVENT_TYPE_REMOTE_PLATFORM_NO_RESPONSE = 3;
    public static final int EVENT_TYPE_CLOSE = 255;

    public static final int STATUS_PARAMETER_DEF = 0;
    public static final int STATUS_PARAMETER_APPLYING = 1;

    public final static int RESULT_SUCCESS = 0;
    public final static int RESULT_FAIL = 1;

    public final static int RESULT_FAIL_FAILURE_AUDIO_VIDEO_SERVER_EXCEPTION = 2;
    public final static int RESULT_FAIL_FAILURE_AUDIO_VIDEO_PARAMETER_PARSE_FAIL = 3;
    public final static int RESULT_FAIL_FAILURE_LOCAL_DEVICE_HARDWARE_EXCEPTION = 4;
    public final static int RESULT_FAIL_FAILURE_LOCAL_DEVICE_HANDLE_TIME_OUT = 5;
    public final static int RESULT_FAIL_FAILURE_OTHER = 6;

    public final static String TOKEN_NULL = "none";

    //Alarm
    /**
     * action:脱帽报警标志位
     */
    public static final int ALARM_FLAG_CAP_OFF = 15;
    /**
     * action:sos报警标志位
     */
    public static final int ALARM_FLAG_SOS = 16;
    /**
     * action:近电报警标志位
     */
    public static final int ALARM_FLAG_NEAR_POWER = 17;
    /**
     * action:进出报警标志位
     */
    public static final int ALARM_FLAG_ENTRY_AND_EXIT = 20;
    /**
     * action:气体报警标志位
     */
    public static final int ALARM_FLAG_GAS = 24;
    /**
     * action:甲烷气体报警标志位
     */
    public static final int ALARM_FLAG_GAS_METHANE = 25;
    /**
     * action:六氟化硫气体报警标志位
     */
    public static final int ALARM_FLAG_GAS_SULFUR_HEXAFLUORIDE = 26;
    /**
     * action:一氧化碳气体报警标志位
     */
    public static final int ALARM_FLAG_GAS_CARBON_MONOXIDE = 27;
    /**
     * action:跌倒报警标志位
     */
    public static final int ALARM_FLAG_FALL = 30;
}
