package kuyou.common.ku09.event.vc;

import kuyou.common.ku09.event.vc.base.EventVoiceControl;

/**
 * action :事件[安全帽模块:语音控制 初始化完成]
 * <p>
 * remarks:  <br/>
 * author: wuguoxian <br/>
 * date: 21-3-29 <br/>
 * </p>
 */
public class EventVCModuleLiveInitFinish extends EventVoiceControl {
    @Override
    public int getCode() {
        return Code.MODULE_INIT_FINISH;
    }
}
