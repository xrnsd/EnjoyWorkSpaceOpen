package com.kuyou.avc.handler;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;

import com.kuyou.avc.BuildConfig;
import com.kuyou.avc.R;
import com.kuyou.avc.handler.base.AudioVideoRequestResultHandler;
import com.kuyou.avc.ui.MultiCaptureAudio;
import com.kuyou.avc.ui.MultiCaptureGroup;
import com.kuyou.avc.ui.MultiCaptureInfeared;
import com.kuyou.avc.ui.MultiCaptureVideo;
import com.kuyou.avc.ui.MultiRenderGroup;
import com.kuyou.avc.ui.TakePhoto;
import com.kuyou.avc.ui.base.BaseAVCActivity;
import com.kuyou.avc.util.CameraLightControl;

import java.util.Iterator;
import java.util.Set;

import kuyou.common.ipc.RemoteEvent;
import kuyou.common.ku09.config.DevicesConfig;
import kuyou.common.ku09.event.avc.EventAudioVideoOperateRequest;
import kuyou.common.ku09.event.avc.EventAudioVideoOperateResult;
import kuyou.common.ku09.event.avc.EventPhotoTakeRequest;
import kuyou.common.ku09.event.avc.EventPhotoTakeResult;
import kuyou.common.ku09.event.avc.base.EventAudioVideoCommunication;
import kuyou.common.ku09.event.avc.base.IAudioVideo;
import kuyou.common.ku09.event.common.base.EventCommon;
import kuyou.common.ku09.event.rc.EventAudioVideoParametersApplyRequest;
import kuyou.common.ku09.event.rc.EventAudioVideoParametersApplyResult;
import kuyou.common.ku09.event.rc.base.EventRemoteControl;

import static kuyou.common.ku09.event.avc.base.IAudioVideo.MEDIA_TYPE_AUDIO;
import static kuyou.common.ku09.event.avc.base.IAudioVideo.MEDIA_TYPE_GROUP;
import static kuyou.common.ku09.event.avc.base.IAudioVideo.MEDIA_TYPE_INFEARED;
import static kuyou.common.ku09.event.avc.base.IAudioVideo.MEDIA_TYPE_VIDEO;

/**
 * action :
 * <p>
 * remarks:  <br/>
 * author: wuguoxian <br/>
 * date: 21-7-23 <br/>
 * </p>
 */
public class PeergineAudioVideoHandler extends AudioVideoRequestResultHandler {
    protected final String TAG = "com.kuyou.avc.handle > " + this.getClass().getSimpleName();

    private static PeergineAudioVideoHandler sMain;
    //用于群组通话，区分设备是否为采集端,相同才是，不同不是
    private String mCollectingEndIdLocal = null, mCollectingEndIdRemote = null;
    private Runnable mRunnableRequestAudioVideoParametersTimeout = null;
    private RingtoneHandler mRingtoneHandler;

    private int mAudioVideoType = IAudioVideo.MEDIA_TYPE_DEFAULT;

    private PeergineAudioVideoHandler() {
    }

    public static PeergineAudioVideoHandler getInstance(Context context) {
        if (null == sMain) {
            sMain = new PeergineAudioVideoHandler();
            sMain.setContext(context.getApplicationContext());
            sMain.mRingtoneHandler = RingtoneHandler.getInstance(context.getApplicationContext());
        }
        return sMain;
    }

    @Override
    public void setDevicesConfig(DevicesConfig devicesConfig) {
        super.setDevicesConfig(devicesConfig);
        Log.d(TAG, "setDevicesConfig > 获取设备配置 > ");
        mCollectingEndIdLocal = devicesConfig.getCollectingEndId();
        Log.d(TAG, "setDevicesConfig > 本地采集端ID = " + mCollectingEndIdLocal);
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        super.onActivityCreated(activity, savedInstanceState);
        if (!(activity instanceof BaseAVCActivity)) {
            Log.d(TAG, "onActivityCreated > get up activity =" + activity.getLocalClassName());
            return;
        }
        BaseAVCActivity item = (BaseAVCActivity) activity;
        int typeCode = item.getTypeCode();
        if (-1 != typeCode) {
            Log.d(TAG, "onActivityCreated > put activity =" + activity.getLocalClassName());
            mItemListOnline.put(typeCode, item);
            setHandlerStatus(HS_OPEN);
        }
    }

    @Override
    public void onActivityDestroyed(@NonNull Activity activity) {
        super.onActivityDestroyed(activity);
        if (!(activity instanceof BaseAVCActivity)) {
            Log.d(TAG, "onActivityCreated > get up activity =" + activity.getLocalClassName());
            return;
        }
        int typeCode = ((BaseAVCActivity) activity).getTypeCode();
        if (-1 != typeCode) {
            setHandlerStatus(HS_CLOSE_BE_EXECUTING);
            switch (typeCode) {
                case MEDIA_TYPE_GROUP:
                    mCollectingEndIdRemote = null;
                    break;
                default:
                    break;
            }
        }
    }

    protected void onResult(RemoteEvent event, int result) {
        dispatchEvent(new EventAudioVideoOperateResult()
                .setFlowId(EventAudioVideoOperateRequest.getFlowId(event))
                .setToken(EventAudioVideoOperateRequest.getToken(event))
                .setResultCode(result));
        if (IAudioVideo.RESULT_FAIL_FAILURE_AUDIO_VIDEO_PARAMETER_PARSE_FAIL == result
                && BuildConfig.DEBUG) {
            play(getContext().getString(R.string.media_request_open_handle_parameter_fail));
        }
    }

    @Override
    public String getTitleByMediaType(final int mediaType, int combinationStrResId) {
        String title = null;
        switch (mediaType) {
            case MEDIA_TYPE_AUDIO:
                title = "语音";
                break;
            case MEDIA_TYPE_VIDEO:
                title = "视频";
                break;
            case MEDIA_TYPE_INFEARED:
                title = "红外";
                break;
            case MEDIA_TYPE_GROUP:
                title = "群组";
                break;
            default:
                return null;
        }
        return 0 > combinationStrResId ? title : getContext().getString(combinationStrResId, title);
    }

    @Override
    public void exitAllLiveActivity() {
        Log.d(TAG, " exitAllLiveActivity -------------------------------------- ");
        setHandlerStatus(HS_CLOSE_BE_EXECUTING);
        synchronized (mItemListOnline) {
            Set<Integer> set = mItemListOnline.keySet();
            Iterator<Integer> it = set.iterator();
            BaseAVCActivity activity;
            while (it.hasNext()) {
                final int typeCode = it.next();
                activity = mItemListOnline.get(typeCode);
                if (null == activity) {
                    continue;
                }
                if (!activity.isDestroyed()) {
                    activity.finish();
                }
                Log.d(TAG, "exitAllLiveActivity > activity =" + activity);
            }
            mItemListOnline.clear();
        }
    }

    @Override
    public boolean isLiveByTypeCode(final int typeCode) {
        if (-1 == typeCode) {
            return mItemListOnline.size() > 0;
        }
        return mItemListOnline.containsKey(typeCode);
    }

    /**
     * 添加超时计时
     */
    public void addRequestAudioVideoParametersFlag() {
        setHandlerStatus(HS_OPEN_REQUEST_BE_EXECUTING);
        if (null == mRunnableRequestAudioVideoParametersTimeout) {
            mRunnableRequestAudioVideoParametersTimeout = new Runnable() {
                @Override
                public void run() {
                    Log.d(TAG, "申请音视频参数失败：未响应");
                    clearRequestAudioVideoParametersFlag();
                    setHandlerStatus(HS_NORMAL);
                    onModuleEvent(new EventAudioVideoParametersApplyResult()
                            .setResult(false)
                            .setEventType(IAudioVideo.EVENT_TYPE_REMOTE_PLATFORM_NO_RESPONSE));
                }
            };
        } else {
            clearRequestAudioVideoParametersFlag();
        }
        getHandlerKeepAliveClient().postDelayed(mRunnableRequestAudioVideoParametersTimeout, 30000);
    }

    /**
     * 清除超时计时
     */
    public void clearRequestAudioVideoParametersFlag() {
        Log.d(TAG, "clearRequestAudioVideoParametersFlag > ");
        try {
            getHandlerKeepAliveClient().removeCallbacks(mRunnableRequestAudioVideoParametersTimeout);
        } catch (Exception e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }
    }

    @Override
    public void performOperate(int typeCode) {
        Log.d(TAG, new StringBuilder("performOperate > ")
                .append(" \n typeCode = ").append(typeCode)
                .append(" \n getHandlerStatus = ").append(getHandlerStatus())
                .append(" \n isOnLine = ").append(isLiveByTypeCode(-1))
                .append(" \n mCollectingEndIdLocal = ").append(mCollectingEndIdLocal)
                .append(" , mCollectingEndIdRemote = ").append(mCollectingEndIdRemote)
                .toString());

        mAudioVideoType = typeCode;

        //终端取消,申请成功正在打开的通话
        if (isItInHandlerState(HS_OPEN_HANDLE_BE_EXECUTING)) {
            clearRequestAudioVideoParametersFlag();
            setHandlerStatus(HS_NORMAL);
            return;
        }

        //终端取消,正在申请的通话
        if (isItInHandlerState(HS_OPEN_REQUEST_BE_EXECUTING)) {
            clearRequestAudioVideoParametersFlag();
            setHandlerStatus(HS_NORMAL);
            play(getTitleByMediaType(mAudioVideoType, R.string.media_request_cancel_request));
            return;
        }

        //终端关闭，已经打开的通话
        if (isLiveByTypeCode(-1)) {
            play(getTitleByMediaType(typeCode, R.string.media_request_close_request));
            CameraLightControl.getInstance(getContext()).switchLaserLight(false);
            exitAllLiveActivity();
            setHandlerStatus(HS_NORMAL);
            if (MEDIA_TYPE_GROUP == typeCode //群聊
                    && null != mCollectingEndIdRemote //为群主时
                    && null != mCollectingEndIdLocal
                    && mCollectingEndIdLocal.equals(mCollectingEndIdRemote)) {
                dispatchEvent(new EventAudioVideoParametersApplyRequest()
                        .setMediaType(typeCode)
                        .setPlatformType(IAudioVideo.PLATFORM_TYPE_PEERGIN)
                        .setEventType(IAudioVideo.EVENT_TYPE_CLOSE)
                        .setRemote(true));
            }
        }

        //终端申请，开启通话
        addRequestAudioVideoParametersFlag();
        play(getTitleByMediaType(typeCode, R.string.media_request_open_request));
        dispatchEvent(new EventAudioVideoParametersApplyRequest()
                .setMediaType(typeCode)
                .setPlatformType(IAudioVideo.PLATFORM_TYPE_PEERGIN)
                .setEventType(IAudioVideo.EVENT_TYPE_LOCAL_DEVICE_INITIATE)
                .setRemote(true));
    }

    @Override
    public String openItem(Context context, RemoteEvent event) {
        int mediaType = EventAudioVideoOperateRequest.getMediaType(event);
        Intent item = new Intent();
        Log.d(TAG, "openItem >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> ");
        switch (mediaType) {
            case MEDIA_TYPE_AUDIO:
                Log.d(TAG, "openItem > MEDIA_TYPE_AUDIO");
                item.setClass(context, MultiCaptureAudio.class);
                break;
            case MEDIA_TYPE_VIDEO:
                Log.d(TAG, "openItem > MEDIA_TYPE_VIDEO");
                item.setClass(context, MultiCaptureVideo.class);
                break;
            case MEDIA_TYPE_INFEARED:
                Log.d(TAG, "openItem > MEDIA_TYPE_INFEARED");
                item.setClass(context, MultiCaptureInfeared.class);
                break;
            case MEDIA_TYPE_GROUP:
                Log.d(TAG, "openItem > MEDIA_TYPE_GROUP");
                mCollectingEndIdRemote = EventAudioVideoOperateRequest.getToken(event);
                item.setClass(context, MultiCaptureGroup.class);
                try {
                    Log.d(TAG, new StringBuilder("openItem > ")
                            .append(" mCollectingEndIdLocal = ").append(mCollectingEndIdLocal)
                            .append("\n mCollectingEndIdRemote = ").append(mCollectingEndIdRemote)
                            .toString());
                    if (null == mCollectingEndIdLocal || null == mCollectingEndIdRemote) {
                        throw new Exception(new StringBuilder("mCollectingEndId is invalid :")
                                .append(" mCollectingEndIdLocal = ").append(mCollectingEndIdLocal)
                                .append("\n mCollectingEndIdRemote = ").append(mCollectingEndIdRemote)
                                .toString());
                    }
                    if (!mCollectingEndIdLocal.equals(mCollectingEndIdRemote)) {
                        Log.d(TAG, "openItem > 即将打开群组[播放端]");
                        item.setClass(context, MultiRenderGroup.class);
                    } else {
                        Log.d(TAG, "openItem > 即将打开群组[采集端]");
                    }
                } catch (Exception e) {
                    Log.e(TAG, Log.getStackTraceString(e));
                    onResult(event, IAudioVideo.RESULT_FAIL_FAILURE_AUDIO_VIDEO_PARAMETER_PARSE_FAIL);
                    return getTitleByMediaType(mediaType, R.string.media_request_open_handle_fail);
                }
                break;
            default:
                item = null;
                Log.e(TAG, "onCreate > process fail : mode config is invalid = " + mediaType);
                break;
        }
        try {
            if (null != item) {
                item.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                item.putExtras(event.getData());
                context.startActivity(item);
                return null;
            }
        } catch (Exception e) {
            Log.e(TAG, Log.getStackTraceString(e));
            onResult(event, IAudioVideo.RESULT_FAIL_FAILURE_AUDIO_VIDEO_PARAMETER_PARSE_FAIL);
        }
        return getTitleByMediaType(mediaType, R.string.media_request_open_handle_fail);
    }

    @Override
    public boolean onModuleEvent(RemoteEvent event) {
        String resultStr = null;
        Log.d(TAG, "onModuleEvent > event = " + event.getCode());
        switch (event.getCode()) {
            case EventCommon.Code.NETWORK_CONNECTED:

                break;
            case EventCommon.Code.NETWORK_DISCONNECT:
                Log.d(TAG, "onModuleEvent > 网络断开");

                break;
            case EventRemoteControl.Code.AUDIO_AND_VIDEO_PARAMETERS_APPLY_RESULT:
                //平台未响应
                if (IAudioVideo.EVENT_TYPE_REMOTE_PLATFORM_NO_RESPONSE ==
                        EventAudioVideoParametersApplyResult.getEventType(event)) {
                    play("您好，通话暂时无人接听，请稍后再试");
                }
                //申请参数失败或超时处理
                if (!EventAudioVideoParametersApplyResult.isResultSuccess(event)) {
                    setHandlerStatus(HS_NORMAL);
                }
                break;

            case EventAudioVideoCommunication.Code.PHOTO_TAKE_REQUEST:
                Log.d(TAG, "onModuleEvent > 处理拍照申请");

                //存在通话时不允许拍照
                if (isLiveByTypeCode(-1)
                        || !isItInHandlerState(HS_NORMAL)) {
                    final int eventType = EventPhotoTakeResult.getEventType(event);
                    dispatchEvent(new EventPhotoTakeResult()
                            .setResult(false)
                            .setEventType(eventType)
                            .setRemote(IAudioVideo.EVENT_TYPE_REMOTE_PLATFORM_INITIATE == eventType));//平台发起才回复

                    Log.w(TAG, "onModuleEvent > process fail : 拍照失败，请先关闭通话");
                    if (!isItInHandlerState(HS_OPEN))//没有正在进行的通话提示
                        play("拍照失败，请先关闭通话");
                    return true;
                }
                if (IAudioVideo.EVENT_TYPE_LOCAL_DEVICE_INITIATE
                        == EventPhotoTakeRequest.getEventType(event)) {
                    play("正在为您拍照");
                }
                TakePhoto.open(getContext(), event.getData());
                return true;

            case EventAudioVideoCommunication.Code.AUDIO_VIDEO_OPERATE_REQUEST:
                Log.d(TAG, "onModuleEvent > 处理音视频申请");

                final int eventType = EventAudioVideoOperateRequest.getEventType(event);

                //平台拒绝
                if (IAudioVideo.EVENT_TYPE_REMOTE_PLATFORM_REFUSE == eventType
                        && isItInHandlerState(HS_OPEN_REQUEST_BE_EXECUTING)) {
                    play("通话被拒，请稍后尝试");
                    clearRequestAudioVideoParametersFlag();
                    setHandlerStatus(HS_NORMAL);
                    return true;
                }

                //平台关闭
                if (IAudioVideo.EVENT_TYPE_CLOSE == eventType) {
                    CameraLightControl.getInstance(getContext()).switchLaserLight(false);
                    exitAllLiveActivity();
                    onResult(event, IAudioVideo.RESULT_SUCCESS);
                    setHandlerStatus(HS_NORMAL);
                    return true;
                }

                clearRequestAudioVideoParametersFlag();
                setHandlerStatus(HS_OPEN_HANDLE_BE_EXECUTING);

                resultStr = openItem(getContext(), event);
                if (null != resultStr) {
                    Log.e(TAG, "onModuleEvent > process fail : " + resultStr);
                    dispatchEvent(new EventAudioVideoOperateResult().setResult(false));
                    play(resultStr);
                    setHandlerStatus(HS_NORMAL);
                } else {
                    mAudioVideoType = EventAudioVideoOperateRequest.getMediaType(event);
                }
                return true;
            default:
                break;
        }
        return false;
    }

    @Override
    public void setHandlerStatus(int handlerStatus) {
        super.setHandlerStatus(handlerStatus);
        if (HS_OPEN_REQUEST_BE_EXECUTING == handlerStatus) {
            mRingtoneHandler.play();
        } else {
            mRingtoneHandler.stop();
        }
    }
}
