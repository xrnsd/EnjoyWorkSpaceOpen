package kuyou.common.ku09.event.avc;

import kuyou.common.ipc.RemoteEvent;
import kuyou.common.ku09.event.avc.basic.EventAudioVideoCommunication;

/**
 * action :事件[安全帽模块关闭]
 * <p>
 * remarks:  <br/>
 * author: wuguoxian <br/>
 * date: 21-3-29 <br/>
 * </p>
 */
public class EventAVCModuleLiveExit extends EventAudioVideoCommunication {

    public static interface ExitType {
        public final static int SHUTDOWN = 0;
        public final static int REBOOT = 1;
    }

    protected static final String KEY_EXIT_TYPE = "keyEventData.exitType";
    
    @Override
    public int getCode() {
        return Code.MODULE_EXIT;
    }

    public EventAVCModuleLiveExit setExitType(int val) {
        getData().putInt(KEY_EXIT_TYPE, val);
        return EventAVCModuleLiveExit.this;
    }

    public static int getExitType(RemoteEvent event) {
        return event.getData().getInt(KEY_EXIT_TYPE);
    }

    public static boolean isReboot(RemoteEvent event) {
        return ExitType.REBOOT == getExitType(event);
    }
}
