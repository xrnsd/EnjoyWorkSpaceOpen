package com.kuyou.jt808;

import android.util.Log;

import kuyou.common.utils.SystemPropertiesUtils;
import kuyou.sdk.jt808.base.Jt808Config;

/**
 * action :
 * <p>
 * remarks:  <br/>
 * author: wuguoxian <br/>
 * date: 21-3-22 <br/>
 * </p>
 */
public class DeviceConfig extends Jt808Config {

    private String mDevId = null;
    private int mUwbId = -1;
    private String mRemoteServerAddress = null;
    private int mRemoteServerPort = -1;
    private String mRemoteServerAddressPhoto = null;
    private String mAuthenticationCode = null;
    private int mHeartbeatInterval = -1;

    @Override
    public int getSocketMsgCount() {
        return 0;
    }

    @Override
    public String getDevId() {
        if (null == mDevId) {
            mDevId = SystemPropertiesUtils.get(KEY_DEV_ID, "015651821852");
        }
        return mDevId;
    }

    @Override
    public void setDevId(String devId) {
        mDevId = devId;
        kuyou.common.utils.SystemPropertiesUtils.set(KEY_DEV_ID, devId);
    }

    @Override
    public int getUwbId() {
        if (-1 == mUwbId) {
            try {
                mUwbId = Integer.valueOf(SystemPropertiesUtils.get(KEY_UWB_ID, "12345678"));
            } catch (Exception e) {
                Log.e(TAG, Log.getStackTraceString(e));
                mUwbId = 0;
            }
        }
        return mUwbId;
    }

    @Override
    public void setUwbId(int uwbId) {
        mUwbId = uwbId;
        kuyou.common.utils.SystemPropertiesUtils.set(KEY_DEV_ID, uwbId);
    }

    @Override
    public String getKeyAuthenticationCode() {
        if (null == mAuthenticationCode) {
            mAuthenticationCode = SystemPropertiesUtils.get(KEY_AUTHENTICATION_CODE, "SP_JT808AUTHCODE");
        }
        return "SP_JT808AUTHCODE";
    }

    @Override
    public String getRemoteServerAddress() {
        if (null == mRemoteServerAddress) {
            mRemoteServerAddress = SystemPropertiesUtils.get(KEY_SERVER_ADDRESS, "centos40.geointech.cn");
        }
        return mRemoteServerAddress;
    }

    @Override
    public int getRemoteServerPort() {
        if (-1 == mRemoteServerPort) {
            try {
                mRemoteServerPort = Integer.valueOf(SystemPropertiesUtils.get(KEY_SERVER_PORT, "8110"));
            } catch (Exception e) {
                Log.e(TAG, Log.getStackTraceString(e));
                return -1;
            }
        }
        return mRemoteServerPort;
    }

    @Override
    public String getRemoteServerAddressPhoto() {
        if (null == mRemoteServerAddressPhoto) {
            mRemoteServerAddressPhoto = SystemPropertiesUtils.get(KEY_SERVER_ADDRESS_PHOTO,
                    "https://centos40.geointech.cn:8019/smart-cap/api/aqm/photo/savePhotoToCos");
        }
        return mRemoteServerAddressPhoto;
    }

    @Override
    public int getHeartbeatInterval() {
        if (-1 == mHeartbeatInterval) {
            try {
                mHeartbeatInterval = Integer.valueOf(SystemPropertiesUtils.get(KEY_HEARTBEAT_INTERVAL, "5000"));
            } catch (Exception e) {
                Log.e(TAG, Log.getStackTraceString(e));
                return -1;
            }
        }
        return 5000;
    }

    @Override
    public String getDirPathStoragePhoto() {
        return "/sdcard/kuyou/img/";
    }
}
