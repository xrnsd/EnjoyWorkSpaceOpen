 package com.kuyou.openlive;

import android.app.Activity;
import android.app.IHelmetModuleOpenLiveCallback;
import android.content.Intent;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;

import com.kuyou.openlive.activities.AudioPushActivity;
import com.kuyou.openlive.activities.InfearedPushActivity;
import com.kuyou.openlive.activities.TakePhoto;
import com.kuyou.openlive.activities.VideoPushActivity;
import com.kuyou.openlive.activities.base.BaseLiveActivity;
import com.kuyou.openlive.utils.CameraLightControl;
import com.kuyou.openlive.utils.InfearedCameraControl;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import io.agora.advancedvideo.AgoraApplication;
import kuyou.common.ipc.RemoteEvent;
import kuyou.common.ku09.event.jt808.EventAudioVideoParametersApplyRequest;
import kuyou.common.ku09.event.jt808.base.ModuleEventJt808;
import kuyou.common.ku09.event.openlive.EventAudioRequest;
import kuyou.common.ku09.event.openlive.EventAudioResult;
import kuyou.common.ku09.event.openlive.EventFlashlightRequest;
import kuyou.common.ku09.event.openlive.EventFlashlightResult;
import kuyou.common.ku09.event.openlive.EventInfearedVideoRequest;
import kuyou.common.ku09.event.openlive.EventInfearedVideoResult;
import kuyou.common.ku09.event.openlive.EventLaserLightRequest;
import kuyou.common.ku09.event.openlive.EventLaserLightResult;
import kuyou.common.ku09.event.openlive.EventMediaRequest;
import kuyou.common.ku09.event.openlive.EventPhotoTakeRequest;
import kuyou.common.ku09.event.openlive.EventVideoRequest;
import kuyou.common.ku09.event.openlive.EventVideoResult;
import kuyou.common.ku09.event.openlive.base.ModuleEventOpenLive;
import kuyou.common.ku09.key.KeyConfig;
import kuyou.common.ku09.JT808ExtensionProtocol.IAudioVideo;

public class ModuleApplication extends AgoraApplication implements IAudioVideo {

    private static final String TAG = "com.kuyou.openlive > ModuleApplication";
    private static ModuleApplication sApplication;

    private int mLiveStatus = 0;
    private int mCameraMode = CameraMode.PHOTO;
    private Map<Integer, Activity> mItemListOnline = new HashMap<>();

    public static ModuleApplication getInstance() {
        return sApplication;
    }

    @Override
    protected void init() {
        super.init();
        sApplication = ModuleApplication.this;

        //重置USB
        InfearedCameraControl.close();
    }

    @Override
    protected void initCallBack() {
        super.initCallBack();
        mHelmetModuleManageServiceManager.registerHelmetModuleOpenLiveCallback(new IHelmetModuleOpenLiveCallback.Stub() {
            @Override
            public int getLiveStatus() throws RemoteException {
                return -1;
            }
        });
    }

    @Override
    protected String getApplicationName() {
        return "OpenLive";
    }

    @Override
    protected void onConnectionStateChanged(int state, int reason) {
        super.onConnectionStateChanged(state, reason);
        String reasonMsg = null;
        switch (reason) {
            case io.agora.rtc.Constants.CONNECTION_CHANGED_LEAVE_CHANNEL:
                reasonMsg = "离开频道";
                break;
            case io.agora.rtc.Constants.CONNECTION_CHANGED_JOIN_SUCCESS:
                reasonMsg = "成功加入频道";
                break;
            case io.agora.rtc.Constants.CONNECTION_CHANGED_INVALID_APP_ID:
                reasonMsg = "不是有效的 APP ID。请更换有效的 APP ID 重新加入频道";
                break;
            case io.agora.rtc.Constants.CONNECTION_CHANGED_INVALID_CHANNEL_NAME:
                reasonMsg = "不是有效的频道名。请更换有效的频道名重新加入频道";
                break;
            case io.agora.rtc.Constants.CONNECTION_CHANGED_INVALID_TOKEN:
                reasonMsg = "生成的 Token 无效。一般有以下原因：\n" +
                        "在控制台上启用了 App Certificate，但加入频道未使用 Token。当启用了 App Certificate，必须使用 Token\n" +
                        "在调用 joinChannel 加入频道时指定的 uid 与生成 Token 时传入的 uid 不一致";
                break;
            case io.agora.rtc.Constants.CONNECTION_CHANGED_TOKEN_EXPIRED: //当前使用的 Token 过期，不再有效，需要重新在你的服务端申请生成 Token
                reasonMsg = "当前使用的 Token 过期，不再有效，需要重新在你的服务端申请生成 Token";
                break;
            case io.agora.rtc.Constants.CONNECTION_CHANGED_REJECTED_BY_SERVER: //此用户被服务器禁止
                reasonMsg = "此用户被服务器禁止";
                break;
        }
    }


    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        if (!(activity instanceof BaseLiveActivity)) {
            return;
        }
        int typeCode = ((BaseLiveActivity) activity).getTypeCode();
        if (-1 != typeCode) {
            mItemListOnline.put(typeCode, activity);
        }
        Log.d(TAG, new StringBuilder(" onActivityCreated : ")
                .append(Activity.class.toString())
                .append("\n mLiveStatus = ").append(getLiveStatus())
                .toString());
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
    }

    private void exitAllLiveActivity() {
        Log.d(TAG, " exitAllLiveActivity ");
        synchronized (mItemListOnline) {
            Set<Integer> set = mItemListOnline.keySet();
            Iterator<Integer> it = set.iterator();
            Activity activity;
            while (it.hasNext()) {
                activity = mItemListOnline.get(it.next());
                Log.d(TAG, "exitAllLiveActivity > activity =" + activity);
                if (null != activity && !activity.isDestroyed()) {
                    if (activity instanceof InfearedPushActivity) {
                        InfearedPushActivity ipa = (InfearedPushActivity) activity;
                        ipa.onDestroyDelay(1500);
                        continue;
                    }
                    activity.finish();
                }
            }
            mItemListOnline.clear();
        }
    }

    private boolean isLiveByTypeCode(final int typeCode) {
        return mItemListOnline.containsKey(typeCode)
                && !mItemListOnline.get(typeCode).isDestroyed();
    }

    @Override
    public int getLiveStatus() {
        return mLiveStatus;
    }

    @Override
    protected void onKeyClick(int keyCode) {
        super.onKeyClick(keyCode);
        switch (keyCode) {
            case KeyConfig.CALL:
                boolean isOnLine = isLiveByTypeCode(MEDIA_TYPE_AUDIO);
                dispatchEvent(new EventAudioVideoParametersApplyRequest()
                        .setMediaType(IAudioVideo.MEDIA_TYPE_AUDIO)
                        .setEventType(EVENT_TYPE_LOCAL_INITIATED)
                        .setSwitch(!isOnLine)
                        .setRemote(true));
                if (isOnLine) {
                    exitAllLiveActivity();
                }
                break;
            case KeyConfig.FLASHLIGHT:
                dispatchEvent(new EventFlashlightRequest().setSwitch(
                        !CameraLightControl.getInstance(getApplicationContext())
                                .isFlashLightOn()));
                break;
            case KeyConfig.CAMERA:
                onKeyClickCamera();
                break;
            default:
                break;
        }
    }

    @Override
    protected void onKeyDoubleClick(int keyCode) {
        super.onKeyDoubleClick(keyCode);
        switch (keyCode) {
            case KeyConfig.CAMERA:
                if (mItemListOnline.size() >= 0) {
                    String content = null;
                    if (isLiveByTypeCode(MEDIA_TYPE_AUDIO))
                        content = "请先单击关闭语言";
                    if (isLiveByTypeCode(MEDIA_TYPE_VIDEO))
                        content = "请先单击关闭视频";
                    if (isLiveByTypeCode(MEDIA_TYPE_INFEARED))
                        content = "请先单击关闭红外";
                    if (null != content) {
                        Log.d(TAG, "onKeyDoubleClick > 存在已开启音视频项");
                        play(content);
                        return;
                    }
                }
                mCameraMode = mCameraMode >= CameraMode.INFRARED
                        ? mCameraMode = CameraMode.PHOTO
                        : mCameraMode + 1;
                switch (mCameraMode) {
                    case CameraMode.PHOTO:
                        play("拍照模式");
                        break;
                    case CameraMode.VIDEO:
                        play("视频模式");
                        break;
                    case CameraMode.INFRARED:
                        play("红外模式");
                        break;
                    default:
                        play("模式切换失败，请重新尝试");
                        break;
                }
                break;
            default:
                break;
        }
    }

    private void onKeyClickCamera() {
        switch (mCameraMode) {
            case CameraMode.PHOTO:
                dispatchEvent(new EventPhotoTakeRequest().setRemote(false));
                break;
            case CameraMode.VIDEO:
                boolean isOnLine = isLiveByTypeCode(MEDIA_TYPE_VIDEO);
                if (isOnLine) {
                    exitAllLiveActivity();
                } else {
                    play("普通视频打开中");
                }
                dispatchEvent(new EventAudioVideoParametersApplyRequest()
                        .setMediaType(IAudioVideo.MEDIA_TYPE_VIDEO)
                        .setEventType(EVENT_TYPE_LOCAL_INITIATED)
                        .setSwitch(!isOnLine)
                        .setRemote(true));
                break;
            case CameraMode.INFRARED:
                isOnLine = isLiveByTypeCode(MEDIA_TYPE_INFEARED);
                dispatchEvent(new EventAudioVideoParametersApplyRequest()
                        .setMediaType(IAudioVideo.MEDIA_TYPE_INFEARED)
                        .setEventType(EVENT_TYPE_LOCAL_INITIATED)
                        .setSwitch(!isOnLine)
                        .setRemote(true));
                if (isOnLine) {
                    exitAllLiveActivity();
                } else {
                    play("红外视频打开中");
                }
                break;
            default:
                break;
        }
    }

    private boolean openItem(RemoteEvent event, Class<?> cls) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.putExtra(KEY_TOKEN, EventMediaRequest.getToken(event));
        intent.putExtra(KEY_CHANNEL_ID, EventMediaRequest.getChannelId(event));
        intent.putExtra(KEY_EVENT_TYPE, EventMediaRequest.getEventType(event));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setClass(getApplicationContext(), cls);
        try {
            startActivity(intent);
        } catch (Exception e) {
            Log.e(TAG, Log.getStackTraceString(e));
            return false;
        }
        return true;
    }

    @Override
    public void onModuleEvent(RemoteEvent event) {
        super.onModuleEvent(event);
        switch (event.getCode()) {
            case ModuleEventOpenLive.Code.VIDEO_REQUEST:
                Log.d(TAG, "onModuleEvent > 处理视频申请");
                mCameraMode = CameraMode.VIDEO;
                if (EventVideoRequest.isCloseAction(event)) {
                    CameraLightControl.getInstance(getApplicationContext()).switchLaserLight(false);
                    exitAllLiveActivity();
                    break;
                }
                boolean result = openItem(event, VideoPushActivity.class);
                dispatchEvent(new EventVideoResult().setResult(result));
                if (!result) {
                    Log.e(TAG, "onModuleEvent > process fail : 无法打开视频");
                    play("无法打开视频");
                }
                break;

            case ModuleEventOpenLive.Code.INFEARED_VIDEO_REQUEST:
                mCameraMode = CameraMode.INFRARED;
                Log.d(TAG, "onModuleEvent > 处理红外申请");
                if (EventInfearedVideoRequest.isCloseAction(event)) {
                    exitAllLiveActivity();
                    reboot(200);
                    break;
                }
                InfearedCameraControl.open();
                result = openItem(event, InfearedPushActivity.class);
                dispatchEvent(new EventInfearedVideoResult().setResult(result));
                if (!result) {
                    Log.e(TAG, "onModuleEvent > process fail : 无法打开红外");
                    play("无法打开红外");
                }
                break;

            case ModuleEventOpenLive.Code.AUDIO_REQUEST:
                Log.d(TAG, "onModuleEvent > 处理语音申请");
                if (EventAudioRequest.isCloseAction(event)) {
                    exitAllLiveActivity();
                    break;
                }
                result = openItem(event, AudioPushActivity.class);
                dispatchEvent(new EventAudioResult().setResult(result));
                if (!result) {
                    Log.e(TAG, "onModuleEvent > process fail : 无法打开语言");
                    play("无法打开语言");
                }
                break;

            case ModuleEventOpenLive.Code.PHOTO_TAKE_REQUEST:
                mCameraMode = CameraMode.PHOTO;
                Log.d(TAG, "onModuleEvent > 处理拍照申请");
                play("正在为您拍照");
                TakePhoto.open(getApplicationContext(), event.getData());
                break;

            case ModuleEventOpenLive.Code.FLASHLIGHT_REQUEST:
                Log.d(TAG, "onModuleEvent > 处理手电筒申请");
                result = CameraLightControl.getInstance(getApplicationContext())
                        .switchFlashLight(EventFlashlightRequest.isSwitch(event));
                dispatchEvent(new EventFlashlightResult().setResult(result));
                if (!result) {
                    Log.e(TAG, "onModuleEvent > process fail : 无法打开手电筒");
                    play("无法打开手电筒");
                }
                break;

            case ModuleEventOpenLive.Code.LASER_LIGHT_REQUEST:
                Log.d(TAG, "onModuleEvent > 处理激光灯申请");
                result = CameraLightControl.getInstance(
                        getApplicationContext()).switchLaserLight(EventLaserLightRequest.isSwitch(event));
                dispatchEvent(new EventLaserLightResult().setResult(result));
                if (!result) {
                    Log.e(TAG, "onModuleEvent > process fail : 无法打开激光灯");
                    play("无法打开激光灯");
                }
                break;

            // ================== 返回结果处理 ========================
            case ModuleEventJt808.Code.AUDIO_AND_VIDEO_PARAMETERS_APPLY_RESULT:
                break;
            default:
                break;
        }
    }
    
    public static interface CameraMode {
        public final static int PHOTO = 0;
        public final static int VIDEO = 1;
        public final static int INFRARED = 2;
    }
}
