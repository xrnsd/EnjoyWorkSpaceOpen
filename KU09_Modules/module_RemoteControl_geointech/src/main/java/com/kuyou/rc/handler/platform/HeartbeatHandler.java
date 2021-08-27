package com.kuyou.rc.handler.platform;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import androidx.annotation.NonNull;

import com.kuyou.rc.handler.platform.basic.IHeartbeat;
import com.kuyou.rc.handler.platform.basic.IHeartbeatReportCallBack;

import kuyou.common.ipc.RemoteEvent;
import kuyou.common.ku09.event.rc.EventHeartbeatReply;
import kuyou.common.ku09.event.rc.basic.EventRemoteControl;
import kuyou.common.ku09.handler.BasicEventHandler;
import kuyou.sdk.jt808.basic.RemoteControlDeviceConfig;

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

    private IHeartbeatReportCallBack mHeartbeatReportCallBack;
    private ReportHandler mReportHandler;
    private RemoteControlDeviceConfig RemoteControlDeviceConfig;

    public HeartbeatHandler(Looper looper) {
        mReportHandler = new ReportHandler(looper);
    }

    @Override
    protected void initHandleEventCodeList() {
        registerHandleEvent(EventRemoteControl.Code.HEARTBEAT_REPORT, false);
        registerHandleEvent(EventRemoteControl.Code.HEARTBEAT_REPLY, false);
    }

    @Override
    public boolean onModuleEvent(RemoteEvent event) {
        switch (event.getCode()) {
            case EventRemoteControl.Code.HEARTBEAT_REPORT:
                onHeartbeatReport();
                break;
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
    public boolean isHeartbeatConnected() {

        if (!mReportHandler.isStart()) {
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
        boolean result = (2 > Math.abs(mReportHandler.getReportTime() - mHeartbeatReplyFlowId));
        Log.d(TAG, "isHeartbeatConnected > result = " + result);
    }

    @Override
    public void start() {
        mReportHandler.start();
    }

    @Override
    public void stop() {
        mReportHandler.stop();
    }

    protected RemoteControlDeviceConfig getRemoteControlDeviceConfig() {
        return RemoteControlDeviceConfig;
    }

    public HeartbeatHandler setRemoteControlDeviceConfig(RemoteControlDeviceConfig config) {
        RemoteControlDeviceConfig = config;
        mReportHandler.setReportLocationFreq(config.getHeartbeatInterval());
        return HeartbeatHandler.this;
    }

    protected IHeartbeatReportCallBack getHeartbeatReportCallBack() {
        return mHeartbeatReportCallBack;
    }

    protected void onHeartbeatReport() {
        if (null == getHeartbeatReportCallBack()) {
            Log.e(TAG, "onHeartbeatReport > process fail : getHeartbeatReportCallBack is null");
            return;
        }
        getHeartbeatReportCallBack().onHeartbeatReport();
    }

    public HeartbeatHandler setHeartbeatReportCallBack(IHeartbeatReportCallBack callback) {
        mHeartbeatReportCallBack = callback;
        mReportHandler.setCallBack(callback);
        return HeartbeatHandler.this;
    }

    public class ReportHandler extends Handler {
        public static final int MSG_REPORT = 2;

        private int mReportLocationFreq = 5000;
        private IHeartbeatReportCallBack mCallBack;
        private long mReportTime = 0;

        public ReportHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (MSG_REPORT != msg.what)
                return;
            removeMessages(msg.what);
            if (null == mCallBack)
                mCallBack.onHeartbeatReport();
            mReportTime += 1;
            sendEmptyMessageDelayed(MSG_REPORT, getReportLocationFreq());
        }

        protected IHeartbeatReportCallBack getCallBack() {
            return mCallBack;
        }

        public ReportHandler setCallBack(IHeartbeatReportCallBack callBack) {
            mCallBack = callBack;
            return ReportHandler.this;
        }

        public long getReportTime() {
            return mReportTime;
        }

        public boolean isStart() {
            return hasMessages(MSG_REPORT);
        }

        public ReportHandler start() {
            removeMessages(MSG_REPORT);
            sendEmptyMessage(MSG_REPORT);
            return ReportHandler.this;
        }

        public void stop() {
            removeMessages(MSG_REPORT);
        }

        protected int getReportLocationFreq() {
            return mReportLocationFreq;
        }

        public ReportHandler setReportLocationFreq(int val) {
            mReportLocationFreq = val;
            return ReportHandler.this;
        }
    }
}
