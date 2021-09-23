package kuyou.common.ku09.event.tts;

import kuyou.common.ipc.RemoteEvent;

/**
 * action :事件[语音生成][抽象]
 * <p>
 * remarks:  <br/>
 * author: wuguoxian <br/>
 * date: 21-3-29 <br/>
 * </p>
 */
public abstract class EventTextToSpeech extends RemoteEvent {

    protected final String TAG = "kuyou.common.ku09.event.tts > EventTextToSpeech";

    //8292~10339
    protected final static int FLAG_CODE = 8292;
    //FLAG_CODE+0 ~ FLAG_CODE+2047
    public static interface Code {
        //模块状态相关 0~127
        public final static int MODULE_INIT_FINISH = FLAG_CODE + 0;
        public final static int MODULE_EXIT = FLAG_CODE + 1;
        public final static int MODULE_INIT_REQUEST = FLAG_CODE + 2;

        //业务需求相关 128 ~ 2047
        public final static int TEXT_PLAY = FLAG_CODE + 128;
    }
}
