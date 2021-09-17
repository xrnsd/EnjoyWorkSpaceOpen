package kuyou.common.ku09.protocol.basic;

public interface IDeviceConfig {
    public final static String KEY_HEARTBEAT_INTERVAL = "persist.kub.hm.hf";

    public final static String KEY_HARDWARE_CARRY = "persist.kuh.hmc";

    public final static String KEY_SERVER_ADDRESS = "persist.kud.hm.sa";
    public final static String KEY_SERVER_PORT = "persist.kud.hm.sap";
    public final static String KEY_SERVER_ADDRESS_PHOTO = "persist.kud.hm.psa";
    public final static String KEY_AUTHENTICATION_CODE = "persist.kud.hm.ac";
    public final static String KEY_DEV_ID = "persist.kud.hm.di";
    public final static String KEY_UWB_ID = "persist.kud.hm.ui";
    public final static String KEY_COLLECTING_END_CODE = "persist.kud.hm.ci";

    public String getDevId();

    public String getUwbId();

    public String getCollectingEndId();

    public String getAuthenticationCode();

    public String getRemoteControlServerAddress();

    public int getRemoteControlServerPort();

    public String getRemotePhotoServerAddress();

    public int getHeartbeatInterval();

    public String getDirPathStoragePhoto();

    public boolean isHardwareModuleCarry(int typeId);
}
