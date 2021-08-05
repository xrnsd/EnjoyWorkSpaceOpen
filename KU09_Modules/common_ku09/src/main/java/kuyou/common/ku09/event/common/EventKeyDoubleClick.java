package kuyou.common.ku09.event.common;

import kuyou.common.ku09.event.common.base.EventKey;

/**
 * action :
 * <p>
 * remarks:  <br/>
 * author: wuguoxian <br/>
 * date: 21-3-23 <br/>
 * </p>
 */
public class EventKeyDoubleClick extends EventKey {

    public EventKeyDoubleClick(){
        super(Code.KEY_DOUBLE_CLICK);
    }

    public EventKeyDoubleClick(int keyCode) {
        super(keyCode);
    }

    @Override
    public int getCode() {
        return Code.KEY_DOUBLE_CLICK;
    }
}
