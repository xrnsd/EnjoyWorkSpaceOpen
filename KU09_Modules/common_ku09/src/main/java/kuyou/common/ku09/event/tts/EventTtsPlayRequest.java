package kuyou.common.ku09.event.tts;

import kuyou.common.ipc.RemoteEvent;

/**
 * action :
 * <p>
 * remarks:  <br/>
 * author: wuguoxian <br/>
 * date: 21-3-27 <br/>
 * </p>
 */
public class EventTtsPlayRequest extends ModuleEventTts {

    public final static String KEY_DATA_PLAT_TEXT = "key.data.play.text";

    public EventTtsPlayRequest() {
        setRemote(false);
    }

    public EventTtsPlayRequest(String text) {
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