package com.kuyou.rc.location;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.kuyou.rc.location.amap.LocationActivity;

/**
 * action :
 * <p>
 * remarks:  <br/>
 * author: wuguoxian <br/>
 * date: 21-7-28 <br/>
 * </p>
 */
public class AMapLocationProvider extends HMLocationProvider implements Application.ActivityLifecycleCallbacks {

    protected final String TAG = "com.kuyou.rc.location > AMapLocationProvider";

    boolean isPositioningStart = false;

    Activity mLocationProviderReal;

    public AMapLocationProvider(Context context) {
        super(context);
    }

    @Override
    protected void init() {
    }

    @Override
    public void start() {
        Log.d(TAG, "start > ");
        try {
            Intent intent = new Intent();
            intent.setClass(getContext(), LocationActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            getContext().startActivity(intent);
        } catch (Exception e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }
    }

    @Override
    public boolean isStart() {
        return null != mLocationProviderReal && !mLocationProviderReal.isDestroyed();
    }

    @Override
    public void stop() {
        if (null != mLocationProviderReal && !mLocationProviderReal.isDestroyed()) {
            mLocationProviderReal.finish();
            mLocationProviderReal = null;
        }

    }

    @Override
    public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {
        if (!(activity instanceof LocationActivity)) {
            return;
        }
        mLocationProviderReal = activity;
    }

    @Override
    public void onActivityStarted(@NonNull Activity activity) {

    }

    @Override
    public void onActivityResumed(@NonNull Activity activity) {

    }

    @Override
    public void onActivityPaused(@NonNull Activity activity) {

    }

    @Override
    public void onActivityStopped(@NonNull Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(@NonNull Activity activity) {
        if (!(activity instanceof LocationActivity)) {
            return;
        }
        mLocationProviderReal = null;
    }
}
