package com.kuyou.voicecontrol;

import kuyou.common.ipc.RemoteEventConverter;
import kuyou.common.ku09.event.openlive.base.ModuleEventOpenLive;

/**
 * action :远程事件接收，本地分发器[808相关]
 * <p>
 * remarks:  <br/>
 * author: wuguoxian <br/>
 * date: 21-3-27 <br/>
 * </p>
 */
public class EventDisPatcher extends RemoteEventConverter {

    @Override
    protected void initEventDispatchList() {
        addEventCodeLocalDisPatch(ModuleEventOpenLive.Code.AUDIO_RESULT);
        addEventCodeLocalDisPatch(ModuleEventOpenLive.Code.VIDEO_RESULT);
        addEventCodeLocalDisPatch(ModuleEventOpenLive.Code.INFEARED_VIDEO_RESULT);
        addEventCodeLocalDisPatch(ModuleEventOpenLive.Code.PHOTO_TAKE_RESULT);

        addEventCodeLocalDisPatch(ModuleEventOpenLive.Code.FLASHLIGHT_RESULT);
        addEventCodeLocalDisPatch(ModuleEventOpenLive.Code.LASER_LIGHT_RESULT);
    }
}
