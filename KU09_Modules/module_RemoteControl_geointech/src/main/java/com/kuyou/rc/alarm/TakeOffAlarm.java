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
public class TakeOffAlarm extends AlarmHandler {
//
//    @Override
//    protected int getSensorType() {
//        return Sensor.TYPE_PROXIMITY;
//    }
//
//    @Override
//    public void onSensorChanged(SensorEvent event) {
//        if (event.values[0] == 0.0) {
//            Log.d(TAG, "handleProximitySensorData > 靠近");
//        } else {
//            Log.d(TAG, "handleProximitySensorData > 远离");
//            sendMsgCapOffAlarm();
//        }
//    }

    protected LocationInfo getLocationInfo() {
        return null;
    }

    protected void sendMsgCapOffAlarm() {
        Log.d(TAG, " sendMsgCapOffAlarm");
        getLocationInfo().setAlarmFlag(FLAG_CAP_OFF);
    }
}
