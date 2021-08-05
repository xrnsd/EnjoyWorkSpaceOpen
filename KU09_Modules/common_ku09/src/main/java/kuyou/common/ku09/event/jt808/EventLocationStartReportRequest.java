package kuyou.common.ku09.event.jt808;

import kuyou.common.ku09.event.jt808.base.ModuleEventJt808;

/**
 * action :
 * <p>
 * remarks:  <br/>
 * author: wuguoxian <br/>
 * date: 21-3-23 <br/>
 * </p>
 */
public class EventLocationStartReportRequest extends ModuleEventJt808 {
    @Override
    public int getCode() {
        return Code.LOCATION_START_REPORT_REQUEST;
    }
}
