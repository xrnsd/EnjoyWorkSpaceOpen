package com.kuyou.openlive;

import android.Manifest;
import android.os.Bundle;

import kuyou.common.ku09.ui.BasePermissionsActivity;

public class MainActivity extends BasePermissionsActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("");
        onBackPressed();
    }

    @Override
    protected int getContentViewResId() {
        return R.layout.activity_main_translucent;
    }

    @Override
    protected String[] getPermissions() {
        return new String[]{
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS
        };
    }
}
