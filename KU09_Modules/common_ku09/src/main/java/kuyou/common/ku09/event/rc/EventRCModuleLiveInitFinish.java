package kuyou.common.ku09.event.rc;

import kuyou.common.ku09.event.rc.basic.EventRemoteControl;

/**
 * action :事件[安全帽模块:远程控制 初始化完成]
 * <p>
 * remarks:  <br/>
 * author: wuguoxian <br/>
 * date: 21-3-29 <br/>
 * </p>
 */
public class EventRCModuleLiveInitFinish extends EventRemoteControl {
    @Override
    public int getCode() {
        return MODULE_INIT_FINISH;
    }
}
