package com.kuyou.avc.basic.ringtone;

import android.content.Context;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.util.Log;

import com.kuyou.avc.R;

/**
 * action :协处理器[铃声][RingtoneManager实现本地铃声播放]
 * <p>
 * remarks:  <br/>
 * author: wuguoxian <br/>
 * date: 21-8-7 <br/>
 * </p>
 */
public class LocalRingtonePlayer extends RingtonePlayer {

    protected final String TAG = "com.kuyou.avc.basic.ringtone > LocalRingtoneHandler";

    private Ringtone mRingtone;

    public LocalRingtonePlayer(Context context) {
        super(context);
    }

    @Override
    protected void init() {
        try {
            Uri path = Uri.parse(new StringBuilder()
                    .append("android.resource://")
                    .append(getContext().getPackageName())
                    .append("/").append(R.raw.ring_htc_win8)
                    .toString());
            mRingtone = RingtoneManager.getRingtone(getContext(), path);
        } catch (Exception e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }
    }

    @Override
    public void play() {
        Log.d(TAG, "play > 开始播放铃声");
        try {
            mRingtone.play();
        } catch (Exception e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }
    }

    @Override
    public void stop() {
        Log.d(TAG, "stop > 停止播放铃声");
        mRingtone.stop();
    }

    @Override
    public void exit() {
    }

}
