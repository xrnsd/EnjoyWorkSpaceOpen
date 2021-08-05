package kuyou.common.ku09.event.common;

import kuyou.common.ipc.RemoteEvent;
import kuyou.common.ku09.event.common.base.EventKey;

/**
 * action :
 * <p>
 * remarks:  <br/>
 * author: wuguoxian <br/>
 * date: 21-3-23 <br/>
 * </p>
 */
public class EventPowerChange extends EventKey {

    public static interface POWER_STATUS {
        public final static int BOOT_READY = 0;
        public final static int SHUTDOWN = 1;
        public final static int CHARGE = 2;
        public final static int CHARGE_DIS = 3;
    }

    public static final String KEY_STATUS = "power.status.code";

    public EventPowerChange() {
        super(Code.POWER_CHANGE);
    }

    public EventPowerChange(int keyCode) {
        super(keyCode);
    }

    public EventPowerChange setPowerStatus(int val) {
        getData().putInt(KEY_STATUS, val);
        return EventPowerChange.this;
    }

    @Override
    public int getCode() {
        return Code.POWER_CHANGE;
    }

    public static int getPowerStatus(RemoteEvent event) {
        return event.getData().getInt(KEY_STATUS);
    }
}
