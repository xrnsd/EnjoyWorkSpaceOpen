package com.kuyou.rc.ui;

import android.os.Build;
import android.os.Bundle;

import com.kuyou.rc.R;

import kuyou.common.ku09.ui.BasicPermissionsHandlerActivity;

public class MainActivity extends BasicPermissionsHandlerActivity {

    private static final String TAG = "com.kuyou.rc.ui > MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("");
        onBackPressed();
    }

    @Override
    protected String[] getPermissions() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P
                && getApplicationInfo().targetSdkVersion > 28) {
            return new String[]{
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE,

                    android.Manifest.permission.READ_PHONE_STATE,

                    android.Manifest.permission.ACCESS_COARSE_LOCATION,
                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.ACCESS_BACKGROUND_LOCATION
            };
        }
        return new String[]{
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                android.Manifest.permission.READ_EXTERNAL_STORAGE,

                android.Manifest.permission.READ_PHONE_STATE,

                android.Manifest.permission.ACCESS_COARSE_LOCATION,
                android.Manifest.permission.ACCESS_FINE_LOCATION
        };
    }

    @Override
    protected int getContentViewResId() {
        return R.layout.activity_main;
    }
}
