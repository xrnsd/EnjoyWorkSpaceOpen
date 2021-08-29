package com.kuyou.rc.handler;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.kuyou.rc.handler.platform.PlatformConnectManager;
import com.kuyou.rc.protocol.jt808extend.Jt808ExtendProtocolCodec;
import com.kuyou.rc.protocol.jt808extend.basic.InstructionParserListener;
import com.kuyou.rc.protocol.jt808extend.basic.SicBasic;
import com.kuyou.rc.protocol.jt808extend.item.SicAudioVideo;
import com.kuyou.rc.protocol.jt808extend.item.SicAuthentication;
import com.kuyou.rc.protocol.jt808extend.item.SicPhotoTake;
import com.kuyou.rc.protocol.jt808extend.item.SicPhotoUploadReply;
import com.kuyou.rc.protocol.jt808extend.item.SicTextMessage;
import com.kuyou.rc.utils.UploadUtil;

import java.io.File;

import kuyou.common.bytes.ByteUtils;
import kuyou.common.ipc.RemoteEvent;
import kuyou.common.ku09.config.DeviceConfig;
import kuyou.common.ku09.event.avc.EventAudioVideoOperateRequest;
import kuyou.common.ku09.event.avc.EventAudioVideoOperateResult;
import kuyou.common.ku09.event.avc.EventPhotoTakeRequest;
import kuyou.common.ku09.event.avc.EventPhotoTakeResult;
import kuyou.common.ku09.event.avc.basic.EventAudioVideoCommunication;
import kuyou.common.ku09.event.common.EventNetworkConnect;
import kuyou.common.ku09.event.common.EventNetworkDisconnect;
import kuyou.common.ku09.event.rc.EventAudioVideoParametersApplyRequest;
import kuyou.common.ku09.event.rc.EventAudioVideoParametersApplyResult;
import kuyou.common.ku09.event.rc.EventAuthenticationRequest;
import kuyou.common.ku09.event.rc.EventAuthenticationResult;
import kuyou.common.ku09.event.rc.EventConnectResult;
import kuyou.common.ku09.event.rc.EventHeartbeatReply;
import kuyou.common.ku09.event.rc.EventLocationReportStartRequest;
import kuyou.common.ku09.event.rc.EventLocationReportStopRequest;
import kuyou.common.ku09.event.rc.EventPhotoUploadRequest;
import kuyou.common.ku09.event.rc.EventPhotoUploadResult;
import kuyou.common.ku09.event.rc.EventSendToRemoteControlPlatformRequest;
import kuyou.common.ku09.event.rc.basic.EventRemoteControl;
import kuyou.common.ku09.event.rc.basic.EventResult;
import kuyou.common.ku09.handler.BasicEventHandler;
import kuyou.common.ku09.protocol.IJT808ExtensionProtocol;
import kuyou.common.utils.NetworkUtils;
import kuyou.sdk.jt808.basic.RemoteControlDeviceConfig;
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

    public Jt808ExtendProtocolCodec getJt808ExtendProtocolCodec() {
        if (null == mJt808ExtendProtocolCodec) {
            mJt808ExtendProtocolCodec = Jt808ExtendProtocolCodec.getInstance(getContext());
            mJt808ExtendProtocolCodec.setInstructionParserListener(new InstructionParserListener() {
                @Override
                public void onRemote2LocalBasic(JTT808Bean bean, byte[] data) {
                    switch (bean.getMsgId()) {
                        case IJT808ExtensionProtocol.S2C_RESULT_CONNECT_REPLY:
                            if (0 != bean.getReplyFlowNumber()) {
                                dispatchEvent(new EventHeartbeatReply()
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
            getPlatformConnectManager().connect(getDeviceConfig(), new SocketActionAdapter() {
                @Override
                public void onSocketReadResponse(ConnectionInfo info, String action, OriginalData data) {
                    PlatformInteractiveHandler.this.getJt808ExtendProtocolCodec().handler(data);
                }

                @Override
                public void onSocketDisconnection(ConnectionInfo info, String action, Exception e) {
                    super.onSocketDisconnection(info, action, e);
                    //暂时不使用
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

    boolean isRemoteControlPlatformConnected = false;

    public void sendToRemoteControlPlatform(byte[] msg) {
        if (null == msg || msg.length <= 0) {
            Log.e(TAG, "sendToRemoteControlPlatform > process fail : msg is none");
            return;
        }
        Log.d(TAG, "sendToRemoteControlPlatform > " + ByteUtils.bytes2Hex(msg));
        getPlatformConnectManager().send(msg);
    }

    protected SicBasic getSingleInstructionParserByEventCode(RemoteEvent event) {
        SicBasic SingleInstructionParser = null;
        if (null != event)
            SingleInstructionParser = getJt808ExtendProtocolCodec().getResultBodyList().get(event.getCode());
        if (null == SingleInstructionParser) {
            Log.e(TAG, "getSicByEventCode > process fail : event is invalid =" + event.getCode());
        }
        return SingleInstructionParser;
    }

    @Override
    protected void initHandleEventCodeList() {
        registerHandleEvent(EventRemoteControl.Code.CONNECT_RESULT, false);
        registerHandleEvent(EventRemoteControl.Code.AUTHENTICATION_REQUEST, false);
        registerHandleEvent(EventRemoteControl.Code.AUTHENTICATION_RESULT, false);
        registerHandleEvent(EventRemoteControl.Code.SEND_TO_REMOTE_CONTROL_PLATFORM, false);
        registerHandleEvent(EventRemoteControl.Code.PHOTO_UPLOAD_RESULT, false);

        registerHandleEvent(EventRemoteControl.Code.AUDIO_VIDEO_PARAMETERS_APPLY_REQUEST, true);
        registerHandleEvent(EventAudioVideoCommunication.Code.AUDIO_VIDEO_OPERATE_RESULT, true);
        registerHandleEvent(EventAudioVideoCommunication.Code.PHOTO_TAKE_RESULT, true);
    }

    @Override
    public boolean onModuleEvent(RemoteEvent event) {
        switch (event.getCode()) {
            case EventRemoteControl.Code.CONNECT_RESULT:
                isRemoteControlPlatformConnected = EventConnectResult.isResultSuccess(event);
                if (isRemoteControlPlatformConnected) {
                    Log.i(TAG, "onModuleEvent > 连接服务器成功");
                    dispatchEvent(new EventAuthenticationRequest()
                            .setRemote(false));
                    break;
                }
                if (EventResult.ResultCode.DIS == EventConnectResult.getResultCode(event)) {
                    dispatchEvent(new EventLocationReportStopRequest()
                            .setRemote(false));
                    Log.w(TAG, "onModuleEvent > 服务器连接断开");
                    break;
                }
                Log.w(TAG, "onModuleEvent > 连接服务器失败");
                break;

            case EventRemoteControl.Code.AUTHENTICATION_REQUEST:
                //待实现鉴权失败，超时提示
                Log.i(TAG, "onModuleEvent > 开始鉴权 ");
                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        SicBasic singleInstructionParser = PlatformInteractiveHandler.this.getSingleInstructionParserByEventCode(event);
                        if (null == singleInstructionParser) {
                            return;
                        }
                        SicAuthentication authentication = (SicAuthentication) singleInstructionParser;
                        authentication.setConfig(PlatformInteractiveHandler.this.getDeviceConfig());
                        authentication.setBodyConfig(SicBasic.BodyConfig.REQUEST);
                        sendToRemoteControlPlatform(authentication.getBody());
                    }
                }, 500);
                break;

            case EventRemoteControl.Code.AUTHENTICATION_RESULT:
                if (EventAuthenticationResult.isResultSuccess(event)) {
                    dispatchEvent(new EventLocationReportStartRequest()
                            .setRemote(false));
                } else {
                    //Log.w(TAG, "onModuleEvent > 鉴权失败 ");
                }
                break;

            case EventRemoteControl.Code.SEND_TO_REMOTE_CONTROL_PLATFORM:
                sendToRemoteControlPlatform(EventSendToRemoteControlPlatformRequest.getMsg(event));
                break;

            case EventAudioVideoCommunication.Code.PHOTO_TAKE_RESULT:
                Log.i(TAG, "onModuleEvent > 拍照状态上传");
                if (!EventPhotoTakeResult.isResultSuccess(event)) {
                    SicBasic singleInstructionParser = getSingleInstructionParserByEventCode(event);
                    if (null == singleInstructionParser) {
                        break;
                    }
                    byte[] msg = ((SicPhotoTake) singleInstructionParser)
                            .setEventType(EventPhotoTakeResult.getEventType(event))
                            .setResult(SicPhotoTake.ResultCode.LOCAL_DEVICE_SHOOT_FAIL)
                            .getBody(SicBasic.BodyConfig.RESULT);
                    sendToRemoteControlPlatform(msg);
                }
                break;

            case EventRemoteControl.Code.PHOTO_UPLOAD_REQUEST:
                Log.i(TAG, "onModuleEvent > 开始上传照片");
                final String filePath = EventPhotoUploadRequest.getImgFilePath(event);
                File imgFile = new File(filePath);

                boolean isUploadReady = true;
                if (!imgFile.exists()) {
                    isUploadReady = false;
                    Log.e(TAG, "onModuleEvent > 开始上传照片 > process fail : 照片不存在 = " + filePath);
                }
                if (!isRemoteControlPlatformConnected) {
                    Log.w(TAG, "onModuleEvent > 开始上传照片 > process fail : 未联网");
                    play("上传失败，请检查网络链接");
                }
                if (!isUploadReady) {
                    dispatchEvent(new EventPhotoUploadResult()
                            .setResult(false)
                            .setEventType(EventPhotoUploadRequest.getEventType(event))
                            .setRemote(false));
                    break;
                }
                UploadUtil.getInstance()
                        .setOnUploadCallBack(new UploadUtil.OnUploadCallBack() {
                            @Override
                            public UploadUtil.UploadConfig getConfig() {
                                return new UploadUtil.UploadConfig()
                                        .setStrDeviceId(PlatformInteractiveHandler.this.getDeviceConfig().getDevId())
                                        .setStrServerUrl(PlatformInteractiveHandler.this.getDeviceConfig().getRemotePhotoServerAddress())
                                        .setFileImageLocal(imgFile);
                            }

                            @Override
                            public void onUploadFinish(int resultCode) {
                                boolean isUploadSuccess = UploadUtil.ResultCode.UPLOAD_SUCCESS == resultCode;
                                Log.i(TAG, "onModuleEvent > onUploadFinish > " + (isUploadSuccess ? "上传成功" : "上传失败"));
                                dispatchEvent(new EventPhotoUploadResult()
                                        .setResult(isUploadSuccess)
                                        .setEventType(EventPhotoUploadRequest.getEventType(event))
                                        .setRemote(false));
                            }
                        })
                        .uploadImageBySubThread();
                break;

            case EventRemoteControl.Code.PHOTO_UPLOAD_RESULT:
                boolean isUploadSuccess = EventPhotoUploadResult.isResultSuccess(event);
                if (isUploadSuccess) {
                    Log.i(TAG, "onModuleEvent > 照片上传成功");
                } else {
                    Log.w(TAG, "onModuleEvent > 照片上传失败");
                }
                if (!isRemoteControlPlatformConnected) {
                    break;
                }
                SicBasic singleInstructionParser = getSingleInstructionParserByEventCode(event);
                if (null == singleInstructionParser) {
                    break;
                }
                byte[] msg = ((SicPhotoUploadReply) singleInstructionParser)
                        .setEventType(EventPhotoUploadRequest.getEventType(event))
                        .setResult(isUploadSuccess ? SicPhotoUploadReply.ResultCode.SUCCESS :
                                SicPhotoUploadReply.ResultCode.LOCAL_DEVICE_UPLOAD_FAIL)
                        .getBody(SicBasic.BodyConfig.RESULT);
                sendToRemoteControlPlatform(msg);

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
                Log.w(TAG, "onModuleEvent > 返回平台音视频参数下发的请求处理结果");

                SicBasic singleInstructionParserAVOR = getSingleInstructionParserByEventCode(event);
                if (null == singleInstructionParserAVOR) {
                    break;
                }
                byte[] LocalDeviceHandleAVCResultMsg = ((SicAudioVideo) singleInstructionParserAVOR)
                        .setToken(EventAudioVideoOperateResult.getToken(event))
                        .setResult(EventAudioVideoOperateResult.getResult(event))
                        .setFlowNumber(EventAudioVideoOperateResult.getFlowNumber(event))
                        .getBody();
                sendToRemoteControlPlatform(LocalDeviceHandleAVCResultMsg);
                break;
            default:
                return false;
        }
        return true;
    }
}