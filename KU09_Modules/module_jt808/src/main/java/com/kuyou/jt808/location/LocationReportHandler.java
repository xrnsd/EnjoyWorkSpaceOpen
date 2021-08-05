package com.kuyou.jt808.location;

import android.location.Location;
import android.location.LocationManager;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import androidx.annotation.NonNull;

import com.kuyou.jt808.info.LocationInfo;

/**
 * action :
 * <p>
 * remarks:  <br/>
 * author: wuguoxian <br/>
 * date: 21-4-12 <br/>
 * </p>
 */
public class LocationReportHandler extends Handler implements HMLocationProvider.IOnLocationChangeListener {
    protected final String TAG = "com.kuyou.jt808 > " + this.getClass().getSimpleName();

    public static final int MSG_REPORT_LOCATION = 2;

    private static LocationReportHandler sMain;

    private int mReportLocationFreq = 5000;

    private IOnLocationReportCallBack mCallBack;

    private Location mLocation = null, mLocationFake = null;

    private LocationReportHandler() {
        super();
    }

    private LocationReportHandler(Looper looper) {
        super(looper);
    }

    public static LocationReportHandler getInstance(Looper looper, IOnLocationReportCallBack callBack) {
        if (null == sMain) {
            sMain = new LocationReportHandler(looper);
            sMain.mCallBack = callBack;
        }
        return sMain;
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
        if (null == mCallBack) {
            Log.e(TAG, "onLocationReport > process fail : mCallBack is null");
            return;
        }
        Log.d(TAG, "onLocationReport > ");
        mCallBack.onLocationReport(getLocation());
    }

    private Location getLocation() {
        if (null == mLocation) {
            if (null == mLocationFake) {
                mLocationFake = new Location(LocationManager.NETWORK_PROVIDER);
                mLocationFake.setLongitude(113.907817D);
                mLocationFake.setLatitude(22.548229D);
                mLocationFake.setProvider(LocationInfo.CONFIG.FAKE_PROVIDER);
            }
            return mLocationFake;
        }
        return mLocation;
    }

    @Override
    public void onLocationChange(Location location) {
        mLocation = location;
    }

    public static interface IOnLocationReportCallBack {
        public void onLocationReport(Location location);
    }
}
