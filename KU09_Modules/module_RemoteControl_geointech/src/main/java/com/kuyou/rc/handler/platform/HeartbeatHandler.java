package com.kuyou.rc.handler.platform;

import android.os.Looper;
import android.util.Log;

import com.kuyou.rc.handler.platform.basic.IHeartbeat;

import kuyou.common.ipc.RemoteEvent;
import kuyou.common.ku09.status.IStatusBus;
import kuyou.common.ku09.status.StatusBusProcessCallbackImpl;
import kuyou.common.ku09.event.rc.EventHeartbeatReply;
import kuyou.common.ku09.event.rc.EventHeartbeatReport;
import kuyou.common.ku09.event.rc.EventHeartbeatRequest;
import kuyou.common.ku09.event.rc.EventLocalDeviceStatus;
import kuyou.common.ku09.event.rc.basic.EventRemoteControl;
import kuyou.common.ku09.handler.BasicEventHandler;

/**
 * action :
 * <p>
 * remarks:  <br/>
 * author: wuguoxian <br/>
 * date: 21-8-27 <br/>
 * </p>
 */
public class HeartbeatHandler extends BasicEventHandler implements IHeartbeat {

    protected final String TAG = "com.kuyou.rc.handler.platform > HeartbeatHandler";

    private boolean isHeartbeatReply = false;

    private long mHeartbeatReplyFlowId = 0;
    private long mHeartbeatReportFlowId = 0;

    private int mStaProFlagHeartbeatReport = -1;
    private int mStaProFlagHeartbeatReportStartTimeOut = -1;
    private int mStaProFlagDeviceOffline = -1;

    @Override
    protected void initHandleEventCodeList() {
        registerHandleEvent(EventRemoteControl.Code.HEARTBEAT_REPORT_REQUEST, false);
        //registerHandleEvent(EventRemoteControl.Code.HEARTBEAT_REPORT, false);
        registerHandleEvent(EventRemoteControl.Code.HEARTBEAT_REPLY, false);
    }

    @Override
    public boolean onModuleEvent(RemoteEvent event) {
        switch (event.getCode()) {
            case EventRemoteControl.Code.HEARTBEAT_REPORT_REQUEST:
                Log.i(TAG, "onModuleEvent > 心跳请求 ");
                if (EventHeartbeatRequest.RequestCode.OPEN == EventHeartbeatRequest.getRequestCode(event)) {
                    start();
                } else if (EventHeartbeatRequest.RequestCode.CLOSE == EventHeartbeatRequest.getRequestCode(event)) {
                    stop();
                } else {
                    Log.e(TAG, "onModuleEvent > process fail : EventHeartbeatRequest is invalid");
                }
                break;
//            case EventRemoteControl.Code.HEARTBEAT_REPORT:
//                onHeartbeatReport();
//                break;
            case EventRemoteControl.Code.HEARTBEAT_REPLY:

                mHeartbeatReplyFlowId = EventHeartbeatReply.getFlowNumber(event);
                isHeartbeatReply = EventHeartbeatReply.isResultSuccess(event);

                if (getStatusBus().isStart(mStaProFlagHeartbeatReportStartTimeOut)) {
                    play(isHeartbeatReply ? "设备上线成功" : "设备上线失败");
                    getStatusBus().stop(mStaProFlagHeartbeatReportStartTimeOut);

                    if (isHeartbeatReply) {
                        play("设备上线成功");
                        dispatchEvent(new EventLocalDeviceStatus()
                                .setDeviceStatus(EventLocalDeviceStatus.Status.ON_LINE)
                                .setRemote(true));
                    } else {
                        play("设备上线失败");
                    }
                }
                getStatusBus().stop(mStaProFlagDeviceOffline);

                Log.i(TAG, new StringBuilder(256)
                        .append("<<<<<<<<<<  流水号：").append(mHeartbeatReplyFlowId)
                        .append(",服务器回复:").append(isHeartbeatReply ? "成功" : "失败")
                        .append("  <<<<<<<<<<\n\n")
                        .toString());
                break;
            default:
                return false;
        }
        return true;
    }

    @Override
    public void setStatusBusImpl(IStatusBus handler) {
        super.setStatusBusImpl(handler);

        mStaProFlagHeartbeatReport = handler.registerStatusBusProcessCallback(
                new StatusBusProcessCallbackImpl(true, getDeviceConfig().getHeartbeatInterval(), Looper.getMainLooper()) {
                    @Override
                    public void onReceiveMessage(boolean isRemove) {
                        HeartbeatHandler.this.mHeartbeatReportFlowId += 1;
                        HeartbeatHandler.this.dispatchEvent(new EventHeartbeatReport().setRemote(false));
                    }
                });

        mStaProFlagHeartbeatReportStartTimeOut = handler.registerStatusBusProcessCallback(
                new StatusBusProcessCallbackImpl(false, 5000, Looper.getMainLooper()) {
                    @Override
                    public void onReceiveMessage(boolean isRemove) {
                        Log.e(TAG, "onReceiveMessage > process fail : 心跳提交失败，请重新尝试");
                        HeartbeatHandler.this.play("设备上线失败");
                    }
                });

        mStaProFlagDeviceOffline = handler.registerStatusBusProcessCallback(new StatusBusProcessCallbackImpl(false, 5000, Looper.getMainLooper()) {
            @Override
            public void onReceiveMessage(boolean isRemove) {
                Log.e(TAG, "onReceiveMessage > 设备已离线");
                HeartbeatHandler.this.play("设备已离线");
                HeartbeatHandler.this.dispatchEvent(new EventLocalDeviceStatus()
                        .setDeviceStatus(EventLocalDeviceStatus.Status.OFF_LINE)
                        .setRemote(true));
            }
        });
    }

    @Override
    public boolean isHeartbeatConnected() {
        if (!isStart()) {
            Log.w(TAG, "isHeartbeatConnected > process fail : Heartbeat none start");
            return false;
        }
        if (0 == mHeartbeatReplyFlowId) {
            Log.w(TAG, "isHeartbeatConnected > process fail : Heartbeat none reply");
            return false;
        }
        if (!isHeartbeatReply) {
            Log.w(TAG, "isHeartbeatConnected > process fail : Heartbeat reply fail");
            return false;
        }
        boolean result = (2 > Math.abs(mHeartbeatReportFlowId - mHeartbeatReplyFlowId));
        Log.d(TAG, "isHeartbeatConnected > result = " + result);
        return true;
    }

    @Override
    public void start() {
        if (-1 == mStaProFlagHeartbeatReport) {
            Log.e(TAG, "start > process fail : mHeartbeatHandlerMsgFlag is invalid");
            return;
        }
        Log.i(TAG, "start > 心跳开始  ");
        HeartbeatHandler.this.dispatchEvent(new EventHeartbeatReport().setRemote(false));
        getStatusBus().start(mStaProFlagHeartbeatReport);
        getStatusBus().start(mStaProFlagHeartbeatReportStartTimeOut);
    }

    @Override
    public void stop() {
        if (-1 == mStaProFlagHeartbeatReport) {
            Log.e(TAG, "start > process fail : mHeartbeatHandlerMsgFlag is invalid");
            return;
        }
        Log.i(TAG, "stop > 心跳停止 ");
        getStatusBus().stop(mStaProFlagHeartbeatReport);
        getStatusBus().stop(mStaProFlagHeartbeatReportStartTimeOut);
        getStatusBus().start(mStaProFlagDeviceOffline);
    }

    @Override
    public boolean isStart() {
        if (null == getStatusBus()
                || -1 == mStaProFlagHeartbeatReport) {
            Log.e(TAG, "isStart > process fail : getStatusGuardHandler is invalid");
            return false;
        }
        return getStatusBus().isStart(mStaProFlagHeartbeatReport);
    }
}
