package com.kuyou.rc;

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
        return R.layout.activity_main;
    }
}
