package kuyou.common.ku09.event.avc;

import kuyou.common.ku09.event.avc.base.EventResult;

/**
 * action :
 * <p>
 * remarks:  <br/>
 * author: wuguoxian <br/>
 * date: 21-3-27 <br/>
 * </p>
 */
public class EventFlashlightResult extends EventResult {

    @Override
    public int getCode() {
        return Code.FLASHLIGHT_RESULT;
    }
}