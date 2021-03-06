package kuyou.common.ku09.event.voicecontrol.base;

import kuyou.common.ipc.RemoteEvent;

/**
 * action :
 * <p>
 * remarks:  <br/>
 * author: wuguoxian <br/>
 * date: 21-3-29 <br/>
 * </p>
 */
public abstract class ModuleEventVoiceControl extends RemoteEvent {

    protected final String TAG = "kuyou.common.ku09.event.voicecontrol > " + this.getClass().getSimpleName();

    protected final static int FLAG_CODE = 8292;

    //8292~10339
    public static interface Code {
        //模块状态相关 0~127
        public final static int MODULE_INIT_FINISH = FLAG_CODE + 0;
        public final static int MODULE_EXIT = FLAG_CODE + 1;

        //业务需求相关 128 ~ 2047
        public final static int VOICE_WAKEUP = FLAG_CODE + 128;
        public final static int VOICE_SLEEP = FLAG_CODE + 129;
    }
}
