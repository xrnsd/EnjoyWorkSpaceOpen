package com.kuyou.ft.item;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.WindowManager;

import com.kuyou.ft.R;
import com.kuyou.ft.basic.TestItem;

public class TestItemLoudspeaker extends TestItem {

    private MediaPlayer mMediaPlayer;

    @Override
    public int getTestId() {
        return R.id.test_loudspeaker;
    }

    @Override
    public int getSubContentId() {
        return R.layout.test_item_loudspeaker;
    }

    @Override
    public String getTestTitle(Context context) {
        return context.getString(R.string.loudspeaker_test);
    }

    @Override
    protected void onResult(boolean status) {
        if (mMediaPlayer != null) {
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
        super.onResult(status);
    }

    @Override
    public void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);

        ((AudioManager) getSystemService("audio")).setMode(0);
        mMediaPlayer = MediaPlayer.create(this, R.raw.test);
        mMediaPlayer.setLooping(true);
        mMediaPlayer.start();
    }

    @Override
    protected void initWindowConfig() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    @Override
    protected void onAging() {
        super.onAging();
        onAgingTestItem(() -> onResult(true), 5000);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (this.mMediaPlayer != null) {
            this.mMediaPlayer.release();
            this.mMediaPlayer = null;
        }
    }
}
