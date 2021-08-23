package kuyou.common.ku09.handler;

import android.util.Log;

import kuyou.common.ipc.RemoteEvent;
import kuyou.common.ku09.IPowerStatusListener;
import kuyou.common.ku09.event.common.EventPowerChange;

/**
 * action :模块通用事件处理器
 * <p>
 * remarks:  <br/>
 * author: wuguoxian <br/>
 * date: 21-8-21 <br/>
 * </p>
 */
public class ModuleCommonHandler extends BasicEventHandler {

    protected final String TAG = "kuyou.common.ku09 > KeyHandler";

    protected IPowerStatusListener mPowerStatusListener;

    private int mPowerStatus = EventPowerChange.POWER_STATUS.BOOT_READY;

    protected IPowerStatusListener getPowerStatusListener() {
        return mPowerStatusListener;
    }

    public ModuleCommonHandler setPowerStatusListener(IPowerStatusListener powerStatusListener) {
        mPowerStatusListener = powerStatusListener;
        return ModuleCommonHandler.this;
    }

    public int getPowerStatus() {
        return mPowerStatus;
    }

    protected void setPowerStatus(int powerStatus) {
        mPowerStatus = powerStatus;
        if (null == getPowerStatusListener()) {
            Log.e(TAG, "onPowerStatus > process fail : powerStatusListener is null");
            return;
        }
        getPowerStatusListener().onPowerStatus(powerStatus);
    }

    @Override
    public boolean onModuleEvent(RemoteEvent event) {
        switch (event.getCode()) {
            case EventPowerChange.Code.POWER_CHANGE:
                final int val = EventPowerChange.getPowerStatus(event);
                if (val == getPowerStatus()) {
                    return true;
                }
                setPowerStatus(val);
                return true;
            default:
                return false;
        }
    }
}
