package kuyou.sdk.jt808.basic;


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

    public int getSocketMsgCount() {
        return 0;
    }

    public String getOrderId() {
        return mOrderId;
    }
}
