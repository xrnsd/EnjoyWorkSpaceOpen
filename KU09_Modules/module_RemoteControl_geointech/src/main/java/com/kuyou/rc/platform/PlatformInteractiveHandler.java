package com.kuyou.rc.platform;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.kuyou.rc.protocol.ExtendInteractiveCodec;
import com.kuyou.rc.protocol.InstructionParserListener;
import com.kuyou.rc.protocol.base.JT808ExtensionProtocol;
import com.kuyou.rc.protocol.base.SicBasic;
import com.kuyou.rc.protocol.item.SicAudioVideo;
import com.kuyou.rc.protocol.item.SicAuthentication;
import com.kuyou.rc.protocol.item.SicPhotoTake;
import com.kuyou.rc.protocol.item.SicPhotoUploadReply;
import com.kuyou.rc.protocol.item.SicTextMessage;
import com.kuyou.rc.utils.UploadUtil;

import java.io.File;

import kuyou.common.bytes.ByteUtils;
import kuyou.common.ipc.RemoteEvent;
import kuyou.common.ku09.event.IDispatchEventCallBack;
import kuyou.common.ku09.event.avc.EventAudioVideoOperateRequest;
import kuyou.common.ku09.event.avc.EventAudioVideoOperateResult;
import kuyou.common.ku09.event.avc.EventPhotoTakeRequest;
import kuyou.common.ku09.event.avc.EventPhotoTakeResult;
import kuyou.common.ku09.event.avc.base.EventAudioVideoCommunication;
import kuyou.common.ku09.event.avc.base.IAudioVideo;
import kuyou.common.ku09.event.common.EventNetworkConnect;
import kuyou.common.ku09.event.common.EventNetworkDisconnect;
import kuyou.common.ku09.event.rc.EventAudioVideoParametersApplyRequest;
import kuyou.common.ku09.event.rc.EventAudioVideoParametersApplyResult;
import kuyou.common.ku09.event.rc.EventAuthenticationRequest;
import kuyou.common.ku09.event.rc.EventAuthenticationResult;
import kuyou.common.ku09.event.rc.EventConnectResult;
import kuyou.common.ku09.event.rc.EventLocationReportStartRequest;
import kuyou.common.ku09.event.rc.EventLocationReportStopRequest;
import kuyou.common.ku09.event.rc.EventPhotoUploadRequest;
import kuyou.common.ku09.event.rc.EventPhotoUploadResult;
import kuyou.common.ku09.event.rc.EventSendToRemoteControlPlatformRequest;
import kuyou.common.ku09.event.rc.base.EventRemoteControl;
import kuyou.common.ku09.event.rc.base.EventResult;
import kuyou.common.ku09.event.tts.EventTextToSpeechPlayRequest;
import kuyou.common.utils.NetworkUtils;
import kuyou.sdk.jt808.base.RemoteControlDeviceConfig;
import kuyou.sdk.jt808.base.jt808bean.JTT808Bean;
import kuyou.sdk.jt808.oksocket.client.sdk.client.ConnectionInfo;
import kuyou.sdk.jt808.oksocket.core.pojo.OriginalData;

/**
 * <p>
 * action :[协处理器]远程控制平台交互
 * remarks:  <br/>
 * author: wuguoxian <br/>
 * date: 21-3-29 <br/>
 * </p>
 */
public class PlatformInteractiveHandler {

    protected final String TAG = "com.kuyou.rc.handler > PlatformInteractiveHandler ";

    protected boolean isNetworkAvailable = false;

    protected ExtendInteractiveCodec mExtendInteractiveCodec;

    private IControlHandlerCallback mControlHandlerCallback;

    private IDispatchEventCallBack mEventCallBack;

    public static interface IControlHandlerCallback {
        public Context getContext();

        public RemoteControlDeviceConfig getConfig();
    }

    public PlatformInteractiveHandler(IControlHandlerCallback callback) {
        super();
        mControlHandlerCallback = callback;
    }

    public PlatformInteractiveHandler setEventCallBack(IDispatchEventCallBack callBack) {
        mEventCallBack = callBack;
        return PlatformInteractiveHandler.this;
    }

    protected void dispatchEvent(RemoteEvent event) {
        if (null == mEventCallBack) {
            Log.e(TAG, "dispatchEvent > process fail : mEventCallBack is null");
            return;
        }
        mEventCallBack.dispatchEvent(event);
    }

    public IControlHandlerCallback getControlHandlerCallback() {
        return mControlHandlerCallback;
    }

    public ExtendInteractiveCodec getExtendInteractiveCodec() {
        if (null == mExtendInteractiveCodec) {
            mExtendInteractiveCodec = ExtendInteractiveCodec.getInstance(getContext());
            mExtendInteractiveCodec.setInstructionParserListener(new InstructionParserListener() {
                @Override
                public void onRemote2LocalBasic(JTT808Bean bean, byte[] data) {
                    switch (bean.getMsgId()) {
                        case JT808ExtensionProtocol.S2C_RESULT_CONNECT_REPLY:
                            if (0 != bean.getReplyFlowNumber()) {
                                Log.i(TAG, new StringBuilder(256)
                                        .append("<<<<<<<<<<  流水号：").append(bean.getReplyFlowNumber())
                                        .append(",服务器回复:").append(0 == bean.getReplyResult() ? "成功" : "失败")
                                        .append("  <<<<<<<<<<\n\n")
                                        .toString());
                                break;
                            }
                        case JT808ExtensionProtocol.S2C_RESULT_AUTHENTICATION_REPLY:
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
                            .setFlowId(instruction.getFlowId())
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
            }).load(PlatformInteractiveHandler.this.getConfig());
        }
        return mExtendInteractiveCodec;
    }

    private void play(String text) {
        Log.d(TAG, "play > text = " + text);
        dispatchEvent(new EventTextToSpeechPlayRequest(text));
    }

    public String isReady() {
        //联网检测
        boolean isNetworkAvailableNow = NetworkUtils.isNetworkAvailable(getControlHandlerCallback().getContext());
        if (!isNetworkAvailableNow) {
            Log.w(TAG, "isReady > 未联网,放弃平台链接状态检查和模块自动重置 ");
            return null;
        }

        if (!isNetworkAvailable && isNetworkAvailableNow) {
            dispatchEvent(new EventNetworkConnect());
        } else if (isNetworkAvailable && !isNetworkAvailableNow) {
            dispatchEvent(new EventNetworkDisconnect());
        }
        isNetworkAvailable = isNetworkAvailableNow;

        //联网后以socketManager连接状态为准
        if (!getPlatformConnectManager().isConnect()) {
            Log.w(TAG, "isReady > 未连接平台,尝试链接平台 ");
            connect();
            //return "平台连接异常";
            return null;
        }

        return null;
    }

    private PlatformConnectManager getPlatformConnectManager() {
        return PlatformConnectManager.getInstance(getConfig());
    }

    public void connect() {
        if (getPlatformConnectManager().isConnect()) {
            Log.e(TAG, "connect > process fail : HelmetSocketManager is connected");
            return;
        }
        try {
            getPlatformConnectManager().connect(getConfig(), new RemoteCommandCallback() {
                @Override
                public void onRemote2LocalMessage(OriginalData data) {
                    PlatformInteractiveHandler.this.getExtendInteractiveCodec().handler(data);
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
        }
    }

    protected Context getContext() {
        return getControlHandlerCallback().getContext().getApplicationContext();
    }

    protected RemoteControlDeviceConfig getConfig() {
        return getControlHandlerCallback().getConfig();
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
            SingleInstructionParser = getExtendInteractiveCodec().getResultBodyList().get(event.getCode());
        if (null == SingleInstructionParser) {
            Log.e(TAG, "getSicByEventCode > process fail : event is invalid =" + event.getCode());
        }
        return SingleInstructionParser;
    }

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
                if (EventConnectResult.getResultCode(event) == EventResult.ResultCode.DIS) {
                    dispatchEvent(new EventLocationReportStopRequest()
                            .setRemote(false));
                    Log.w(TAG, "onModuleEvent > 服务器连接断开");
                    break;
                }
                Log.w(TAG, "onModuleEvent > 连接服务器失败");
                break;

            case EventRemoteControl.Code.AUTHENTICATION_REQUEST:
                Log.d(TAG, "onModuleEvent > 开始鉴权 ");
                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        SicBasic singleInstructionParser = PlatformInteractiveHandler.this.getSingleInstructionParserByEventCode(event);
                        if (null == singleInstructionParser) {
                            return;
                        }
                        SicAuthentication authentication = (SicAuthentication) singleInstructionParser;
                        authentication.setConfig(PlatformInteractiveHandler.this.getConfig());
                        authentication.setBodyConfig(SicBasic.BodyConfig.REQUEST);
                        sendToRemoteControlPlatform(authentication.getBody());
                    }
                }, 500);
                break;

            case EventRemoteControl.Code.AUTHENTICATION_RESULT:
                if (EventAuthenticationResult.isResultSuccess(event)) {
                    //Log.d(TAG, "onModuleEvent > 鉴权成功 ");
                    dispatchEvent(new EventLocationReportStartRequest()
                            .setRemote(false));
                    return true;
                }
                //Log.w(TAG, "onModuleEvent > 鉴权失败 ");
                break;

            case EventRemoteControl.Code.SEND_TO_REMOTE_CONTROL_PLATFORM:
                //Log.d(TAG, "onModuleEvent > 发送指令给平台");
                sendToRemoteControlPlatform(EventSendToRemoteControlPlatformRequest.getMsg(event));
                break;

            case EventAudioVideoCommunication.Code.PHOTO_TAKE_RESULT:
                Log.d(TAG, "onModuleEvent > 拍照状态上传");
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
                Log.d(TAG, "onModuleEvent > 开始上传照片");
                final String filePath = EventPhotoUploadRequest.getImgFilePath(event);
                File imgFile = new File(filePath);
                if (!imgFile.exists()) {
                    Log.e(TAG, "onModuleEvent > 开始上传照片 > process fail : img is`not exists = " + filePath);
                    dispatchEvent(new EventPhotoUploadResult()
                            .setResult(false)
                            .setEventType(EventPhotoUploadRequest.getEventType(event))
                            .setRemote(false));
                    return true;
                }
                UploadUtil.getInstance()
                        .setOnUploadCallBack(new UploadUtil.OnUploadCallBack() {
                            @Override
                            public UploadUtil.UploadConfig getConfig() {
                                return new UploadUtil.UploadConfig()
                                        .setStrDeviceId(PlatformInteractiveHandler.this.getConfig().getDevId())
                                        .setStrServerUrl(PlatformInteractiveHandler.this.getConfig().getRemotePhotoServerAddress())
                                        .setFileImageLocal(imgFile);
                            }

                            @Override
                            public void onUploadFinish(int resultCode) {
                                boolean isUploadSuccess = UploadUtil.ResultCode.UPLOAD_SUCCESS == resultCode;
                                Log.d(TAG, "onModuleEvent > onUploadFinish > " + (isUploadSuccess ? "上传成功" : "上传失败"));
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
                    Log.d(TAG, "onModuleEvent > 照片上传成功");
                } else {
                    Log.w(TAG, "onModuleEvent > 照片上传失败");
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
                boolean isClose = IAudioVideo.EVENT_TYPE_CLOSE == eventType;

                //处理：在未链接平台情况下申请打开参数
                if (!isRemoteControlPlatformConnected) {
                    Log.w(TAG, "onModuleEvent > 申请视频参数和操作 > 未链接平台");
                    dispatchEvent(new EventAudioVideoParametersApplyResult()
                            .setResult(false)
                            .setRemote(true));
                    play("打开失败，请检查网络链接");
                    return true;
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
                        .setFlowId(EventAudioVideoOperateResult.getFlowId(event))
                        .getBody();
                sendToRemoteControlPlatform(LocalDeviceHandleAVCResultMsg);
                break;
            default:
                break;
        }
        return false;
    }
}