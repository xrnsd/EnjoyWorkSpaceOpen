package kuyou.common.ku09.event.tts;

/**
 * action :事件[安全帽模块:语言合成 初始化完成]
 * <p>
 * remarks:  <br/>
 * author: wuguoxian <br/>
 * date: 21-3-29 <br/>
 * </p>
 */
public class EventInitFinish extends EventTextToSpeech {
    @Override
    public int getCode() {
        return Code.MODULE_INIT_FINISH;
    }
}
