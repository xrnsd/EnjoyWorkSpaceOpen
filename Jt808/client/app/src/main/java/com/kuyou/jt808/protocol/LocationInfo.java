package com.kuyou.jt808.protocol;

import android.app.Activity;
import android.location.Location;
import android.util.Log;

import com.cuichen.jt808_sdk.sdk.SocketConfig;
import com.cuichen.jt808_sdk.sdk.jt808coding.JTT808Coding;
import com.cuichen.jt808_sdk.sdk.jt808utils.BitOperator;
import com.cuichen.jt808_sdk.sdk.jt808utils.ByteUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * action :
 * <p>
 * author: wuguoxian <br/>
 * date: 20-10-24 <br/>
 * <p>
 */
public class LocationInfo extends MsgInfo {

    private final int LOCATION_BATCH_SIZE = 3;

    private final int INDEX_CAP_OFF_ALARM_FLAG = 15;//脱帽报警标志位
    private final int INDEX_SOS_ALARM_FLAG = 16;//sos报警标志位
    private final int INDEX_NEAR_POWER_ALARM_FLAG = 17;//近电报警标志位
    private final int INDEX_ENTRY_AND_EXIT_ALARM_FLAG = 20;//进出报警标志位
    private final int INDEX_GAS_ALARM_FLAG = 24;//气体报警标志位
    private final int INDEX_GAS_METHANE_ALARM_FLAG = 25;//甲烷气体报警标志位
    private final int INDEX_GAS_SULFUR_HEXAFLUORIDE_ALARM_FLAG = 26;//六氟化硫气体报警标志位
    private final int INDEX_GAS_CARBON_MONOXIDE_ALARM_FLAG = 27;//一氧化碳气体报警标志位
    private final int INDEX_FALL_ALARM_FLAG = 30;//跌倒报警标志位

    private int[] AlarmFlags = {
            0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0};

    private Location mLocation;
    private List<byte[]> mBatchLocationByteList = new ArrayList<>();

    public LocationInfo() {
        super();
    }

    @Override
    public boolean parse(byte[] bytes) {
        return super.parse(bytes);
    }

    public byte[] getLocation808ItemAlarmFlag() {
        return ByteUtil.bits2DWord(AlarmFlags);
    }

    private void setLocation(Location location) {
        mLocation = location;
    }

    private Location getLocation() {
        return mLocation;
    }

    public String getTime(String pattern) {
        if (null == pattern)
            pattern = "yyyyMMddHHmmss";
        SimpleDateFormat df = new SimpleDateFormat(pattern);
        Date date = new Date(getLocation().getTime());
        return df.format(date);
    }

    public double getLatitude() {
        return getLocation().getLatitude();
    }

    public double getLongitude() {
        return getLocation().getLongitude();
    }

    public double getAltitude() {
        return getLocation().getAltitude();
    }

    public double getSpeed() {
        return getLocation().getSpeed();
    }

    public double getBearing() {
        return getLocation().getBearing();
    }

    public double getAccuracy() {
        return getLocation().getAccuracy();
    }

    public byte[] reportLocation(Location location) {
        return reportLocation(location, false);
    }

    public byte[] reportLocation(Location location, boolean isBatch) {
        setLocation(location);

        byte[] alarm = getLocation808ItemAlarmFlag();
        byte[] state = getLocation808ItemState(getLatitude(), getLongitude());
        //DWORD经纬度
        double pow106 = Math.pow(10, 6);
        double lat106 = getLatitude() * pow106;
        double lng106 = getLongitude() * pow106;
        byte[] latb = ByteUtil.longToDword(Math.round(lat106));
        byte[] lngb = ByteUtil.longToDword(Math.round(lng106));
        // WORD 高度 速度 方向
        byte[] gaoChen = BitOperator.numToByteArray((int) getAltitude(), 2);
        byte[] speedb = BitOperator.numToByteArray((int) (getSpeed() * 3.6), 2);
        byte[] orientation = BitOperator.numToByteArray((int) getBearing(), 2);
        //bcd时间
        byte[] bcdTime = ByteUtil.str2Bcd(getTime("yyMMddHHmmss"));
        //位置信息附加项
        byte[] additionLocation = getLocation808ItemAddition(SocketConfig.getmOrderId(), (int) getAltitude(), String.valueOf(getAccuracy()));

        Log.d("123456", toString());
        byte[] body = ByteUtil.byteMergerAll(alarm, state, latb, lngb, gaoChen, speedb, orientation, bcdTime, additionLocation);

        //非批量
        if (!isBatch) {
            return JTT808Coding.generate808(0x0200, SocketConfig.getmPhont(), body);
        }

        if (LOCATION_BATCH_SIZE > mBatchLocationByteList.size()) {
            mBatchLocationByteList.add(body);
            return null;
        }
        byte[] counts = ByteUtil.int2Word(LOCATION_BATCH_SIZE); //数据项个数
        byte[] batchType = {0}; //位置数据类型 0：正常位置批量汇报，1：盲区补报

        List<byte[]> formatLocations = new ArrayList<>();
        for (int i = 0; i < mBatchLocationByteList.size(); i++) {
            byte[] cLocation = mBatchLocationByteList.get(i);
            byte[] clocationLength = ByteUtil.int2Word(cLocation.length); //位置汇报数据体长度
            formatLocations.add(ByteUtil.byteMergerAll(clocationLength, cLocation));
        }

        byte[] batchLocations = ByteUtil.byteMergerAll(formatLocations); //位置汇报数据体
        mBatchLocationByteList.clear();
        return JTT808Coding.generate808(0x0704, SocketConfig.getmPhont(), ByteUtil.byteMergerAll(counts, batchType, batchLocations));
    }


    /**
     * 位置信息状态项
     *
     * @param
     * @return
     */
    private static byte[] getLocation808ItemState(double lat, double lng) {
        String state = "00";
        state = state + (lat < 0 ? "1" : "0");
        state = state + (lng < 0 ? "1" : "0");
        state = state + "0000000000000000000000000000";
        byte[] stateByte = ByteUtil.int2DWord(Integer.parseInt(state, 2));
        return stateByte;
    }


    /**
     * 位置信息附加项
     *
     * @param order    订单号
     * @param altitude 高度
     * @param accuracy 精度
     * @return
     */
    private byte[] getLocation808ItemAddition(String order, int altitude, String accuracy) {
        byte[] orderType = new byte[2]; //订单号
        orderType[0] = (byte) (0xE1);
        orderType[1] = ByteUtil.int2Byte(order.getBytes().length);
        byte[] orderMsg = ByteUtil.byteMergerAll(orderType, order.getBytes());

        byte[] altitudeType = new byte[3]; //高度的正负
        altitudeType[0] = (byte) (0xE2);
        altitudeType[1] = (byte) (0x01);
        altitudeType[2] = (byte) (altitude < 0 ? 0x31 : 0x30);

        byte[] accuracyType = new byte[2]; //订单号
        accuracyType[0] = (byte) (0xE3);
        accuracyType[1] = ByteUtil.int2Byte(accuracy.getBytes().length);
        byte[] accuracyMsg = ByteUtil.byteMergerAll(accuracyType, accuracy.getBytes());

        return ByteUtil.byteMergerAll(orderMsg, altitudeType, accuracyMsg);
    }

    public void setCapOffAlarmFlag(int val) {
        AlarmFlags[INDEX_CAP_OFF_ALARM_FLAG] = val;
    }

    public void setSOSAlarmFlag(int val) {
        AlarmFlags[INDEX_SOS_ALARM_FLAG] = val;
    }

    public void setNearPowerAlarmFlag(int val) {
        AlarmFlags[INDEX_NEAR_POWER_ALARM_FLAG] = val;
    }

    public void setEntryAndExitAlarmFlag(int val) {
        AlarmFlags[INDEX_ENTRY_AND_EXIT_ALARM_FLAG] = val;
    }

    public void setGasAlarmFlag(int val) {
        AlarmFlags[INDEX_GAS_ALARM_FLAG] = val;
    }

    public void setGasMethaneAlarmFlag(int val) {
        AlarmFlags[INDEX_GAS_METHANE_ALARM_FLAG] = val;
    }

    public void setGasSulfurHexafluorideAlarmFlag(int val) {
        AlarmFlags[INDEX_GAS_SULFUR_HEXAFLUORIDE_ALARM_FLAG] = val;
    }

    public void setGasCarbonMonoxideAlarmFlag(int val) {
        AlarmFlags[INDEX_GAS_CARBON_MONOXIDE_ALARM_FLAG] = val;
    }

    public void setFallAlarmFlag(int val) {
        AlarmFlags[INDEX_FALL_ALARM_FLAG] = val;
    }

    public int getCapOffAlarmFlag() {
        return AlarmFlags[INDEX_CAP_OFF_ALARM_FLAG];
    }

    public int getSOSAlarmFlag() {
        return AlarmFlags[INDEX_SOS_ALARM_FLAG];
    }

    public int getNearPowerAlarmFlag() {
        return AlarmFlags[INDEX_NEAR_POWER_ALARM_FLAG];
    }

    public int getEntryAndExitAlarmFlag() {
        return AlarmFlags[INDEX_ENTRY_AND_EXIT_ALARM_FLAG];
    }

    public int getGasAlarmFlag() {
        return AlarmFlags[INDEX_GAS_ALARM_FLAG];
    }

    public int getGasMethaneAlarmFlag() {
        return AlarmFlags[INDEX_GAS_METHANE_ALARM_FLAG];
    }//甲烷气体报警标志位

    public int getGasSulfurHexafluorideAlarmFlag() {
        return AlarmFlags[INDEX_GAS_SULFUR_HEXAFLUORIDE_ALARM_FLAG];
    }//六氟化硫气体报警标志位

    public int getGasCarbonMonoxideAlarmFlag() {
        return AlarmFlags[INDEX_GAS_CARBON_MONOXIDE_ALARM_FLAG];
    }//一氧化碳气体报警标志位

    public int getFallAlarmFlag() {
        return AlarmFlags[INDEX_FALL_ALARM_FLAG];
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(1024);
        sb.append("\nget = ").append(getTime(null));
        sb.append("\ngetLongitude = ").append(getLongitude());
        sb.append("\ngetLatitude = ").append(getLatitude());
        sb.append("\ngetAltitude = ").append(getAltitude());
        sb.append("\ngetSpeed = ").append(getSpeed());
        sb.append("\ngetLocation808ItemAlarmFlag = ").append(ByteUtil.bytes2Heb(getLocation808ItemAlarmFlag()));
        sb.append("\ngetLocation808ItemState = ").append(getLocation808ItemState(getLatitude(), getLongitude()));
        sb.append("\ngetLocation808ItemAddition = ").append(getLocation808ItemAddition(SocketConfig.getmOrderId(), (int) getAltitude(), String.valueOf(getAccuracy())));
        return sb.toString();
    }
}
