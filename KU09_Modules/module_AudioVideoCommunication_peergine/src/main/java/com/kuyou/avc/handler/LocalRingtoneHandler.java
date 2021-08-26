package com.kuyou.avc.handler;

import android.content.Context;
import android.media.MediaPlayer;
import android.util.Log;

import com.kuyou.avc.R;

/**
 * action :协处理器[铃声][本地]
 * <p>
 * remarks:  <br/>
 * author: wuguoxian <br/>
 * date: 21-8-7 <br/>
 * </p>
 */
public class LocalRingtoneHandler extends RingtoneHandler {

    protected final String TAG = "com.kuyou.avc.handler > LocalRingtoneHandler";

    protected MediaPlayer mPlayer;
    protected MediaPlayer.OnPreparedListener mOnPreparedListener;

    public LocalRingtoneHandler(Context context) {
        super(context);
    }

    @Override
    protected void init() {
        mPlayer = MediaPlayer.create(getContext(), R.raw.ring_htc_win8);
        mPlayer.setVolume(5.0f, 5.0f);
        mOnPreparedListener = new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mPlayer.start();
            }
        };
    }

    @Override
    public void play() {
        try {
            mPlayer.stop();
            mPlayer.setOnPreparedListener(mOnPreparedListener);
            mPlayer.prepare();
        } catch (Exception e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }
    }

    @Override
    public void stop() {
        mPlayer.pause();
    }

    @Override
    public void exit() {
        try {
            mPlayer.stop();
            mPlayer.release();
        } catch (Exception e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }
    }

}
