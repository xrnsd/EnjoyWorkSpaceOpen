package kuyou.common.ku09.handler;

import kuyou.common.ipc.RemoteEvent;
import kuyou.common.ku09.event.common.EventPowerChange;
import kuyou.common.ku09.event.common.basic.EventCommon;

/**
 * action :协处理器[模块通用事件]
 * <p>
 * remarks:  <br/>
 * author: wuguoxian <br/>
 * date: 21-8-21 <br/>
 * </p>
 */
public class ModuleCommonHandler extends BasicAssistHandler {

    protected final String TAG = "kuyou.common.ku09.handler > KeyHandler";

    @Override
    protected void initReceiveEventNotices() {
        registerHandleEvent(EventCommon.Code.NETWORK_CONNECTED, true);
        registerHandleEvent(EventCommon.Code.NETWORK_DISCONNECT, true);
        registerHandleEvent(EventPowerChange.Code.POWER_CHANGE, false);
    }

    @Override
    public boolean onReceiveEventNotice(RemoteEvent event) {
        switch (event.getCode()) {
            case EventPowerChange.Code.POWER_CHANGE:
                break;
            case EventPowerChange.Code.NETWORK_CONNECTED:
                break;
            case EventPowerChange.Code.NETWORK_DISCONNECT:
                break;
            default:
                return false;
        }
        return true;
    }
}
