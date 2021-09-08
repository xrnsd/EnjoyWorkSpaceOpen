package kuyou.common.ku09.event.tts;

import kuyou.common.ipc.RemoteEvent;

/**
 * action :事件[语音生成]
 * <p>
 * remarks:  <br/>
 * author: wuguoxian <br/>
 * date: 21-3-27 <br/>
 * </p>
 */
public class EventTextToSpeechPlayRequest extends EventTextToSpeech {

    protected final static String KEY_DATA_PLAT_TEXT = "keyEventData.dataPlayText";

    public EventTextToSpeechPlayRequest() {
        setRemote(false);
    }

    public EventTextToSpeechPlayRequest(String text) {
        getData().putString(KEY_DATA_PLAT_TEXT, text);
        setRemote(true);
    }

    @Override
    public int getCode() {
        return Code.TEXT_PLAY;
    }

    public static String getPlayContent(RemoteEvent event) {
        return event.getData().getString(KEY_DATA_PLAT_TEXT);
    }
}