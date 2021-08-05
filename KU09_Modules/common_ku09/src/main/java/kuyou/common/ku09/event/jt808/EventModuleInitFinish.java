package kuyou.common.ku09.event.jt808;

import kuyou.common.ku09.event.jt808.base.ModuleEventJt808;

/**
 * action :
 * <p>
 * remarks:  <br/>
 * author: wuguoxian <br/>
 * date: 21-3-29 <br/>
 * </p>
 */
public class EventModuleInitFinish extends ModuleEventJt808 {
    @Override
    public int getCode() {
        return Code.MODULE_INIT_FINISH;
    }
}
