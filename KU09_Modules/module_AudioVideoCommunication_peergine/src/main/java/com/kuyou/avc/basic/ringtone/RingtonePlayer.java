package com.kuyou.avc.basic.ringtone;

import android.content.Context;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.util.Log;

/**
 * action :协处理器[铃声][抽象]
 * <p>
 * remarks:  <br/>
 * author: wuguoxian <br/>
 * date: 21-8-7 <br/>
 * </p>
 */
public class RingtonePlayer {
    protected final String TAG = "com.kuyou.avc.basic.ringtone > RingtoneHandler";
    
    private Context mContext;
    private Uri mUriDefaultRingtone;
    private Ringtone mRingtone;
    
    public RingtonePlayer(Context context){
        mContext = context.getApplicationContext();
        init();
    }

    protected Context getContext() {
        return mContext;
    }

    protected void init() {
        if (null != mUriDefaultRingtone) {
            return;
        }
        mUriDefaultRingtone = RingtoneManager.getActualDefaultRingtoneUri(getContext(), RingtoneManager.TYPE_ALARM);
        mRingtone = RingtoneManager.getRingtone(getContext(), mUriDefaultRingtone);
    }
    
    public void play(){
        Log.d(TAG, "play > 开始播放铃声");
        mRingtone.play();
    }
    
    public void stop(){
        Log.d(TAG, "stop > 停止播放铃声");
        mRingtone.stop();
    }
    
    public void exit(){
    }

}
