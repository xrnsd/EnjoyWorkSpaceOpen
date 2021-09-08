package kuyou.common.ku09.handler;

import kuyou.common.ipc.RemoteEvent;
import kuyou.common.ku09.event.common.key.EventKey;

/**
 * action :协处理器[按键]
 * <p>
 * remarks:  <br/>
 * author: wuguoxian <br/>
 * date: 21-8-21 <br/>
 * </p>
 */
public class KeyHandler extends BasicAssistHandler {

    protected final String TAG = "kuyou.common.ku09 > KeyHandler";

    @Override
    protected void initReceiveEventNotices() {
        registerHandleEvent(EventKey.KEY_CLICK, false);
        registerHandleEvent(EventKey.KEY_LONG_CLICK, false);
        registerHandleEvent(EventKey.KEY_DOUBLE_CLICK, false);
    }

    @Override
    public boolean onReceiveEventNotice(RemoteEvent event) {
        switch (event.getCode()) {
            case EventKey.KEY_CLICK:
                onKeyClick(EventKey.getKeyCode(event));
                break;
            case EventKey.KEY_LONG_CLICK:
                onKeyLongClick(EventKey.getKeyCode(event));
                break;
            case EventKey.KEY_DOUBLE_CLICK:
                onKeyDoubleClick(EventKey.getKeyCode(event));
                break;
            default:
                return false;
        }
        return true;
    }

    protected void onKeyClick(int keyCode) {

    }

    protected void onKeyLongClick(int keyCode) {

    }

    protected void onKeyDoubleClick(int keyCode) {

    }
}
