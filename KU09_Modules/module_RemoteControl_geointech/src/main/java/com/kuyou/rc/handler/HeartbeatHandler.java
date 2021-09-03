package com.kuyou.rc.handler;

import android.util.Log;

import com.kuyou.rc.handler.platform.basic.IHeartbeat;

import kuyou.common.ipc.RemoteEvent;
import kuyou.common.ku09.event.rc.EventAuthenticationResult;
import kuyou.common.ku09.event.rc.EventConnectRequest;
import kuyou.common.ku09.event.rc.EventHeartbeatReply;
import kuyou.common.ku09.event.rc.EventHeartbeatReport;
import kuyou.common.ku09.event.rc.EventHeartbeatRequest;
import kuyou.common.ku09.event.rc.EventLocalDeviceStatus;
import kuyou.common.ku09.event.rc.basic.EventRemoteControl;
import kuyou.common.ku09.handler.BasicEventHandler;
import kuyou.common.ku09.status.StatusProcessBusCallbackImpl;
import kuyou.common.ku09.status.basic.IStatusProcessBusCallback;

/**
 * action :
 * <p>
 * remarks:  <br/>
 * author: wuguoxian <br/>
 * date: 21-8-27 <br/>
 * </p>
 */
public class HeartbeatHandler extends BasicEventHandler implements IHeartbeat {

    protected final static String TAG = "com.kuyou.rc.handler.platform > HeartbeatHandler";

    protected final static int PS_AUTHENTICATION_TIME_OUT = 0;
    protected final static int PS_AUTHENTICATION_TIME_OUT_HANDLE = 1;
    protected final static int PS_HEARTBEAT_REPORT = 2;
    protected final static int PS_HEARTBEAT_REPORT_START_TIME_OUT = 3;
    protected final static int PS_DEVICE_OFF_LINE = 43;

    private boolean isAuthenticationSuccess = false;
    private boolean isHeartbeatReply = false;

    private long mHeartbeatReplyFlowId = 0;
    private long mHeartbeatReportFlowId = 0;

    @Override
    protected void initHandleEventCodeList() {
        registerHandleEvent(EventRemoteControl.Code.AUTHENTICATION_REQUEST, false);
        registerHandleEvent(EventRemoteControl.Code.AUTHENTICATION_RESULT, false);
        registerHandleEvent(EventRemoteControl.Code.HEARTBEAT_REPORT_REQUEST, false);
        //registerHandleEvent(EventRemoteControl.Code.HEARTBEAT_REPORT, false);
        registerHandleEvent(EventRemoteControl.Code.HEARTBEAT_REPLY, false);
    }

    @Override
    public boolean onReceiveEventNotice(RemoteEvent event) {
        switch (event.getCode()) {
            case EventRemoteControl.Code.AUTHENTICATION_REQUEST:
                getStatusProcessBus().stop(PS_DEVICE_OFF_LINE);
                Log.i(TAG, "onReceiveEventNotice > 开始鉴权 ");
                //添加鉴权失败超时提示
                getStatusProcessBus().start(PS_AUTHENTICATION_TIME_OUT);
                break;

            case EventRemoteControl.Code.AUTHENTICATION_RESULT:
                isAuthenticationSuccess = EventAuthenticationResult.isResultSuccess(event);

                if (isAuthenticationSuccess) {
                    getStatusProcessBus().stop(PS_AUTHENTICATION_TIME_OUT);
                    start();
                }
                Log.i(TAG, new StringBuilder(128)
                        .append("\n<<<<<<<<<<  服务器鉴权:").append(isAuthenticationSuccess ? "成功" : "失败")
                        .append("  <<<<<<<<<<")
                        .toString());
                break;

            case EventRemoteControl.Code.HEARTBEAT_REPORT_REQUEST:
                Log.d(TAG, "onReceiveEventNotice > 心跳请求 ");
                if (EventHeartbeatRequest.RequestCode.OPEN == EventHeartbeatRequest.getRequestCode(event)) {
                    start();
                } else if (EventHeartbeatRequest.RequestCode.CLOSE == EventHeartbeatRequest.getRequestCode(event)) {
                    stop();
                } else {
                    Log.e(TAG, "onReceiveEventNotice > process fail : EventHeartbeatRequest is invalid");
                }
                break;

            case EventRemoteControl.Code.HEARTBEAT_REPORT:
                getStatusProcessBus().start(PS_DEVICE_OFF_LINE);
                return false;

            case EventRemoteControl.Code.HEARTBEAT_REPLY:
                mHeartbeatReplyFlowId = EventHeartbeatReply.getFlowNumber(event);
                isHeartbeatReply = EventHeartbeatReply.isResultSuccess(event);

                if (isHeartbeatReply) {
                    getStatusProcessBus().stop(PS_DEVICE_OFF_LINE);
                }

                if (getStatusProcessBus().isStart(PS_HEARTBEAT_REPORT_START_TIME_OUT)) {
                    getStatusProcessBus().stop(PS_HEARTBEAT_REPORT_START_TIME_OUT);
                    if (isHeartbeatReply) {
                        dispatchEvent(new EventLocalDeviceStatus()
                                .setDeviceStatus(EventLocalDeviceStatus.Status.ON_LINE)
                                .setPolicyDispatch2Myself(true)
                                .setEnableConsumeSeparately(false)
                                .setRemote(true));
                        play("设备上线成功");
                    } else {
                        getStatusProcessBus().stop(PS_DEVICE_OFF_LINE);
                        play("设备上线失败,错误3");
                    }
                }

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
    protected void initStatusProcessBusCallbackList() {
        super.initStatusProcessBusCallbackList();

        registerStatusProcessBusCallback(
                PS_AUTHENTICATION_TIME_OUT,
                new StatusProcessBusCallbackImpl(false, 2000)
                        .setNoticeHandleLooperPolicy(IStatusProcessBusCallback.LOOPER_POLICY_MAIN));

        registerStatusProcessBusCallback(
                PS_AUTHENTICATION_TIME_OUT_HANDLE,
                new StatusProcessBusCallbackImpl(false, 1000)
                        .setNoticeHandleLooperPolicy(IStatusProcessBusCallback.LOOPER_POLICY_MAIN));

        registerStatusProcessBusCallback(
                PS_HEARTBEAT_REPORT,
                new StatusProcessBusCallbackImpl(true, getDeviceConfig().getHeartbeatInterval())
                        .setNoticeHandleLooperPolicy(IStatusProcessBusCallback.LOOPER_POLICY_MAIN));

        registerStatusProcessBusCallback(
                PS_HEARTBEAT_REPORT_START_TIME_OUT,
                new StatusProcessBusCallbackImpl(false, 5000)
                        .setNoticeHandleLooperPolicy(IStatusProcessBusCallback.LOOPER_POLICY_MAIN));

        registerStatusProcessBusCallback(
                PS_DEVICE_OFF_LINE,
                new StatusProcessBusCallbackImpl(false, 5 * 1000)
                        .setNoticeHandleLooperPolicy(IStatusProcessBusCallback.LOOPER_POLICY_MAIN));
    }

    @Override
    protected void onReceiveStatusProcessNotice(int statusCode, boolean isRemove) {
        super.onReceiveStatusProcessNotice(statusCode, isRemove);
        switch (statusCode) {
            case PS_AUTHENTICATION_TIME_OUT:
                getStatusProcessBus().stop(PS_DEVICE_OFF_LINE);
                Log.e(TAG, "onReceiveStatusProcessNotice > process fail : 鉴权失败，请重新尝试");
                play("设备上线失败,错误1");
                getStatusProcessBus().start(PS_AUTHENTICATION_TIME_OUT_HANDLE);
                break;

            case PS_AUTHENTICATION_TIME_OUT_HANDLE:
                dispatchEvent(new EventConnectRequest()
                        .setRequestCode(EventConnectRequest.RequestCode.REOPEN)
                        .setRemote(false));
                break;

            case PS_HEARTBEAT_REPORT:
                HeartbeatHandler.this.mHeartbeatReportFlowId += 1;
                HeartbeatHandler.this.dispatchEvent(new EventHeartbeatReport().setRemote(false));
                break;

            case PS_HEARTBEAT_REPORT_START_TIME_OUT:
                getStatusProcessBus().stop(PS_DEVICE_OFF_LINE);
                Log.e(TAG, "onReceiveStatusProcessNotice > process fail : 心跳提交失败，请重新尝试");
                HeartbeatHandler.this.getStatusProcessBus().stop(PS_DEVICE_OFF_LINE);
                HeartbeatHandler.this.play("设备上线失败,错误2");
                break;

            case PS_DEVICE_OFF_LINE:
                HeartbeatHandler.this.play("设备已离线");
                HeartbeatHandler.this.dispatchEvent(new EventLocalDeviceStatus()
                        .setDeviceStatus(EventLocalDeviceStatus.Status.OFF_LINE)
                        .setEnableConsumeSeparately(false)
                        .setPolicyDispatch2Myself(true)
                        .setRemote(true));
                break;

            default:
                break;
        }
    }

    @Override
    public boolean isHeartbeatConnected() {
        if (!isAuthenticationSuccess) {
            Log.w(TAG, "isHeartbeatConnected > authentication is fail");
            return false;
        }

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
        if (-1 == PS_HEARTBEAT_REPORT) {
            Log.e(TAG, "start > process fail : mHeartbeatHandlerMsgFlag is invalid");
            return;
        }
        Log.i(TAG, "start > 心跳开始  ");
        HeartbeatHandler.this.dispatchEvent(new EventHeartbeatReport().setRemote(false));
        getStatusProcessBus().start(PS_HEARTBEAT_REPORT);
        getStatusProcessBus().start(PS_HEARTBEAT_REPORT_START_TIME_OUT);
    }

    @Override
    public void stop() {
        if (-1 == PS_HEARTBEAT_REPORT) {
            Log.e(TAG, "start > process fail : mHeartbeatHandlerMsgFlag is invalid");
            return;
        }
        Log.i(TAG, "stop > 心跳停止 ");
        getStatusProcessBus().stop(PS_HEARTBEAT_REPORT);
        getStatusProcessBus().stop(PS_HEARTBEAT_REPORT_START_TIME_OUT);
        getStatusProcessBus().start(PS_DEVICE_OFF_LINE);
    }

    @Override
    public boolean isStart() {
        if (null == getStatusProcessBus()
                || -1 == PS_HEARTBEAT_REPORT) {
            Log.e(TAG, "isStart > process fail : getStatusGuardHandler is invalid");
            return false;
        }
        return getStatusProcessBus().isStart(PS_HEARTBEAT_REPORT);
    }
}
