package kuyou.common.ku09.event.jt808.alarm;

import kuyou.common.ku09.event.jt808.alarm.EventAlarm;

/**
 * action :跌倒抱紧
 * <p>
 * remarks:  <br/>
 * author: wuguoxian <br/>
 * date: 21-3-23 <br/>
 * </p>
 */
public class EventAlarmFall extends EventAlarm {
    @Override
    public int getCode() {
        return Code.ALARM_FALL;
    }
}
