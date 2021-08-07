package kuyou.common.ku09.event.avc;

import kuyou.common.ku09.event.avc.base.EventAudioVideoCommunication;

/**
 * action :事件[安全帽模块关闭]
 * <p>
 * remarks:  <br/>
 * author: wuguoxian <br/>
 * date: 21-3-29 <br/>
 * </p>
 */
public class EventModuleExit extends EventAudioVideoCommunication {
    @Override
    public int getCode() {
        return Code.MODULE_EXIT;
    }
}
