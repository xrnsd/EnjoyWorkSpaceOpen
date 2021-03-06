package com.kuyou.jt808.alarm;

import android.hardware.Sensor;
import android.hardware.SensorEvent;

import com.kuyou.jt808.info.LocationInfo;

/**
 * action :
 * <p>
 * remarks:  <br/>
 * author: wuguoxian <br/>
 * date: 21-4-12 <br/>
 * </p>
 */
public class CrashOrFallAlarm extends AlarmHandler {
    protected float mAccelerometerX, mAccelerometerY, mAccelerometerZ;

    @Override
    protected int getSensorType() {
        return Sensor.TYPE_ACCELEROMETER;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        mAccelerometerY = event.values[1];
        if (Math.abs(mAccelerometerY) > 20) {
            sendMsgFallAlarm();
        }
    }

    protected LocationInfo getLocationInfo() {
        return null;
    }

    protected void sendMsgFallAlarm() {
        getLocationInfo().setAlarmFlag(FLAG_FALL);
    }
}
