package com.kuyou.avc.handler;

import android.content.Context;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;

/**
 * action :
 * <p>
 * remarks:  <br/>
 * author: wuguoxian <br/>
 * date: 21-8-7 <br/>
 * </p>
 */
public class RingtoneHandler {

    private static RingtoneHandler sMain;

    private RingtoneHandler() {

    }

    public static RingtoneHandler getInstance(Context context) {
        if (null == sMain) {
            sMain = new RingtoneHandler();
            sMain.mContext = context.getApplicationContext();
            sMain.init();
        }
        return sMain;
    }

    private Context mContext;
    private Uri mUriDefaultRingtone;
    private Ringtone mRingtone;

    protected Context getContext() {
        return mContext;
    }

    protected void init() {
        if (null != mUriDefaultRingtone) {
            return;
        }
        mUriDefaultRingtone = RingtoneManager.getActualDefaultRingtoneUri(getContext(), RingtoneManager.TYPE_RINGTONE);
        mRingtone = RingtoneManager.getRingtone(getContext(), mUriDefaultRingtone);
    }
    
    public void play(){
        mRingtone.play();
    }
    
    public void stop(){
        mRingtone.stop();
    }

}
