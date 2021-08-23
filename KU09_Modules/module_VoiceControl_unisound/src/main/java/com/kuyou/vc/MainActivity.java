package com.kuyou.vc;

import android.Manifest;
import kuyou.common.ku09.ui.BasicPermissionsHandlerActivity;

public class MainActivity extends BasicPermissionsHandlerActivity {
    protected final String TAG = "com.kuyou.voicecontrol > MainActivity";

    @Override
    protected int getContentViewResId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initViews() {
        super.initViews();
        setTitle("");
        onBackPressed();
    }

    @Override
    protected String[] getPermissions() {
        return new String[]{
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.READ_CONTACTS
        };
    }
}
