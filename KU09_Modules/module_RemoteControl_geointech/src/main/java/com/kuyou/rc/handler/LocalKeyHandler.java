package com.kuyou.rc.handler;

import android.util.Log;

import kuyou.common.ku09.IPowerStatusListener;
import kuyou.common.ku09.event.common.EventPowerChange;
import kuyou.common.ku09.event.rc.alarm.EventAlarmGas;
import kuyou.common.ku09.event.rc.alarm.EventAlarmNearPower;
import kuyou.common.ku09.event.rc.alarm.EventAlarmSos;
import kuyou.common.ku09.handler.KeyHandler;
import kuyou.common.ku09.key.KeyConfig;

/**
 * action :协处理器[实体按键]
 * <p>
 * remarks:  <br/>
 * author: wuguoxian <br/>
 * date: 21-7-23 <br/>
 * </p>
 */
public class LocalKeyHandler extends KeyHandler implements IPowerStatusListener {
    protected final String TAG = "com.kuyou.rc.handler > KeyHandler";

    private boolean isEnableNearPowerAlarm = true;

    @Override
    public void onKeyClick(int keyCode) {
        Log.d(TAG, "onKeyClick > keyCode = " + keyCode);
        switch (keyCode) {
            case KeyConfig.ALARM_NEAR_POWER:
                if (!isEnableNearPowerAlarm) {
                    Log.w(TAG, "onKeyClick > 充电中取消近电报警");
                    return;
                }
                dispatchEvent(new EventAlarmNearPower()
                        .setRemote(false));
                break;
            case KeyConfig.ALARM_GAS:
                dispatchEvent(new EventAlarmGas()
                        .setRemote(false));
                break;
            case KeyConfig.ALARM_GAS_OFF:
                dispatchEvent(new EventAlarmGas().setSwitch(false)
                        .setRemote(false));
                break;
            default:
                break;
        }
    }

    @Override
    public void onKeyDoubleClick(int keyCode) {
        Log.d(TAG, "onKeyDoubleClick > keyCode = " + keyCode);
    }

    @Override
    public void onKeyLongClick(int keyCode) {
        Log.d(TAG, "onKeyLongClick > keyCode = " + keyCode);
        switch (keyCode) {
            case KeyConfig.CALL:
                dispatchEvent(new EventAlarmSos()
                        .setRemote(false));
                break;
            default:
                break;
        }
    }

    @Override
    public void onPowerStatus(int status) {
        isEnableNearPowerAlarm = EventPowerChange.POWER_STATUS.CHARGE != status
                && EventPowerChange.POWER_STATUS.SHUTDOWN != status;
        Log.d(TAG, "onPowerStatus > isEnableNearPowerAlarm = " + isEnableNearPowerAlarm);
    }
}
