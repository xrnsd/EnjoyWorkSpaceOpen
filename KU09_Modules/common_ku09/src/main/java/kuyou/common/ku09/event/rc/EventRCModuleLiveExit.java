package kuyou.common.ku09.event.rc;

import kuyou.common.ku09.event.rc.base.EventRemoteControl;

/**
 * action :事件[安全帽模块:远程控制 退出]
 * <p>
 * remarks:  <br/>
 * author: wuguoxian <br/>
 * date: 21-3-29 <br/>
 * </p>
 */
public class EventRCModuleLiveExit extends EventRemoteControl {
    @Override
    public int getCode() {
        return Code.MODULE_EXIT;
    }
}
