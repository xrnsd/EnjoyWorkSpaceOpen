package com.kuyou.avc.handler;

import android.content.Context;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;

/**
 * action :协处理器[铃声][抽象]
 * <p>
 * remarks:  <br/>
 * author: wuguoxian <br/>
 * date: 21-8-7 <br/>
 * </p>
 */
public class RingtoneHandler {
    
    private Context mContext;
    private Uri mUriDefaultRingtone;
    private Ringtone mRingtone;
    
    public RingtoneHandler(Context context){
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
        mRingtone.play();
    }
    
    public void stop(){
        mRingtone.stop();
    }
    
    public void exit(){
    }

}
