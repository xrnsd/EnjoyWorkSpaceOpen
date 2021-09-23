package kuyou.common.ku09.protocol.basic;

/**
 * action :
 * <p>
 * remarks:  <br/>
 * author: wuguoxian <br/>
 * date: 21-9-9 <br/>
 * </p>
 */
public interface IHardwareDetection extends IHardwareControlDetectionV1_1 {

    /**
     * action:协议头,附加信息: 设备硬件模块搭载状态信息
     * */
    public final static int HM_ADDITIONAL_ITEM_HEAD = (byte)0xE3;

    /**
     * action:红外热成像
     * */
    public final static int HM_TYPE_INPUT_CAMERA_INFRARED_THERMAL = 0;
    /**
     * action:UWB
     * */
    public final static int HM_TYPE_INPUT_LOCATION_UWB = 1;
    /**
     * action:普通后摄
     * */
    public final static int HM_TYPE_INPUT_CAMERA_NORMAL = 2;
    /**
     * action:光波导
     * */
    public final static int HM_TYPE_OUTPUT_SCREEN_UNIQUE_OPTICAL_WAVEGUIDE = 3;
    /**
     * action:语音控制
     * */
    public final static int HM_TYPE_INPUT_VOICE_CONTROL = 4;
    /**
     * action:气体检测[一氧化碳]
     * */
    public final static int HM_TYPE_INPUT_GAS_DETECTION_CARBON_MONOXIDE = 5;
    /**
     * action:气体检测[甲烷]
     * */
    public final static int HM_TYPE_INPUT_GAS_DETECTION_METHANE = 6;
    /**
     * action:温湿度检测
     * */
    public final static int HM_TYPE_INPUT_TEMPERATURE_HUMIDITY = 7;
    /**
     * action:陀螺仪
     * */
    public final static int HM_TYPE_INPUT_GYROSCOPE = 8;
    /**
     * action:北斗2
     * */
    public final static int HM_TYPE_INPUT_BEIDOU_TWO = 9;
    /**
     * action:气压计
     * */
    public final static int HM_TYPE_INPUT_BAROMETER = 10;
    /**
     * action:激光指向灯
     * */
    public final static int HM_TYPE_OUTPUT_LASER_LIGHT = 11;
    /**
     * action:手电筒
     * */
    public final static int HM_TYPE_OUTPUT_FLASHLIGHT = 12;
    /**
     * action:SIM卡
     * */
    public final static int HM_TYPE_INPUT_SIM = 13;
    /**
     * action:SD卡
     * */
    public final static int HM_TYPE_INPUT_SD_CARD = 14;
    /**
     * action:强电靠近检测
     * */
    public final static int HM_TYPE_INPUT_STRONG_POWER_DETECTION = 15;



    /**
     * action:搭载,工作正常
     * */
    public final static int HM_STATUS_BE_EQUIPPED_NORMAL = 0;

    /**
     * action:搭载,工作异常
     * */
    public final static int HM_STATUS_BE_EQUIPPED_EXCEPTION = 1;

    /**
     * action:搭载,未检测到
     * */
    public final static int HM_STATUS_BE_EQUIPPED_NOT_DETECTED = 2;

    /**
     * action:搭载,已禁用
     * */
    public final static int HM_STATUS_BE_EQUIPPED_DISABLE = 3;

    /**
     * action:未搭载
     * */
    public final static int HM_STATUS_NOT_EQUIPPED = 4;
}
