package com.kuyou.rc.handler;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.kuyou.rc.handler.platform.PlatformConnectManager;
import com.kuyou.rc.handler.platform.basic.IHeartbeat;
import com.kuyou.rc.protocol.jt808extend.Jt808ExtendProtocolCodec;
import com.kuyou.rc.protocol.jt808extend.basic.InstructionParserListener;
import com.kuyou.rc.protocol.jt808extend.basic.SicBasic;
import com.kuyou.rc.protocol.jt808extend.item.SicAudioVideo;
import com.kuyou.rc.protocol.jt808extend.item.SicAuthentication;
import com.kuyou.rc.protocol.jt808extend.item.SicPhotoTake;
import com.kuyou.rc.protocol.jt808extend.item.SicPhotoUploadReply;
import com.kuyou.rc.protocol.jt808extend.item.SicTextMessage;

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
import kuyou.common.ku09.event.rc.EventAudioVideoParametersApplyRequest;
import kuyou.common.ku09.event.rc.EventAudioVideoParametersApplyResult;
import kuyou.common.ku09.event.rc.EventAuthenticationRequest;
import kuyou.common.ku09.event.rc.EventAuthenticationResult;
import kuyou.common.ku09.event.rc.EventConnectResult;
import kuyou.common.ku09.event.rc.EventHeartbeatReply;
import kuyou.common.ku09.event.rc.EventHeartbeatRequest;
import kuyou.common.ku09.event.rc.EventLocalDeviceStatus;
import kuyou.common.ku09.event.rc.EventPhotoUploadResult;
import kuyou.common.ku09.event.rc.EventSendToRemoteControlPlatformRequest;
import kuyou.common.ku09.event.rc.basic.EventRemoteControl;
import kuyou.common.ku09.event.rc.basic.EventRequest;
import kuyou.common.ku09.event.rc.basic.EventResult;
import kuyou.common.ku09.handler.BasicEventHandler;
import kuyou.common.ku09.protocol.IJT808ExtensionProtocol;
import kuyou.common.ku09.status.IStatusProcessBus;
import kuyou.common.ku09.status.StatusProcessBusCallbackImpl;
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
public class PlatformInteractiveHandler extends BasicEventHandler {

    protected final String TAG = "com.kuyou.rc.handler > PlatformInteractiveHandler ";

    protected boolean isNetworkAvailable = false;

    protected Jt808ExtendProtocolCodec mJt808ExtendProtocolCodec;
    private IHeartbeat mHeartbeatHandler;

    protected IHeartbeat getHeartbeatHandler() {
        return mHeartbeatHandler;
    }

    @Override
    public List<BasicEventHandler> getSubEventHandlers() {
        List<BasicEventHandler> handlers = new ArrayList<>();
        Log.d(TAG, "getSubEventHandlers > ");
        HeartbeatHandler handler = new HeartbeatHandler();
        handlers.add(handler);
        mHeartbeatHandler = handler;
        return handlers;
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
                            boolean isAuthenticationSuccess = (0 == bean.getReplyResult());
                            Log.i(TAG, new StringBuilder(128)
                                    .append("\n<<<<<<<<<<  服务器鉴权:").append(isAuthenticationSuccess ? "成功" : "失败")
                                    .append("  <<<<<<<<<<")
                                    .toString());
                            dispatchEvent(new EventAuthenticationResult().setResult(isAuthenticationSuccess));
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
                    PlatformInteractiveHandler.this.play(instruction.getText());
                }

                @Override
                public void onRemote2LocalExpand(SicAudioVideo instruction) {
                    Log.d(TAG, "onParse > SICAudioVideo");

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
                    instruction.setMediaId(System.currentTimeMillis());
                    dispatchEvent(new EventPhotoTakeRequest()
                            .setFileName(instruction.getFileName())
                            .setUpload(true)
                            .setRemote(true));
                }

                @Override
                public void onRemote2LocalExpand(SicPhotoUploadReply instruction) {
                    dispatchEvent(new EventPhotoUploadResult()
                            .setResult(instruction.isResultSuccess()));
                }
            }).load(PlatformInteractiveHandler.this.getDeviceConfig());
        }
        return mJt808ExtendProtocolCodec;
    }

    public String isReady() {
        //联网检测
        boolean isNetworkAvailableNow = NetworkUtils.isNetworkAvailable(getContext());
        if (!isNetworkAvailableNow) {
            Log.w(TAG, "isReady > 未联网,放弃平台链接状态检查和模块自动重置 ");

            //服务器断开没有正常回调时，强制停止心跳
            if (null != getHeartbeatHandler() && getHeartbeatHandler().isStart()) {
                getPlatformConnectManager().disconnect();
                getHeartbeatHandler().stop();
            }

            return null;
        }

        if (!isNetworkAvailable && isNetworkAvailableNow) {
            dispatchEvent(new EventNetworkConnect().setRemote(true));
        } else if (isNetworkAvailable && !isNetworkAvailableNow) {
            dispatchEvent(new EventNetworkDisconnect().setRemote(true));
        }
        isNetworkAvailable = isNetworkAvailableNow;

        //联网后以socketManager连接状态为准
        if (isNetworkAvailable) {
            if (getPlatformConnectManager().isConnect()) {
                Log.i(TAG, "isReady > 已联网,平台链接正常 ");

                //心跳未正常开启，尝试开启
//                if (!getHeartbeatHandler().isStart()) {
//                    getHeartbeatHandler().start();
//                }
                return null;
            }
            Log.w(TAG, "isReady >  已联网,未连接平台,尝试链接平台 ");
            if (!connect()) {
                return "平台连接异常";
            }
            return null;
        }

        return null;
    }

    private PlatformConnectManager getPlatformConnectManager() {
        return PlatformConnectManager.getInstance(getDeviceConfig());
    }

    public void initialConnect() {
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
        try {
            getPlatformConnectManager().connect(new SocketActionAdapter() {
                @Override
                public void onSocketReadResponse(ConnectionInfo info, String action, OriginalData data) {
                    PlatformInteractiveHandler.this.getJt808ExtendProtocolCodec().handler(data);
                }

                @Override
                public void onSocketDisconnection(ConnectionInfo info, String action, Exception e) {
                    super.onSocketDisconnection(info, action, e);
                    PlatformInteractiveHandler.this.onModuleEvent(new EventConnectResult()
                            .setResultCode(EventResult.ResultCode.DIS));
                }

                @Override
                public void onSocketConnectionSuccess(ConnectionInfo info, String action) {
                    super.onSocketConnectionSuccess(info, action);
                    PlatformInteractiveHandler.this.onModuleEvent(new EventConnectResult()
                            .setResultCode(EventResult.ResultCode.SUCCESS));
                }

                @Override
                public void onSocketConnectionFailed(ConnectionInfo info, String action, Exception e) {
                    super.onSocketConnectionFailed(info, action, e);
                    PlatformInteractiveHandler.this.onModuleEvent(new EventConnectResult()
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
    private boolean isAuthenticationSuccess = false;
    private int mStaProFlagAuthenticationTimeOut = -1;
    private boolean isHeartbeatStartSuccess = false;

    public void sendToRemoteControlPlatform(byte[] msg) {
        if (null == msg || msg.length <= 0) {
            Log.e(TAG, "sendToRemoteControlPlatform > process fail : msg is none");
            return;
        }
        Log.d(TAG, "sendToRemoteControlPlatform > " + ByteUtils.bytes2Hex(msg));
        getPlatformConnectManager().send(msg);
    }

    protected SicBasic getSingleInstructionParserByEventCode(RemoteEvent event) {
        if (null == event) {
            Log.e(TAG, "getSicByEventCode > process fail : event is null");
            return null;
        }
        final int eventCode = event.getCode();
        for (SicBasic singleInstructionParse : getJt808ExtendProtocolCodec().getSicBasicList()) {
            if (singleInstructionParse.isMatchEventCode(eventCode)) {
                return singleInstructionParse;
            }
        }
        Log.e(TAG, "getSicByEventCode > process fail : event is invalid =" + eventCode);
        return null;
    }

    @Override
    public void setStatusProcessBus(IStatusProcessBus handler) {
        super.setStatusProcessBus(handler);

        mStaProFlagAuthenticationTimeOut = handler.registerStatusBusProcessCallback(
                new StatusProcessBusCallbackImpl(false, 5000, Looper.getMainLooper()) {
                    @Override
                    public void onReceiveStatusNotice(boolean isRemove) {
                        if (isRemove) {
                            return;
                        }
                        Log.e(TAG, "onReceiveStatusNotice > process fail : 鉴权失败，请重新尝试");
                        PlatformInteractiveHandler.this.isAuthenticationSuccess = false;
                        PlatformInteractiveHandler.this.play("设备上线失败,错误1");
                    }
                });
    }

    @Override
    protected void initHandleEventCodeList() {
        registerHandleEvent(EventRemoteControl.Code.CONNECT_RESULT, false);
        registerHandleEvent(EventRemoteControl.Code.AUTHENTICATION_REQUEST, false);
        registerHandleEvent(EventRemoteControl.Code.AUTHENTICATION_RESULT, false);
        registerHandleEvent(EventRemoteControl.Code.SEND_TO_REMOTE_CONTROL_PLATFORM, false);

        registerHandleEvent(EventRemoteControl.Code.AUDIO_VIDEO_PARAMETERS_APPLY_REQUEST, true);
        registerHandleEvent(EventAudioVideoCommunication.Code.AUDIO_VIDEO_OPERATE_RESULT, true);
    }

    @Override
    public boolean onModuleEvent(RemoteEvent event) {
        switch (event.getCode()) {
            case EventPowerChange.Code.POWER_CHANGE:
                //关机时主动关闭心跳
                if (EventPowerChange.POWER_STATUS.SHUTDOWN == EventPowerChange.getPowerStatus(event)) {
                    getPlatformConnectManager().disconnect();
                    getHeartbeatHandler().stop();
                }
                break;

            case EventRemoteControl.Code.LOCAL_DEVICE_STATUS:
                final int status = EventLocalDeviceStatus.getDeviceStatus(event);

                if (EventLocalDeviceStatus.Status.OFF_LINE == status
                        && getPlatformConnectManager().isConnect()) {
                    Log.d(TAG, "onModuleEvent > 设备离线，断开后台连接");
                    getPlatformConnectManager().disconnect();
                }
                break;

            case EventRemoteControl.Code.CONNECT_RESULT:
                isRemoteControlPlatformConnected = EventConnectResult.isResultSuccess(event);
                if (isRemoteControlPlatformConnected) {
                    Log.i(TAG, "onModuleEvent > 连接服务器成功");
                    isAuthenticationSuccess = false;
                    dispatchEvent(new EventAuthenticationRequest()
                            .setRemote(false));
                    break;
                }
                if (EventResult.ResultCode.DIS == EventConnectResult.getResultCode(event)) {
                    Log.w(TAG, "onModuleEvent > 服务器连接断开");
                    dispatchEvent(new EventHeartbeatRequest()
                            .setRequestCode(EventRequest.RequestCode.CLOSE)
                            .setRemote(false));
                    break;
                }
                Log.w(TAG, "onModuleEvent > 连接服务器失败");
                break;

            case EventRemoteControl.Code.AUTHENTICATION_REQUEST:
                Log.i(TAG, "onModuleEvent > 开始鉴权 ");
                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        SicBasic singleInstructionParser = PlatformInteractiveHandler.this.getSingleInstructionParserByEventCode(event);
                        if (null == singleInstructionParser) {
                            return;
                        }
                        SicAuthentication authentication = (SicAuthentication) singleInstructionParser;
                        authentication.setDeviceConfig(PlatformInteractiveHandler.this.getDeviceConfig());
                        authentication.setBodyConfig(SicBasic.BodyConfig.REQUEST);
                        sendToRemoteControlPlatform(authentication.getBody());
                        //鉴权失败，超时提示
                        PlatformInteractiveHandler.this.getStatusProcessBus().start(mStaProFlagAuthenticationTimeOut);
                    }
                }, 500);
                break;

            case EventRemoteControl.Code.AUTHENTICATION_RESULT:
                if (EventAuthenticationResult.isResultSuccess(event)) {
                    isAuthenticationSuccess = true;
                    dispatchEvent(new EventHeartbeatRequest()
                            .setRequestCode(EventRequest.RequestCode.OPEN)
                            .setRemote(false));
                    getStatusProcessBus().stop(mStaProFlagAuthenticationTimeOut);
                } else {
                    //Log.w(TAG, "onModuleEvent > 鉴权失败 ");
                }
                break;

            case EventRemoteControl.Code.SEND_TO_REMOTE_CONTROL_PLATFORM:
                sendToRemoteControlPlatform(EventSendToRemoteControlPlatformRequest.getMsg(event));
                break;

            case EventRemoteControl.Code.AUDIO_VIDEO_PARAMETERS_APPLY_REQUEST:
                Log.i(TAG, "onModuleEvent > 申请音视频参数和操作");
                final int eventType = EventAudioVideoParametersApplyRequest.getEventType(event);
                boolean isClose = IJT808ExtensionProtocol.EVENT_TYPE_CLOSE == eventType;

                //处理：在未链接平台情况下申请打开参数
                if (!isRemoteControlPlatformConnected) {
                    Log.w(TAG, "onModuleEvent > 申请视频参数和操作 > 未链接平台");
                    dispatchEvent(new EventAudioVideoParametersApplyResult()
                            .setResult(false)
                            .setRemote(true));
                    play("打开失败，请检查网络链接");
                    break;
                }

                //处理：平台未响应时的超时机制
                int platformType = EventAudioVideoParametersApplyRequest.getPlatformType(event);
                int mediaTypeCode = EventAudioVideoParametersApplyRequest.getMediaType(event);

                //处理：通知平台
                SicBasic singleInstructionParserAVOAR = getSingleInstructionParserByEventCode(event);
                if (null == singleInstructionParserAVOAR) {
                    break;
                }
                byte[] PlatformDirectiveAVCMsg = ((SicAudioVideo) singleInstructionParserAVOAR)
                        .setPlatformType(platformType)
                        .setMediaType(mediaTypeCode)
                        .setEventType(eventType)
                        .getBody(SicBasic.BodyConfig.REQUEST);
                sendToRemoteControlPlatform(PlatformDirectiveAVCMsg);
                break;

            case EventAudioVideoCommunication.Code.AUDIO_VIDEO_OPERATE_RESULT:
                Log.w(TAG, "onModuleEvent > 平台音视频参数请求处理结果 > 回复给平台");

                SicBasic singleInstructionParserAVOR = getSingleInstructionParserByEventCode(event);
                if (null == singleInstructionParserAVOR) {
                    Log.e(TAG, "onModuleEvent > 返回平台音视频参数下发的请求处理结果 > process fail : singleInstructionParserAVOR is null");
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