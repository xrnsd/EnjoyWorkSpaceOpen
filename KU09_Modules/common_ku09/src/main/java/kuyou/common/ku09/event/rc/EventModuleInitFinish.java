package kuyou.common.ku09.event.rc;

import kuyou.common.ku09.event.rc.base.EventRemoteControl;

/**
 * action :
 * <p>
 * remarks:  <br/>
 * author: wuguoxian <br/>
 * date: 21-3-29 <br/>
 * </p>
 */
public class EventModuleInitFinish extends EventRemoteControl {
    @Override
    public int getCode() {
        return Code.MODULE_INIT_FINISH;
    }
}
