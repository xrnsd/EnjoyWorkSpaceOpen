package kuyou.common.ku09.event.common.basic;

/**
 * action :定义模块事件Code范围
 * <p>
 * remarks:  <br/>
 * author: wuguoxian <br/>
 * date: 21-3-29 <br/>
 * </p>
 */
public abstract class EventCommon extends RemoteEventCommon {

    //2048~4095
    protected final static int FLAG_CODE = 2048;

    //FLAG_CODE+0 ~ FLAG_CODE+2047
    public static interface Code {
        public final static int KEY_CLICK = FLAG_CODE + 0;
        public final static int KEY_LONG_CLICK = FLAG_CODE + 1;
        public final static int KEY_DOUBLE_CLICK = FLAG_CODE + 2;

        public final static int POWER_CHANGE = FLAG_CODE + 3;

        public final static int ALARM_CAP_OFF = FLAG_CODE + 4;
        public final static int ALARM_FALL = FLAG_CODE + 5;
        public final static int ALARM_GAS = FLAG_CODE + 6;
        public final static int ALARM_NEAR_POWER = FLAG_CODE + 7;
        public final static int ALARM_SOS = FLAG_CODE + 8;

        public final static int ALARM_REPLY = FLAG_CODE + 9;

        public final static int NETWORK_CONNECTED = FLAG_CODE + 9;
        public final static int NETWORK_DISCONNECT = FLAG_CODE + 10;
    }
}
