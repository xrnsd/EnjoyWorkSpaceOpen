package com.kuyou.rc.handler;

import android.os.Handler;
import android.util.Log;

import com.kuyou.rc.business.Jt808Codec;
import com.kuyou.rc.info.AudioVideoInfo;
import com.kuyou.rc.info.ImageInfo;
import com.kuyou.rc.info.MsgInfo;
import com.kuyou.rc.info.TextInfo;

import kuyou.common.ku09.event.IDispatchEventCallBack;
import kuyou.common.ku09.event.avc.EventAudioVideoOperateRequest;
import kuyou.common.ku09.event.avc.EventPhotoTakeRequest;
import kuyou.common.ku09.event.avc.base.IAudioVideo;
import kuyou.common.ku09.event.rc.EventAudioVideoParametersApplyResult;
import kuyou.common.ku09.event.rc.EventAuthenticationResult;
import kuyou.common.ku09.event.tts.EventTtsPlayRequest;
import kuyou.sdk.jt808.base.JT808ExtensionProtocol;
import kuyou.sdk.jt808.base.RemoteControlDeviceConfig;
import kuyou.sdk.jt808.base.jt808bean.JTT808Bean;

/**
 * <p>
 * action :处理平台消息[JT808协议]
 * remarks:  <br/>
 * author: wuguoxian <br/>
 * date: 21-3-29 <br/>
 * </p>
 */
public class PlatformInteractiveCommandHandler extends Jt808Codec {

    protected final String TAG = "com.kuyou.rc > ControlPlatformMessageHandler ";

    private RemoteControlDeviceConfig mRemoteControlDeviceConfig;
    protected TextInfo mTextInfo;
    protected AudioVideoInfo mAudioVideoInfo;
    protected ImageInfo mImageInfo;

    public PlatformInteractiveCommandHandler(IDispatchEventCallBack callBack, RemoteControlDeviceConfig config) {
        super(callBack);
        mRemoteControlDeviceConfig = config;
        initParsers(config);
    }

    @Override
    public void onMessage(JTT808Bean bean, byte[] data) {
        switch (bean.getMsgId()) {
            case JT808ExtensionProtocol.SERVER_ANSWER.CONNECT_REPLY:
                Log.i(TAG, new StringBuilder(256)
                        .append("<<<<<<<<<<<<<<<  流水号：").append(bean.getReplyFlowNumber())
                        .append(",服务器回复:").append(0 == bean.getReplyResult() ? "成功" : "失败")
                        .append("  <<<<<<<<<<<<<<<\n\n")
                        .toString());
                if (0 != bean.getReplyFlowNumber()) {
                    break;
                }
            case JT808ExtensionProtocol.SERVER_ANSWER.AUTHENTICATION_REPLY:
                Log.d(TAG, "----------------------平台应答：鉴权-----------------------------");
                boolean isAuthenticationSuccess = (0 == bean.getReplyResult());
                Log.i(TAG, new StringBuilder(128)
                        .append("\n\n<<<<<<<<<<<<<<<  服务器鉴权:").append(isAuthenticationSuccess ? "成功" : "失败")
                        .append("  <<<<<<<<<<<<<<<")
                        .toString());
                dispatchEvent(new EventAuthenticationResult().setResult(isAuthenticationSuccess));
                break;
            case JT808ExtensionProtocol.SERVER_ANSWER.PHOTO_UPLOAD_FINISH:
                Log.d(TAG, "----------------------平台应答：接收照片-----------------------------");
                mImageInfo.parse(data);
                break;


            case JT808ExtensionProtocol.SERVER_CMD.TEXT_DELIVERY:
                Log.d(TAG, "----------------------平台指令：文本信息----------------------------");
                mTextInfo.parse(data);
                play(mTextInfo.getText());
                break;
            case JT808ExtensionProtocol.SERVER_CMD.AUDIO_VIDEO_PARAMETERS_DELIVERY://平台下发音视频参数
                Log.d(TAG, "----------------------平台指令：音视频-----------------------------");
                mAudioVideoInfo.parse(data);
                break;
            case JT808ExtensionProtocol.SERVER_CMD.TAKE_PHOTO_UPLOAD:
                Log.d(TAG, "----------------------平台指令：拍照-----------------------------");
                if (mImageInfo.parse(data)) {
                    break;
                }
                play("拍照指令解析失败");
            default:
                break;
        }
    }

    protected void initParsers(RemoteControlDeviceConfig config) {
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
                if (!info.isClose()
                        && isLocalInitiatedFlag()
                        && IAudioVideo.EVENT_TYPE_LOCAL_DEVICE_INITIATE == info.getEventType()) {
                    clearRequestAudioVideoParametersFlag();
                }
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
                play("拍照成功上传");
                //mHelmetSocketManager.send(mImageInfo.getResultMsgBytes());
            }
        });
        mImageInfo.setMsgHandler(JT808ExtensionProtocol.SERVER_ANSWER.PHOTO_UPLOAD_FINISH, new MsgInfo.onMsgHandlerTts() {
            @Override
            public void onHandlerTts(String text) {
                dispatchEvent(new EventTtsPlayRequest(text));
            }
        });
    }

    private void play(String text) {
        Log.d(TAG, "play > text = " + text);
        dispatchEvent(new EventTtsPlayRequest(text));
    }

    public AudioVideoInfo getAudioVideoInfo() {
        return mAudioVideoInfo;
    }

    public ImageInfo getImageInfo() {
        return mImageInfo;
    }

    //协议里面的终端发起的值不正常，暂时使用本地标识位代替
    private int mRequestAudioVideoParametersStatus = -1;
    private Handler mHandlerRequestAudioVideoParametersTimeout = null;
    private Runnable mRunnableRequestAudioVideoParametersTimeout = null;

    public void addRequestAudioVideoParametersFlag(final int mediaTypeCode, Handler handler, long delay) {
        mRequestAudioVideoParametersStatus = IAudioVideo.STATUS_PARAMETER_APPLYING;
        if (null == mRunnableRequestAudioVideoParametersTimeout) {
            mRunnableRequestAudioVideoParametersTimeout = new Runnable() {
                @Override
                public void run() {
                    mRequestAudioVideoParametersStatus = IAudioVideo.STATUS_PARAMETER_DEF;
                    Log.d(PlatformInteractiveCommandHandler.this.TAG, " 申请音视频参数失败：超时");
                    play("平台端暂时无人接听 请稍后再试");
                    dispatchEvent(new EventAudioVideoParametersApplyResult()
                            .setResult(false)
                            .setMediaType(mediaTypeCode)
                            .setRemote(true));
                    try {
                        mHandlerRequestAudioVideoParametersTimeout.removeCallbacks(mRunnableRequestAudioVideoParametersTimeout);
                        mHandlerRequestAudioVideoParametersTimeout = null;
                    } catch (Exception e) {
                        Log.e(TAG, Log.getStackTraceString(e));
                    }
                }
            };
        }
        if (!handler.hasCallbacks(mRunnableRequestAudioVideoParametersTimeout))
            handler.postDelayed(mRunnableRequestAudioVideoParametersTimeout, delay);
        mHandlerRequestAudioVideoParametersTimeout = handler;
    }

    public int getRequestAudioVideoParametersStatus() {
        return mRequestAudioVideoParametersStatus;
    }

    private boolean isLocalInitiatedFlag() {
        return IAudioVideo.STATUS_PARAMETER_APPLYING == getRequestAudioVideoParametersStatus();
    }

    public void clearRequestAudioVideoParametersFlag() {
        mRequestAudioVideoParametersStatus = IAudioVideo.STATUS_PARAMETER_DEF;
        if (null == mHandlerRequestAudioVideoParametersTimeout) {
            return;
        }
        mHandlerRequestAudioVideoParametersTimeout.removeCallbacks(
                mRunnableRequestAudioVideoParametersTimeout);
    }
}
