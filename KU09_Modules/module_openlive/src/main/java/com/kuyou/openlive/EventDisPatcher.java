package com.kuyou.openlive;

import kuyou.common.ipc.RemoteEventConverter;
import kuyou.common.ku09.event.jt808.base.ModuleEventJt808;
import kuyou.common.ku09.event.openlive.base.ModuleEventOpenLive;

/**
 * action :远程事件接收，本地分发器[OpenLive]
 * <p>
 * remarks:  <br/>
 * author: wuguoxian <br/>
 * date: 21-3-27 <br/>
 * </p>
 */
public class EventDisPatcher extends RemoteEventConverter {

    @Override
    protected void initEventDispatchList() {
        addEventCodeLocalDisPatch(ModuleEventJt808.Code.PHOTO_UPLOAD_RESULT);
        addEventCodeLocalDisPatch(ModuleEventJt808.Code.AUDIO_AND_VIDEO_PARAMETERS_APPLY_RESULT);

        addEventCodeLocalDisPatch(ModuleEventOpenLive.Code.VIDEO_REQUEST);
        addEventCodeLocalDisPatch(ModuleEventOpenLive.Code.AUDIO_REQUEST);
        addEventCodeLocalDisPatch(ModuleEventOpenLive.Code.INFEARED_VIDEO_REQUEST);
        addEventCodeLocalDisPatch(ModuleEventOpenLive.Code.PHOTO_TAKE_REQUEST);

        addEventCodeLocalDisPatch(ModuleEventOpenLive.Code.FLASHLIGHT_REQUEST);
    }
}
