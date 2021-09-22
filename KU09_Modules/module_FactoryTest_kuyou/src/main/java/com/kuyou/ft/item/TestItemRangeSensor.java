package com.kuyou.ft.item;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.kuyou.ft.R;
import com.kuyou.ft.basic.TestItemBasic;

public class TestItemRangeSensor extends TestItemBasic {

    private float mValue = -999.0F;

    private TextView mTvShowRange, mTvShowValue;
    private Button mBtnCalibrate;
    private SensorManager mSensorManager;
    private SensorEventListener mRangeSensorListener;

    @Override
    public int getTestId() {
        return R.id.test_rangesensor;
    }

    @Override
    public int getSubContentId() {
        return R.layout.test_item_rangegsensor;
    }

    @Override
    public String getTestTitle(Context context) {
        return context.getString(R.string.test_rangesensor);
    }

    @Override
    public void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);
        mSensorManager = ((SensorManager) getSystemService(Context.SENSOR_SERVICE));
        Sensor localSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);

        //modify chh android9.0的工厂测试读取mValue2会导致程序崩溃所以进行删除
        mRangeSensorListener = new SensorEventListener() {
            public void onAccuracyChanged(Sensor paramSensor, int paramInt) {
            }

            public void onSensorChanged(SensorEvent paramSensorEvent) {
                mValue = paramSensorEvent.values[0];
                if (mValue == 0) {
                    mTvShowRange.setBackgroundColor(0xFF0000FF);
                } else {
                    mTvShowRange.setBackgroundColor(0xFF000000);
                }
                mTvShowRange.setText(getString(R.string.access) + mValue);
            }
        };
        mSensorManager.registerListener(mRangeSensorListener, localSensor, 2);
    }

    @Override
    protected void initViews() {
        super.initViews();
        mTvShowRange = findViewById(R.id.show_rsensor);
        mTvShowValue = findViewById(R.id.show_rsensor_value);
        mBtnCalibrate = findViewById(R.id.rsensor_calibrate);
        mBtnSuccess.setEnabled(true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSensorManager.unregisterListener(mRangeSensorListener);
    }

    @Override
    protected void onAging() {
        super.onAging();
        onAgingTestItem(() -> onResult(true), 8000);
    }
}
