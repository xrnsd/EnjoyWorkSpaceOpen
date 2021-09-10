package com.kuyou.avc.ui;

import android.Manifest;
import android.os.Bundle;

import com.kuyou.avc.R;

import kuyou.common.ku09.ui.BasicPermissionsHandlerActivity;

public class MainActivity extends BasicPermissionsHandlerActivity {

    protected final String TAG = "com.kuyou.peergine > MainActivity";

    private static final String KEY_HSM_BOOT_MODE = "key.hsm.boot.mode";

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