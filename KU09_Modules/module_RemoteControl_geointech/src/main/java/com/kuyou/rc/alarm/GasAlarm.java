package com.kuyou.rc.alarm;

import android.util.Log;

import com.kuyou.rc.info.LocationInfo;

/**
 * action :
 * <p>
 * remarks:  <br/>
 * author: wuguoxian <br/>
 * date: 21-4-12 <br/>
 * </p>
 */
public class GasAlarm extends AlarmHandler {
    private static final String PORT_DEV_PATH = "/dev/ttyS1";
    private static final String PORT_BAUDRATE = "9600";

    private int mGasAlarmFlag = ALARM.FLAG_GAS;

//    @Override
//    protected int getSensorType() {
//        return -1;
//    }
//
//    @Override
//    public void init(Context context) {
//        super.init(context);
//
//
//    }

    private void parseData(byte[] data) {
        mGasAlarmFlag = ALARM.FLAG_GAS_CARBON_MONOXIDE;
    }

    public void close() {
    }

    private void resetGasAlarmFlag() {
        mGasAlarmFlag = ALARM.FLAG_GAS;
    }

    protected LocationInfo getLocationInfo() {
        return null;
    }

    protected void sendMsgGasAlarm() {
        Log.d(TAG, " sendMsgGasAlarm");
        //getLocationInfo().setAlarmFlag(FLAG_GAS_CARBON_MONOXIDE);
        getLocationInfo().setAlarmFlag(FLAG_GAS);
    }
}
