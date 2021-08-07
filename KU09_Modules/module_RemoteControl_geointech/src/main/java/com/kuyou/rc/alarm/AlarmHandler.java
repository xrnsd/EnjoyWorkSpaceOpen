package com.kuyou.rc.alarm;

import android.util.Log;

import com.kuyou.rc.info.LocationInfo;
import com.kuyou.rc.location.base.ILocationProvider;

import kuyou.common.ipc.RemoteEvent;
import kuyou.common.ku09.BaseHandler;
import kuyou.common.ku09.event.common.EventPowerChange;
import kuyou.common.ku09.event.rc.alarm.EventAlarm;

/**
 * action :
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

    public void setLocationProvider(ILocationProvider locationProvider) {
        mLocationProvider = locationProvider;
    }

    @Override
    public boolean onModuleEvent(RemoteEvent event) {
        switch (event.getCode()) {
            case EventAlarm.Code.ALARM_NEAR_POWER:
                play("您已进入强电非安全区域");
                mLocationProvider.getLocationInfo().setAlarmFlag(ALARM.FLAG_NEAR_POWER);
                break;

            case EventAlarm.Code.ALARM_CAP_OFF:
                mLocationProvider.getLocationInfo().setAlarmFlag(ALARM.FLAG_CAP_OFF);
                break;

            case EventAlarm.Code.ALARM_FALL:
                mLocationProvider.getLocationInfo().setAlarmFlag(ALARM.FLAG_FALL);
                break;

            case EventAlarm.Code.ALARM_GAS:
                mLocationProvider.getLocationInfo().setAlarmFlag(ALARM.FLAG_GAS);
                break;

            case EventAlarm.Code.ALARM_SOS:
                Log.d(TAG, "onModuleEvent > SOS报警");
                LocationInfo info = mLocationProvider.getLocationInfo();

                if (info.isAutoAddSosFlag()) {
                    info.setAutoAddSosFlag(false, ALARM.FLAG_SOS);
                    play("已关闭SOS");
                    break;
                }
                info.setAutoAddSosFlag(true, ALARM.FLAG_SOS);
                play("已开启SOS");
                break;

            default:
                break;
        }
        return false;
    }
}
