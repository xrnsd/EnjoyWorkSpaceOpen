package com.kuyou.ft.item;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.TextView;

import com.kuyou.ft.R;
import com.kuyou.ft.basic.TestItemBasic;

public class TestItemLSensor extends TestItemBasic {

    private float[] x;

    private TextView mTvShow;
    private SensorEventListener mLightSensorListener;
    private SensorManager mSensorManager;

    @Override
    public int getTestId() {
        return R.id.test_lsensor;
    }

    @Override
    public int getSubContentId() {
        return R.layout.test_item_lsensor;
    }

    @Override
    public String getTestTitle(Context context) {
        return context.getString(R.string.test_lsensor);
    }

    @Override
    public void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);
        mSensorManager = ((SensorManager) getSystemService("sensor"));
        Sensor localSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        mLightSensorListener = new SensorEventListener() {
            public void onAccuracyChanged(Sensor paramSensor, int paramInt) {
            }

            public void onSensorChanged(SensorEvent paramSensorEvent) {
                x = paramSensorEvent.values;
                mTvShow.setText(getString(R.string.num) + x[0]);
                if (x[0] < 200) {
                    mTvShow.setBackgroundColor(0xFF0000FF);
                } else {
                    mTvShow.setBackgroundColor(0xFF000000);
                }
            }
        };
        mSensorManager.registerListener(mLightSensorListener, localSensor, 2);
    }

    @Override
    protected void initViews() {
        super.initViews();
        mTvShow = findViewById(R.id.show_lsensor);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mSensorManager.unregisterListener(mLightSensorListener);
    }

    @Override
    protected void onAging() {
        super.onAging();
        onAgingTestItem(() -> onResult(true), 8000);
    }
}
