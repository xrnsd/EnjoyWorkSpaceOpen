package com.kuyou.vc;

import com.kuyou.vc.base.VoiceControlBaseActivity;

public class MainActivity extends VoiceControlBaseActivity {
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
}
