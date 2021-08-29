package kuyou.common.ku09.config;

import android.util.Log;

import kuyou.common.utils.SystemPropertiesUtils;

/**
 * action :设备配置[安全帽全局固定配置]
 * <p>
 * remarks:  <br/>
 * author: wuguoxian <br/>
 * date: 21-8-3 <br/>
 * </p>
 */
public class DeviceConfig {
    protected final String TAG = "kuyou.common.ku09.config > DevicesConfig";

    protected final static String KEY_DEV_ID = "persist.dev.id";
    protected final static String KEY_COLLECTING_END_CODE = "persist.dev.ce.id";
    protected final static String KEY_UWB_ID = "persist.uwb.id";
    protected final static String KEY_SERVER_ADDRESS = "persist.ser.ads";
    protected final static String KEY_SERVER_ADDRESS_PHOTO = "persist.ser.ads.po";
    protected final static String KEY_SERVER_PORT = "persist.ser.port";
    protected final static String KEY_HEARTBEAT_INTERVAL = "persist.ser.htiv";
    protected final static String KEY_AUTHENTICATION_CODE = "persist.ser.ac";

    private String mDevId = null;
    private String mUwbId = null;
    private String mRemoteServerAddress = null;
    private int mRemoteServerPort = -1;
    private String mRemoteServerAddressPhoto = null;
    private String mAuthenticationCode = null;
    private String mCollectingEndId = null;
    private int mHeartbeatInterval = -1;

    public String getDevId() {
        if (null == mDevId) {
            mDevId = SystemPropertiesUtils.get(KEY_DEV_ID, "015651821852");
        }
        return mDevId;
    }

    public void setDevId(String devId) {
        mDevId = devId;
        kuyou.common.utils.SystemPropertiesUtils.set(KEY_DEV_ID, devId);
    }

    public String getUwbId() {
        if (null == mUwbId) {
            mUwbId = SystemPropertiesUtils.get(KEY_UWB_ID, "12345678");
        }
        return mUwbId;
    }

    public void setUwbId(String val) {
        mUwbId = val;
        kuyou.common.utils.SystemPropertiesUtils.set(KEY_DEV_ID, val);
    }

    public String getCollectingEndId() {
        if (null == mCollectingEndId) {
            mCollectingEndId = SystemPropertiesUtils.get(KEY_COLLECTING_END_CODE, "hzjy070609");
        }
        return mCollectingEndId;
    }

    public void setCollectingEndId(String val) {
        kuyou.common.utils.SystemPropertiesUtils.set(KEY_COLLECTING_END_CODE, val);
    }

    public String getAuthenticationCode() {
        if (null == mAuthenticationCode) {
            mAuthenticationCode = SystemPropertiesUtils.get(KEY_AUTHENTICATION_CODE, "SP_JT808AUTHCODE");
        }
        return mAuthenticationCode;
    }

    public String getRemoteControlServerAddress() {
        if (null == mRemoteServerAddress) {
            mRemoteServerAddress = SystemPropertiesUtils.get(KEY_SERVER_ADDRESS, "centos40.geointech.cn");
        }
        return mRemoteServerAddress;
    }


    public int getRemoteControlServerPort() {
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


    public String getRemotePhotoServerAddress() {
        if (null == mRemoteServerAddressPhoto) {
            mRemoteServerAddressPhoto = SystemPropertiesUtils.get(KEY_SERVER_ADDRESS_PHOTO,
                    "https://centos40.geointech.cn:8019/smart-cap/api/aqm/photo/savePhotoToCos");
        }
        return mRemoteServerAddressPhoto;
    }


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


    public String getDirPathStoragePhoto() {
        return "/sdcard/kuyou/img/";
    }
}
