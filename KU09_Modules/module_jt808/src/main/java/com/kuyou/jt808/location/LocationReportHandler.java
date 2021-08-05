package com.kuyou.jt808.location;

import android.location.Location;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import androidx.annotation.NonNull;

import com.kuyou.jt808.location.base.ILocationProvider;

/**
 * action :位置上报器
 * <p>
 * remarks:  <br/>
 * author: wuguoxian <br/>
 * date: 21-4-12 <br/>
 * </p>
 */
public class LocationReportHandler extends Handler{
    protected final String TAG = "com.kuyou.jt808 > " + this.getClass().getSimpleName();

    public static final int MSG_REPORT_LOCATION = 2;

    private static LocationReportHandler sMain;

    private int mReportLocationFreq = 5000;

    private ILocationProvider mLocationProvider;
    private IOnLocationReportCallBack mLocationReportCallBack;

    private LocationReportHandler() {
        super();
    }

    private LocationReportHandler(Looper looper) {
        super(looper);
    }

    public static LocationReportHandler getInstance(Looper looper) {
        if (null == sMain) {
            sMain = new LocationReportHandler(looper);
        }
        return sMain;
    }

    public LocationReportHandler setLocationProvider(ILocationProvider locationProvider) {
        mLocationProvider = locationProvider;
        return LocationReportHandler.this;
    }

    public LocationReportHandler setLocationReportCallBack(IOnLocationReportCallBack locationReportCallBack) {
        mLocationReportCallBack = locationReportCallBack;
        return LocationReportHandler.this;
    }

    @Override
    public void handleMessage(@NonNull Message msg) {
        super.handleMessage(msg);
        if (MSG_REPORT_LOCATION != msg.what)
            return;
        removeMessages(msg.what);
        onLocationReport();
        sendEmptyMessageDelayed(MSG_REPORT_LOCATION, getReportLocationFreq());
    }

    public void start() {
        removeMessages(MSG_REPORT_LOCATION);
        sendEmptyMessage(MSG_REPORT_LOCATION);
    }

    public void stop() {
        removeMessages(MSG_REPORT_LOCATION);
    }

    public int getReportLocationFreq() {
        return mReportLocationFreq;
    }

    public void setReportLocationFreq(int val) {
        mReportLocationFreq = val;
    }

    private void onLocationReport() {
        if (null == mLocationReportCallBack) {
            Log.e(TAG, "onLocationReport > process fail : mCallBack is null");
            return;
        }
        if (null == mLocationProvider) {
            Log.e(TAG, "onLocationReport > process fail : mLocationProvider is null");
            return;
        }
        mLocationReportCallBack.onLocationReport(mLocationProvider.getLocation());
    }

    public static interface IOnLocationReportCallBack {
        public void onLocationReport(Location location);
    }
}
