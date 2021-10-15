package com.kuyou.rc.basic.sensor;

import android.hardware.Sensor;
import android.hardware.SensorEventListener;
import android.util.Log;

import kuyou.common.ipc.RemoteEvent;

/**
 * action :脱帽检测
 * <p>
 * remarks:  <br/>
 * author: wuguoxian <br/>
 * date: 21-10-14 <br/>
 * </p>
 */
public abstract class BasicAssistSensorEventDiscriminator implements SensorEventListener {
    protected final String TAG = "com.kuyou.rc.basic.sensor > BasicAssistSensorEventDiscriminator";

    public static interface IDispatchSensorEventCallback {
        public void dispatchSensorEvent(RemoteEvent event);
    }

    private IDispatchSensorEventCallback mDispatchSensorEventCallback;

    public abstract int getSensorType();
    
    protected abstract void dispatchEventResult();
    
    public boolean isEnable(){
        return true;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public void dispatchSensorEvent(RemoteEvent event) {
        if (null == getDispatchSensorEventCallback()) {
            Log.e(TAG, " dispatchSensorEvent > process fail : getDispatchSensorEventCallback is null");
            return;
        }
        getDispatchSensorEventCallback().dispatchSensorEvent(event);
    }

    public IDispatchSensorEventCallback getDispatchSensorEventCallback() {
        return mDispatchSensorEventCallback;
    }

    public BasicAssistSensorEventDiscriminator setDispatchSensorEventCallback(IDispatchSensorEventCallback callback) {
        mDispatchSensorEventCallback = callback;
        return BasicAssistSensorEventDiscriminator.this;
    }
}
