package com.kuyou.tts;

import kuyou.common.ipc.RemoteEventConverter;
import kuyou.common.ku09.event.tts.ModuleEventTts;

/**
 * action :远程事件接收，本地分发器[TTS相关]
 * <p>
 * remarks:  <br/>
 * author: wuguoxian <br/>
 * date: 21-3-27 <br/>
 * </p>
 */
public class EventDisPatcher extends RemoteEventConverter {

    @Override
    protected void initEventDispatchList() {
        addEventCodeLocalDisPatch(ModuleEventTts.Code.TEXT_PLAY);
    }
}
