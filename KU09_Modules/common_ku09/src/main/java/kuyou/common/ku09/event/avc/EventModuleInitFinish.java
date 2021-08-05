package kuyou.common.ku09.event.avc;

import kuyou.common.ku09.event.avc.base.EventAudioVideoCommunication;

/**
 * action :
 * <p>
 * remarks:  <br/>
 * author: wuguoxian <br/>
 * date: 21-3-29 <br/>
 * </p>
 */
public class EventModuleInitFinish extends EventAudioVideoCommunication {
    @Override
    public int getCode() {
        return Code.MODULE_INIT_FINISH;
    }
}
