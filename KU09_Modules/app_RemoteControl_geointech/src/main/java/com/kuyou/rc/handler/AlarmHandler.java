package com.kuyou.rc.handler;

import android.util.Log;

import com.kuyou.rc.basic.jt808extend.item.SicAlarmReply;
import com.kuyou.rc.basic.jt808extend.item.SicLocationAlarm;
import com.kuyou.rc.basic.location.ILocationProvider;

import kuyou.common.ipc.RemoteEvent;
import kuyou.common.ku09.event.common.EventPowerChange;
import kuyou.common.ku09.event.rc.alarm.EventAlarm;
import kuyou.common.ku09.event.rc.alarm.EventAlarmReply;
import kuyou.common.ku09.handler.BasicAssistHandler;
import kuyou.common.ku09.protocol.basic.IJT808ExtensionProtocol;

/**
 * action :协处理器[报警]
 * <p>
 * remarks:  <br/>
 * author: wuguoxian <br/>
 * date: 21-4-12 <br/>
 * </p>
 */
public class AlarmHandler extends BasicAssistHandler {
    private int mPowerStatus = EventPowerChange.POWER_STATUS.BOOT_READY;

    private ILocationProvider mLocationProvider;

    public AlarmHandler setLocationProvider(ILocationProvider locationProvider) {
        mLocationProvider = locationProvider;
        return AlarmHandler.this;
    }

    protected ILocationProvider getLocationProvider() {
        return mLocationProvider;
    }

    @Override
    protected void initReceiveEventNotices() {
        registerHandleEvent(EventAlarm.Code.ALARM_NEAR_POWER, false);
        registerHandleEvent(EventAlarm.Code.ALARM_CAP_OFF, false);
        registerHandleEvent(EventAlarm.Code.ALARM_FALL, false);
        registerHandleEvent(EventAlarm.Code.ALARM_GAS, false);
        registerHandleEvent(EventAlarm.Code.ALARM_SOS, false);

        registerHandleEvent(EventAlarm.Code.ALARM_REPLY, false);
    }

    @Override
    public boolean onReceiveEventNotice(RemoteEvent event) {
        switch (event.getCode()) {
            case EventAlarm.Code.ALARM_REPLY:
                final int alarmType = EventAlarmReply.getAlarmType(event);
                final int eventType = EventAlarmReply.getEventType(event);

                switch (eventType) {
                    case IJT808ExtensionProtocol.ALARM_REPLY_PLATFORM_PROCESSED_CLOSE_ALARM:
                        getLocationProvider().getLocationInfo().resetAlarmFlags(alarmType);
                        break;
                    case IJT808ExtensionProtocol.ALARM_REPLY_PLATFORM_PROCESSED_CLOSE_CONTINUOUS_ALARM:
                        getLocationProvider().getLocationInfo().resetAlarmFlags(alarmType);
                        if (IJT808ExtensionProtocol.ALARM_FLAG_SOS == alarmType) {
                            getLocationProvider().getLocationInfo().setAutoAddSosFlag(false, IJT808ExtensionProtocol.ALARM_FLAG_SOS);
                        }
                        break;
                    default:
                        break;
                }
                Log.d(TAG, "onReceiveEventNotice > 平台操作报警状态 : \n " + SicAlarmReply.getAlarmInfo(alarmType, eventType));
                break;

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
                Log.i(TAG, "onReceiveEventNotice > SOS报警");
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
