package com.kuyou.avc.handler;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;

import com.kuyou.avc.BuildConfig;
import com.kuyou.avc.R;
import com.kuyou.avc.handler.basic.AudioVideoRequestResultHandler;
import com.kuyou.avc.handler.basic.CameraLightControl;
import com.kuyou.avc.handler.basic.IAudioVideoRequestCallback;
import com.kuyou.avc.handler.thermal.ThermalCameraControl;
import com.kuyou.avc.photo.ITakePhotoResultListener;
import com.kuyou.avc.photo.TakePhotoBackground;
import com.kuyou.avc.ui.MultiCaptureAudio;
import com.kuyou.avc.ui.MultiCaptureGroup;
import com.kuyou.avc.ui.MultiCaptureThermal;
import com.kuyou.avc.ui.MultiCaptureVideo;
import com.kuyou.avc.ui.MultiRenderGroup;
import com.kuyou.avc.ui.basic.AVCActivity;

import java.util.Iterator;
import java.util.Set;

import kuyou.common.ipc.RemoteEvent;
import kuyou.common.ku09.config.DevicesConfig;
import kuyou.common.ku09.event.avc.EventAudioVideoOperateRequest;
import kuyou.common.ku09.event.avc.EventAudioVideoOperateResult;
import kuyou.common.ku09.event.avc.EventPhotoTakeRequest;
import kuyou.common.ku09.event.avc.EventPhotoTakeResult;
import kuyou.common.ku09.event.avc.basic.EventAudioVideoCommunication;
import kuyou.common.ku09.event.rc.EventAudioVideoParametersApplyRequest;
import kuyou.common.ku09.event.rc.EventAudioVideoParametersApplyResult;
import kuyou.common.ku09.event.rc.EventPhotoUploadRequest;
import kuyou.common.ku09.event.rc.basic.EventRemoteControl;
import kuyou.common.ku09.protocol.IJT808ExtensionProtocol;

/**
 * action :协处理器[音视频][基于Peergine]
 * <p>
 * remarks:  <br/>
 * author: wuguoxian <br/>
 * date: 21-7-23 <br/>
 * </p>
 */
public class PeergineAudioVideoHandler extends AudioVideoRequestResultHandler implements ITakePhotoResultListener {

    protected final String TAG = "com.kuyou.avc.handle > " + this.getClass().getSimpleName();

    private final String KEY_HANDLER_STATUS = "HandlerStatus";
    private final String KEY_MEDIA_TYPE = "MediaType";
    private final String KEY_GROUP_OWNER = "GroupOwner";

    //用于群组通话，区分设备是否为采集端,本地和远程分配的相同就是，不同不是
    private String mCollectingEndIdLocal = null,//本地采集端ID
            mCollectingEndIdRemote = null;//远程分配的采集端ID

    private RingtoneHandler mRingtoneHandler;

    private SharedPreferences mSPHandleStatus;

    private int mMediaType = IJT808ExtensionProtocol.MEDIA_TYPE_DEFAULT;
    private OperateAndTimeoutHandler mOperateAndTimeoutHandler;

    public PeergineAudioVideoHandler(Context context) {
        setContext(context.getApplicationContext());
        ThermalCameraControl.close();
    }

    @Override
    public void onIpcFrameResisterSuccess() {
        syncPlatformAudioVideoCommunicationStatus();
    }

    private void syncPlatformAudioVideoCommunicationStatus() {
        int handleStatusCache = getHandleStatusCache(getContext());
        int mediaTypCache = getMediaTypeCache(getContext());
        if (HS_NORMAL == handleStatusCache) {
            Log.d(TAG, "syncPlatformAudioVideoCommunicationStatus > 没有通话不正常");
            return;
        }

        Log.d(TAG, new StringBuilder("syncPlatformAudioVideoCommunicationStatus > 确认通话是否正常退出")
                .append("\nhandleStatusCache = ").append(handleStatusCache)
                .append("\nmediaTypCache = ").append(mediaTypCache)
                .toString());
        switch (mediaTypCache) {
            case IJT808ExtensionProtocol.MEDIA_TYPE_GROUP:
                if (!getCacheVal(getContext(), KEY_GROUP_OWNER, false)) {
                    Log.d(TAG, "syncPlatformAudioVideoCommunicationStatus > 群组未正常退出,非群主无需处理 ");
                    return;
                }
                Log.d(TAG, "syncPlatformAudioVideoCommunicationStatus > 群组不正常 ");
                break;
            case IJT808ExtensionProtocol.MEDIA_TYPE_VIDEO:
                Log.d(TAG, "syncPlatformAudioVideoCommunicationStatus > 视频不正常 ");
                if (HS_OPEN == handleStatusCache) {
                    handleStatusCache = HS_CLOSE_BE_EXECUTING;
                    //return;
                }
                break;
            case IJT808ExtensionProtocol.MEDIA_TYPE_AUDIO:
                Log.d(TAG, "syncPlatformAudioVideoCommunicationStatus > 语音不正常");
                if (HS_OPEN == handleStatusCache) {
                    handleStatusCache = HS_CLOSE_BE_EXECUTING;
                    //return;
                }
                break;
            default:
                break;
        }

        switch (handleStatusCache) {
            case HS_OPEN:
                mMediaType = mediaTypCache;
                getOperateAndTimeoutHandler().start(HS_OPEN_REQUEST_BE_EXECUTING, 15000);
                break;
            default:
                mMediaType = mediaTypCache;
                getOperateAndTimeoutHandler().start(HS_CLOSE_BE_EXECUTING, 3000);
                break;
        }
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
        if (!(activity instanceof AVCActivity)) {
            Log.d(TAG, "onActivityCreated > get up activity =" + activity.getLocalClassName());
            return;
        }
        AVCActivity item = (AVCActivity) activity;
        int typeCode = item.getTypeCode();
        if (-1 != typeCode) {
            item.setAudioVideoRequestCallback(PeergineAudioVideoHandler.this);
            item.setDispatchEventCallback(PeergineAudioVideoHandler.this.getDispatchEventCallBack());
            Log.d(TAG, "onActivityCreated > put activity =" + activity.getLocalClassName());
            mItemListOnline.put(typeCode, item);
            setHandleStatus(HS_OPEN);
        }
    }

    @Override
    public void onActivityDestroyed(@NonNull Activity activity) {
        super.onActivityDestroyed(activity);
        if (!(activity instanceof AVCActivity)) {
            Log.d(TAG, "onActivityCreated > get up activity =" + activity.getLocalClassName());
            return;
        }
        AVCActivity item = (AVCActivity) activity;
        int typeCode = item.getTypeCode();
        if (-1 != typeCode) {
            switch (typeCode) {
                case IJT808ExtensionProtocol.MEDIA_TYPE_GROUP:
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
        if (IJT808ExtensionProtocol.RESULT_FAIL_FAILURE_AUDIO_VIDEO_PARAMETER_PARSE_FAIL == result
                && BuildConfig.DEBUG) {
            play(getContext().getString(R.string.media_request_open_handle_parameter_fail));
        }
    }

    @Override
    public String getTitleByMediaType(final int mediaType, int combinationStrResId) {
        String title = null;
        switch (mediaType) {
            case IJT808ExtensionProtocol.MEDIA_TYPE_AUDIO:
                title = "语音";
                break;
            case IJT808ExtensionProtocol.MEDIA_TYPE_VIDEO:
                title = "视频";
                break;
            case IJT808ExtensionProtocol.MEDIA_TYPE_THERMAL:
                title = "红外";
                break;
            case IJT808ExtensionProtocol.MEDIA_TYPE_GROUP:
                //非采集端的提示的特殊处理
                if (!isGroupOwner() && R.string.media_request_open_success == combinationStrResId) {
                    return getContext().getString(R.string.media_request_enter_group_success);
                }
                title = "群组";
                break;
            default:
                return "未知模式";
        }
        return 0 > combinationStrResId ? title : getContext().getString(combinationStrResId, title);
    }

    protected String getHandleStatusContent() {
        switch (getHandlerStatus()) {
            case HS_NORMAL:
                return "HandlerStatus() = HS_NORMAL";
            case HS_OPEN:
                return "HandlerStatus() = HS_OPEN";
            case HS_OPEN_HANDLE_BE_EXECUTING:
                return "HandlerStatus() = HS_OPEN_HANDLE_BE_EXECUTING";
            case HS_OPEN_REQUEST_BE_EXECUTING:
                return "HandlerStatus() = HS_OPEN_REQUEST_BE_EXECUTING";
            case HS_CLOSE_BE_EXECUTING:
                return "HandlerStatus() = HS_CLOSE_BE_EXECUTING";
            default:
                return "HandlerStatus() = none";
        }
    }

    @Override
    protected void exitAllLiveItem() {
        Log.d(TAG, " exitAllLiveItem > size = " + mItemListOnline.size());
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                synchronized (mItemListOnline) {
                    if (1 > mItemListOnline.size()) {
                        return;
                    }
                    Set<Integer> set = mItemListOnline.keySet();
                    Iterator<Integer> it = set.iterator();
                    AVCActivity activity;
                    while (it.hasNext()) {
                        final int typeCode = it.next();
                        activity = mItemListOnline.get(typeCode);
                        if (null == activity || activity.isDestroyed()) {
                            continue;
                        }
                        if (-1 != activity.getTypeCode() && IJT808ExtensionProtocol.MEDIA_TYPE_VIDEO == activity.getTypeCode()) {
                            CameraLightControl.getInstance(getContext()).switchLaserLight(false);
                        }
                        activity.exit();
                        Log.d(TAG, "getOperateAndTimeoutCallback > activity =" + activity);
                    }
                    mItemListOnline.clear();
                }
            }
        });
        //群聊且为群主时
        if (IJT808ExtensionProtocol.MEDIA_TYPE_GROUP == PeergineAudioVideoHandler.this.getMediaType()
                && isGroupOwner()) {
            Log.d(TAG, "getOperateAndTimeoutCallback > 通知平台群组即将关闭");
            dispatchEvent(new EventAudioVideoParametersApplyRequest()
                    .setMediaType(IJT808ExtensionProtocol.MEDIA_TYPE_GROUP)
                    .setPlatformType(IJT808ExtensionProtocol.PLATFORM_TYPE_PEERGIN)
                    .setEventType(IJT808ExtensionProtocol.EVENT_TYPE_CLOSE)
                    .setRemote(true));
        }
    }

    @Override
    protected boolean isLiveOnlineByType(final int typeCode) {
        if (-1 == typeCode) {
            return mItemListOnline.size() > 0;
        }
        return mItemListOnline.containsKey(typeCode);
    }

    @Override
    public void switchMediaType() {
        String content = null;
        if (getHandlerStatus() == IAudioVideoRequestCallback.HS_NORMAL) {
            mMediaType = mMediaType >= IJT808ExtensionProtocol.MEDIA_TYPE_GROUP
                    ? mMediaType = IJT808ExtensionProtocol.MEDIA_TYPE_AUDIO
                    : mMediaType + 1;
            content = getTitleByMediaType(mMediaType, R.string.key_switch_mode_success_title);
        } else {
            Log.d(TAG, "switchMediaType > " + getHandleStatusContent());
            content = getTitleByMediaType(mMediaType, R.string.key_switch_mode_cancel_title);
        }
        play(null != content ? content : "模式切换失败，请重新尝试");
    }

    @Override
    public void performOperate() {
        Log.d(TAG, new StringBuilder("performOperate > ")
                .append(" \n typeCode = ").append(mMediaType)
                .append(" \n getHandlerStatus = ").append(getHandlerStatus())
                .append(" \n isOnLine = ").append(isLiveOnlineByType(-1))
                .append(" \n mCollectingEndIdLocal = ").append(mCollectingEndIdLocal)
                .append(" , mCollectingEndIdRemote = ").append(mCollectingEndIdRemote)
                .toString());

        //未支持功能
        if (IJT808ExtensionProtocol.MEDIA_TYPE_THERMAL == mMediaType) {
            play("红外暂时不可用");
            return;
        }

        if (isItInHandlerState(HS_OPEN_HANDLE_BE_EXECUTING)) {
            Log.d(TAG, "performOperate > 终端取消,申请成功正在打开的通话");
            getOperateAndTimeoutHandler().stop(HS_OPEN_HANDLE_BE_EXECUTING);
            exitAllLiveItem();
            return;
        }

        if (isItInHandlerState(HS_OPEN_REQUEST_BE_EXECUTING)) {
            Log.d(TAG, "performOperate > 终端取消,正在申请的通话");
            getOperateAndTimeoutHandler().stop(HS_OPEN_REQUEST_BE_EXECUTING);
            setHandleStatus(HS_NORMAL);
            play(getTitleByMediaType(mMediaType, R.string.media_request_cancel_request));
            return;
        }

        if (isLiveOnlineByType(-1)) {
            Log.d(TAG, "performOperate > 终端关闭，已经打开的通话," + getHandleStatusContent());
            exitAllLiveItem();
            return;
        }

        if (isItInHandlerState(HS_CLOSE_BE_EXECUTING)) {
            Log.d(TAG, "performOperate > 关闭中取消按键操作 ");
            return;
        }

        Log.d(TAG, "performOperate > 终端申请，开启通话");
        getOperateAndTimeoutHandler().start(HS_OPEN_REQUEST_BE_EXECUTING, 15000);
    }

    public OperateAndTimeoutHandler getOperateAndTimeoutHandler() {
        if (null == mOperateAndTimeoutHandler) {
            mOperateAndTimeoutHandler = OperateAndTimeoutHandler.getInstance();
            mOperateAndTimeoutHandler.register(HS_OPEN_REQUEST_BE_EXECUTING,
                    new Runnable() {
                        @Override
                        public void run() {
                            Log.d(TAG, "getOperateAndTimeoutCallback > 向平台发出音视频开启请求 > ");
                            PeergineAudioVideoHandler.this.setHandleStatus(HS_OPEN_REQUEST_BE_EXECUTING);
                            PeergineAudioVideoHandler.this.dispatchEvent(new EventAudioVideoParametersApplyRequest()
                                    .setMediaType(PeergineAudioVideoHandler.this.getMediaType())
                                    .setPlatformType(IJT808ExtensionProtocol.PLATFORM_TYPE_PEERGIN)
                                    .setEventType(IJT808ExtensionProtocol.EVENT_TYPE_LOCAL_DEVICE_INITIATE)
                                    .setRemote(true));
                        }
                    },
                    new Runnable() {
                        @Override
                        public void run() {
                            Log.d(TAG, "getOperateAndTimeoutCallback > 向平台发出音视频开启请求 > 失败：未响应");
                            PeergineAudioVideoHandler.this.onModuleEvent(new EventAudioVideoParametersApplyResult()
                                    .setResult(false)
                                    .setMediaType(mMediaType)
                                    .setEventType(IJT808ExtensionProtocol.EVENT_TYPE_REMOTE_PLATFORM_NO_RESPONSE));
                        }
                    });

            mOperateAndTimeoutHandler.register(HS_CLOSE_BE_EXECUTING,
                    new Runnable() {
                        @Override
                        public void run() {
                            Log.d(TAG, "getOperateAndTimeoutCallback > 向平台发出音视频关闭请求 > ");
                            PeergineAudioVideoHandler.this.setHandleStatus(HS_CLOSE_BE_EXECUTING);
                            PeergineAudioVideoHandler.this.exitAllLiveItem();
                        }
                    },
                    new Runnable() {
                        @Override
                        public void run() {
                            Log.d(TAG, "OperateTimeoutCallback > 向平台发出音视频关闭请求 > 失败：未响应 > 重新发送关闭指令");
//                            dispatchEvent(new EventAudioVideoParametersApplyRequest()
//                                    .setMediaType(getMediaType())
//                                    .setPlatformType(JT808ExtensionProtocol.PLATFORM_TYPE_PEERGIN)
//                                    .setEventType(JT808ExtensionProtocol.EVENT_TYPE_CLOSE)
//                                    .setRemote(true));
                            setHandleStatus(HS_NORMAL);
                        }
                    });
        }
        return mOperateAndTimeoutHandler;
    }

    @Override
    public boolean onModuleEvent(RemoteEvent event) {
        String resultStr = null;
        switch (event.getCode()) {
            case EventRemoteControl.Code.AUDIO_VIDEO_PARAMETERS_APPLY_RESULT:
                //申请参数失败
                if (!EventAudioVideoParametersApplyResult.isResultSuccess(event)) {
                    setHandleStatus(HS_NORMAL);

                    //平台未响应,超时处理
                    if (IJT808ExtensionProtocol.EVENT_TYPE_REMOTE_PLATFORM_NO_RESPONSE ==
                            EventAudioVideoParametersApplyResult.getEventType(event)) {
                        play(getTitleByMediaType(EventAudioVideoParametersApplyResult.getMediaType(event),
                                R.string.media_request_timeout_request));
                    }
                }
                break;

            case EventAudioVideoCommunication.Code.PHOTO_TAKE_REQUEST:
                Log.d(TAG, "onModuleEvent > 处理拍照请求");

                if (IJT808ExtensionProtocol.EVENT_TYPE_LOCAL_DEVICE_INITIATE == EventPhotoTakeRequest.getEventType(event)) {
                    play("正在为您拍照");
                }

                //截图拍照
                if (isItInHandlerState(HS_OPEN)) {
                    int onLineTypeCode = -1;
                    if (isLiveOnlineByType(IJT808ExtensionProtocol.MEDIA_TYPE_VIDEO)) {
                        onLineTypeCode = IJT808ExtensionProtocol.MEDIA_TYPE_VIDEO;
                    }
                    if (isLiveOnlineByType(IJT808ExtensionProtocol.MEDIA_TYPE_THERMAL)) {
                        onLineTypeCode = IJT808ExtensionProtocol.MEDIA_TYPE_THERMAL;
                    }
                    if (-1 != onLineTypeCode) {
                        int result = getOnlineList()
                                .get(onLineTypeCode)
                                .screenshot(event, new AVCActivity.IVideoCameraResultListener() {
                                    @Override
                                    public void onScreenshotResult(String result) {
                                        PeergineAudioVideoHandler.this.onTakePhotoResult(true, result, event.getData());
                                    }
                                });
                        if (-1 != result) {//异常失败处理
                            PeergineAudioVideoHandler.this.onTakePhotoResult(false, "", event.getData());
                        }
                        return true;
                    }
                }
                //后台相机拍照
                TakePhotoBackground.perform(getContext(), event.getData(), PeergineAudioVideoHandler.this);
                ////前台相机拍照
                //TakePhoto.perform(getContext(), event.getData(), PeergineAudioVideoHandler.this);
                return true;

            case EventAudioVideoCommunication.Code.AUDIO_VIDEO_OPERATE_REQUEST:
                Log.d(TAG, "onModuleEvent > 处理音视频请求");

                final int eventType = EventAudioVideoOperateRequest.getEventType(event);

                //平台拒绝
                if (IJT808ExtensionProtocol.EVENT_TYPE_REMOTE_PLATFORM_REFUSE == eventType) {
                    if (isItInHandlerState(HS_OPEN_REQUEST_BE_EXECUTING)) {
                        getOperateAndTimeoutHandler().stop(HS_OPEN_REQUEST_BE_EXECUTING);
                        play(getTitleByMediaType(mMediaType, R.string.media_request_open_rejected_title));
                        setHandleStatus(HS_NORMAL);
                        Log.d(TAG, "onModuleEvent > 处理音视频申请 > 平台拒绝通话申请");
                    } else {
                        Log.d(TAG, "onModuleEvent > 处理音视频申请 > 平台拒绝已取消的通话申请");
                    }
                    return true;
                }

                //关闭通话
                if (IJT808ExtensionProtocol.EVENT_TYPE_CLOSE == eventType) {
                    if (HS_NORMAL == getHandlerStatus()) {
                        return true;
                    }
                    if (HS_CLOSE_BE_EXECUTING == getHandlerStatus()
                            || HS_CLOSE_BE_EXECUTING == getHandleStatusCache(getContext())) {
                        getOperateAndTimeoutHandler().stop(HS_CLOSE_BE_EXECUTING);
                    } else {
                        exitAllLiveItem();
                    }
                    setHandleStatus(HS_NORMAL);
                    return true;
                }

                //开启通话
                getOperateAndTimeoutHandler().stop(HS_OPEN_REQUEST_BE_EXECUTING);
                setHandleStatus(HS_OPEN_HANDLE_BE_EXECUTING);

                resultStr = openItem(getContext(), event);
                if (null != resultStr) {
                    Log.e(TAG, "onModuleEvent > process fail : " + resultStr);
                    dispatchEvent(new EventAudioVideoOperateResult().setResult(false));
                    play(resultStr);
                    setHandleStatus(HS_NORMAL);
                } else {
                    mMediaType = EventAudioVideoOperateRequest.getMediaType(event);
                }
                return true;
            default:
                break;
        }
        return false;
    }

    @Override
    protected String openItem(Context context, RemoteEvent event) {
        int mediaType = EventAudioVideoOperateRequest.getMediaType(event);
        Intent item = new Intent();
        Log.d(TAG, "openItem >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> ");
        switch (mediaType) {
            case IJT808ExtensionProtocol.MEDIA_TYPE_AUDIO:
                Log.d(TAG, "openItem > IJT808ExtensionProtocol.MEDIA_TYPE_AUDIO");
                item.setClass(context, MultiCaptureAudio.class);
                break;
            case IJT808ExtensionProtocol.MEDIA_TYPE_VIDEO:
                Log.d(TAG, "openItem > IJT808ExtensionProtocol.MEDIA_TYPE_VIDEO");
                item.setClass(context, MultiCaptureVideo.class);
                break;
            case IJT808ExtensionProtocol.MEDIA_TYPE_THERMAL:
                Log.d(TAG, "openItem > IJT808ExtensionProtocol.MEDIA_TYPE_INFEARED");
                item.setClass(context, MultiCaptureThermal.class);
                break;
            case IJT808ExtensionProtocol.MEDIA_TYPE_GROUP:
                Log.d(TAG, "openItem > IJT808ExtensionProtocol.MEDIA_TYPE_GROUP");
                mCollectingEndIdRemote = EventAudioVideoOperateRequest.getToken(event);
                item.setClass(context, MultiCaptureGroup.class);
                try {
                    Log.d(TAG, new StringBuilder("openItem > ")
                            .append(" mCollectingEndIdLocal = ").append(mCollectingEndIdLocal)
                            .append("\n mCollectingEndIdRemote = ").append(mCollectingEndIdRemote)
                            .toString());
                    //群组必要参数缺失
                    if (null == mCollectingEndIdLocal || null == mCollectingEndIdRemote) {
                        throw new Exception(new StringBuilder("mCollectingEndId is invalid :")
                                .append(" mCollectingEndIdLocal = ").append(mCollectingEndIdLocal)
                                .append("\n mCollectingEndIdRemote = ").append(mCollectingEndIdRemote)
                                .toString());
                    }
                    //未加入群组
                    if (IJT808ExtensionProtocol.TOKEN_NULL.equals(mCollectingEndIdRemote)) {
                        onResult(event, IJT808ExtensionProtocol.RESULT_FAIL_FAILURE_OTHER);
                        return "设备未加入群组，请联系管理员处理";
                    }
                    if (!mCollectingEndIdLocal.equals(mCollectingEndIdRemote)) {
                        Log.d(TAG, "openItem > 即将打开群组[播放端]");
                        item.setClass(context, MultiRenderGroup.class);
                    } else {
                        Log.d(TAG, "openItem > 即将打开群组[采集端]");
                    }
                } catch (Exception e) {
                    Log.e(TAG, Log.getStackTraceString(e));
                    onResult(event, IJT808ExtensionProtocol.RESULT_FAIL_FAILURE_AUDIO_VIDEO_PARAMETER_PARSE_FAIL);
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
            onResult(event, IJT808ExtensionProtocol.RESULT_FAIL_FAILURE_AUDIO_VIDEO_PARAMETER_PARSE_FAIL);
        }
        return getTitleByMediaType(mediaType, R.string.media_request_open_handle_fail);
    }


    @Override
    public void setHandleStatus(int handlerStatus) {
        super.setHandleStatus(handlerStatus);
        Log.d(TAG, "setHandleStatus > " + getHandleStatusContent());
        if (HS_OPEN_REQUEST_BE_EXECUTING == handlerStatus) {
            getRingtoneHandler().play();
        } else {
            getRingtoneHandler().stop();
        }
        saveStatus2Cache(getContext());
    }

    @Override
    public void onTakePhotoResult(boolean result, String info, Bundle data) {
        if (result) {
            Log.d(TAG, "onResult > 拍照成功 > 申请上传");
            if (IJT808ExtensionProtocol.EVENT_TYPE_LOCAL_DEVICE_INITIATE == EventPhotoTakeResult.getEventType(data)) {
                play("拍照成功");
            }
            dispatchEvent(new EventPhotoUploadRequest()
                    .setImgFilePath(info)
                    .setEventType(EventPhotoUploadRequest.getEventType(data))
                    .setRemote(true));
        } else {
            Log.d(TAG, "onResult > 拍照失败");
            if (IJT808ExtensionProtocol.EVENT_TYPE_LOCAL_DEVICE_INITIATE == EventPhotoTakeResult.getEventType(data)) {
                play("拍照失败");
            }
            dispatchEvent(new EventPhotoTakeResult()
                    .setData(data)
                    .setRemote(true)
                    .setResult(false));
        }
    }

    private int getMediaType() {
        return mMediaType;
    }

    private boolean isGroupOwner() {
        return null != mCollectingEndIdRemote
                && null != mCollectingEndIdLocal
                && mCollectingEndIdLocal.equals(mCollectingEndIdRemote);
    }

    private void initHandleStatusDataBase(Context context) {
        if (null != mSPHandleStatus)
            return;
        mSPHandleStatus = context.getSharedPreferences("data", Context.MODE_PRIVATE);
    }

    private void saveStatus2Cache(Context context) {
        initHandleStatusDataBase(context);
        SharedPreferences.Editor editor = mSPHandleStatus.edit();
        editor.putInt(KEY_HANDLER_STATUS, getHandlerStatus());
        editor.putInt(KEY_MEDIA_TYPE, getMediaType());
        if (IJT808ExtensionProtocol.MEDIA_TYPE_GROUP == getMediaType()) {
            editor.putBoolean(KEY_GROUP_OWNER, isGroupOwner());
        }
        editor.commit();
    }

    private int getHandleStatusCache(Context context) {
        return getCacheVal(context, KEY_HANDLER_STATUS, getHandlerStatus());
    }

    private int getMediaTypeCache(Context context) {
        return getCacheVal(context, KEY_MEDIA_TYPE, getHandlerStatus());
    }

    private int getCacheVal(Context context, String key, int defVal) {
        initHandleStatusDataBase(context);
        return mSPHandleStatus.getInt(key, defVal);
    }

    private boolean getCacheVal(Context context, String key, boolean defVal) {
        initHandleStatusDataBase(context);
        return mSPHandleStatus.getBoolean(key, defVal);
    }

    public RingtoneHandler getRingtoneHandler() {
        if (null == mRingtoneHandler) {
            mRingtoneHandler = new LocalRingtoneHandler(getContext());
        }
        return mRingtoneHandler;
    }
}
