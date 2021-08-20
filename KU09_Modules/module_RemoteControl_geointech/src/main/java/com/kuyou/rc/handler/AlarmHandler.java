package com.kuyou.rc.handler;

import android.util.Log;

import com.kuyou.rc.handler.alarm.ALARM;
import com.kuyou.rc.handler.location.basic.ILocationProvider;
import com.kuyou.rc.protocol.jt808extend.item.SicLocationAlarm;

import kuyou.common.ipc.RemoteEvent;
import kuyou.common.ku09.BaseHandler;
import kuyou.common.ku09.event.common.EventPowerChange;
import kuyou.common.ku09.event.rc.alarm.EventAlarm;
import kuyou.common.ku09.protocol.IJT808ExtensionProtocol;

/**
 * action :协处理器[报警]
 * <p>
 * remarks:  <br/>
 * author: wuguoxian <br/>
 * date: 21-4-12 <br/>
 * </p>
 */
public class AlarmHandler extends BaseHandler implements ALARM {
    private int mPowerStatus = EventPowerChange.POWER_STATUS.BOOT_READY;

    private static AlarmHandler sMain;
    private ILocationProvider mLocationProvider;

    public AlarmHandler setLocationProvider(ILocationProvider locationProvider) {
        mLocationProvider = locationProvider;
        return AlarmHandler.this;
    }

    protected ILocationProvider getLocationProvider() {
        return mLocationProvider;
    }

    @Override
    public boolean onModuleEvent(RemoteEvent event) {
        switch (event.getCode()) {
            case EventAlarm.Code.ALARM_NEAR_POWER:
                play("您已进入强电非安全区域");
                getLocationProvider().getLocationInfo().setAlarmFlag(IJT808ExtensionProtocol.ALARM_FLAG_NEAR_POWER);
                break;

            case EventAlarm.Code.ALARM_CAP_OFF:
                getLocationProvider().getLocationInfo().setAlarmFlag(IJT808ExtensionProtocol.ALARM_FLAG_CAP_OFF);
                break;

            case EventAlarm.Code.ALARM_FALL:
                getLocationProvider().getLocationInfo().setAlarmFlag(IJT808ExtensionProtocol.ALARM_FLAG_FALL);
                break;

            case EventAlarm.Code.ALARM_GAS:
                getLocationProvider().getLocationInfo().setAlarmFlag(IJT808ExtensionProtocol.ALARM_FLAG_GAS);
                break;

            case EventAlarm.Code.ALARM_SOS:
                Log.d(TAG, "onModuleEvent > SOS报警");
                SicLocationAlarm info = getLocationProvider().getLocationInfo();

                if (info.isAutoAddSosFlag()) {
                    info.setAutoAddSosFlag(false, IJT808ExtensionProtocol.ALARM_FLAG_SOS);
                    play("已关闭SOS");
                    break;
                }
                info.setAutoAddSosFlag(true, IJT808ExtensionProtocol.ALARM_FLAG_SOS);
                play("已开启SOS");
                break;

            default:
                break;
        }
        return false;
    }
}
