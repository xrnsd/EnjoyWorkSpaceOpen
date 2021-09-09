package kuyou.common.ku09.event.rc.alarm;

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
