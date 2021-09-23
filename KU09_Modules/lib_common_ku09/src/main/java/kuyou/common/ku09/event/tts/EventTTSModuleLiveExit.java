package kuyou.common.ku09.event.tts;

import kuyou.common.ipc.RemoteEvent;
import kuyou.common.ku09.event.avc.EventAVCModuleLiveExit;

/**
 * action :事件[安全帽模块:语音合成 退出]
 * <p>
 * remarks:  <br/>
 * author: wuguoxian <br/>
 * date: 21-3-29 <br/>
 * </p>
 */
public class EventTTSModuleLiveExit extends EventTextToSpeech {

    public static interface ExitType {
        public final static int SHUTDOWN = 0;
        public final static int REBOOT = 1;
    }

    protected static final String KEY_EXIT_TYPE = "keyEventData.exitType";

    @Override
    public int getCode() {
        return Code.MODULE_EXIT;
    }

    public EventTTSModuleLiveExit setExitType(int val) {
        getData().putInt(KEY_EXIT_TYPE, val);
        return EventTTSModuleLiveExit.this;
    }

    public static int getExitType(RemoteEvent event) {
        return event.getData().getInt(KEY_EXIT_TYPE);
    }

    public static boolean isReboot(RemoteEvent event) {
        return EventAVCModuleLiveExit.ExitType.REBOOT == getExitType(event);
    }
}
