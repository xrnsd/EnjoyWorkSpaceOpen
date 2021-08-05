package kuyou.common.ku09.event.tts;

import kuyou.common.ipc.RemoteEvent;

/**
 * action :
 * <p>
 * remarks:  <br/>
 * author: wuguoxian <br/>
 * date: 21-3-29 <br/>
 * </p>
 */
public abstract class ModuleEventTts extends RemoteEvent {

    protected final String TAG = "kuyou.common.ku09.event.tts > "+ this.getClass().getSimpleName();

    //8292~10339
    protected final static int FLAG_CODE = 8292;
    //FLAG_CODE+0 ~ FLAG_CODE+2047
    public static interface Code {
        //模块状态相关 0~127
        public final static int MODULE_INIT_FINISH = FLAG_CODE + 0;
        public final static int MODULE_EXIT = FLAG_CODE + 1;

        //业务需求相关 128 ~ 2047
        public final static int TEXT_PLAY = FLAG_CODE + 128;
    }
}
