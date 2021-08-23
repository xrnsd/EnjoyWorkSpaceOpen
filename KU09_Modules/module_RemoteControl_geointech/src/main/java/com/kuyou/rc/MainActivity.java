package com.kuyou.rc;

import android.os.Bundle;

import kuyou.common.ku09.ui.BasicPermissionsHandlerActivity;

public class MainActivity extends BasicPermissionsHandlerActivity {

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
