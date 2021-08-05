package com.kuyou.rc.alarm;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

import kuyou.common.ku09.BaseHandler;

/**
 * action :
 * <p>
 * remarks:  <br/>
 * author: wuguoxian <br/>
 * date: 21-4-12 <br/>
 * </p>
 */
public abstract class SensorHandler implements SensorEventListener {
    protected final String TAG = "com.kuyou.rc.alarm > " + this.getClass().getSimpleName();

    private SensorManager mSensorManager;

    public void init(Context context) {
        if (getSensorType() <= 0) {
            Log.d(TAG, "init > invalid sensorType = " + getSensorType());
            return;
        }
        if (null != mSensorManager) {
            Log.e(TAG, "init > process fail : mSensorManager is`not null");
            return;
        }
        mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        mSensorManager.registerListener(SensorHandler.this,
                mSensorManager.getDefaultSensor(getSensorType()), mSensorManager.SENSOR_DELAY_GAME);
    }

    public void close() {
        if (null == mSensorManager)
            return;
        mSensorManager.unregisterListener(SensorHandler.this);
    }

    private boolean isSwitch = false;

    public boolean isSwitch() {
        return isSwitch;
    }

    public void setSwitch(boolean val) {
        isSwitch = val;
    }

    protected void sendEvent(int eventCode) {

    }

    @Override
    public void onSensorChanged(SensorEvent event) {

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    protected abstract int getSensorType();
}
