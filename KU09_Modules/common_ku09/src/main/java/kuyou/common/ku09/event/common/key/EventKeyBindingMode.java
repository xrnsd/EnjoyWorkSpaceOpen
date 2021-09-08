package kuyou.common.ku09.event.common.key;

/**
 * action :事件[按键，单击]
 * <p>
 * remarks:  <br/>
 * author: wuguoxian <br/>
 * date: 21-3-23 <br/>
 * </p>
 */
public class EventKeyBindingMode extends EventKey {

    public static interface NoticeCode {
        public final static int ENTER = 0;
        public final static int EXIT = 1;
    }

    public EventKeyBindingMode(){
        super(KEY_BINDING_MODE);
    }
    
    @Override
    public int getCode() {
        return KEY_BINDING_MODE;
    }
}
