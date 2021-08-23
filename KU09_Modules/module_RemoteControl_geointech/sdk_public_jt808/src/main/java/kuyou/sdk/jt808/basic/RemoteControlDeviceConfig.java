package kuyou.sdk.jt808.base;


public abstract class RemoteControlDeviceConfig {

    public static interface Header {
        /**
         * Jt808 数据加密方式 占3个Bit位 0表示不加密
         */
        public final static String ENCRYPT = "000";
        /**
         * Jt808 保留位 占2个Bit位
         */
        public final static String RESERVE = "00";
    }
    
    private String mOrderId = "1234567";  // 订单号

    /**
     * 设备ID
     */
    public abstract String getDevId();

    /**
     * UWB模块_ID
     */
    public abstract String getUwbId();

    /**
     * 多端视频服务SDK配置信息：采集端ID
     */
    public abstract String getCollectingEndId();

    /**
     * 服务器地址
     */
    public abstract String getRemoteControlServerAddress();

    /**
     * 服务器端口
     */
    public abstract int getRemoteControlServerPort();

    /**
     * 图片服务器地址
     */
    public abstract String getRemotePhotoServerAddress();

    /**
     * 图片存放本地目录
     */
    public abstract String getDirPathStoragePhoto();

    /**
     * 本地保存鉴权码的KEY
     */
    public abstract String getAuthenticationCode();


    /**
     * 心跳间隔
     */
    public abstract int getHeartbeatInterval();

    public int getSocketMsgCount() {
        return 0;
    }

    public String getOrderId() {
        return mOrderId;
    }
}
