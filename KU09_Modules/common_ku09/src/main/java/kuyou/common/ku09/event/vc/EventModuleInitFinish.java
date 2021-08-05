package kuyou.common.ku09.event.vc;

import kuyou.common.ku09.event.vc.base.ModuleEventVoiceControl;

/**
 * action :
 * <p>
 * remarks:  <br/>
 * author: wuguoxian <br/>
 * date: 21-3-29 <br/>
 * </p>
 */
public class EventModuleInitFinish extends ModuleEventVoiceControl {
    @Override
    public int getCode() {
        return Code.MODULE_INIT_FINISH;
    }
}
