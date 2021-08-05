package com.kuyou.jt808;

import android.os.Handler;
import android.util.Log;

import org.json.JSONObject;

import kuyou.common.ku09.JT808ExtensionProtocol;
import kuyou.common.ku09.event.base.IDispatchEventCallBack;
import kuyou.common.ku09.event.jt808.EventAudioVideoParametersApplyResult;
import kuyou.common.ku09.event.jt808.EventAuthenticationResult;
import kuyou.common.ku09.event.jt808.EventPhotoUploadResult;
import kuyou.common.ku09.event.openlive.EventAudioRequest;
import kuyou.common.ku09.event.openlive.EventInfearedVideoRequest;
import kuyou.common.ku09.event.openlive.EventMediaRequest;
import kuyou.common.ku09.event.openlive.EventPhotoTakeRequest;
import kuyou.common.ku09.event.openlive.EventVideoRequest;
import kuyou.common.ku09.event.tts.EventTtsPlayRequest;
import kuyou.sdk.jt808.base.Jt808Config;
import kuyou.sdk.jt808.base.jt808bean.JTT808Bean;
import kuyou.sdk.jt808.base.Jt808Codec;

import com.kuyou.jt808.info.AudioVideoInfo;
import kuyou.common.ku09.JT808ExtensionProtocol.IAudioVideo;
import com.kuyou.jt808.info.ImageInfo;
import com.kuyou.jt808.info.MsgInfo;
import com.kuyou.jt808.info.TextInfo;
import kuyou.sdk.jt808.utils.UploadUtil;

/**
 * action :处理808平台消息
 * <p>
 * remarks:  <br/>
 * author: wuguoxian <br/>
 * date: 21-3-29 <br/>
 * </p>
 */
public class OnlinePlatformMessageHandler extends Jt808Codec {

    protected final String TAG = "com.kuyou.jt808 > OnlinePlatformMessageHandler ";

    private Jt808Config mJt808Config;
    protected TextInfo mTextInfo;
    protected AudioVideoInfo mAudioVideoInfo;
    protected ImageInfo mImageInfo;

    public OnlinePlatformMessageHandler(IDispatchEventCallBack callBack, Jt808Config config) {
        super(callBack);
        mJt808Config = config;
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
                break;
            case JT808ExtensionProtocol.SERVER_ANSWER.AUTHENTICATION_REPLY:
                boolean isAuthenticationSuccess = (0 == bean.getReplyResult());
                Log.i(TAG, new StringBuilder(128)
                        .append("\n\n<<<<<<<<<<<<<<<  服务器鉴权:").append(isAuthenticationSuccess ? "成功" : "失败")
                        .append("  <<<<<<<<<<<<<<<")
                        .toString());
                dispatchEvent(new EventAuthenticationResult().setResult(isAuthenticationSuccess));
                break;
            
            case JT808ExtensionProtocol.SERVER_CMD.TEXT_DELIVERY:
                Log.d(TAG, "----------------------文本信息下发-----------------------------");
                mTextInfo.parse(data);
                play(mTextInfo.getText());
                break;
            case JT808ExtensionProtocol.SERVER_CMD.AUDIO_VIDEO_PARAMETERS_DELIVERY://平台下发音视频参数
                Log.d(TAG, "----------------------平台下发音视频参数-----------------------------");
                mAudioVideoInfo.parse(data);
                break;
            case JT808ExtensionProtocol.SERVER_CMD.TAKE_PHOTO_UPLOAD:
                Log.d(TAG, "----------------------请立即拍照-----------------------------");
                if (mImageInfo.parse(data)) {
                    break;
                }
                play("拍照指令解析失败");
            case JT808ExtensionProtocol.SERVER_ANSWER.PHOTO_UPLOAD_FINISH:
                Log.d(TAG, "----------------------平台接收照片应答-----------------------------");
                mImageInfo.parse(data);
                break;
            default:
                break;
        }
    }

    protected void initParsers(Jt808Config config) {
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
            public void requestOpenLive(AudioVideoInfo info) {
                Log.d(TAG, " requestOpenLive > requestLiveHandle ");
                EventMediaRequest event = null;
                switch (info.getMediaType()) {
                    case IAudioVideo.MEDIA_TYPE_VIDEO:
                        Log.d(TAG, "requestOpenLive > 申请视频");
                        event = new EventVideoRequest()
                                .setToken(info.getToken())
                                .setChannelId(String.valueOf(info.getChannelId()));
                        break;
                    case IAudioVideo.MEDIA_TYPE_AUDIO:
                        Log.d(TAG, "requestOpenLive > 申请语音");
                        event = new EventAudioRequest()
                                .setToken(info.getToken())
                                .setChannelId(String.valueOf(info.getChannelId()));
                        break;
                    case IAudioVideo.MEDIA_TYPE_INFEARED:
                        Log.d(TAG, "requestOpenLive > 申请红外");
                        event = new EventInfearedVideoRequest()
                                .setToken(info.getToken())
                                .setChannelId(String.valueOf(info.getChannelId()));
                        break;
                    default:
                        Log.e(TAG, "requestOpenLive > process fail : 无效模式配置 = " + config);
                        return;
                }
                event.setAction(info.isClose() ? EventMediaRequest.Action.CLOSE : EventMediaRequest.Action.OPEN);
                if (!info.isClose() && isLocalInitiatedFlag()) {//添加本地发起标识
                    resetRequestAudioVideoParametersFlag();
                    event.setEventType(IAudioVideo.EVENT_TYPE_LOCAL_INITIATED);
                    Log.w(TAG, "requestOpenLive > 本地");
                } else {
                    event.setEventType(IAudioVideo.EVENT_TYPE_PLATFORM_INITIATED);
                    Log.w(TAG, "requestOpenLive > 平台");
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

    public void uploadImg(String filePath) {
        mImageInfo.upload(filePath, new UploadUtil.OnUploadImageListener() {
            @Override
            public void onUploadFinish(JSONObject jsonResult) {
                int resultCode = -1;
                try {
                    resultCode = jsonResult.getInt("code");
                } catch (Exception e) {
                    Log.e(TAG, Log.getStackTraceString(e));
                }
                dispatchEvent(new EventPhotoUploadResult()
                        .setMsg(mImageInfo.getResultMsgBytes())
                        .setResult(0 == resultCode)
                        .setRemote(true));
                play("拍照成功");
            }
        });
    }

    public AudioVideoInfo getAudioVideoInfo() {
        return mAudioVideoInfo;
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
                    Log.d("kuyou", " 申请音视频参数失败：超时");
                    play("平台端暂时无人接听 请稍后再试");
                    dispatchEvent(new EventAudioVideoParametersApplyResult()
                            .setMediaType(mediaTypeCode)
                            .setResult(false)
                            .setRemote(true));
                    try {
                        mHandlerRequestAudioVideoParametersTimeout.removeCallbacks(mRunnableRequestAudioVideoParametersTimeout);
                        mHandlerRequestAudioVideoParametersTimeout = null;
                    } catch (Exception e) {
                        Log.e(TAG, android.util.Log.getStackTraceString(e));
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

    public void resetRequestAudioVideoParametersFlag() {
        mRequestAudioVideoParametersStatus = IAudioVideo.STATUS_PARAMETER_DEF;
        if (null == mHandlerRequestAudioVideoParametersTimeout) {
            return;
        }
        mHandlerRequestAudioVideoParametersTimeout.removeCallbacks(
                mRunnableRequestAudioVideoParametersTimeout);
    }
}
