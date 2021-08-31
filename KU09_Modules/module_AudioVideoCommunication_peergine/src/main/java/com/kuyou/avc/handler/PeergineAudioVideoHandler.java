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
import com.kuyou.avc.handler.ringtone.LocalRingtoneHandler;
import com.kuyou.avc.handler.ringtone.RingtoneHandler;
import com.kuyou.avc.handler.thermal.ThermalCameraControl;
import com.kuyou.avc.ui.MultiCaptureAudio;
import com.kuyou.avc.ui.MultiCaptureGroup;
import com.kuyou.avc.ui.MultiCaptureThermal;
import com.kuyou.avc.ui.MultiCaptureVideo;
import com.kuyou.avc.ui.MultiRenderGroup;
import com.kuyou.avc.ui.basic.AVCActivity;

import java.util.Iterator;
import java.util.Set;

import kuyou.common.ipc.RemoteEvent;
import kuyou.common.ku09.basic.IStatusBus;
import kuyou.common.ku09.basic.StatusBusProcessCallback;
import kuyou.common.ku09.config.IDeviceConfig;
import kuyou.common.ku09.event.avc.EventAudioVideoOperateRequest;
import kuyou.common.ku09.event.avc.EventAudioVideoOperateResult;
import kuyou.common.ku09.event.avc.basic.EventAudioVideoCommunication;
import kuyou.common.ku09.event.rc.EventAudioVideoParametersApplyRequest;
import kuyou.common.ku09.event.rc.EventAudioVideoParametersApplyResult;
import kuyou.common.ku09.event.rc.EventLocalDeviceStatus;
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
public class PeergineAudioVideoHandler extends AudioVideoRequestResultHandler {

    protected final String TAG = "com.kuyou.avc.handle > PeergineAudioVideoHandler";

    private final String KEY_HANDLER_STATUS = "HandlerStatus";
    private final String KEY_MEDIA_TYPE = "MediaType";
    private final String KEY_GROUP_OWNER = "GroupOwner";

    private int mMsgFlagOpenRequestBeExecuting = -1;
    private int mMsgFlagOpenRequestBeExecutingTimeOut = -1;
    private int mMsgFlagOpenHandleBeExecuting = -1;
    private int mMsgFlagCloseBeExecuting = -1;
    private int mMsgFlagDeviceOffLineTimeOut = -1;

    //用于群组通话，区分设备是否为采集端,本地和远程分配的相同就是，不同不是
    private String mCollectingEndIdLocal = null,//本地采集端ID
            mCollectingEndIdRemote = null;//远程分配的采集端ID

    private RingtoneHandler mRingtoneHandler;

    private SharedPreferences mSPHandleStatus;

    private int mMediaType = IJT808ExtensionProtocol.MEDIA_TYPE_DEFAULT;

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
            //Log.i(TAG, "syncPlatformAudioVideoCommunicationStatus > 没有通话不正常");
            return;
        }

        Log.i(TAG, new StringBuilder("syncPlatformAudioVideoCommunicationStatus > 确认通话是否正常退出")
                .append("\nhandleStatusCache = ").append(handleStatusCache)
                .append("\nmediaTypCache = ").append(mediaTypCache)
                .toString());
        switch (mediaTypCache) {
            case IJT808ExtensionProtocol.MEDIA_TYPE_GROUP:
                if (!getCacheVal(getContext(), KEY_GROUP_OWNER, false)) {
                    Log.i(TAG, "syncPlatformAudioVideoCommunicationStatus > 群组未正常退出,非群主无需处理 ");
                    saveStatus2Cache(getContext());//重置本地保存的状态
                    return;
                }
                Log.i(TAG, "syncPlatformAudioVideoCommunicationStatus > 群组不正常 ");
                break;
            case IJT808ExtensionProtocol.MEDIA_TYPE_VIDEO:
                Log.i(TAG, "syncPlatformAudioVideoCommunicationStatus > 视频不正常 ");
                if (HS_OPEN == handleStatusCache) {
                    handleStatusCache = HS_CLOSE_BE_EXECUTING;
                    //return;
                }
                break;
            case IJT808ExtensionProtocol.MEDIA_TYPE_AUDIO:
                Log.i(TAG, "syncPlatformAudioVideoCommunicationStatus > 语音不正常");
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
                getStatusBus().start(mMsgFlagOpenRequestBeExecuting);
                break;
            default:
                mMediaType = mediaTypCache;
                getStatusBus().start(mMsgFlagCloseBeExecuting);
                break;
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

    protected void onResult(RemoteEvent event, int result) {
        dispatchEvent(new EventAudioVideoOperateResult()
                .setFlowNumber(EventAudioVideoOperateRequest.getFlowNumber(event))
                .setToken(EventAudioVideoOperateRequest.getToken(event))
                .setResultCode(result));
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
            getStatusBus().stop(mMsgFlagDeviceOffLineTimeOut);
            Set<Integer> set = mItemListOnline.keySet();
            Iterator<Integer> it = set.iterator();
            AVCActivity activity;
            while (it.hasNext()) {
                final int typeCode = it.next();
                activity = mItemListOnline.get(typeCode);
                if (null == activity || activity.isDestroyed()) {
                    continue;
                }
                activity.recreate();
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
                && IJT808ExtensionProtocol.MEDIA_TYPE_GROUP == getMediaType()
                && isGroupOwner()) {
            Log.i(TAG, "getOperateAndTimeoutCallback > 通知平台群组即将关闭");
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
            mMediaType = mMediaType >= IJT808ExtensionProtocol.MEDIA_TYPE_GROUP
                    ? mMediaType = IJT808ExtensionProtocol.MEDIA_TYPE_AUDIO
                    : mMediaType + 1;
            content = getTitleByMediaType(mMediaType, R.string.key_switch_mode_success_title);
        } else {
            Log.i(TAG, "switchMediaType > " + getHandleStatusContent());
            content = getTitleByMediaType(mMediaType, R.string.key_switch_mode_cancel_title);
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
    protected void initHandleEventCodeList() {
        registerHandleEvent(EventRemoteControl.Code.LOCAL_DEVICE_STATUS, true);
        registerHandleEvent(EventRemoteControl.Code.AUDIO_VIDEO_PARAMETERS_APPLY_RESULT, true);
        registerHandleEvent(EventAudioVideoCommunication.Code.AUDIO_VIDEO_OPERATE_REQUEST, true);
    }

    @Override
    public void setStatusBusImpl(IStatusBus handler) {
        super.setStatusBusImpl(handler);

        //HS_OPEN_REQUEST_BE_EXECUTING
        mMsgFlagOpenRequestBeExecuting = handler.registerStatusBusProcessCallback(
                new StatusBusProcessCallback(false, 0, Looper.getMainLooper()) {
                    @Override
                    public void onReceiveMessage(boolean isRemove) {
                        if (isRemove) {
                            PeergineAudioVideoHandler.this.getStatusBus().stop(
                                    PeergineAudioVideoHandler.this.mMsgFlagOpenRequestBeExecutingTimeOut);
                            return;
                        }
                        PeergineAudioVideoHandler.this.getStatusBus().start(
                                PeergineAudioVideoHandler.this.mMsgFlagOpenRequestBeExecutingTimeOut);
                        Log.i(TAG, "getOperateAndTimeoutCallback > 向平台发出音视频开启请求 > ");
                        PeergineAudioVideoHandler.this.setHandleStatus(HS_OPEN_REQUEST_BE_EXECUTING);
                        PeergineAudioVideoHandler.this.dispatchEvent(new EventAudioVideoParametersApplyRequest()
                                .setMediaType(PeergineAudioVideoHandler.this.getMediaType())
                                .setPlatformType(IJT808ExtensionProtocol.PLATFORM_TYPE_PEERGIN)
                                .setEventType(IJT808ExtensionProtocol.EVENT_TYPE_LOCAL_DEVICE_INITIATE)
                                .setRemote(true));
                    }
                });

        //HS_OPEN_REQUEST_BE_EXECUTING_TIME_OUT
        mMsgFlagOpenRequestBeExecutingTimeOut = handler.registerStatusBusProcessCallback(
                new StatusBusProcessCallback(false, 15000, Looper.getMainLooper()) {
                    @Override
                    public void onReceiveMessage(boolean isRemove) {
                        Log.i(TAG, "getOperateAndTimeoutCallback > 向平台发出音视频开启请求 > 失败：未响应");
                        PeergineAudioVideoHandler.this.onModuleEvent(new EventAudioVideoParametersApplyResult()
                                .setResult(false)
                                .setMediaType(mMediaType)
                                .setEventType(IJT808ExtensionProtocol.EVENT_TYPE_REMOTE_PLATFORM_NO_RESPONSE));
                    }
                });

        //HS_OPEN_HANDLE_BE_EXECUTING_TIME_OUT
        mMsgFlagOpenHandleBeExecuting = handler.registerStatusBusProcessCallback(
                new StatusBusProcessCallback(false, 0, Looper.getMainLooper()) {
                    @Override
                    public void onReceiveMessage(boolean isRemove) {
                        PeergineAudioVideoHandler.this.getStatusBus().stop(
                                PeergineAudioVideoHandler.this.mMsgFlagOpenRequestBeExecuting);
                        PeergineAudioVideoHandler.this.setHandleStatus(HS_OPEN_HANDLE_BE_EXECUTING);

                    }
                });

        //HS_CLOSE_BE_EXECUTING
        mMsgFlagCloseBeExecuting = handler.registerStatusBusProcessCallback(
                new StatusBusProcessCallback(false, 0, Looper.getMainLooper()) {
                    @Override
                    public void onReceiveMessage(boolean isRemove) {
                        Log.i(TAG, "getOperateAndTimeoutCallback > 开始关闭音视频 > ");
                        if (!PeergineAudioVideoHandler.this.isLiveOnlineByType(-1)) {
                            PeergineAudioVideoHandler.this.setHandleStatus(HS_NORMAL);
                        } else {
                            PeergineAudioVideoHandler.this.exitAllLiveItem(IJT808ExtensionProtocol.EVENT_TYPE_LOCAL_DEVICE_INITIATE);
                        }
                    }
                });

        //HS_DEVICE_OFF_LINE_TIME_OUT
        mMsgFlagDeviceOffLineTimeOut = handler.registerStatusBusProcessCallback(
                new StatusBusProcessCallback(false, 30 * 1000, Looper.getMainLooper()) {
                    @Override
                    public void onReceiveMessage(boolean isRemove) {
                        Log.i(TAG, "OperateTimeoutCallback > 设备离线后，超时没有上线,开始关闭音视频");
                        //由于离线30秒后服务器会清除通话状态，所以设备无需发送关闭指令
                        PeergineAudioVideoHandler.this.exitAllLiveItem(IJT808ExtensionProtocol.EVENT_TYPE_CLOSE);
                    }
                });
    }

    @Override
    protected String openItem(Context context, RemoteEvent event) {
        int mediaType = EventAudioVideoOperateRequest.getMediaType(event);
        Intent item = new Intent();
        Log.i(TAG, "openItem >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> ");
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
                        throw new Exception(new StringBuilder("mCollectingEndId is invalid :")
                                .append(" mCollectingEndIdLocal = ").append(mCollectingEndIdLocal)
                                .append("\n mCollectingEndIdRemote = ").append(mCollectingEndIdRemote)
                                .toString());
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
                .append(" \n typeCode = ").append(mMediaType)
                .append(" \n getHandlerStatus = ").append(getHandlerStatus())
                .append(" \n isOnLine = ").append(isLiveOnlineByType(-1))
                .append(" \n mCollectingEndIdLocal = ").append(mCollectingEndIdLocal)
                .append(" , mCollectingEndIdRemote = ").append(mCollectingEndIdRemote)
                .toString());

        //未支持功能
        if (IJT808ExtensionProtocol.MEDIA_TYPE_THERMAL == mMediaType) {
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
            getStatusBus().stop(mMsgFlagOpenRequestBeExecuting);
            setHandleStatus(HS_NORMAL);
            play(getTitleByMediaType(mMediaType, R.string.media_request_cancel_request));
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

        Log.i(TAG, "performOperate > 终端申请，开启通话 : " + getMediaType());
        getStatusBus().start(mMsgFlagOpenRequestBeExecuting);
    }

    @Override
    public boolean onModuleEvent(RemoteEvent event) {
        String resultStr = null;
        switch (event.getCode()) {
            case EventRemoteControl.Code.LOCAL_DEVICE_STATUS:
                final int status = EventLocalDeviceStatus.getDeviceStatus(event);

                if (isItInHandlerState(HS_OPEN)) {//存在已打开通话
                    if (EventLocalDeviceStatus.Status.OFF_LINE == status) {
                        Log.i(TAG, "onModuleEvent > 设备状态 > 设备已离线，存在已打开通话，开始通话状态恢复超时");
                        getStatusBus().start(mMsgFlagDeviceOffLineTimeOut);
                        break;
                    }

                    if (EventLocalDeviceStatus.Status.ON_LINE == status) {
                        Log.i(TAG, "onModuleEvent > 设备状态 > 设备已上线");
                        recoverAllLiveItem();
                        break;
                    }
                }
                break;

            case EventRemoteControl.Code.AUDIO_VIDEO_PARAMETERS_APPLY_RESULT:
                //申请参数失败,或服务器异常
                if (!EventAudioVideoParametersApplyResult.isResultSuccess(event)) {
                    getStatusBus().stop(mMsgFlagOpenRequestBeExecuting);
                    setHandleStatus(HS_NORMAL);

                    //平台未响应,超时处理
                    if (IJT808ExtensionProtocol.MEDIA_TYPE_GROUP == EventAudioVideoParametersApplyResult.getMediaType(event)) {
                        Log.e(TAG, "onModuleEvent >  音视频请求异常，服务器可能下发指令异常");
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
                Log.i(TAG, "onModuleEvent > 处理音视频请求");

                final int eventType = EventAudioVideoOperateRequest.getEventType(event);

                //平台拒绝
                if (IJT808ExtensionProtocol.EVENT_TYPE_REMOTE_PLATFORM_REFUSE == eventType) {
                    if (isItInHandlerState(HS_OPEN_REQUEST_BE_EXECUTING)) {
                        getStatusBus().stop(mMsgFlagOpenRequestBeExecuting);
                        play(getTitleByMediaType(mMediaType, R.string.media_request_open_rejected_title));
                        setHandleStatus(HS_NORMAL);
                        Log.i(TAG, "onModuleEvent > 处理音视频申请 > 平台拒绝通话申请");
                    } else {
                        Log.i(TAG, "onModuleEvent > 处理音视频申请 > 平台拒绝已取消的通话申请");
                    }
                    break;
                }

                //关闭通话
                if (IJT808ExtensionProtocol.EVENT_TYPE_CLOSE == eventType) {
//                    if (HS_NORMAL == getHandlerStatus()) {
//                        return true;
//                    }
//                    if (HS_CLOSE_BE_EXECUTING == getHandlerStatus()
//                            || HS_CLOSE_BE_EXECUTING == getHandleStatusCache(getContext())) {
//                        getStatusGuardHandler().stop(HS_CLOSE_BE_EXECUTING);
//                    } else {
//                        exitAllLiveItem(IJT808ExtensionProtocol.EVENT_TYPE_REMOTE_PLATFORM_INITIATE);
//                    }
//                    setHandleStatus(HS_NORMAL);
                    if (HS_NORMAL == getHandlerStatus() && !isLiveOnlineByType(-1)) {
                        Log.i(TAG, "onModuleEvent > 处理音视频请求 > 关闭通话 > 没有已打开通话，取消操作");
                        break;
                    }
                    if (HS_CLOSE_BE_EXECUTING == getHandlerStatus()) {
                        Log.d(TAG, "onModuleEvent > 处理音视频请求 > 关闭通话 > 正在关闭，请稍等");
                        break;
                    }
                    Log.i(TAG, "onModuleEvent > 处理音视频请求 > 关闭通话");
                    getStatusBus().start(mMsgFlagCloseBeExecuting);
                    break;
                }

                //开启通话
                if (!isLiveOnlineByType(-1)) {
                    getStatusBus().start(mMsgFlagOpenHandleBeExecuting);

                    resultStr = openItem(getContext(), event);
                    if (null != resultStr) {
                        Log.e(TAG, "onModuleEvent > process fail : " + resultStr);
                        dispatchEvent(new EventAudioVideoOperateResult().setResult(false));
                        play(resultStr);
                        setHandleStatus(HS_NORMAL);
                    } else {
                        mMediaType = EventAudioVideoOperateRequest.getMediaType(event);
                    }
                    break;
                }

                //开启通话,存在已开启项
                final int mediaTypeRequest = EventAudioVideoOperateRequest.getMediaType(event);
                String medTypeContentRequest = getTitleByMediaType(mediaTypeRequest, -1);
                String medTypeContentLocal = getTitleByMediaType(getMediaType(), -1);
                Log.w(TAG, new StringBuilder("onModuleEvent > 处理音视频申请 > 存在打开项")
                        .append("\n").append(getHandlerStatus())
                        .append("\nrequest mediaType = ").append(medTypeContentRequest)
                        .append("\nlocal mediaType = ").append(medTypeContentLocal)
                        .toString());
                if (mediaTypeRequest == getMediaType()) {
                    play(getTitleByMediaType(getMediaType(), R.string.media_request_already_open_request));
                } else {
                    play(getContext().getString(R.string.media_handle_fail_already_open, medTypeContentRequest, medTypeContentLocal));
                    onResult(event, IJT808ExtensionProtocol.RESULT_FAIL_FAILURE_OTHER);
                }
                break;
            default:
                return false;
        }
        return true;
    }
}
