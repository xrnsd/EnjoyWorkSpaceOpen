package kuyou.common.ku09.handler;

import kuyou.common.ipc.RemoteEvent;
import kuyou.common.ku09.event.common.EventCommon;

/**
 * action :协处理器[模块通用事件]
 * <p>
 * remarks:  <br/>
 * author: wuguoxian <br/>
 * date: 21-8-21 <br/>
 * </p>
 */
public class ModuleCommonHandler extends BasicAssistHandler {

    protected final String TAG = "kuyou.common.ku09 > KeyHandler";

    @Override
    protected void initReceiveEventNotices() {
        registerHandleEvent(EventCommon.NETWORK_CONNECTED, true);
        registerHandleEvent(EventCommon.NETWORK_DISCONNECT, true);
        registerHandleEvent(EventCommon.POWER_CHANGE, false);
    }

    @Override
    public boolean onReceiveEventNotice(RemoteEvent event) {
        switch (event.getCode()) {
            case EventCommon.POWER_CHANGE:
                break;
            case EventCommon.NETWORK_CONNECTED:
                break;
            case EventCommon.NETWORK_DISCONNECT:
                break;
            default:
                return false;
        }
        return true;
    }
}
