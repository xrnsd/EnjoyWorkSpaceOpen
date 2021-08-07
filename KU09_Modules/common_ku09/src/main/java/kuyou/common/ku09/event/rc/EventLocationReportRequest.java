package kuyou.common.ku09.event.rc;

import kuyou.common.ku09.event.rc.base.EventRemoteControl;

/**
 * action :暂时弃用
 * <p>
 * remarks:  <br/>
 * author: wuguoxian <br/>
 * date: 21-3-23 <br/>
 * </p>
 */
public class EventLocationReportRequest extends EventRemoteControl {
    
    public EventLocationReportRequest(){
        setRemote(false);
    }
    
    @Override
    public int getCode() {
        return Code.LOCATION_REPORT_REQUEST;
    }
}
