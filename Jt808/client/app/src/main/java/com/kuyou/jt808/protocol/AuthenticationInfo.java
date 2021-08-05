package com.kuyou.jt808.protocol;

import android.os.Process;

import com.kuyou.jt808.utils.Base64Util;
import com.kuyou.jt808.utils.TU;
import com.cuichen.jt808_sdk.sdk.SocketConfig;
import com.cuichen.jt808_sdk.sdk.jt808coding.JTT808Coding;

/**
 * action :
 * <p>
 * author: wuguoxian <br/>
 * date: 20-10-29 <br/>
 * <p>
 */
public class AuthenticationInfo extends MsgInfo{

    private static AuthenticationInfo sMain;

    private AuthenticationInfo() {
        super();
    }

    public static AuthenticationInfo getInstance(){
        android.os.Process.setThreadPriority(Process.THREAD_PRIORITY_DISPLAY);
        if(null==sMain){
            sMain=new AuthenticationInfo();
        }
        return sMain;
    }

    public byte[] getAuthenticationMsgBytes(byte[] authCode){
        return JTT808Coding.generate808(0x0102, SocketConfig.getmPhont(), authCode);
    }

    public byte[] getAuthenticationMsgBytes(){
        byte[] authCode = Base64Util.encrypt(SocketConfig.getmPhont());
        return JTT808Coding.generate808(0x0102, SocketConfig.getmPhont(), authCode);
    }

    @Override
    public String toString() {
        return null;
    }
}
