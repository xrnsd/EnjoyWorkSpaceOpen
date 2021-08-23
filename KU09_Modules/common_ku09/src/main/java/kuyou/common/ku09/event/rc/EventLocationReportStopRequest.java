package kuyou.common.ku09.event.rc;

import kuyou.common.ku09.event.rc.basic.EventRemoteControl;

/**
 * action :事件[位置上报/心跳停止]
 * <p>
 * remarks:  <br/>
 * author: wuguoxian <br/>
 * date: 21-3-23 <br/>
 * </p>
 */
public class EventLocationReportStopRequest extends EventRemoteControl {
    @Override
    public int getCode() {
        return Code.LOCATION_REPORT_STOP_REQUEST;
    }
}
