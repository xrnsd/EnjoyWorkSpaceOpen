package kuyou.common.ku09.event.common;

import kuyou.common.ku09.event.common.base.EventKey;

/**
 * action :事件[按键，单击]
 * <p>
 * remarks:  <br/>
 * author: wuguoxian <br/>
 * date: 21-3-23 <br/>
 * </p>
 */
public class EventKeyClick extends EventKey {

    public EventKeyClick(){
        super(Code.KEY_CLICK);
    }

    public EventKeyClick(int keyCode) {
        super(keyCode);
    }

    @Override
    public int getCode() {
        return Code.KEY_CLICK;
    }
}
