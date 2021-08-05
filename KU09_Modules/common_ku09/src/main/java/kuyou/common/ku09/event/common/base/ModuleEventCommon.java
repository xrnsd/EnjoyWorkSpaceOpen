package kuyou.common.ku09.event.common.base;

import kuyou.common.ipc.RemoteEvent;

/**
 * action :定义模块事件Code范围
 * <p>
 * remarks:  <br/>
 * author: wuguoxian <br/>
 * date: 21-3-29 <br/>
 * </p>
 */
public abstract class ModuleEventCommon extends RemoteEvent {
    //0~2047
    protected final static int FLAG_CODE = 0;

    public static interface Code {
        //业务需求相关 0 ~ 2047
        public final static int KEY_CLICK = FLAG_CODE + 0;
        public final static int KEY_LONG_CLICK = FLAG_CODE + 1;
        public final static int KEY_DOUBLE_CLICK = FLAG_CODE + 2;

        public final static int POWER_CHANGE = FLAG_CODE + 3;

        public final static int ALARM_CAP_OFF = FLAG_CODE + 4;
        public final static int ALARM_FALL = FLAG_CODE + 5;
        public final static int ALARM_GAS = FLAG_CODE + 6;
        public final static int ALARM_NEAR_POWER = FLAG_CODE + 7;
        public final static int ALARM_SOS = FLAG_CODE + 8;
    }
}
