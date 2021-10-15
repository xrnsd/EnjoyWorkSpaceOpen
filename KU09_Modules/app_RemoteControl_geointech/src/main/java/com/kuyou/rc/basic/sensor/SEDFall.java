package com.kuyou.rc.basic.sensor;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.util.Log;

import kuyou.common.ku09.event.rc.alarm.EventAlarmFall;

/**
 * action :跌倒检测
 * <p>
 * remarks:  <br/>
 * author: wuguoxian <br/>
 * date: 21-10-14 <br/>
 * </p>
 */
public class SEDFall extends BasicAssistSensorEventDiscriminator {
    protected final String TAG = "com.kuyou.rc.basic.sensor > FallChecker";

    private float accX, accY, accZ, svm;

    public SEDFall() {
        setThresholdValue(25, 5, 3);
    }

    @Override
    public int getSensorType() {
        return Sensor.TYPE_ACCELEROMETER;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (getSensorType() != event.sensor.getType()) {
            Log.e(TAG, "handleSensorData > process fail : sensor type invalid");
            return;
        }
        accX = event.values[0];
        accY = event.values[1];
        accZ = event.values[2];
        svm = (float) Math.sqrt(accX * accX + accY * accY + accZ * accZ);
//        if (Math.abs(accY) > 20) {
//            dispatchEventResult();
//        } else {
//            Log.d(TAG, "onSensorChanged > accY = " + accY);
//        }

        svmCollector(svm);
    }

    @Override
    protected void dispatchEventResult() {
        dispatchSensorEvent(new EventAlarmFall()
                .setRemote(false));
    }

    //=========================== 算法部分 ===============================

    private float[] mSvmData = new float[150];
    private float[] mSvmFilteringData = new float[150];
    private float mHighThresholdValue;
    private float mLowThresholdValue;
    private int mSvmCount = 0;
    private int mSensorEventTime = 0;
    private int mSensorEventTimeFlag = 0;
    private boolean isFell = false;

    protected void setThresholdValue(float highThreshold, float lowThreshold, int eventTimeFlag) {
        this.mHighThresholdValue = highThreshold;
        this.mLowThresholdValue = lowThreshold;
        mSensorEventTimeFlag = eventTimeFlag;
    }

    protected void resetSvmCache() {
        mSvmData = new float[150];
        mSvmFilteringData = new float[150];
        mSensorEventTime = 0;
        mSvmCount = 0;
    }

    /*
    3s内svm原始数据收集
     */
    protected void svmCollector(float svm) {
        if (mSvmCount < mSvmData.length) {
            mSvmData[mSvmCount] = svm;
        } else {
            mSvmCount = 0;
            mSvmData[mSvmCount] = svm;
        }
        mSvmCount++;

        setSvmFilteringData();
    }

    /*
    svm中值滤波
     */
    protected void setSvmFilteringData() {
        //中值滤波取的三个值
        float s1, s2, s3, temp;
        //冒泡排序
        for (int i = 0, count = mSvmFilteringData.length; i < count - 1; i++) {
            if (i == 0) {
                s1 = mSvmData[i];
                s2 = mSvmData[i + 1];
                s3 = mSvmData[i + 2];
            } else if (i < mSvmFilteringData.length - 2) {
                s1 = mSvmData[i - 1];
                s2 = mSvmData[i];
                s3 = mSvmData[i + 1];
            } else {
                s1 = mSvmData[i - 1];
                s2 = mSvmData[i];
                s3 = mSvmData[0];
            }
            if (s1 > s2) {
                temp = s1;
                s1 = s2;
                s2 = temp;
            }
            if (s2 > s3) {
                temp = s2;
                s2 = s3;
                s3 = temp;
            }
            mSvmFilteringData[i] = s2;

            //Log.d(TAG, s1 + " " + s2 + " " + s3);
        }

        discriminatorFall();
    }

    protected void discriminatorFall() {
        if (mSensorEventTime < mSensorEventTimeFlag) {
            mSensorEventTime += 1;
            return;
        }
        mSensorEventTime = 0;
        //阈值法
        boolean isFall = false;
        for (int i = 0,j=0,k=0,count = mSvmFilteringData.length; i < count; i++) {

            if (mSvmFilteringData[i] <= mLowThresholdValue) {
                if (i < count - 10) {
                    for (j = i; j < i + 10; j++) {
                        if (mSvmFilteringData[j] >= mHighThresholdValue) {
                            isFall = true;
                            break;
                        }
                    }
                } else {
                    for (j = i; j < count; j++) {
                        if (mSvmFilteringData[j] >= mHighThresholdValue) {
                            isFall = true;
                            break;
                        }
                    }
                    for (k = 0; k < (10 - (count - i)); k++) {
                        if (mSvmFilteringData[k] >= mHighThresholdValue) {
                            isFall = true;
                            break;
                        }
                    }
                }
            }

            if (isFall) {
                resetSvmCache();
                dispatchEventResult();
            }
        }
    }
}
