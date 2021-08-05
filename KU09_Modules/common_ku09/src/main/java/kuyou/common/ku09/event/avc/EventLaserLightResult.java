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
public class EventLaserLightResult extends EventResult {

    @Override
    public int getCode() {
        return Code.LASER_LIGHT_RESULT;
    }
}