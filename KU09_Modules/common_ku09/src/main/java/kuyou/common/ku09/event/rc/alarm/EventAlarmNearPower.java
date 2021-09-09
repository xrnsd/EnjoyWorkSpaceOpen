package kuyou.common.ku09.event.rc.alarm;

/**
 * action :近电报警
 * <p>
 * remarks:  <br/>
 * author: wuguoxian <br/>
 * date: 21-3-23 <br/>
 * </p>
 */
public class EventAlarmNearPower extends EventAlarm {
    @Override
    public int getCode() {
        return Code.ALARM_NEAR_POWER;
    }
}