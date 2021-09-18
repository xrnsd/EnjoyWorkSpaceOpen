package com.kuyou.rc.handler;

import android.os.Looper;
import android.util.Log;

import com.kuyou.rc.basic.jt808extend.InstructionParserListener;
import com.kuyou.rc.basic.jt808extend.Jt808ExtendProtocolCodec;
import com.kuyou.rc.basic.jt808extend.PlatformConnectManager;
import com.kuyou.rc.basic.jt808extend.item.SicAudioVideo;
import com.kuyou.rc.basic.jt808extend.item.SicAuthentication;
import com.kuyou.rc.basic.jt808extend.item.SicBasic;
import com.kuyou.rc.basic.jt808extend.item.SicPhotoTake;
import com.kuyou.rc.basic.jt808extend.item.SicTextMessage;

import java.util.ArrayList;
import java.util.List;

import kuyou.common.bytes.ByteUtils;
import kuyou.common.ipc.RemoteEvent;
import kuyou.common.ku09.event.avc.EventAudioVideoOperateRequest;
import kuyou.common.ku09.event.avc.EventAudioVideoOperateResult;
import kuyou.common.ku09.event.avc.EventPhotoTakeRequest;
import kuyou.common.ku09.event.avc.basic.EventAudioVideoCommunication;
import kuyou.common.ku09.event.common.EventNetworkConnect;
import kuyou.common.ku09.event.common.EventNetworkDisconnect;
import kuyou.common.ku09.event.common.EventPowerChange;
import kuyou.common.ku09.event.common.basic.EventCommon;
import kuyou.common.ku09.event.rc.EventAudioVideoParametersApplyRequest;
import kuyou.common.ku09.event.rc.EventAudioVideoParametersApplyResult;
import kuyou.common.ku09.event.rc.EventAuthenticationRequest;
import kuyou.common.ku09.event.rc.EventAuthenticationResult;
import kuyou.common.ku09.event.rc.EventConnectRequest;
import kuyou.common.ku09.event.rc.EventConnectResult;
import kuyou.common.ku09.event.rc.EventHeartbeatReply;
import kuyou.common.ku09.event.rc.EventHeartbeatRequest;
import kuyou.common.ku09.event.rc.EventLocalDeviceStatus;
import kuyou.common.ku09.event.rc.EventSendToRemoteControlPlatformRequest;
import kuyou.common.ku09.event.rc.basic.EventRemoteControl;
import kuyou.common.ku09.event.rc.basic.EventRequest;
import kuyou.common.ku09.event.rc.basic.EventResult;
import kuyou.common.ku09.event.rc.hmd.EventHardwareModuleStatusDetectionFinish;
import kuyou.common.ku09.handler.BasicAssistHandler;
import kuyou.common.ku09.protocol.basic.IDeviceConfig;
import kuyou.common.ku09.protocol.basic.IJT808ExtensionProtocol;
import kuyou.common.status.StatusProcessBusCallbackImpl;
import kuyou.common.status.basic.IStatusProcessBusCallback;
import kuyou.common.utils.NetworkUtils;
import kuyou.sdk.jt808.basic.jt808bean.JTT808Bean;
import kuyou.sdk.jt808.oksocket.client.sdk.client.ConnectionInfo;
import kuyou.sdk.jt808.oksocket.client.sdk.client.action.SocketActionAdapter;
import kuyou.sdk.jt808.oksocket.core.pojo.OriginalData;

/**
 * <p>
 * action :[协处理器]远程控制平台交互
 * remarks:  <br/>
 * author: wuguoxian <br/>
 * date: 21-3-29 <br/>
 * </p>
 */
public class PlatformInteractiveHandler extends BasicAssistHandler {

    protected final String TAG = "com.kuyou.rc.handler > PlatformInteractiveHandler ";

    protected final static int PS_AUTHENTICATION_REQUEST_WAIT_TIME_OUT = 1;

    protected boolean isNetworkAvailable = false;

    protected Jt808ExtendProtocolCodec mJt808ExtendProtocolCodec;
    protected HeartbeatHandler mHeartbeatHandler;

    protected HeartbeatHandler getHeartbeatHandler() {
        if (null == mHeartbeatHandler) {
            mHeartbeatHandler = new HeartbeatHandler();
        }
        return mHeartbeatHandler;
    }

    protected Jt808ExtendProtocolCodec getJt808ExtendProtocolCodec() {
        if (null == mJt808ExtendProtocolCodec) {
            mJt808ExtendProtocolCodec = Jt808ExtendProtocolCodec.getInstance(getContext());
            mJt808ExtendProtocolCodec.setInstructionParserListener(new InstructionParserListener() {
                @Override
                public void onRemote2LocalBasic(JTT808Bean bean, byte[] data) {
                    switch (bean.getMsgId()) {
                        case IJT808ExtensionProtocol.S2C_RESULT_CONNECT_REPLY:
                            if (0 != bean.getReplyFlowNumber()) {
                                PlatformInteractiveHandler.this.dispatchEvent(new EventHeartbeatReply()
                                        .setFlowNumber(bean.getReplyFlowNumber())
                                        .setResult(0 == bean.getReplyResult())
                                        .setRemote(false));
                                break;
                            }
                        case IJT808ExtensionProtocol.S2C_RESULT_AUTHENTICATION_REPLY:
                            dispatchEvent(new EventAuthenticationResult()
                                    .setResult(0 == bean.getReplyResult())
                                    .setEnableConsumeSeparately(false)
                                    .setRemote(false));
                            break;
                        default:
                            break;
                    }
                }

                @Override
                public void onRemote2LocalExpandFail(Exception e) {
                    Log.e(TAG, Log.getStackTraceString(e));
                }

                @Override
                public void onRemote2LocalExpand(SicTextMessage instruction) {
                    if (!PlatformInteractiveHandler.this.getHeartbeatHandler().isConnect()) {
                        Log.e(TAG, "onRemote2LocalExpand > SicTextMessage > process fail : 设备心跳异常，放弃处理服务器请求 \n"
                                + instruction.toString());
                        return;
                    }
                    PlatformInteractiveHandler.this.play(instruction.getText());
                }

                @Override
                public void onRemote2LocalExpand(SicAudioVideo instruction) {
                    if (!PlatformInteractiveHandler.this.getHeartbeatHandler().isConnect()) {
                        Log.e(TAG, "onRemote2LocalExpand > SICAudioVideo > process fail : 设备心跳异常，放弃处理服务器请求 \n"
                                + instruction.toString());
                        return;
                    }
                    Log.d(TAG, "onRemote2LocalExpand > SICAudioVideo");
                    EventAudioVideoOperateRequest event = null;
                    event = new EventAudioVideoOperateRequest()
                            .setFlowNumber(instruction.getFlowNumber())
                            .setMediaType(instruction.getMediaType())
                            .setToken(instruction.getToken())
                            .setChannelId(String.valueOf(instruction.getChannelId()))
                            .setEventType(instruction.getEventType());
                    event.setRemote(true);
                    dispatchEvent(event);
                }

                @Override
                public void onRemote2LocalExpand(SicPhotoTake instruction) {
                    if (!PlatformInteractiveHandler.this.getHeartbeatHandler().isConnect()) {
                        Log.e(TAG, "onRemote2LocalExpand > SicPhotoTake > process fail : 设备心跳异常，放弃处理服务器请求 \n"
                                + instruction.toString());
                        return;
                    }
                    instruction.setMediaId(System.currentTimeMillis());
                    dispatchEvent(new EventPhotoTakeRequest()
                            .setFileName(instruction.getFileName())
                            .setUpload(true)
                            .setRemote(true));
                }
            }).load(PlatformInteractiveHandler.this.getDeviceConfig());
        }
        return mJt808ExtendProtocolCodec;
    }

    public String isReady() {
        //网络检测
        boolean isNetworkAvailableNow = NetworkUtils.isNetworkAvailable(getContext());
        if (!isNetworkAvailableNow) {
            Log.w(TAG, "isReady > 网络:未连接 > 放弃平台连接状态检查和模块自动重置 ");

            //服务器断开没有正常回调时，强制停止心跳
            if (null != getHeartbeatHandler() && getHeartbeatHandler().isStart()) {
                getHeartbeatHandler().stop();
            }
            if (!getPlatformConnectManager().isClean()) {
                getPlatformConnectManager().disconnect();
            }

            return null;
        }

        if (!isNetworkAvailable && isNetworkAvailableNow) {
            dispatchEvent(new EventNetworkConnect()
                    .setPolicyDispatch2Myself(true)
                    .setRemote(true));
        } else if (isNetworkAvailable && !isNetworkAvailableNow) {
            dispatchEvent(new EventNetworkDisconnect()
                    .setPolicyDispatch2Myself(true)
                    .setRemote(true));
        }
        isNetworkAvailable = isNetworkAvailableNow;

        //网络连接后以socketManager连接状态为准
        if (isNetworkAvailable) {
            if (getPlatformConnectManager().isConnect()) {
                if (!getHeartbeatHandler().isConnect()) {
                    Log.w(TAG, "isReady > 网络:已连接,平台:已连接,心跳:未连接 ");
                    return "心跳:未连接";
                }
                Log.i(TAG, "isReady > 网络:已连接,平台:已连接,心跳:已连接 ");
                return null;
            }
            Log.w(TAG, "isReady >  网络:已连接,平台:未连接 > 尝试连接平台 ");
            if (!connect()) {
                return "平台:连接异常";
            }
            return null;
        }

        return null;
    }

    private PlatformConnectManager getPlatformConnectManager() {
        return PlatformConnectManager.getInstance();
    }

    @Override
    public void start() {
        super.start();
        isNetworkAvailable = NetworkUtils.isNetworkAvailable(getContext());
        if (!isNetworkAvailable) {
            Log.e(TAG, "InitialConnect > process fail : isNetworkAvailable is false");
            return;
        }
        connect();
    }

    protected boolean connect() {
        if (getPlatformConnectManager().isConnect()) {
            Log.e(TAG, "connect > process fail : HelmetSocketManager is connected");
            return true;
        }
        if (IDeviceConfig.VAL_NONE.equals(getDeviceConfig().getRemoteControlServerAddress())) {
            play("上线失败，设备配置无效");
            return false;
        }
        try {
            getPlatformConnectManager().connect(
                    getDeviceConfig().getRemoteControlServerAddress(),
                    getDeviceConfig().getRemoteControlServerPort(),
                    getDeviceConfig().getHeartbeatInterval(),
                    new SocketActionAdapter() {
                        @Override
                        public void onSocketReadResponse(ConnectionInfo info, String action, OriginalData data) {
                            PlatformInteractiveHandler.this.getJt808ExtendProtocolCodec().handler(data);
                        }

                        @Override
                        public void onSocketDisconnection(ConnectionInfo info, String action, Exception e) {
                            super.onSocketDisconnection(info, action, e);
                            PlatformInteractiveHandler.this.onReceiveEventNotice(new EventConnectResult()
                                    .setResultCode(EventResult.ResultCode.DIS));
                        }

                        @Override
                        public void onSocketConnectionSuccess(ConnectionInfo info, String action) {
                            super.onSocketConnectionSuccess(info, action);
                            PlatformInteractiveHandler.this.onReceiveEventNotice(new EventConnectResult()
                                    .setResultCode(EventResult.ResultCode.SUCCESS));
                        }

                        @Override
                        public void onSocketConnectionFailed(ConnectionInfo info, String action, Exception e) {
                            super.onSocketConnectionFailed(info, action, e);
                            PlatformInteractiveHandler.this.onReceiveEventNotice(new EventConnectResult()
                                    .setResultCode(EventResult.ResultCode.FAIL));
                        }
                    });
        } catch (Exception e) {
            Log.e(TAG, Log.getStackTraceString(e));
            return false;
        }
        return true;
    }

    // =====================  事件处理 =============================

    private boolean isRemoteControlPlatformConnected = false;
    private boolean isHardwareModuleDetectionFinish = !HardwareModuleDetectionHandler.IS_ENABLE;

    public void sendToRemoteControlPlatform(byte[] msg) {
        if (null == msg || msg.length <= 0) {
            Log.e(TAG, "sendToRemoteControlPlatform > process fail : msg is none");
            return;
        }
        Log.d(TAG, "sendToRemoteControlPlatform > " + ByteUtils.bytes2Hex(msg));
        if (Looper.myLooper() == Looper.getMainLooper()) {
            getPlatformConnectManager().send(msg);
            return;
        }
        mMainThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                getPlatformConnectManager().send(msg);
            }
        });
    }

    protected SicBasic getSingleInstructionParserByEventCode(RemoteEvent event) {
        if (null == event) {
            Log.e(TAG, "getSicByEventCode > process fail : event is null");
            return null;
        }
        return getSingleInstructionParserByEventCode(event.getCode());
    }

    protected SicBasic getSingleInstructionParserByEventCode(int eventCode) {
        for (SicBasic singleInstructionParse : getJt808ExtendProtocolCodec().getSicBasicList()) {
            if (singleInstructionParse.isMatchEventCode(eventCode)) {
                return singleInstructionParse;
            }
        }
        Log.e(TAG, "getSicByEventCode > process fail : event is invalid =" + eventCode);
        return null;
    }

    @Override
    protected void initReceiveProcessStatusNotices() {
        super.initReceiveProcessStatusNotices();

        getStatusProcessBus().registerStatusNoticeCallback(PS_AUTHENTICATION_REQUEST_WAIT_TIME_OUT,
                new StatusProcessBusCallbackImpl(false, 10 * 1000)
                        .setNoticeHandleLooperPolicy(IStatusProcessBusCallback.LOOPER_POLICY_MAIN));
    }

    @Override
    protected void onReceiveProcessStatusNotice(int statusCode, boolean isRemove) {
        super.onReceiveProcessStatusNotice(statusCode, isRemove);
        switch (statusCode) {
            case PS_AUTHENTICATION_REQUEST_WAIT_TIME_OUT:
                Log.w(TAG, "onReceiveProcessStatusNotice > process fail : 鉴权等待超时，强制进行鉴权操作");
                isHardwareModuleDetectionFinish = true;
                dispatchEvent(new EventAuthenticationRequest()
                        .setRemote(false));
                break;
            default:
                break;
        }
    }

    @Override
    public List<BasicAssistHandler> getSubEventHandlers() {
        List<BasicAssistHandler> handlers = new ArrayList<>();

        handlers.add(getHeartbeatHandler());
        return handlers;
    }

    @Override
    protected void initReceiveEventNotices() {
        registerHandleEvent(EventCommon.Code.POWER_CHANGE, false);

        registerHandleEvent(EventRemoteControl.Code.CONNECT_RESULT, false);
        registerHandleEvent(EventRemoteControl.Code.AUTHENTICATION_REQUEST, false);
        registerHandleEvent(EventRemoteControl.Code.SEND_TO_REMOTE_CONTROL_PLATFORM, false);

        registerHandleEvent(EventRemoteControl.Code.AUDIO_VIDEO_PARAMETERS_APPLY_REQUEST, true);
        registerHandleEvent(EventAudioVideoCommunication.Code.AUDIO_VIDEO_OPERATE_RESULT, true);
    }

    @Override
    public boolean onReceiveEventNotice(RemoteEvent event) {
        switch (event.getCode()) {
            case EventPowerChange.Code.POWER_CHANGE:
                //关机时主动关闭心跳
                if (EventPowerChange.POWER_STATUS.SHUTDOWN == EventPowerChange.getPowerStatus(event)) {
                    getPlatformConnectManager().disconnect();
                    getHeartbeatHandler().stop();
                    Log.d(TAG, "onReceiveEventNotice > POWER_CHANGE:SHUTDOWN ================================ ");
                }
                break;

            case EventRemoteControl.Code.LOCAL_DEVICE_STATUS:
                final int status = EventLocalDeviceStatus.getDeviceStatus(event);

                if (EventLocalDeviceStatus.Status.OFF_LINE == status) {
                    //Log.d(TAG, "onReceiveEventNotice > 设备离线，断开后台连接");
                    isRemoteControlPlatformConnected = false;
                    getPlatformConnectManager().disconnect();
                }
                break;

            case EventRemoteControl.Code.CONNECT_REQUEST:
                if (EventRequest.RequestCode.REOPEN == EventConnectRequest.getRequestCode(event)) {
                    Log.d(TAG, "onReceiveEventNotice > 设备重新连接后台");
                    if (!isNetworkAvailable) {
                        Log.i(TAG, "onReceiveEventNotice > 设备重新连接后台 > 未联网，放弃操作");
                        break;
                    }
                    getPlatformConnectManager().disconnect();
                }
                break;

            case EventRemoteControl.Code.CONNECT_RESULT:
                isRemoteControlPlatformConnected = EventConnectResult.isResultSuccess(event);
                if (isRemoteControlPlatformConnected) {
                    Log.i(TAG, "onReceiveEventNotice > 连接服务器成功");
                    if (isHardwareModuleDetectionFinish) {
                        dispatchEvent(new EventAuthenticationRequest()
                                .setEnableConsumeSeparately(false)
                                .setRemote(false));
                    } else {
                        getStatusProcessBus().start(PS_AUTHENTICATION_REQUEST_WAIT_TIME_OUT);
                        Log.i(TAG, "onReceiveEventNotice > 取消鉴权,等待硬件检测完成");
                    }
                    break;
                } else {
                    Log.w(TAG, "onReceiveEventNotice > 服务器连接断开 > resultCode = " + EventConnectResult.getResultCode(event));
                    dispatchEvent(new EventHeartbeatRequest()
                            .setRequestCode(EventRequest.RequestCode.CLOSE)
                            .setRemote(false));
                }
                Log.w(TAG, "onReceiveEventNotice > 连接服务器失败");
                break;

            case EventRemoteControl.Code.AUTHENTICATION_REQUEST:
                SicBasic singleInstructionParser = getSingleInstructionParserByEventCode(event);
                if (null == singleInstructionParser) {
                    Log.i(TAG, "onReceiveEventNotice > 鉴权,process fail : 找不到 SicBasic");
                    break;
                }
                Log.i(TAG, "onReceiveEventNotice > 开始鉴权 ");
                getStatusProcessBus().stop(PS_AUTHENTICATION_REQUEST_WAIT_TIME_OUT);
                if (IDeviceConfig.VAL_NONE.equals(getDeviceConfig().getDevId())) {
                    break;
                }
                SicAuthentication authentication = (SicAuthentication) singleInstructionParser;
                authentication.setDeviceConfig(PlatformInteractiveHandler.this.getDeviceConfig());
                sendToRemoteControlPlatform(authentication.getBody());
                break;

            case EventRemoteControl.Code.HARDWARE_MODULE_STATUS_DETECTION_FINISH:
                isHardwareModuleDetectionFinish = true;
                Log.i(TAG, "onReceiveEventNotice > 硬件检测完成");
                singleInstructionParser = getSingleInstructionParserByEventCode(event);
                if (null != singleInstructionParser) {
                    Log.w(TAG, "onReceiveEventNotice > 硬件检测完成,process fail : 找不到 SicBasic");
                    break;
                }
                authentication = (SicAuthentication) singleInstructionParser;
                authentication.setItemAdditionHardwareModuleDetection(EventHardwareModuleStatusDetectionFinish.getMsg(event));
                if (isNetworkAvailable && getPlatformConnectManager().isConnect()) {
                    dispatchEvent(new EventAuthenticationRequest()
                            .setRemote(false));
                }
                break;

            case EventRemoteControl.Code.SEND_TO_REMOTE_CONTROL_PLATFORM:
                sendToRemoteControlPlatform(EventSendToRemoteControlPlatformRequest.getMsg(event));
                break;

            case EventRemoteControl.Code.AUDIO_VIDEO_PARAMETERS_APPLY_REQUEST:
                Log.i(TAG, "onReceiveEventNotice > 申请音视频参数和操作");

                //处理：在未连接平台情况下申请打开参数
                if (!isRemoteControlPlatformConnected) {
                    Log.w(TAG, "onReceiveEventNotice > 申请视频参数和操作 > 未连接平台");
                    dispatchEvent(new EventAudioVideoParametersApplyResult()
                            .setResult(false)
                            .setRemote(true));
                    play("打开失败，请检查网络连接");
                    break;
                }

                //处理：通知平台
                SicBasic singleInstructionParserAVOAR = getSingleInstructionParserByEventCode(event);
                if (null == singleInstructionParserAVOAR) {
                    break;
                }
                byte[] PlatformDirectiveAVCMsg = ((SicAudioVideo) singleInstructionParserAVOAR)
                        .setPlatformType(EventAudioVideoParametersApplyRequest.getPlatformType(event))
                        .setMediaType(EventAudioVideoParametersApplyRequest.getMediaType(event))
                        .setEventType(EventAudioVideoParametersApplyRequest.getEventType(event))
                        .getBody(SicBasic.BodyConfig.REQUEST);
                sendToRemoteControlPlatform(PlatformDirectiveAVCMsg);
                break;

            case EventAudioVideoCommunication.Code.AUDIO_VIDEO_OPERATE_RESULT:
                Log.w(TAG, "onReceiveEventNotice > 平台音视频参数请求处理结果 > 回复给平台");

                SicBasic singleInstructionParserAVOR = getSingleInstructionParserByEventCode(event);
                if (null == singleInstructionParserAVOR) {
                    Log.e(TAG, "onReceiveEventNotice > 返回平台音视频参数下发的请求处理结果 > process fail : singleInstructionParserAVOR is null");
                    break;
                }
                byte[] LocalDeviceHandleAVCResultMsg = ((SicAudioVideo) singleInstructionParserAVOR)
                        .setToken(EventAudioVideoOperateResult.getToken(event))
                        .setResult(EventAudioVideoOperateResult.getResult(event))
                        .setFlowNumber(EventAudioVideoOperateResult.getFlowNumber(event))
                        .getBody(SicBasic.BodyConfig.RESULT);
                sendToRemoteControlPlatform(LocalDeviceHandleAVCResultMsg);
                break;
            default:
                return false;
        }
        return true;
    }
}