package com.kuyou.rc.basic.sensor;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.util.Log;

import kuyou.common.ku09.event.rc.alarm.EventAlarmCapOff;
import kuyou.common.ku09.event.rc.alarm.EventAlarmFall;

/**
 * action :脱帽检测
 * <p>
 * remarks:  <br/>
 * author: wuguoxian <br/>
 * date: 21-10-14 <br/>
 * </p>
 */
public class SEDCapOff extends BasicAssistSensorEventDiscriminator {
    protected final String TAG = "com.kuyou.rc.basic.sensor > CapOffChecker";

    private static boolean isNeared = false;

    @Override
    public int getSensorType() {
        return Sensor.TYPE_PROXIMITY;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (getSensorType() != event.sensor.getType()) {
            Log.e(TAG, "handleSensorData > process fail : sensor type invalid");
            return;
        }
        if (event.values[0] == 0.0) {
            isNeared = true;
        } else {
            if (isNeared) {
                dispatchEventResult();
            }
            isNeared = false;
        }
    }

    @Override
    protected void dispatchEventResult() {
        dispatchSensorEvent(new EventAlarmCapOff()
                .setRemote(false));
    }
}
