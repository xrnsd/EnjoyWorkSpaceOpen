package com.kuyou.rc.platform;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.kuyou.rc.info.AudioVideoInfo;
import com.kuyou.rc.info.AuthenticationInfo;
import com.kuyou.rc.info.ImageInfo;
import com.kuyou.rc.info.MsgInfo;
import com.kuyou.rc.info.TextInfo;
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
import kuyou.sdk.jt808.base.JT808ExtensionProtocol;
import kuyou.sdk.jt808.base.RemoteControlDeviceConfig;
import kuyou.sdk.jt808.base.jt808bean.JTT808Bean;
import kuyou.sdk.jt808.oksocket.client.sdk.client.ConnectionInfo;

/**
 * <p>
 * action :平台交互处理
 * remarks:  <br/>
 * author: wuguoxian <br/>
 * date: 21-3-29 <br/>
 * </p>
 */
public class PlatformInteractiveHandler {

    protected final String TAG = "com.kuyou.rc.handler > PlatformInteractiveHandler ";

    protected boolean isNetworkAvailable = false;

    protected TextInfo mTextInfo;
    protected AudioVideoInfo mAudioVideoInfo;
    protected ImageInfo mImageInfo;

    public static interface IControlHandlerCallback {
        public Context getContext();

        public RemoteControlDeviceConfig getConfig();
    }

    public PlatformInteractiveHandler(IControlHandlerCallback callback) {
        super();
        mControlHandlerCallback = callback;

        initParsers(getConfig());
    }

    private IControlHandlerCallback mControlHandlerCallback;

    private IDispatchEventCallBack mEventCallBack;

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

    protected void initParsers(RemoteControlDeviceConfig config) {
        if (null != mTextInfo) {
            return;
        }

        mTextInfo = new TextInfo(config);
        mTextInfo.setMsgHandler(JT808ExtensionProtocol.SERVER_CMD.TEXT_DELIVERY, new MsgInfo.onMsgHandlerTts() {
            @Override
            public void onHandlerTts(String text) {
                play(text);
            }
        });
        mAudioVideoInfo = new AudioVideoInfo();
        mAudioVideoInfo.setConfig(config);
        mAudioVideoInfo.setRequestOpenLiveListener(new AudioVideoInfo.IRequestOpenLiveListener() {
            @Override
            public void onAudioVideoOperateRequest(AudioVideoInfo info) {
                Log.d(TAG, "requestOpenLive > requestLiveHandle ");
                EventAudioVideoOperateRequest event = null;
                event = new EventAudioVideoOperateRequest()
                        .setFlowId(info.getMsgFlowNumber())
                        .setMediaType(info.getMediaType())
                        .setToken(info.getToken())
                        .setChannelId(String.valueOf(info.getChannelId()))
                        .setEventType(info.getEventType());
                event.setRemote(true);
                dispatchEvent(event);
            }
        });
        mImageInfo = new ImageInfo(config);
        //解析成功开始执行
        mImageInfo.setMsgHandler(JT808ExtensionProtocol.SERVER_CMD.TAKE_PHOTO_UPLOAD, new MsgInfo.onMsgHandlerTts() {
            @Override
            public void onHandlerTts(String text) {
                Log.d(TAG, "onHandlerTts > 申请拍照");
                play(text);
                mImageInfo.setMediaId(System.currentTimeMillis());
                dispatchEvent(new EventPhotoTakeRequest()
                        .setFileName(mImageInfo.getFileName())
                        .setUpload(true)
                        .setRemote(true));
            }
        });
        //执行成功发送回复
        mImageInfo.setMsgHandler(JT808ExtensionProtocol.SERVER_CMD.TAKE_PHOTO_UPLOAD, new MsgInfo.onMsgHandler() {
            @Override
            public void onHandler(int resultCode) {

            }
        });
        mImageInfo.setMsgHandler(JT808ExtensionProtocol.SERVER_ANSWER.PHOTO_UPLOAD_FINISH, new MsgInfo.onMsgHandlerTts() {
            @Override
            public void onHandlerTts(String text) {
                dispatchEvent(new EventTextToSpeechPlayRequest(text));
            }
        });
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

        initParsers(getConfig());

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
                public void onRemote2LocalMessage(JTT808Bean bean, byte[] data) {
                    PlatformInteractiveHandler.this.onRemote2LocalMessage(bean, data);
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

    public TextInfo getTextInfo() {
        return mTextInfo;
    }

    public AudioVideoInfo getAudioVideoInfo() {
        return mAudioVideoInfo;
    }

    public ImageInfo getImageInfo() {
        return mImageInfo;
    }

    public void onRemote2LocalMessage(JTT808Bean bean, byte[] data) {
        switch (bean.getMsgId()) {
            case JT808ExtensionProtocol.SERVER_ANSWER.AUTHENTICATION_REPLY:
                boolean isAuthenticationSuccess = (0 == bean.getReplyResult());
                Log.i(TAG, new StringBuilder(128)
                        .append("\n<<<<<<<<<<  服务器鉴权:").append(isAuthenticationSuccess ? "成功" : "失败")
                        .append("  <<<<<<<<<<")
                        .toString());
                dispatchEvent(new EventAuthenticationResult().setResult(isAuthenticationSuccess));
                break;

            case JT808ExtensionProtocol.SERVER_ANSWER.CONNECT_REPLY:
                Log.i(TAG, new StringBuilder(256)
                        .append("<<<<<<<<<<  流水号：").append(bean.getReplyFlowNumber())
                        .append(",服务器回复:").append(0 == bean.getReplyResult() ? "成功" : "失败")
                        .append("  <<<<<<<<<<\n")
                        .toString());
                if (0 != bean.getReplyFlowNumber()) {
                    break;
                }

            case JT808ExtensionProtocol.SERVER_ANSWER.PHOTO_UPLOAD_FINISH:
                Log.d(TAG, "----------平台应答：接收照片----------");
                mImageInfo.parse(data);
                break;

            case JT808ExtensionProtocol.SERVER_CMD.TEXT_DELIVERY:
                Log.d(TAG, "----------平台指令：文本信息----------");
                mTextInfo.parse(data);
                play(mTextInfo.getText());
                break;
            case JT808ExtensionProtocol.SERVER_CMD.AUDIO_VIDEO_PARAMETERS_DELIVERY://平台下发音视频参数
                Log.d(TAG, "----------平台指令：音视频----------");
                mAudioVideoInfo.parse(data);
                break;
            case JT808ExtensionProtocol.SERVER_CMD.TAKE_PHOTO_UPLOAD:
                Log.d(TAG, "----------平台指令：拍照----------");
                mImageInfo.parse(data);
            default:
                break;
        }
    }

    // =====================  本地事件处理 =============================

    boolean isRemoteControlPlatformConnected = false;

    public void sendToRemoteControlPlatform(byte[] msg) {
        Log.d(TAG, "sendToRemoteControlPlatform > " + ByteUtils.bytes2Hex(msg));
        getPlatformConnectManager().send(msg);
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
                        AuthenticationInfo.getInstance().setConfig(getConfig());
                        sendToRemoteControlPlatform(AuthenticationInfo.getInstance().getAuthenticationMsgBytes());
                    }
                }, 2000);
                break;

            case EventRemoteControl.Code.AUTHENTICATION_RESULT:
                if (EventAuthenticationResult.isResultSuccess(event)) {
                    Log.d(TAG, "onModuleEvent > 鉴权成功 ");
                    dispatchEvent(new EventLocationReportStartRequest()
                            .setRemote(false));
                    return true;
                }
                Log.w(TAG, "onModuleEvent > 鉴权失败 ");
                break;

            case EventRemoteControl.Code.SEND_TO_REMOTE_CONTROL_PLATFORM:
                //Log.d(TAG, "onModuleEvent > 发送指令给平台");
                sendToRemoteControlPlatform(EventSendToRemoteControlPlatformRequest.getMsg(event));
                break;

            case EventAudioVideoCommunication.Code.PHOTO_TAKE_RESULT:
                Log.d(TAG, "onModuleEvent > 拍照状态上传");
                if (!EventPhotoTakeResult.isResultSuccess(event)) {
                    byte[] msg = getImageInfo()
                            .setEventType(EventPhotoTakeResult.getEventType(event))
                            .setResult(ImageInfo.ResultCode.LOCAL_DEVICE_SHOOT_FAIL)
                            .getResultMsgBytes();
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
                byte[] PhotoUploadResultMsg = getImageInfo()
                        .setEventType(EventPhotoUploadRequest.getEventType(event))
                        .setResult(isUploadSuccess ? ImageInfo.ResultCode.SUCCESS : ImageInfo.ResultCode.LOCAL_DEVICE_UPLOAD_FAIL)
                        .getResultMsgBytes();
                sendToRemoteControlPlatform(PhotoUploadResultMsg);
                break;

            case EventRemoteControl.Code.AUDIO_AND_VIDEO_PARAMETERS_APPLY_REQUEST:
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
                byte[] PlatformDirectiveAVCMsg = getAudioVideoInfo()
                        .setEventType(eventType)
                        .getApplyAudioVideoParametersMsgByMediaTypeCode(platformType, mediaTypeCode, isClose);
                sendToRemoteControlPlatform(PlatformDirectiveAVCMsg);
                break;

            case EventAudioVideoCommunication.Code.AUDIO_VIDEO_OPERATE_RESULT:
                Log.w(TAG, "onModuleEvent > 返回平台音视频参数下发的请求处理结果");
                byte[] LocalDeviceHandleAVCResultMsg = getAudioVideoInfo().getResultMsgBytes(
                        EventAudioVideoOperateResult.getToken(event),
                        EventAudioVideoOperateResult.getFlowId(event),
                        EventAudioVideoOperateResult.getResult(event));
                sendToRemoteControlPlatform(LocalDeviceHandleAVCResultMsg);
                break;
            default:
                break;
        }
        return false;
    }
}
