package kuyou.sdk.jt808.base;


public abstract class Jt808Config {

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

    protected final static String KEY_DEV_ID = "persist.dev.id";
    protected final static String KEY_UWB_ID = "persist.uwb.id";
    protected final static String KEY_SERVER_ADDRESS = "persist.ser.ads";
    protected final static String KEY_SERVER_ADDRESS_PHOTO = "persist.ser.ads.po";
    protected final static String KEY_SERVER_PORT = "persist.ser.port";
    protected final static String KEY_HEARTBEAT_INTERVAL = "persist.ser.htiv";
    protected final static String KEY_AUTHENTICATION_CODE = "persist.ser.ac";

    protected final String TAG = "kuyou.sdk.jt808 > " + this.getClass().getSimpleName();

    //注册用
    public String mManufacturerId = "CCCBB";
    public String mTerminalModel = "ANDROID0000000000000";

    private String mTerminalId = "AAAAAAA";  // 终端号
    private String mOrderId = "1234567";  // 订单号

    //发送消息流水号
    private int mSocketMsgCount = 0;

    /**
     * 设备ID
     */
    public abstract String getDevId();

    public abstract void setDevId(String devId);

    /**
     * UWB模块_ID
     */
    public int getUwbId(){
        return 0;
    }

    public void setUwbId(int uwbId){

    }

    /**
     * 服务器地址
     */
    public abstract String getRemoteServerAddress();

    /**
     * 服务器端口
     */
    public abstract int getRemoteServerPort();

    /**
     * 图片服务器地址
     */
    public abstract String getRemoteServerAddressPhoto();

    /**
     * 图片存放本地目录
     */
    public abstract String getDirPathStoragePhoto();

    /**
     * 本地保存鉴权码的KEY
     */
    public abstract String getKeyAuthenticationCode();


    /**
     * 心跳间隔
     */
    public abstract int getHeartbeatInterval();

    public int getSocketMsgCount() {
        return ++mSocketMsgCount;
    }

    public String getTerminalId() {
        return mTerminalId;
    }

    public void setTerminalId(String terminalId) {
        mTerminalId = terminalId;
    }

    public String getOrderId() {
        return mOrderId;
    }

    public void setOrderId(String orderId) {
        mOrderId = orderId;
    }

    public String getManufacturerId() {
        return mManufacturerId;
    }

    public void setManufacturerId(String manufacturerId) {
        mManufacturerId = manufacturerId;
    }

    public String getTerminalModel() {
        return mTerminalModel;
    }

    public void setTerminalModel(String terminalModel) {
        mTerminalModel = terminalModel;
    }
}
