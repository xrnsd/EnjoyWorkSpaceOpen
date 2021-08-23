package kuyou.common.ku09.handler;

import kuyou.common.ipc.RemoteEvent;
import kuyou.common.ku09.event.common.base.EventKey;

/**
 * action :
 * <p>
 * remarks:  <br/>
 * author: wuguoxian <br/>
 * date: 21-8-21 <br/>
 * </p>
 */
public class KeyHandler extends BasicEventHandler {

    protected final String TAG = "kuyou.common.ku09 > KeyHandler";

    @Override
    public boolean onModuleEvent(RemoteEvent event) {
        switch (event.getCode()) {
            case EventKey.Code.KEY_CLICK:
                onKeyClick(EventKey.getKeyCode(event));
                return true;
            case EventKey.Code.KEY_LONG_CLICK:
                onKeyLongClick(EventKey.getKeyCode(event));
                return true;
            case EventKey.Code.KEY_DOUBLE_CLICK:
                onKeyDoubleClick(EventKey.getKeyCode(event));
                return true;
            default:
                return false;
        }
    }

    protected void onKeyClick(int keyCode){
        
    }

    protected void onKeyLongClick(int keyCode){
        
    }

    protected void onKeyDoubleClick(int keyCode){
        
    }
}
