package kuyou.common.ku09.event.rc;

import kuyou.common.ku09.event.rc.base.EventRemoteControl;

/**
 * action :事件[位置上报/心跳开始]
 * <p>
 * remarks:  <br/>
 * author: wuguoxian <br/>
 * date: 21-3-23 <br/>
 * </p>
 */
public class EventLocationReportStartRequest extends EventRemoteControl {
    @Override
    public int getCode() {
        return Code.LOCATION_REPORT_START_REQUEST;
    }
}
