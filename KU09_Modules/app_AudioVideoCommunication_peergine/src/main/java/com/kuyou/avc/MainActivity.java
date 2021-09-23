package com.kuyou.avc;

import android.Manifest;
import android.os.Bundle;

import kuyou.common.ku09.ui.BasicPermissionsHandlerActivity;

public class MainActivity extends BasicPermissionsHandlerActivity {

    protected final String TAG = "com.kuyou.avc.ui > MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("");
        onBackPressed();
    }

    @Override
    protected String[] getPermissions() {
        return new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.READ_PHONE_STATE
        };
    }

    @Override
    protected int getContentViewResId() {
        return R.layout.activity_main;
    }
}