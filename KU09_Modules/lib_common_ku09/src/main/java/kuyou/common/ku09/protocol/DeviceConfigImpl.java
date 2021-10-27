package kuyou.common.ku09.protocol;

import android.util.Log;

import kuyou.common.ku09.protocol.basic.IDeviceConfig;
import kuyou.common.utils.SystemPropertiesUtils;

/**
 * action :设备配置[安全帽全局固定配置]
 * <p>
 * remarks:  <br/>
 * author: wuguoxian <br/>
 * date: 21-8-3 <br/>
 * </p>
 */
public class DeviceConfigImpl implements IDeviceConfig {
    protected final String TAG = "kuyou.common.ku09.protocol > DevicesConfig";

    protected final static int VAL_ON = 1;
    protected final static int VAL_NFF = 0;

    private String mDevId = null;
    private String mUwbId = null;
    private String mRemoteServerAddress = null;
    private int mRemoteServerPort = -1;
    private String mRemoteServerAddressPhoto = null;
    private String mAuthenticationCode = null;
    private String mCollectingEndId = null;
    private int mHeartbeatInterval = -1;
    private String mFilePathWifiConfig = null;

    @Override
    public String getDevId() {
        if (null == mDevId) {
            mDevId = SystemPropertiesUtils.get(KEY_DEV_ID, VAL_NONE);
        }
        return mDevId;
    }

    public void setDevId(String devId) {
        mDevId = devId;
        kuyou.common.utils.SystemPropertiesUtils.set(KEY_DEV_ID, devId);
    }

    @Override
    public String getUwbId() {
        if (null == mUwbId) {
            mUwbId = SystemPropertiesUtils.get(KEY_UWB_ID, VAL_NONE);
        }
        return mUwbId;
    }

    public void setUwbId(String val) {
        mUwbId = val;
        kuyou.common.utils.SystemPropertiesUtils.set(KEY_DEV_ID, val);
    }

    @Override
    public String getCollectingEndId() {
        if (null == mCollectingEndId) {
            mCollectingEndId = SystemPropertiesUtils.get(KEY_COLLECTING_END_CODE, VAL_NONE);
        }
        return mCollectingEndId;
    }

    public void setCollectingEndId(String val) {
        kuyou.common.utils.SystemPropertiesUtils.set(KEY_COLLECTING_END_CODE, val);
    }

    @Override
    public String getAuthenticationCode() {
        if (null == mAuthenticationCode) {
            mAuthenticationCode = SystemPropertiesUtils.get(KEY_AUTHENTICATION_CODE, "SP_JT808AUTHCODE");
        }
        return mAuthenticationCode;
    }

    @Override
    public String getRemoteControlServerAddress() {
        if (null == mRemoteServerAddress) {
            mRemoteServerAddress = SystemPropertiesUtils.get(KEY_SERVER_ADDRESS, VAL_NONE);
        }
        return mRemoteServerAddress;
    }

    @Override
    public int getRemoteControlServerPort() {
        if (-1 == mRemoteServerPort) {
            try {
                mRemoteServerPort = Integer.valueOf(SystemPropertiesUtils.get(KEY_SERVER_PORT, VAL_NONE));
            } catch (Exception e) {
                Log.e(TAG, Log.getStackTraceString(e));
                return -1;
            }
        }
        return mRemoteServerPort;
    }

    @Override
    public String getRemotePhotoServerAddress() {
        if (null == mRemoteServerAddressPhoto) {
            mRemoteServerAddressPhoto = SystemPropertiesUtils.get(KEY_SERVER_ADDRESS_PHOTO,
                    VAL_NONE);
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

    @Override
    public boolean isHardwareModuleCarry(int typeId) {
        String result = SystemPropertiesUtils.get(KEY_HARDWARE_CARRY, String.valueOf(VAL_NONE));
        if (result.equals(String.valueOf(VAL_NONE))) {
            return false;
        }
        try {
            String resultType = result.substring(typeId, typeId + 1);
            return resultType.equals(String.valueOf(VAL_ON));
        } catch (Exception e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }
        return false;
    }

    @Override
    public boolean isAutoEnableWifi() {
        String result = SystemPropertiesUtils.get(KEY_WIFI_AUTO_ENABLE, String.valueOf(VAL_NONE));
        return result.equals(String.valueOf(VAL_ON));
    }

    @Override
    public String getWifiConfigPath() {
        if (null == mFilePathWifiConfig) {
            mFilePathWifiConfig = SystemPropertiesUtils.get(KEY_WIFI_CONFIG_FILE_PATH, VAL_NONE);
        }
        Log.d(TAG, "getWifiConfigPath > val = " + mFilePathWifiConfig);
        return mFilePathWifiConfig;
    }
}
