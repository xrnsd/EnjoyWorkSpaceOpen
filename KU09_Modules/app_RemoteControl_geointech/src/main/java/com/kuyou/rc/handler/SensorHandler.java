package com.kuyou.rc.handler;

import android.content.Context;
import android.hardware.SensorManager;
import android.util.Log;

import com.kuyou.rc.basic.sensor.BasicAssistSensorEventDiscriminator;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import kuyou.common.ipc.RemoteEvent;
import kuyou.common.ku09.event.common.basic.EventCommon;
import kuyou.common.ku09.event.rc.alarm.EventAlarm;
import kuyou.common.ku09.handler.BasicAssistHandler;
import kuyou.common.utils.ClassUtils;

/**
 * action :
 * <p>
 * remarks:  <br/>
 * author: wuguoxian <br/>
 * date: 21-10-14 <br/>
 * </p>
 */
public class SensorHandler extends BasicAssistHandler implements BasicAssistSensorEventDiscriminator.IDispatchSensorEventCallback {

    protected final String TAG = "com.kuyou.rc.handler > SensorHandler";

    private Map<Integer, BasicAssistSensorEventDiscriminator> mSensorEventDiscriminatorList =
            new HashMap<Integer, BasicAssistSensorEventDiscriminator>();

    private SensorManager mSensorManager = null;

    private boolean isStart = false;

    @Override
    public void start() {
        super.start();
        isStart = true;
        initSensors();
    }

    @Override
    public void stop() {
        super.stop();
        if (!isStart || null == mSensorManager || mSensorEventDiscriminatorList.size() == 0) {
            return;
        }
        isStart = false;
        synchronized (mSensorEventDiscriminatorList) {
            Set<Integer> set = mSensorEventDiscriminatorList.keySet();
            Iterator<Integer> it = set.iterator();
            while (it.hasNext()) {
                final int sensorType = it.next();
                mSensorManager.unregisterListener(mSensorEventDiscriminatorList.get(sensorType));
            }
            mSensorEventDiscriminatorList.clear();
        }
    }

    public boolean isStart() {
        return isStart;
    }

    protected void initSensors() {
        mSensorManager = (SensorManager) getContext().getSystemService(Context.SENSOR_SERVICE);

        Log.d(TAG, "enableForegroundMode > ");
        if (mSensorEventDiscriminatorList.size() > 0) {
            Log.e(TAG, "initSedList > process warn : mSensorEventDiscriminatorList is not null");
            return;
        }
        try {
            BasicAssistSensorEventDiscriminator instruction;
            for (Class item : ClassUtils.getAllClasses(getContext().getApplicationContext(), BasicAssistSensorEventDiscriminator.class)) {
                instruction = (BasicAssistSensorEventDiscriminator) item.newInstance();
                if (!instruction.isEnable()) {
                    continue;
                }
                instruction.setDispatchSensorEventCallback(SensorHandler.this);

                mSensorManager.registerListener(instruction,
                        mSensorManager.getDefaultSensor(instruction.getSensorType()),
                        SensorManager.SENSOR_DELAY_NORMAL);

                mSensorEventDiscriminatorList.put(Integer.valueOf(instruction.getSensorType()), instruction);
            }
        } catch (Exception e) {
            Log.e(TAG, Log.getStackTraceString(e));
            Log.e(TAG, "load > process fail : load instruction parser");
        }
    }

    @Override
    public void dispatchSensorEvent(RemoteEvent event) {
        SensorHandler.this.dispatchEvent(event);
    }
}
