package com.kuyou.rc.platform;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import androidx.annotation.NonNull;

/**
 * action :
 * <p>
 * remarks:  <br/>
 * author: wuguoxian <br/>
 * date: 21-8-12 <br/>
 * </p>
 */
public class AuthenticationHandler extends Handler {

    protected final static int MSG_SEND_AUTHENTICATION_REQUEST = 0;
    protected final static int MSG_SEND_AUTHENTICATION_REQUEST_TIMEOUT = 0;

    public AuthenticationHandler(@NonNull Looper looper) {
        super(looper);
    }

    @Override
    public void handleMessage(@NonNull Message msg) {
        super.handleMessage(msg);
    }
}
