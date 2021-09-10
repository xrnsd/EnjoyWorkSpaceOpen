package com.kuyou.rc.handler;

import android.util.Log;

import com.kuyou.rc.BuildConfig;

import kuyou.common.ipc.RemoteEvent;
import kuyou.common.ku09.event.rc.EventAuthenticationResult;
import kuyou.common.ku09.event.rc.EventConnectRequest;
import kuyou.common.ku09.event.rc.EventHeartbeatReply;
import kuyou.common.ku09.event.rc.EventHeartbeatReport;
import kuyou.common.ku09.event.rc.EventHeartbeatRequest;
import kuyou.common.ku09.event.rc.EventLocalDeviceStatus;
import kuyou.common.ku09.event.rc.basic.EventRemoteControl;
import kuyou.common.ku09.handler.BasicAssistHandler;
import kuyou.common.status.StatusProcessBusCallbackImpl;
import kuyou.common.status.basic.IStatusProcessBusCallback;

/**
 * action :协处理器[心跳和设备上下线]
 * <p>
 * remarks:  <br/>
 * author: wuguoxian <br/>
 * date: 21-8-27 <br/>
 * </p>
 */
public class HeartbeatHandler extends BasicAssistHandler {

    protected final static String TAG = "com.kuyou.rc.handler.platform > HeartbeatHandler";

    protected final static int PS_AUTHENTICATION_TIME_OUT = 0;
    protected final static int PS_AUTHENTICATION_TIME_OUT_HANDLE = 1;
    protected final static int PS_HEARTBEAT_REPORT = 2;
    protected final static int PS_HEARTBEAT_REPORT_START_TIME_OUT = 3;
    protected final static int PS_DEVICE_OFF_LINE = 43;

    private boolean isAuthenticationSuccess = false;
    private boolean isHeartbeatReply = false;
    private boolean isDeviceOnLine = false;

    private long mHeartbeatReplyFlowId = 0;
    private long mHeartbeatReportFlowId = 0;

    @Override
    protected void initReceiveEventNotices() {
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

            case EventRemoteControl.Code.HEARTBEAT_REPLY:
                mHeartbeatReplyFlowId = EventHeartbeatReply.getFlowNumber(event);
                isHeartbeatReply = EventHeartbeatReply.isResultSuccess(event);

                if (getStatusProcessBus().isStart(PS_HEARTBEAT_REPORT_START_TIME_OUT)) {
                    getStatusProcessBus().stop(PS_HEARTBEAT_REPORT_START_TIME_OUT);
                    getStatusProcessBus().stop(PS_DEVICE_OFF_LINE);
                    if (isHeartbeatReply) {
                        mHeartbeatReportFlowId = mHeartbeatReplyFlowId;//防止明明心跳正常，数字对不上导致心跳连接判断异常
                        isDeviceOnLine = true;

                        dispatchEvent(new EventLocalDeviceStatus()
                                .setDeviceStatus(EventLocalDeviceStatus.Status.ON_LINE)
                                .setPolicyDispatch2Myself(true)
                                .setEnableConsumeSeparately(false)
                                .setRemote(true));
                        play("设备上线成功");
                    } else {
                        play("设备上线失败,错误3");
                    }
                } else if (isHeartbeatReply) {
                    getStatusProcessBus().stop(PS_DEVICE_OFF_LINE);
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
    protected void initReceiveProcessStatusNotices() {
        super.initReceiveProcessStatusNotices();

        getStatusProcessBus().registerStatusNoticeCallback(PS_AUTHENTICATION_TIME_OUT,
                new StatusProcessBusCallbackImpl(false, 2000)
                        .setNoticeHandleLooperPolicy(IStatusProcessBusCallback.LOOPER_POLICY_MAIN));

        getStatusProcessBus().registerStatusNoticeCallback(PS_AUTHENTICATION_TIME_OUT_HANDLE,
                new StatusProcessBusCallbackImpl(false, 1000)
                        .setNoticeHandleLooperPolicy(IStatusProcessBusCallback.LOOPER_POLICY_MAIN));

        getStatusProcessBus().registerStatusNoticeCallback(PS_HEARTBEAT_REPORT,
                new StatusProcessBusCallbackImpl(true, getDeviceConfig().getHeartbeatInterval())
                        .setNoticeHandleLooperPolicy(IStatusProcessBusCallback.LOOPER_POLICY_MAIN));

        getStatusProcessBus().registerStatusNoticeCallback(PS_HEARTBEAT_REPORT_START_TIME_OUT,
                new StatusProcessBusCallbackImpl(false, 5000)
                        .setNoticeHandleLooperPolicy(IStatusProcessBusCallback.LOOPER_POLICY_MAIN));

        getStatusProcessBus().registerStatusNoticeCallback(PS_DEVICE_OFF_LINE,
                new StatusProcessBusCallbackImpl(false, 5 * 1000)
                        .setNoticeHandleLooperPolicy(IStatusProcessBusCallback.LOOPER_POLICY_MAIN));
    }

    @Override
    protected void onReceiveProcessStatusNotice(int statusCode, boolean isRemove) {
        super.onReceiveProcessStatusNotice(statusCode, isRemove);
        switch (statusCode) {
            case PS_AUTHENTICATION_TIME_OUT:
                getStatusProcessBus().stop(PS_DEVICE_OFF_LINE);
                Log.e(TAG, "onReceiveProcessStatusNotice > process fail : 鉴权失败，请重新尝试");
                play("设备上线失败,错误1");
                getStatusProcessBus().start(PS_AUTHENTICATION_TIME_OUT_HANDLE);
                break;

            case PS_AUTHENTICATION_TIME_OUT_HANDLE:
                dispatchEvent(new EventConnectRequest()
                        .setRequestCode(EventConnectRequest.RequestCode.REOPEN)
                        .setRemote(false));
                break;

            case PS_HEARTBEAT_REPORT:
                if (Math.abs(mHeartbeatReportFlowId - mHeartbeatReplyFlowId) >= 3) {
                    Log.w(TAG, "onReceiveProcessStatusNotice > 心跳异常，主动离线");
                    stop();
                    break;
                }

//                if (!getStatusProcessBus().isStart(PS_DEVICE_OFF_LINE))
//                    getStatusProcessBus().start(PS_DEVICE_OFF_LINE);

                mHeartbeatReportFlowId += 1;
                dispatchEvent(new EventHeartbeatReport().setRemote(false));
                break;

            case PS_HEARTBEAT_REPORT_START_TIME_OUT:
                getStatusProcessBus().stop(PS_DEVICE_OFF_LINE);
                Log.e(TAG, "onReceiveProcessStatusNotice > process fail : 心跳提交失败，请重新尝试");
                play("设备上线失败,错误2");
                break;

            case PS_DEVICE_OFF_LINE:
                resetFlags();

                dispatchEvent(new EventLocalDeviceStatus()
                        .setDeviceStatus(EventLocalDeviceStatus.Status.OFF_LINE)
                        .setEnableConsumeSeparately(false)
                        .setPolicyDispatch2Myself(true)
                        .setRemote(true));

                getStatusProcessBus().stop(PS_HEARTBEAT_REPORT);
                getStatusProcessBus().stop(PS_HEARTBEAT_REPORT_START_TIME_OUT);

                play("设备已离线");
                break;

            default:
                break;
        }
    }

    private void resetFlags() {
        isAuthenticationSuccess = false;
        isHeartbeatReply = false;
        isDeviceOnLine = false;

        mHeartbeatReplyFlowId = 0;
        mHeartbeatReportFlowId = 0;
    }

    @Override
    protected void play(String content) {
        if (BuildConfig.IS_ENABLE_CONFUSE) {
            //Log.i(TAG, "play > content = "+ content);
        }
        super.play(content);
    }

    public boolean isConnect() {
        if (!isAuthenticationSuccess) {
            Log.w(TAG, "isConnect > authentication is fail");
            return false;
        }

        if (!isStart()) {
            Log.w(TAG, "isConnect > process fail : Heartbeat none start");
            return false;
        }
        if (0 == mHeartbeatReplyFlowId) {
            Log.w(TAG, "isConnect > process fail : Heartbeat none reply");
            return false;
        }
        if (!isDeviceOnLine) {
            Log.w(TAG, "isConnect > device is offline");
            return false;
        }

        boolean heartbeatStatusResult = (30001 >
                Math.abs(mHeartbeatReportFlowId - mHeartbeatReplyFlowId) * getDeviceConfig().getHeartbeatInterval());
        Log.d(TAG, "isConnect > heartbeatStatusResult = " + heartbeatStatusResult);

        return heartbeatStatusResult;
    }

    public void start() {
        if (-1 == PS_HEARTBEAT_REPORT) {
            Log.e(TAG, "start > process fail : mHeartbeatHandlerMsgFlag is invalid");
            return;
        }
        Log.i(TAG, "start > 心跳开始  ");
        dispatchEvent(new EventHeartbeatReport().setRemote(false));
        getStatusProcessBus().start(PS_HEARTBEAT_REPORT);
        getStatusProcessBus().start(PS_HEARTBEAT_REPORT_START_TIME_OUT);
    }

    public void stop() {
        if (-1 == PS_HEARTBEAT_REPORT) {
            Log.e(TAG, "stop > process fail : mHeartbeatHandlerMsgFlag is invalid");
            return;
        }
        if (!isAuthenticationSuccess) {
            Log.e(TAG, "stop > process fail : 还没心动过");
            return;
        }
        Log.i(TAG, "stop > 心跳停止 ");
        getStatusProcessBus().start(PS_DEVICE_OFF_LINE, 0);
    }

    public boolean isStart() {
        if (null == getStatusProcessBus()
                || -1 == PS_HEARTBEAT_REPORT) {
            Log.e(TAG, "isStart > process fail : getStatusGuardHandler is invalid");
            return false;
        }
        return getStatusProcessBus().isStart(PS_HEARTBEAT_REPORT);
    }
}
