package kuyou.common.ku09.event.vc;

import kuyou.common.ku09.event.vc.basic.EventVoiceControl;

/**
 * action :事件[模块关闭]
 * <p>
 * remarks:  <br/>
 * author: wuguoxian <br/>
 * date: 21-3-29 <br/>
 * </p>
 */
public class EventVCModuleLiveExit extends EventVoiceControl {
    @Override
    public int getCode() {
        return Code.MODULE_EXIT;
    }
}
