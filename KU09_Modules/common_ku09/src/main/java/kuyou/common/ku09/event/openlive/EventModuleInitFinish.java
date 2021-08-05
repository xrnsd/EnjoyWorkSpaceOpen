package kuyou.common.ku09.event.openlive;

import kuyou.common.ku09.event.openlive.base.ModuleEventOpenLive;

/**
 * action :
 * <p>
 * remarks:  <br/>
 * author: wuguoxian <br/>
 * date: 21-3-29 <br/>
 * </p>
 */
public class EventModuleInitFinish extends ModuleEventOpenLive {
    @Override
    public int getCode() {
        return Code.MODULE_INIT_FINISH;
    }
}
