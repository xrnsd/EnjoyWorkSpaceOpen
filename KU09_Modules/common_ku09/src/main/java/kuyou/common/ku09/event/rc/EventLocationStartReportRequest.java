package kuyou.common.ku09.event.rc;

import kuyou.common.ku09.event.rc.base.EventRemoteControl;

/**
 * action :
 * <p>
 * remarks:  <br/>
 * author: wuguoxian <br/>
 * date: 21-3-23 <br/>
 * </p>
 */
public class EventLocationStartReportRequest extends EventRemoteControl {
    @Override
    public int getCode() {
        return Code.LOCATION_START_REPORT_REQUEST;
    }
}
