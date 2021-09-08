package kuyou.common.ku09.event.rc.alarm;

/**
 * action :脱帽报警
 * <p>
 * remarks:  <br/>
 * author: wuguoxian <br/>
 * date: 21-3-23 <br/>
 * </p>
 */
public class EventAlarmCapOff extends EventAlarm {
    @Override
    public int getCode() {
        return ALARM_CAP_OFF;
    }
}
