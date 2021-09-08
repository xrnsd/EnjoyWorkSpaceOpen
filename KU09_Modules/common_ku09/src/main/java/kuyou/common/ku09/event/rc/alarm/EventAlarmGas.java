package kuyou.common.ku09.event.rc.alarm;

import kuyou.common.ipc.RemoteEvent;

/**
 * action :气体报警
 * <p>
 * remarks:  <br/>
 * author: wuguoxian <br/>
 * date: 21-3-23 <br/>
 * </p>
 */
public class EventAlarmGas extends EventAlarm {

    protected final static String KEY_TYPE = "keyEventData.gasAlarmType";

    public static interface Type {
        public final static int OFF = 999;
        public final static int DEF = 0;
        public final static int METHANE = 1;//甲烷
        public final static int SULFUR_HEXAFLUORIDE = 2;//六氟化硫
        public final static int CARBON_MONOXIDE = 3;//一氧化碳
    }

    public int getType() {
        return getData().getInt(KEY_TYPE);
    }

    public EventAlarmGas setType(int val) {
        getData().putInt(KEY_TYPE, val);
        return EventAlarmGas.this;
    }

    public EventAlarmGas setSwitch(boolean val) {
        getData().putInt(KEY_TYPE, val?Type.DEF:Type.OFF);
        return EventAlarmGas.this;
    }

    @Override
    public int getCode() {
        return ALARM_GAS;
    }

    public static int getType(RemoteEvent event) {
        return event.getData().getInt(KEY_TYPE);
    }

    public boolean isSwitch(RemoteEvent event) {
        return Type.OFF != event.getData().getInt(KEY_TYPE);
    }
}
