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

        //??????USB
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
                reasonMsg = "????????????";
                break;
            case io.agora.rtc.Constants.CONNECTION_CHANGED_JOIN_SUCCESS:
                reasonMsg = "??????????????????";
                break;
            case io.agora.rtc.Constants.CONNECTION_CHANGED_INVALID_APP_ID:
                reasonMsg = "??????????????? APP ID????????????????????? APP ID ??????????????????";
                break;
            case io.agora.rtc.Constants.CONNECTION_CHANGED_INVALID_CHANNEL_NAME:
                reasonMsg = "????????????????????????????????????????????????????????????????????????";
                break;
            case io.agora.rtc.Constants.CONNECTION_CHANGED_INVALID_TOKEN:
                reasonMsg = "????????? Token ?????????????????????????????????\n" +
                        "???????????????????????? App Certificate??????????????????????????? Token??????????????? App Certificate??????????????? Token\n" +
                        "????????? joinChannel ???????????????????????? uid ????????? Token ???????????? uid ?????????";
                break;
            case io.agora.rtc.Constants.CONNECTION_CHANGED_TOKEN_EXPIRED: //??????????????? Token ?????????????????????????????????????????????????????????????????? Token
                reasonMsg = "??????????????? Token ?????????????????????????????????????????????????????????????????? Token";
                break;
            case io.agora.rtc.Constants.CONNECTION_CHANGED_REJECTED_BY_SERVER: //???????????????????????????
                reasonMsg = "???????????????????????????";
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
                        content = "????????????????????????";
                    if (isLiveByTypeCode(MEDIA_TYPE_VIDEO))
                        content = "????????????????????????";
                    if (isLiveByTypeCode(MEDIA_TYPE_INFEARED))
                        content = "????????????????????????";
                    if (null != content) {
                        Log.d(TAG, "onKeyDoubleClick > ???????????????????????????");
                        play(content);
                        return;
                    }
                }
                mCameraMode = mCameraMode >= CameraMode.INFRARED
                        ? mCameraMode = CameraMode.PHOTO
                        : mCameraMode + 1;
                switch (mCameraMode) {
                    case CameraMode.PHOTO:
                        play("????????????");
                        break;
                    case CameraMode.VIDEO:
                        play("????????????");
                        break;
                    case CameraMode.INFRARED:
                        play("????????????");
                        break;
                    default:
                        play("????????????????????????????????????");
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
                    play("?????????????????????");
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
                    play("?????????????????????");
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
                Log.d(TAG, "onModuleEvent > ??????????????????");
                mCameraMode = CameraMode.VIDEO;
                if (EventVideoRequest.isCloseAction(event)) {
                    CameraLightControl.getInstance(getApplicationContext()).switchLaserLight(false);
                    exitAllLiveActivity();
                    break;
                }
                boolean result = openItem(event, VideoPushActivity.class);
                dispatchEvent(new EventVideoResult().setResult(result));
                if (!result) {
                    Log.e(TAG, "onModuleEvent > process fail : ??????????????????");
                    play("??????????????????");
                }
                break;

            case ModuleEventOpenLive.Code.INFEARED_VIDEO_REQUEST:
                mCameraMode = CameraMode.INFRARED;
                Log.d(TAG, "onModuleEvent > ??????????????????");
                if (EventInfearedVideoRequest.isCloseAction(event)) {
                    exitAllLiveActivity();
                    reboot(200);
                    break;
                }
                InfearedCameraControl.open();
                result = openItem(event, InfearedPushActivity.class);
                dispatchEvent(new EventInfearedVideoResult().setResult(result));
                if (!result) {
                    Log.e(TAG, "onModuleEvent > process fail : ??????????????????");
                    play("??????????????????");
                }
                break;

            case ModuleEventOpenLive.Code.AUDIO_REQUEST:
                Log.d(TAG, "onModuleEvent > ??????????????????");
                if (EventAudioRequest.isCloseAction(event)) {
                    exitAllLiveActivity();
                    break;
                }
                result = openItem(event, AudioPushActivity.class);
                dispatchEvent(new EventAudioResult().setResult(result));
                if (!result) {
                    Log.e(TAG, "onModuleEvent > process fail : ??????????????????");
                    play("??????????????????");
                }
                break;

            case ModuleEventOpenLive.Code.PHOTO_TAKE_REQUEST:
                mCameraMode = CameraMode.PHOTO;
                Log.d(TAG, "onModuleEvent > ??????????????????");
                play("??????????????????");
                TakePhoto.open(getApplicationContext(), event.getData());
                break;

            case ModuleEventOpenLive.Code.FLASHLIGHT_REQUEST:
                Log.d(TAG, "onModuleEvent > ?????????????????????");
                result = CameraLightControl.getInstance(getApplicationContext())
                        .switchFlashLight(EventFlashlightRequest.isSwitch(event));
                dispatchEvent(new EventFlashlightResult().setResult(result));
                if (!result) {
                    Log.e(TAG, "onModuleEvent > process fail : ?????????????????????");
                    play("?????????????????????");
                }
                break;

            case ModuleEventOpenLive.Code.LASER_LIGHT_REQUEST:
                Log.d(TAG, "onModuleEvent > ?????????????????????");
                result = CameraLightControl.getInstance(
                        getApplicationContext()).switchLaserLight(EventLaserLightRequest.isSwitch(event));
                dispatchEvent(new EventLaserLightResult().setResult(result));
                if (!result) {
                    Log.e(TAG, "onModuleEvent > process fail : ?????????????????????");
                    play("?????????????????????");
                }
                break;

            // ================== ?????????????????? ========================
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
