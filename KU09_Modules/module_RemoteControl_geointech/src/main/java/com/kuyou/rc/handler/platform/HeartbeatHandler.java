package com.kuyou.rc.handler.platform;

import android.os.Looper;
import android.util.Log;


import com.kuyou.rc.handler.platform.basic.IHeartbeat;
import com.kuyou.rc.handler.platform.basic.IHeartbeatReportCallBack;

import kuyou.common.ipc.RemoteEvent;
import kuyou.common.ku09.event.rc.EventHeartbeatReply;
import kuyou.common.ku09.event.rc.EventHeartbeatReport;
import kuyou.common.ku09.event.rc.EventHeartbeatRequest;
import kuyou.common.ku09.event.rc.basic.EventRemoteControl;
import kuyou.common.ku09.handler.BasicEventHandler;
import kuyou.common.ku09.handler.basic.IStatusGuard;
import kuyou.common.ku09.handler.basic.IStatusGuardCallback;
import kuyou.common.ku09.handler.basic.StatusGuardRequestConfig;

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
    private int mHeartbeatHandlerMsgFlag = -1;

    private IHeartbeatReportCallBack mHeartbeatReportCallBack;

    @Override
    protected void initHandleEventCodeList() {
        registerHandleEvent(EventRemoteControl.Code.HEARTBEAT_REPORT_REQUEST, false);
        //registerHandleEvent(EventRemoteControl.Code.HEARTBEAT_REPORT, false);
        registerHandleEvent(EventRemoteControl.Code.HEARTBEAT_REPLY, false);
        registerHandleEvent(EventRemoteControl.Code.HEARTBEAT_REPLY, false);
    }

    @Override
    public boolean onModuleEvent(RemoteEvent event) {
        switch (event.getCode()) {
            case EventRemoteControl.Code.HEARTBEAT_REPORT_REQUEST:
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
    public void setStatusGuardHandler(IStatusGuard handler) {
        super.setStatusGuardHandler(handler);
        handler.registerStatusGuardCallback(new IStatusGuardCallback() {
            @Override
            public void onReceiveMessage() {
                HeartbeatHandler.this.mHeartbeatReportFlowId += 1;
                HeartbeatHandler.this.dispatchEvent(new EventHeartbeatReport().setRemote(false));
            }

            @Override
            public void onRemoveMessage() {

            }

            @Override
            public void setReceiveMessage(int what) {
                HeartbeatHandler.this.mHeartbeatHandlerMsgFlag = what;
            }
        }, new StatusGuardRequestConfig(true, getDeviceConfig().getHeartbeatInterval(), Looper.getMainLooper()));
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
        if (null == getStatusGuardHandler()
                || -1 == mHeartbeatHandlerMsgFlag) {
            Log.e(TAG, "start > process fail : getStatusGuardHandler is invalid");
            return;
        }
        Log.i(TAG, "onModuleEvent > 心跳 >开始上报位置 ");
        getStatusGuardHandler().start(mHeartbeatHandlerMsgFlag);
    }

    @Override
    public void stop() {
        if (null == getStatusGuardHandler()
                || -1 == mHeartbeatHandlerMsgFlag) {
            Log.e(TAG, "stop > process fail : getStatusGuardHandler is invalid");
            return;
        }
        Log.i(TAG, "onModuleEvent > 心跳 > 停止上报位置 ");
        getStatusGuardHandler().stop(mHeartbeatHandlerMsgFlag);
    }

    protected boolean isStart() {
        if (null == getStatusGuardHandler()
                || -1 == mHeartbeatHandlerMsgFlag) {
            Log.e(TAG, "stop > process fail : getStatusGuardHandler is invalid");
            return false;
        }
        return getStatusGuardHandler().isStart(mHeartbeatHandlerMsgFlag);
    }
}
