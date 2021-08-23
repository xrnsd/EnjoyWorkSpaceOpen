package kuyou.common.ku09.event.avc;

import kuyou.common.ku09.event.avc.basic.EventResult;

/**
 * action :事件[激光指示灯开关结果]
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