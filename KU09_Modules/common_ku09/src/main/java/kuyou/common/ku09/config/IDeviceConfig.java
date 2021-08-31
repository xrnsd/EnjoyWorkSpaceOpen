package kuyou.common.ku09.config;

public interface IDeviceConfig {
    public String getDevId();
    public String getUwbId();
    public String getCollectingEndId();
    public String getAuthenticationCode();
    public String getRemoteControlServerAddress();
    public int getRemoteControlServerPort();
    public String getRemotePhotoServerAddress();
    public int getHeartbeatInterval();
    public String getDirPathStoragePhoto();
}
