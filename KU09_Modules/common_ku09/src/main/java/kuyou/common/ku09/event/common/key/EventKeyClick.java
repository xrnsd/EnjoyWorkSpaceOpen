package kuyou.common.ku09.event.common.key;

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
        super(KEY_CLICK);
    }

    public EventKeyClick(int keyCode) {
        super(keyCode);
    }

    @Override
    public int getCode() {
        return KEY_CLICK;
    }
}
