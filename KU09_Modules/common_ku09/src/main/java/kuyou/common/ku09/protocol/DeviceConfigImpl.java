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

    protected final static int VAL_NONE = -1;
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

    @Override
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

    @Override
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

    @Override
    public String getCollectingEndId() {
        if (null == mCollectingEndId) {
            mCollectingEndId = SystemPropertiesUtils.get(KEY_COLLECTING_END_CODE, "hzjy070609");
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
            mRemoteServerAddress = SystemPropertiesUtils.get(KEY_SERVER_ADDRESS, "centos40.geointech.cn");
        }
        return mRemoteServerAddress;
    }

    @Override
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

    @Override
    public String getRemotePhotoServerAddress() {
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

    @Override
    public boolean isHardwareModuleCarry(int typeId) {
        String result = SystemPropertiesUtils.get(KEY_HARDWARE_CARRY_FLAG + typeId, String.valueOf(VAL_NONE));
        return result.equals(String.valueOf(VAL_ON));
    }
}
