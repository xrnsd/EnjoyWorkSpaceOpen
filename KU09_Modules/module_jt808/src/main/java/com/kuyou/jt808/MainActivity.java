package com.kuyou.jt808;

import android.os.Bundle;

import com.kuyou.jt808.location.LocationActivity;

import kuyou.common.ipc.RemoteEvent;

public class MainActivity extends LocationActivity {

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

    @Override
    protected void dispatchEvent(RemoteEvent event) {
    }
}
