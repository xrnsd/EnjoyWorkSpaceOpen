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

import kuyou.common.ku09.handler.CameraLightControl;
import com.kuyou.avc.handler.ringtone.LocalRingtoneHandler;
import com.kuyou.avc.handler.ringtone.RingtoneHandler;
import kuyou.common.ku09.handler.ThermalCameraControl;
import com.kuyou.avc.ui.MultiCaptureAudio;
import com.kuyou.avc.ui.MultiCaptureGroup;
import com.kuyou.avc.ui.MultiCaptureThermal;
import com.kuyou.avc.ui.MultiCaptureVideo;
import com.kuyou.avc.ui.MultiRenderGroup;
import com.kuyou.avc.ui.basic.AVCActivity;

import java.util.Iterator;
import java.util.Set;

import kuyou.common.ipc.RemoteEvent;
import kuyou.common.ku09.protocol.basic.IDeviceConfig;
import kuyou.common.ku09.event.avc.EventAudioVideoOperateRequest;
import kuyou.common.ku09.event.avc.EventAudioVideoOperateResult;
import kuyou.common.ku09.event.avc.basic.EventAudioVideoCommunication;
import kuyou.common.ku09.event.common.basic.EventCommon;
import kuyou.common.ku09.event.rc.EventAudioVideoParametersApplyRequest;
import kuyou.common.ku09.event.rc.EventAudioVideoParametersApplyResult;
import kuyou.common.ku09.event.rc.EventLocalDeviceStatus;
import kuyou.common.ku09.event.rc.basic.EventRemoteControl;
import kuyou.common.ku09.protocol.basic.IJT808ExtensionProtocol;
import kuyou.common.status.StatusProcessBusCallbackImpl;
import kuyou.common.status.basic.IStatusProcessBusCallback;

/**
 * action :协处理器[音视频][基于Peergine]
 * <p>
 * remarks:  <br/>
 * author: wuguoxian <br/>
 * date: 21-7-23 <br/>
 * </p>
 */
public class PeergineAudioVideoHandler extends AudioVideoRequestResultHandler {

    protected final static String TAG = "com.kuyou.avc.handle > PeergineAudioVideoHandler";
    protected final static String KEY_HANDLER_STATUS = "HandlerStatus";
    protected final static String KEY_MEDIA_TYPE = "MediaType";
    protected final static String KEY_GROUP_OWNER = "GroupOwner";

    protected final static int PS_FLAG = 0;
    protected final static int PS_OPEN_REQUEST_BE_EXECUTING = PS_FLAG + 1;
    protected final static int PS_OPEN_REQUEST_BE_EXECUTING_TIME_OUT = PS_FLAG + 2;
    protected final static int PS_OPEN_HANDLE_BE_EXECUTING = PS_FLAG + 3;
    protected final static int PS_OPEN_PARAMETER_PARSE_FAIL = PS_FLAG + 4;
    protected final static int PS_CLOSE_BE_EXECUTING = PS_FLAG + 5;
    protected final static int PS_DEVICE_OFF_LINE_RECOVERY_TIME_OUT = PS_FLAG + 6;
    protected final static int PS_CACHE_STATUS_CHECK = PS_FLAG + 7;

    private RingtoneHandler mRingtoneHandler;
    private SharedPreferences mSPHandleStatus;

    private int mMediaTypeLocal = IJT808ExtensionProtocol.MEDIA_TYPE_DEFAULT;

    //用于群组通话，区分设备是否为采集端,本地和远程分配的相同就是，不同不是
    private String mCollectingEndIdLocal = null,//本地采集端ID
            mCollectingEndIdRemote = null;//远程分配的采集端ID

    public PeergineAudioVideoHandler(Context context) {
        setContext(context.getApplicationContext());
        ThermalCameraControl.close();
    }

    @Override
    public void onIpcFrameResisterSuccess() {
        if (null != getStatusProcessBus()) {
            getStatusProcessBus().start(PS_CACHE_STATUS_CHECK);
        } else {
            Log.e(TAG, "onIpcFrameResisterSuccess > process fail : cancel perform checkAVCLocalCacheStatus , getStatusProcessBus is null ");
        }
    }

    //模块启动后，确认是否存在未正常退出的通话
    private void checkAVCLocalCacheStatus() {
        int handleStatusCache = getHandleStatusCache(getContext());
        int mediaTypCache = getMediaTypeCache(getContext());
        if (HS_NORMAL == handleStatusCache) {
            //Log.i(TAG, "checkAVCLocalCacheStatus > 没有通话不正常");
            return;
        }

        Log.i(TAG, new StringBuilder("checkAVCLocalCacheStatus > 确认通话是否正常退出")
                .append("\nhandleStatusCache = ").append(handleStatusCache)
                .append("\nmediaTypCache = ").append(mediaTypCache)
                .toString());
        switch (mediaTypCache) {
            case IJT808ExtensionProtocol.MEDIA_TYPE_GROUP:
                if (!getCacheVal(getContext(), KEY_GROUP_OWNER, false)) {
                    Log.i(TAG, "checkAVCLocalCacheStatus > 群组未正常退出,非群主无需处理 ");
                    saveStatus2Cache(getContext());//重置本地保存的状态
                    return;
                }
                Log.i(TAG, "checkAVCLocalCacheStatus > 群组不正常 ");
                break;
            case IJT808ExtensionProtocol.MEDIA_TYPE_VIDEO:
                Log.i(TAG, "checkAVCLocalCacheStatus > 视频不正常 ");
                if (HS_OPEN == handleStatusCache) {
                    handleStatusCache = HS_CLOSE_BE_EXECUTING;
                }
                break;
            case IJT808ExtensionProtocol.MEDIA_TYPE_AUDIO:
                Log.i(TAG, "checkAVCLocalCacheStatus > 语音不正常");
                if (HS_OPEN == handleStatusCache) {
                    handleStatusCache = HS_CLOSE_BE_EXECUTING;
                }
                break;
            default:
                break;
        }

        switch (handleStatusCache) {
            case HS_OPEN:
                mMediaTypeLocal = mediaTypCache;
                getStatusProcessBus().start(PS_OPEN_REQUEST_BE_EXECUTING);
                break;
            default:
                mMediaTypeLocal = mediaTypCache;
                getStatusProcessBus().start(PS_CLOSE_BE_EXECUTING);
                break;
        }
    }

    private int getMediaTypeLocal() {
        return mMediaTypeLocal;
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
        editor.putInt(KEY_MEDIA_TYPE, getMediaTypeLocal());
        if (IJT808ExtensionProtocol.MEDIA_TYPE_GROUP == getMediaTypeLocal()) {
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

    protected void onResult(RemoteEvent event, int result) {
        dispatchEvent(new EventAudioVideoOperateResult()
                .setFlowNumber(EventAudioVideoOperateRequest.getFlowNumber(event))
                .setToken(EventAudioVideoOperateRequest.getToken(event))
                .setResultCode(result)
                .setRemote(true));
        if (IJT808ExtensionProtocol.RESULT_FAIL_FAILURE_AUDIO_VIDEO_PARAMETER_PARSE_FAIL == result
                && BuildConfig.DEBUG) {
            play(getContext().getString(R.string.media_request_open_handle_parameter_fail));
        }
    }

    protected String getHandleStatusContent() {
        if (getHandlerStatus() == HS_NORMAL) {
            return "HandlerStatus() = HS_NORMAL";
        }
        if (getHandlerStatus() == HS_OPEN) {
            return "HandlerStatus() = HS_OPEN";
        }
        if (getHandlerStatus() == HS_OPEN_HANDLE_BE_EXECUTING) {
            return "HandlerStatus() = HS_OPEN_HANDLE_BE_EXECUTING";
        }
        if (getHandlerStatus() == HS_OPEN_REQUEST_BE_EXECUTING) {
            return "HandlerStatus() = HS_OPEN_REQUEST_BE_EXECUTING";
        }
        if (getHandlerStatus() == HS_CLOSE_BE_EXECUTING) {
            return "HandlerStatus() = HS_CLOSE_BE_EXECUTING";
        }
        return "HandlerStatus() = none";
    }

    @Override
    public void setDevicesConfig(IDeviceConfig deviceConfig) {
        super.setDevicesConfig(deviceConfig);
        mCollectingEndIdLocal = deviceConfig.getCollectingEndId();
        Log.i(TAG, "setDevicesConfig > 获取设备配置 > 本地采集端ID = " + mCollectingEndIdLocal);
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        super.onActivityCreated(activity, savedInstanceState);
        if (!(activity instanceof AVCActivity)) {
            Log.i(TAG, "onActivityCreated > get up activity =" + activity.getLocalClassName());
            return;
        }
        AVCActivity item = (AVCActivity) activity;
        int typeCode = item.getTypeCode();
        if (-1 != typeCode) {
            item.setAudioVideoRequestCallback(PeergineAudioVideoHandler.this);
            item.setDispatchEventCallback(PeergineAudioVideoHandler.this.getDispatchEventCallBack());
            Log.i(TAG, "onActivityCreated > put activity =" + activity.getLocalClassName());
            mItemListOnline.put(typeCode, item);
            setHandleStatus(HS_OPEN);
        }
    }

    @Override
    public void onActivityDestroyed(@NonNull Activity activity) {
        super.onActivityDestroyed(activity);
        if (!(activity instanceof AVCActivity)) {
            Log.i(TAG, "onActivityCreated > get up activity =" + activity.getLocalClassName());
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

    protected void recoverAllLiveItem() {
        Log.d(TAG, "recoverAllLiveItem > 尝试恢复正在进行的音视频通话");
        synchronized (mItemListOnline) {
            if (1 > mItemListOnline.size()) {
                Log.i(TAG, "recoverAllLiveItem > cancel ");
                return;
            }
            getStatusProcessBus().stop(PS_DEVICE_OFF_LINE_RECOVERY_TIME_OUT);
            Set<Integer> set = mItemListOnline.keySet();
            Iterator<Integer> it = set.iterator();
            AVCActivity activity;
            while (it.hasNext()) {
                final int typeCode = it.next();
                activity = mItemListOnline.get(typeCode);
                if (null == activity || activity.isDestroyed()) {
                    continue;
                }
                activity.recover();
                Log.i(TAG, "recoverAllLiveItem > " + activity);
            }
        }
    }

    @Override
    protected void exitAllLiveItem(int eventType) {
        setHandleStatus(HS_CLOSE_BE_EXECUTING);
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                synchronized (mItemListOnline) {
                    Log.i(TAG, " exitAllLiveItem > size = " + mItemListOnline.size());
                    if (0 < mItemListOnline.size()) {
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
                            activity.finish();
                            Log.i(TAG, "exitAllLiveItem > " + activity);
                        }
                        mItemListOnline.clear();
                    } else {
                        Log.i(TAG, "exitAllLiveItem > cancel ");
                    }
                }
            }
        });

        //群聊且为群主时
        if (IJT808ExtensionProtocol.EVENT_TYPE_LOCAL_DEVICE_INITIATE == eventType
                && IJT808ExtensionProtocol.MEDIA_TYPE_GROUP == getMediaTypeLocal()
                && isGroupOwner()) {
            Log.i(TAG, "exitAllLiveItem > 通知平台群组即将关闭");
            dispatchEvent(new EventAudioVideoParametersApplyRequest()
                    .setMediaType(IJT808ExtensionProtocol.MEDIA_TYPE_GROUP)
                    .setPlatformType(IJT808ExtensionProtocol.PLATFORM_TYPE_PEERGIN)
                    .setEventType(IJT808ExtensionProtocol.EVENT_TYPE_CLOSE)
                    .setRemote(true));
        }

        setHandleStatus(HS_NORMAL);
    }

    @Override
    protected boolean isLiveOnlineByType(int typeCode) {
        if (-1 == typeCode) {
            return mItemListOnline.size() > 0;
        }
        return mItemListOnline.containsKey(typeCode);
    }

    @Override
    public void switchMediaType() {
        String content = null;
        if (getHandlerStatus() == HS_NORMAL) {
            mMediaTypeLocal = mMediaTypeLocal >= IJT808ExtensionProtocol.MEDIA_TYPE_GROUP
                    ? mMediaTypeLocal = IJT808ExtensionProtocol.MEDIA_TYPE_AUDIO
                    : mMediaTypeLocal + 1;
            content = getTitleByMediaType(mMediaTypeLocal, R.string.key_switch_mode_success_title);
        } else {
            Log.i(TAG, "switchMediaType > " + getHandleStatusContent());
            content = getTitleByMediaType(mMediaTypeLocal, R.string.key_switch_mode_cancel_title);
        }
        if (null != content) {
            play(content);
        } else {
            Log.i(TAG, "模式切换失败，请重新尝试");
        }
    }

    @Override
    public void setHandleStatus(int handlerStatus) {
        super.setHandleStatus(handlerStatus);
        Log.i(TAG, "setHandleStatus > " + getHandleStatusContent());
        if (HS_OPEN_REQUEST_BE_EXECUTING == handlerStatus) {
            getRingtoneHandler().play();
        } else {
            getRingtoneHandler().stop();
        }
        saveStatus2Cache(getContext());
    }

    @Override
    protected void initReceiveProcessStatusNotices() {
        super.initReceiveProcessStatusNotices();

        getStatusProcessBus().registerStatusNoticeCallback(PS_OPEN_REQUEST_BE_EXECUTING,
                new StatusProcessBusCallbackImpl(false, 0)
                        .setNoticeHandleLooperPolicy(IStatusProcessBusCallback.LOOPER_POLICY_MAIN)
                        .setEnableReceiveRemoveNotice(true));

        getStatusProcessBus().registerStatusNoticeCallback(PS_OPEN_REQUEST_BE_EXECUTING_TIME_OUT,
                new StatusProcessBusCallbackImpl(false, 15 * 1000)
                        .setNoticeHandleLooperPolicy(IStatusProcessBusCallback.LOOPER_POLICY_MAIN));

        getStatusProcessBus().registerStatusNoticeCallback(PS_OPEN_HANDLE_BE_EXECUTING,
                new StatusProcessBusCallbackImpl(false, 0)
                        .setNoticeHandleLooperPolicy(IStatusProcessBusCallback.LOOPER_POLICY_MAIN));

        getStatusProcessBus().registerStatusNoticeCallback(PS_OPEN_PARAMETER_PARSE_FAIL,
                new StatusProcessBusCallbackImpl(false, 5 * 1000)
                        .setNoticeHandleLooperPolicy(IStatusProcessBusCallback.LOOPER_POLICY_MAIN));

        getStatusProcessBus().registerStatusNoticeCallback(PS_CLOSE_BE_EXECUTING,
                new StatusProcessBusCallbackImpl(false, 0)
                        .setNoticeHandleLooperPolicy(IStatusProcessBusCallback.LOOPER_POLICY_MAIN));

        getStatusProcessBus().registerStatusNoticeCallback(PS_DEVICE_OFF_LINE_RECOVERY_TIME_OUT,
                new StatusProcessBusCallbackImpl(false, 30 * 1000)
                        .setNoticeHandleLooperPolicy(IStatusProcessBusCallback.LOOPER_POLICY_MAIN));

        getStatusProcessBus().registerStatusNoticeCallback(PS_CACHE_STATUS_CHECK,
                new StatusProcessBusCallbackImpl(false, 0)
                        .setNoticeHandleLooperPolicy(IStatusProcessBusCallback.LOOPER_POLICY_BACKGROUND));
    }

    @Override
    protected void onReceiveProcessStatusNotice(int statusCode, boolean isRemove) {
        super.onReceiveProcessStatusNotice(statusCode, isRemove);

        switch (statusCode) {
            case PS_OPEN_REQUEST_BE_EXECUTING:
                if (isRemove) {
                    getStatusProcessBus().stop(PS_OPEN_REQUEST_BE_EXECUTING_TIME_OUT);
                    break;
                }
                Log.i(TAG, "onReceiveProcessStatusNotice > 向平台发出音视频开启请求 > ");
                getStatusProcessBus().start(PS_OPEN_REQUEST_BE_EXECUTING_TIME_OUT);
                setHandleStatus(HS_OPEN_REQUEST_BE_EXECUTING);
                dispatchEvent(new EventAudioVideoParametersApplyRequest()
                        .setMediaType(getMediaTypeLocal())
                        .setPlatformType(IJT808ExtensionProtocol.PLATFORM_TYPE_PEERGIN)
                        .setEventType(IJT808ExtensionProtocol.EVENT_TYPE_LOCAL_DEVICE_INITIATE)
                        .setRemote(true));
                break;

            case PS_OPEN_REQUEST_BE_EXECUTING_TIME_OUT:
                Log.i(TAG, "onReceiveProcessStatusNotice > 向平台发出音视频开启请求 > 失败：未响应");
                onReceiveEventNotice(new EventAudioVideoParametersApplyResult()
                        .setResult(false)
                        .setMediaType(mMediaTypeLocal)
                        .setEventType(IJT808ExtensionProtocol.EVENT_TYPE_REMOTE_PLATFORM_NO_RESPONSE));
                break;

            case PS_OPEN_HANDLE_BE_EXECUTING:
                getStatusProcessBus().stop(PS_OPEN_REQUEST_BE_EXECUTING);
                setHandleStatus(HS_OPEN_HANDLE_BE_EXECUTING);
                break;

            case PS_OPEN_PARAMETER_PARSE_FAIL:
                Log.i(TAG, new StringBuilder("onReceiveProcessStatusNotice > 参数解析失败 > mCollectingEndId is invalid :")
                        .append("\n mCollectingEndIdLocal = ").append(mCollectingEndIdLocal)
                        .append("\n mCollectingEndIdRemote = ").append(mCollectingEndIdRemote)
                        .toString());
                break;

            case PS_CLOSE_BE_EXECUTING:
                Log.i(TAG, "onReceiveProcessStatusNotice > 开始关闭音视频 > ");
                if (!isLiveOnlineByType(-1)) {
                    setHandleStatus(HS_NORMAL);
                } else {
                    exitAllLiveItem(IJT808ExtensionProtocol.EVENT_TYPE_LOCAL_DEVICE_INITIATE);
                }
                break;

            case PS_DEVICE_OFF_LINE_RECOVERY_TIME_OUT:
                Log.i(TAG, "OperateTimeoutCallback > 设备离线后，超时没有上线,开始关闭音视频");
                //由于离线30秒后服务器会清除通话状态，所以设备无需发送关闭指令
                exitAllLiveItem(IJT808ExtensionProtocol.EVENT_TYPE_CLOSE);
                break;

            case PS_CACHE_STATUS_CHECK:
                checkAVCLocalCacheStatus();
                break;

            default:
                break;
        }
    }

    @Override
    protected void initReceiveEventNotices() {
        registerHandleEvent(EventCommon.Code.NETWORK_CONNECTED, true);
        registerHandleEvent(EventCommon.Code.NETWORK_DISCONNECT, true);

        registerHandleEvent(EventRemoteControl.Code.LOCAL_DEVICE_STATUS, true);
        registerHandleEvent(EventRemoteControl.Code.AUDIO_VIDEO_PARAMETERS_APPLY_RESULT, true);
        registerHandleEvent(EventAudioVideoCommunication.Code.AUDIO_VIDEO_OPERATE_REQUEST, true);
    }

    @Override
    protected String openItem(Context context, RemoteEvent event) {
        int mediaType = EventAudioVideoOperateRequest.getMediaType(event);
        Intent item = new Intent();
        Log.i(TAG, "openItem >");
        switch (mediaType) {
            case IJT808ExtensionProtocol.MEDIA_TYPE_AUDIO:
                Log.i(TAG, "openItem > IJT808ExtensionProtocol.MEDIA_TYPE_AUDIO");
                item.setClass(context, MultiCaptureAudio.class);
                break;
            case IJT808ExtensionProtocol.MEDIA_TYPE_VIDEO:
                Log.i(TAG, "openItem > IJT808ExtensionProtocol.MEDIA_TYPE_VIDEO");
                item.setClass(context, MultiCaptureVideo.class);
                break;
            case IJT808ExtensionProtocol.MEDIA_TYPE_THERMAL:
                Log.i(TAG, "openItem > IJT808ExtensionProtocol.MEDIA_TYPE_INFEARED");
                item.setClass(context, MultiCaptureThermal.class);
                break;
            case IJT808ExtensionProtocol.MEDIA_TYPE_GROUP:
                Log.i(TAG, "openItem > IJT808ExtensionProtocol.MEDIA_TYPE_GROUP");
                mCollectingEndIdRemote = EventAudioVideoOperateRequest.getToken(event);
                item.setClass(context, MultiCaptureGroup.class);
                try {
                    Log.i(TAG, new StringBuilder("openItem > ")
                            .append(" mCollectingEndIdLocal = ").append(mCollectingEndIdLocal)
                            .append("\n mCollectingEndIdRemote = ").append(mCollectingEndIdRemote)
                            .toString());
                    //群组必要参数缺失
                    if (null == mCollectingEndIdLocal || null == mCollectingEndIdRemote) {
                        Log.w(TAG, "openItem > 群组必要参数缺失");
                        getStatusProcessBus().start(PS_OPEN_PARAMETER_PARSE_FAIL);
                        onResult(event, IJT808ExtensionProtocol.RESULT_FAIL_FAILURE_AUDIO_VIDEO_PARAMETER_PARSE_FAIL);
                    }
                    //未加入群组
                    if (IJT808ExtensionProtocol.TOKEN_NULL.equals(mCollectingEndIdRemote)) {
                        onResult(event, IJT808ExtensionProtocol.RESULT_FAIL_FAILURE_OTHER);
                        Log.i(TAG, "openItem > 设备未加入群组，请联系管理员处理");
                        return "设备未加入群组，请联系管理员处理";
                    }
                    if (!mCollectingEndIdLocal.equals(mCollectingEndIdRemote)) {
                        Log.i(TAG, "openItem > 即将打开群组[播放端]");
                        item.setClass(context, MultiRenderGroup.class);
                    } else {
                        Log.i(TAG, "openItem > 即将打开群组[采集端]");
                    }
                } catch (Exception e) {
                    Log.e(TAG, Log.getStackTraceString(e));
                    onResult(event, IJT808ExtensionProtocol.RESULT_FAIL_FAILURE_AUDIO_VIDEO_PARAMETER_PARSE_FAIL);
                    return getTitleByMediaType(mediaType, R.string.media_request_open_handle_fail);
                }
                break;
            default:
                item = null;
                Log.e(TAG, "openItem > process fail : mode config is invalid = " + mediaType);
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
    public void performOperate() {
        Log.i(TAG, new StringBuilder("performOperate > ")
                .append(" \n typeCode = ").append(mMediaTypeLocal)
                .append(" \n getHandlerStatus = ").append(getHandlerStatus())
                .append(" \n isOnLine = ").append(isLiveOnlineByType(-1))
                .append(" \n mCollectingEndIdLocal = ").append(mCollectingEndIdLocal)
                .append(" , mCollectingEndIdRemote = ").append(mCollectingEndIdRemote)
                .toString());

        //未支持功能
        if (IJT808ExtensionProtocol.MEDIA_TYPE_THERMAL == mMediaTypeLocal) {
            Log.i(TAG, "performOperate > 红外暂时不可用");
            play("红外暂时不可用");
            return;
        }

        if (isItInHandlerState(HS_OPEN_HANDLE_BE_EXECUTING)) {
            Log.i(TAG, "performOperate > 终端取消,申请成功正在打开的通话");
            exitAllLiveItem(IJT808ExtensionProtocol.EVENT_TYPE_LOCAL_DEVICE_INITIATE);
            return;
        }

        if (isItInHandlerState(HS_OPEN_REQUEST_BE_EXECUTING)) {
            Log.i(TAG, "performOperate > 终端取消,正在申请的通话");
            getStatusProcessBus().stop(PS_OPEN_REQUEST_BE_EXECUTING);
            setHandleStatus(HS_NORMAL);
            play(getTitleByMediaType(mMediaTypeLocal, R.string.media_request_cancel_request));
            return;
        }

        if (isLiveOnlineByType(-1)) {
            Log.i(TAG, "performOperate > 终端关闭，已经打开的通话," + getHandleStatusContent());
            exitAllLiveItem(IJT808ExtensionProtocol.EVENT_TYPE_LOCAL_DEVICE_INITIATE);
            return;
        }

        if (isItInHandlerState(HS_CLOSE_BE_EXECUTING)) {
            Log.i(TAG, "performOperate > 关闭中取消按键操作 ");
            return;
        }

        Log.i(TAG, "performOperate > 终端申请，开启通话 : " + getMediaTypeLocal());
        getStatusProcessBus().start(PS_OPEN_REQUEST_BE_EXECUTING);
    }

    @Override
    public boolean onReceiveEventNotice(RemoteEvent event) {
        String resultStr = null;
        switch (event.getCode()) {
            case EventCommon.Code.NETWORK_DISCONNECT:
                if (isItInHandlerState(HS_OPEN)) {//存在已打开通话
                    Log.i(TAG, "onReceiveEventNotice > 设备状态 > 设备已离线，存在已打开通话，开启超时关闭");
                    getStatusProcessBus().start(PS_DEVICE_OFF_LINE_RECOVERY_TIME_OUT);
                }
                break;

            case EventRemoteControl.Code.LOCAL_DEVICE_STATUS:
                if (isItInHandlerState(HS_OPEN)) {//存在已打开通话
                    if (EventLocalDeviceStatus.Status.ON_LINE == EventLocalDeviceStatus.getDeviceStatus(event)) {
                        recoverAllLiveItem();
                        break;
                    }
                }
                break;

            case EventRemoteControl.Code.AUDIO_VIDEO_PARAMETERS_APPLY_RESULT:
                //申请参数失败,或服务器异常
                if (!EventAudioVideoParametersApplyResult.isResultSuccess(event)) {
                    getStatusProcessBus().stop(PS_OPEN_REQUEST_BE_EXECUTING);
                    setHandleStatus(HS_NORMAL);

                    //平台未响应,超时处理
                    if (IJT808ExtensionProtocol.MEDIA_TYPE_GROUP == EventAudioVideoParametersApplyResult.getMediaType(event)) {
                        Log.e(TAG, "onReceiveEventNotice >  音视频请求异常，服务器可能下发指令异常");
                        play(getContext().getString(R.string.media_request_exit_group_exception));
                        break;
                    }
                    if (IJT808ExtensionProtocol.EVENT_TYPE_REMOTE_PLATFORM_NO_RESPONSE ==
                            EventAudioVideoParametersApplyResult.getEventType(event)) {
                        play(getTitleByMediaType(EventAudioVideoParametersApplyResult.getMediaType(event),
                                R.string.media_request_timeout_request));
                    }
                }
                break;

            case EventAudioVideoCommunication.Code.AUDIO_VIDEO_OPERATE_REQUEST:
                Log.i(TAG, "onReceiveEventNotice > 处理音视频请求");

                final int eventType = EventAudioVideoOperateRequest.getEventType(event);

                //平台拒绝
                if (IJT808ExtensionProtocol.EVENT_TYPE_REMOTE_PLATFORM_REFUSE == eventType) {
                    if (isItInHandlerState(HS_OPEN_REQUEST_BE_EXECUTING)) {
                        getStatusProcessBus().stop(PS_OPEN_REQUEST_BE_EXECUTING);
                        play(getTitleByMediaType(mMediaTypeLocal, R.string.media_request_open_rejected_title));
                        setHandleStatus(HS_NORMAL);
                        Log.i(TAG, "onReceiveEventNotice > 处理音视频申请 > 平台拒绝通话申请");
                    } else {
                        Log.i(TAG, "onReceiveEventNotice > 处理音视频申请 > 平台拒绝已取消的通话申请");
                    }
                    break;
                }

                //关闭通话
                if (IJT808ExtensionProtocol.EVENT_TYPE_CLOSE == eventType) {
                    if (HS_NORMAL == getHandlerStatus() && !isLiveOnlineByType(-1)) {
                        Log.i(TAG, "onReceiveEventNotice > 处理音视频请求 > 关闭通话 > 没有已打开通话，取消操作");
                        break;
                    }
                    if (HS_CLOSE_BE_EXECUTING == getHandlerStatus()) {
                        Log.d(TAG, "onReceiveEventNotice > 处理音视频请求 > 关闭通话 > 正在关闭，请稍等");
                        break;
                    }
                    if (getMediaTypeLocal() != EventAudioVideoOperateRequest.getMediaType(event)) {
                        onResult(event, IJT808ExtensionProtocol.RESULT_FAIL);
                        Log.i(TAG, "onReceiveEventNotice > 处理音视频请求 > 关闭通话 > 请求类型和打开类型不一致，取消操作");
                        break;
                    }
                    Log.i(TAG, "onReceiveEventNotice > 处理音视频请求 > 关闭通话");
                    getStatusProcessBus().start(PS_CLOSE_BE_EXECUTING);
                    break;
                }

                //开启通话
                if (!isLiveOnlineByType(-1)) {
                    getStatusProcessBus().start(PS_OPEN_HANDLE_BE_EXECUTING);

                    resultStr = openItem(getContext(), event);
                    if (null != resultStr) {
                        Log.e(TAG, "onReceiveEventNotice > process fail : " + resultStr);
                        dispatchEvent(new EventAudioVideoOperateResult().setResult(false));
                        play(resultStr);
                        setHandleStatus(HS_NORMAL);
                    } else {
                        mMediaTypeLocal = EventAudioVideoOperateRequest.getMediaType(event);
                    }
                    break;
                }

                //开启通话,存在已开启项
                final int mediaTypeRequest = EventAudioVideoOperateRequest.getMediaType(event);
                String medTypeContentRequest = getTitleByMediaType(mediaTypeRequest, -1);
                String medTypeContentLocal = getTitleByMediaType(getMediaTypeLocal(), -1);
                Log.w(TAG, new StringBuilder("onReceiveEventNotice > 处理音视频申请 > 存在打开项")
                        .append("\n").append(getHandlerStatus())
                        .append("\nrequest mediaType = ").append(medTypeContentRequest)
                        .append("\nlocal mediaType = ").append(medTypeContentLocal)
                        .toString());
                if (mediaTypeRequest == getMediaTypeLocal()) {
                    play(getTitleByMediaType(getMediaTypeLocal(), R.string.media_request_already_open_request));
                    onResult(event, IJT808ExtensionProtocol.RESULT_SUCCESS);
                } else {
                    play(getContext().getString(R.string.media_handle_fail_already_open_head, medTypeContentRequest)
                            + getContext().getString(R.string.media_handle_fail_already_open_tail, medTypeContentLocal));
                    onResult(event, IJT808ExtensionProtocol.RESULT_FAIL_FAILURE_OTHER);
                }
                break;

            default:
                return false;
        }
        return true;
    }
}
