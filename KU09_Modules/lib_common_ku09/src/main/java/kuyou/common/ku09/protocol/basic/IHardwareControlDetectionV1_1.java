package kuyou.common.ku09.protocol.basic;

/**
 * action :硬件块控制检测[接口][基于设备节点]
 * <p>
 * remarks:  <br/>
 * author: wuguoxian <br/>
 * date: 21-9-23 <br/>
 * 定义说明: 硬件模块控制检测API/V1.1.txt
 * </p>
 */
public interface IHardwareControlDetectionV1_1 {
    //PRESSURE,近电报警，强电检测
    public static final String DEV_PTAH_PRESSURE = "/sys/kernel/lactl/attr/pressure";
    public static final String DEV_VAL_PRESSURE_POWER_ON_220 = "pressure_grade_a";
    public static final String DEV_VAL_PRESSURE_POWER_ON_10K = "pressure_grade_b";
    public static final String DEV_VAL_PRESSURE_POWER_ON_35K = "pressure_grade_c";
    public static final String DEV_VAL_PRESSURE_POWER_ON_220K = "pressure_grade_d";
    public static final String DEV_VAL_PRESSURE_POWER_OFF = "pressure_grade_n";

    //GAS，气体检测
    public static final String DEV_PTAH_GAS = "/sys/kernel/lactl/attr/gas";
    public static final String DEV_VAL_GAS_POWER_ON = "gas_pwr_on";
    public static final String DEV_VAL_GAS_POWER_OFF = "gas_pwr_off";
    public static final String DEV_VAL_GAS_UART_ON = "gas_uart_on";
    public static final String DEV_VAL_GAS_UART_OFF = "gas_uart_off";

    //UWB，室内定位
    public static final String DEV_PTAH_UWB = "/sys/kernel/lactl/attr/uwb";
    public static final String DEV_VAL_UWB_POWER_ON = "uwb_pwr_on";
    public static final String DEV_VAL_UWB_POWER_OFF = "uwb_pwr_off";
    public static final String DEV_VAL_UWB_UART_ON = "uwb_uart_on";
    public static final String DEV_VAL_UWB_UART_OFF = "uwb_uart_off";

    //红外热成像
    public static final String DEV_PTAH_THERMAL = "/sys/kernel/lactl/attr/usbir";
    public static final String DEV_VAL_THERMAL_POWER_ON = "thermal_pwr_on";
    public static final String DEV_VAL_THERMAL_POWER_OFF = "thermal_pwr_off";

    //语音识别，语音控制
    public static final String DEV_PTAH_VOICE_CONTROL = "/sys/kernel/lactl/attr/wakeup";
    public static final String DEV_VAL_VOICE_CONTROL_POWER_ON = "wakeup_pwr_on";
    public static final String DEV_VAL_VOICE_CONTROL_POWER_OFF = "wakeup_pwr_off";

    //光波导
    public static final String DEV_PTAH_OPTICAL_WAVEGUIDE = "/sys/class/op02220ba/op02220ba/val";
    public static final String DEV_VAL_OPTICAL_WAVEGUIDE_DETECTION_EXIST = "1";
    public static final String DEV_VAL_OPTICAL_WAVEGUIDE_DETECTION_DOT_EXIST = "0";
}
