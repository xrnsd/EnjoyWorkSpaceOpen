package com.kuyou.ft.item;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Arrays;

import com.kuyou.ft.R;
import com.kuyou.ft.basic.TestItemBasic;

public class TestItemGsensor extends TestItemBasic {

    private float x, y, z;

    private ImageView mIVShow;
    private TextView mGsensorXYZ;
    private SensorManager mSensorManager;
    private SensorEventListener mGSensorListener;

    @Override
    public int getTestId() {
        return R.id.test_gsensor;
    }

    @Override
    public int getSubContentId() {
        return R.layout.test_item_gsensor;
    }

    @Override
    public String getTestTitle(Context context) {
        return context.getString(R.string.test_gsensor);
    }

    @Override
    public void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);
        mSensorManager = ((SensorManager) getSystemService("sensor"));
        Sensor localSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mGSensorListener = new SensorEventListener() {
            public void onAccuracyChanged(Sensor paramSensor, int paramInt) {
            }

            public void onSensorChanged(SensorEvent paramSensorEvent) {
                x = paramSensorEvent.values[0];
                y = paramSensorEvent.values[1];
                z = paramSensorEvent.values[2];
                mGsensorXYZ.setText(getString(R.string.num) + x + ",  " + y + ",  " + z);

                int i = (int) x;
                int j = (int) y;
                int k = (int) z;
                int m = getMax(i, j, k);
                if (Math.abs(i) == m) {
                    if (i < 0)//x direction
                        mIVShow.setImageResource(R.drawable.gsensor_x_2);
                    else
                        mIVShow.setImageResource(R.drawable.gsensor_x);
                } else if (j == m) {
                    mIVShow.setImageResource(R.drawable.gsensor_y);
                } else
                    mIVShow.setImageResource(R.drawable.gsensor_z);
            }
        };
        mSensorManager.registerListener(mGSensorListener, localSensor, 1);
    }

    @Override
    protected void initViews() {
        super.initViews();
        mIVShow = findViewById(R.id.show_gsensor);
        mGsensorXYZ = findViewById(R.id.gsensor_xyz);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSensorManager.unregisterListener(mGSensorListener);
    }

    private int getMax(int paramInt1, int paramInt2, int paramInt3) {
        int[] arrayOfInt = new int[3];
        arrayOfInt[0] = Math.abs(paramInt1);
        arrayOfInt[1] = paramInt2;
        arrayOfInt[2] = paramInt3;
        Arrays.sort(arrayOfInt);
        return arrayOfInt[(arrayOfInt.length - 1)];
    }

    @Override
    protected void onAging() {
        super.onAging();
        onAgingTestItem(() -> onResult(true), 8000);
    }

}
