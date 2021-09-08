package kuyou.common.ku09.event.common.key;

/**
 * action :事件[按键，长按]
 * <p>
 * remarks:  <br/>
 * author: wuguoxian <br/>
 * date: 21-3-23 <br/>
 * </p>
 */
public class EventKeyLongClick extends EventKey {

    public EventKeyLongClick() {
        super(KEY_LONG_CLICK);
    }

    public EventKeyLongClick(int keyCode) {
        super(keyCode);
    }

    @Override
    public int getCode() {
        return KEY_LONG_CLICK;
    }
}
