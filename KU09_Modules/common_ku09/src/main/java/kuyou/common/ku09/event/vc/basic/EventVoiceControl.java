package kuyou.common.ku09.event.vc.basic;

import kuyou.common.ipc.RemoteEvent;

/**
 * action :事件[语言控制][抽象]
 * <p>
 * remarks:  <br/>
 * author: wuguoxian <br/>
 * date: 21-3-29 <br/>
 * </p>
 */
public abstract class EventVoiceControl extends RemoteEvent {

    protected final String TAG = "kuyou.common.ku09.event.voicecontrol > EventVoiceControl";

    //10340 ~ 12387
    protected final static int FLAG_CODE = 10340;
    //FLAG_CODE+0 ~ FLAG_CODE+2047
    public static interface Code {
        //模块状态相关 0~127
        public final static int MODULE_INIT_FINISH = FLAG_CODE + 0;
        public final static int MODULE_EXIT = FLAG_CODE + 1;

        //业务需求相关 128 ~ 2047
        public final static int VOICE_WAKEUP_REQUEST = FLAG_CODE + 128;
        public final static int VOICE_WAKEUP_RESULT = FLAG_CODE + 129;
        public final static int VOICE_SLEEP = FLAG_CODE + 129;
    }
}
