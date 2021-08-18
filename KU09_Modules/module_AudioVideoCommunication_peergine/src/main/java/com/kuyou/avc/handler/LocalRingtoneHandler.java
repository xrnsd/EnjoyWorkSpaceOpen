package com.kuyou.avc.handler;

import android.content.Context;
import android.media.MediaPlayer;

import com.kuyou.avc.R;

/**
 * action :
 * <p>
 * remarks:  <br/>
 * author: wuguoxian <br/>
 * date: 21-8-7 <br/>
 * </p>
 */
public class LocalRingtoneHandler extends RingtoneHandler {

    protected MediaPlayer mPlayer;

    public LocalRingtoneHandler(Context context) {
        super(context);
    }

    @Override
    protected void init() {
        mPlayer = MediaPlayer.create(getContext(), R.raw.ring_htc_win8);
        mPlayer.setVolume(5.0f, 5.0f);
    }

    @Override
    public void play() {
        mPlayer.start();
        mPlayer.setLooping(true);
    }

    @Override
    public void stop() {
        mPlayer.pause();
    }

    @Override
    public void exit() {
        mPlayer.stop();
        mPlayer.release();
    }

}
