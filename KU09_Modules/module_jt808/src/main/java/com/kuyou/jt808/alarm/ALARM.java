package com.kuyou.jt808.alarm;

/**
 * action :
 * <p>
 * remarks:  <br/>
 * author: wuguoxian <br/>
 * date: 21-4-12 <br/>
 * </p>
 */
public interface ALARM {
    /**
     * action:脱帽报警标志位
     */
    public static final int FLAG_CAP_OFF = 15;
    /**
     * action:sos报警标志位
     */
    public static final int FLAG_SOS = 16;
    /**
     * action:近电报警标志位
     */
    public static final int FLAG_NEAR_POWER = 17;
    /**
     * action:进出报警标志位
     */
    public static final int FLAG_ENTRY_AND_EXIT = 20;
    /**
     * action:气体报警标志位
     */
    public static final int FLAG_GAS = 24;
    /**
     * action:甲烷气体报警标志位
     */
    public static final int FLAG_GAS_METHANE = 25;
    /**
     * action:六氟化硫气体报警标志位
     */
    public static final int FLAG_GAS_SULFUR_HEXAFLUORIDE = 26;
    /**
     * action:一氧化碳气体报警标志位
     */
    public static final int FLAG_GAS_CARBON_MONOXIDE = 27;
    /**
     * action:跌倒报警标志位
     */
    public static final int FLAG_FALL = 30;
}
