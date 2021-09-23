package com.kuyou.ft.item;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.WindowManager;

import com.kuyou.ft.R;
import com.kuyou.ft.basic.TestItemBasic;

public class TestItemReceiver extends TestItemBasic {

    private AudioManager mAudioManager;
    private MediaPlayer mMediaPlayer;

    @Override
    public int getTestId() {
        return R.id.test_receiver;
    }

    @Override
    public int getSubContentId() {
        return R.layout.test_item_receiver;
    }

    @Override
    public String getTestTitle(Context context) {
        return context.getString(R.string.test_receiver);
    }

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        mAudioManager = ((AudioManager) getSystemService("audio"));
        mAudioManager.setMode(AudioManager.MODE_IN_CALL);
        mMediaPlayer = MediaPlayer.create(this, R.raw.mp3_1khz);
        mMediaPlayer.setLooping(true);
        mMediaPlayer.start();
    }

    @Override
    protected void initWindowConfig() {
        //super.initWindowConfig();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    @Override
    protected void onAging() {
        super.onAging();
        onAgingTestItem(() -> onResult(true), 5000);
    }

    @Override
    public void onPause() {
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
        mAudioManager.setMode(AudioManager.MODE_NORMAL);
        super.onPause();
    }
}
