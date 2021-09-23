package com.kuyou.ft.item;

import android.content.Context;
import android.content.Intent;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.Chronometer;
import android.widget.TextView;

import java.util.Iterator;

import com.kuyou.ft.R;
import com.kuyou.ft.basic.TestItemBasic;

public class TestItemGps extends TestItemBasic implements GpsStatus.Listener, LocationListener {

    private int mNumberSatellites = 0;
    private int[] mSingleStrength = new int[128];

    private TextView mTvShow;
    private LocationManager mLocationManager;
    private Chronometer mGpsSearchTime;

    @Override
    public int getTestId() {
        return R.id.test_gps;
    }

    @Override
    public int getSubContentId() {
        return R.layout.test_item_gps;
    }

    @Override
    public String getTestTitle(Context context) {
        return context.getString(R.string.test_gps);
    }

    @Override
    public void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);

        mNumberSatellites = 0;

        mLocationManager = ((LocationManager) getSystemService("location"));
        if (mLocationManager != null) {
            mLocationManager.requestLocationUpdates("gps", 0, 0, TestItemGps.this);
            mLocationManager.addGpsStatusListener(TestItemGps.this);
        } else {
            Log.w(TAG, "new mLocationManager failed");
        }

        if (mLocationManager.isProviderEnabled("gps")) {
            mTvShow.setText(getString(R.string.gps_open));
        } else {
            Settings.Secure.setLocationProviderEnabled(getContentResolver(), LocationManager.GPS_PROVIDER, true);
            mTvShow.setText(getString(R.string.gps_open));
        }
        mGpsSearchTime.start();

        if (!isTestModeAging()) {
            try {
                Intent localIntent = new Intent();
                localIntent.setClassName("com.mediatek.ygps", "com.mediatek.ygps.YgpsActivity");
                startActivity(localIntent);
            } catch (Exception e) {
                Log.e(TAG, "Start Ygps error:" + e);
            }
        }
    }

    @Override
    protected void initWindowConfig() {
        super.initWindowConfig();
    }

    @Override
    protected void initViews() {
        super.initViews();
        mTvShow = findViewById(R.id.gps_show);
        mGpsSearchTime = findViewById(R.id.gps_time_id);
        mGpsSearchTime.setFormat(getString(R.string.GPS_time));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mGpsSearchTime.stop();
        mLocationManager.removeUpdates(TestItemGps.this);
        mLocationManager.removeGpsStatusListener(TestItemGps.this);
        mNumberSatellites = 0;
        Log.d(TAG, "onDestroy");
    }

    @Override
    public void onGpsStatusChanged(int event) {
        if (event == GpsStatus.GPS_EVENT_SATELLITE_STATUS) {
            GpsStatus status = mLocationManager.getGpsStatus(null);
            showGPS();
        } else {
            mTvShow.setText(getString(R.string.gps_searching));
        }
    }

    @Override
    public void onLocationChanged(Location location) {
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onProviderDisabled(String provider) {
    }

    private void showGPS() {
        if (mLocationManager.isProviderEnabled("gps")) {
            Iterator localIterator = mLocationManager.getGpsStatus(null).getSatellites().iterator();
            mNumberSatellites = 0;
            while (localIterator.hasNext()) {
                float f = ((GpsSatellite) localIterator.next()).getSnr();
                if (f > 20.0F) {
                    mSingleStrength[mNumberSatellites] = (int) f;
                    mNumberSatellites = (1 + mNumberSatellites);
                }
            }
            String signal = "";
            for (int i = 0; i < mNumberSatellites; i++) {
                signal = signal + String.valueOf(mSingleStrength[i]) + ", ";
            }
            mTvShow.setText(getString(R.string.gps_num) + mNumberSatellites + "\n" + getString(R.string.gps_signal) + signal);
        } else {
            mTvShow.setText(getString(R.string.gps_no));
            onAgingTestItem(() -> onResult(false), 1500);
        }
    }

    @Override
    protected void onAging() {
        super.onAging();
        onAgingTestItem(() -> onResult(true), 10000);
    }
}
