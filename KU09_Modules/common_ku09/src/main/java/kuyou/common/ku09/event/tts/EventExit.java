package kuyou.common.ku09.event.tts;

/**
 * action :事件[安全帽模块:语音合成 退出]
 * <p>
 * remarks:  <br/>
 * author: wuguoxian <br/>
 * date: 21-3-29 <br/>
 * </p>
 */
public class EventExit extends EventTextToSpeech {
    @Override
    public int getCode() {
        return Code.MODULE_EXIT;
    }
}
