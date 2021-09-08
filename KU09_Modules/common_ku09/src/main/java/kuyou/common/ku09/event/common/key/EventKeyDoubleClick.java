package kuyou.common.ku09.event.common.key;

/**
 * action :事件[按键，双击]
 * <p>
 * remarks:  <br/>
 * author: wuguoxian <br/>
 * date: 21-3-23 <br/>
 * </p>
 */
public class EventKeyDoubleClick extends EventKey {

    public EventKeyDoubleClick(){
        super(KEY_DOUBLE_CLICK);
    }

    public EventKeyDoubleClick(int keyCode) {
        super(keyCode);
    }

    @Override
    public int getCode() {
        return KEY_DOUBLE_CLICK;
    }
}
