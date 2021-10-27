package com.kuyou.rc.basic.wifi;

import android.util.Log;

/**
 * action :
 * <p>
 * remarks:  <br/>
 * author: wuguoxian <br/>
 * date: 21-10-23 <br/>
 * </p>
 */
public class WifiConnectInfo {
    protected final String TAG = "com.kuyou.rc.basic.wifi > WifiConnectInfo";
    
    private String mSsid = null;
    private String mPassword = null;

    public String getSsid() {
        return mSsid;
    }

    public void setSsid(String ssid) {
        Log.d(TAG, "setSsid > val = "+ssid);
        mSsid = ssid;
    }

    public String getPassword() {
        return mPassword;
    }

    public void setPassword(String password) {
        Log.d(TAG, "setPassword > val = "+password);
        mPassword = password;
    }
}
