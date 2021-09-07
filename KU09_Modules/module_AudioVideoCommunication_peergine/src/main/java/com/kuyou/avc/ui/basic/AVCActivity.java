package com.kuyou.avc.ui.basic;

import android.util.Log;

import com.kuyou.avc.R;
import com.kuyou.avc.handler.basic.IAudioVideoRequestCallback;
import com.kuyou.avc.handler.photo.ITakePhotoByScreenshotResultCallback;

import kuyou.common.ipc.RemoteEvent;
import kuyou.common.ku09.event.common.basic.IEventBusDispatchCallback;
import kuyou.common.ku09.event.avc.EventAVCModuleLiveExit;
import kuyou.common.ku09.event.avc.EventAudioVideoOperateRequest;
import kuyou.common.ku09.event.avc.EventAudioVideoOperateResult;
import kuyou.common.ku09.protocol.IJT808ExtensionProtocol;
import kuyou.common.ku09.ui.BasicPermissionsHandlerActivity;

/**
 * action :音视频通信[抽象]
 * <p>
 * remarks:  <br/>
 * author: wuguoxian <br/>
 * date: 21-7-23 <br/>
 * </p>
 */
public abstract class AVCActivity extends BasicPermissionsHandlerActivity {

    protected static final String TAG = "com.kuyou.avc.ui.base > BaseAVCActivity";

    protected static final int RECREATE_COUNT_MAX = 3;
    protected static final String KEY_RECREATE_COUNT = "keyEventData.recreateCount";

    private PeergineConfig mConfig = null;
    private int mResult = -1;
    private ITakePhotoByScreenshotResultCallback mTakePhotoByScreenshotResultCallback;
    private IAudioVideoRequestCallback mAudioVideoRequestCallback;
    private IEventBusDispatchCallback mDispatchEventCallback;
    private boolean isLoginSuccess = false, isRecovering = false;

    public abstract int getTypeCode();

    public void setDispatchEventCallback(IEventBusDispatchCallback dispatchEventCallback) {
        mDispatchEventCallback = dispatchEventCallback;
    }

    protected IAudioVideoRequestCallback getAudioVideoRequestCallback() {
        return mAudioVideoRequestCallback;
    }

    public void setAudioVideoRequestCallback(IAudioVideoRequestCallback audioVideoRequestCallback) {
        mAudioVideoRequestCallback = audioVideoRequestCallback;
    }

    public int screenshot(ITakePhotoByScreenshotResultCallback callback) {
        mTakePhotoByScreenshotResultCallback = callback;
        return -1024;
    }

    protected IEventBusDispatchCallback getDispatchEventCallback() {
        return mDispatchEventCallback;
    }

    protected ITakePhotoByScreenshotResultCallback getTakePhotoByScreenshotResultCallback() {
        return mTakePhotoByScreenshotResultCallback;
    }

    protected void onTakePhotoResult(boolean result, String info) {
        if (null == getTakePhotoByScreenshotResultCallback()) {
            Log.e(TAG, "onTakePhotoResult > process fail : mTakePhotoByScreenshotResultCallback is null");
            return;
        }
        getTakePhotoByScreenshotResultCallback()
                .onTakePhotoResult(result, info, getTakePhotoByScreenshotResultCallback().getEventData());
    }

    @Override
    protected void dispatchEvent(RemoteEvent event) {
        if (null == getDispatchEventCallback()) {
            Log.e(TAG, "dispatchEvent > process fail : mDispatchEventCallBack is null");
            Log.e(TAG, "dispatchEvent > process fail : event send fail : " + event.getCode());
            return;
        }
        getDispatchEventCallback().dispatchEvent(event);
    }

    protected void onResult(int result) {
        Log.w(TAG, "onResult > result = " + result);
        mResult = result;

        int recreateCount = getIntent().getIntExtra(KEY_RECREATE_COUNT, 0);
        if (IJT808ExtensionProtocol.RESULT_FAIL_FAILURE_AUDIO_VIDEO_SERVER_EXCEPTION == result) {
            if (recreateCount <= RECREATE_COUNT_MAX) {
                exit();
                recreateCount += 1;
                getIntent().putExtra(KEY_RECREATE_COUNT, recreateCount);
                Log.e(TAG, new StringBuilder("onResult > 模块视频服务异常，第").append(recreateCount).append("次重新打开").toString());
                recreate();
                return;
            }
            Log.e(TAG, "onResult > 模块视频服务异常，重新打开多次无效，放弃快速重置");
            dispatchEvent(new EventAVCModuleLiveExit()
                    .setExitType(EventAVCModuleLiveExit.ExitType.REBOOT)
                    .setRemote(false));
            return;
        }

        if (!isRecovering && !isLoginSuccess) {
            playTitleByResId(IJT808ExtensionProtocol.RESULT_SUCCESS == result ? R.string.media_request_open_success : R.string.media_request_open_handle_fail);
        }

        dispatchEvent(new EventAudioVideoOperateResult()
                .setFlowNumber(EventAudioVideoOperateRequest.getFlowNumber(getIntent().getExtras()))
                .setToken(EventAudioVideoOperateRequest.getToken(getIntent().getExtras()))
                .setResultCode(result));
    }

    protected PeergineConfig getConfig() {
        if (null == mConfig) {

            String token = EventAudioVideoOperateRequest.getToken(getIntent().getExtras());
            String channel = EventAudioVideoOperateRequest.getChannelId(getIntent().getExtras());

            if (null == token) {
                Log.w(TAG, "getConfig > token is null");
                onResult(IJT808ExtensionProtocol.RESULT_FAIL_FAILURE_AUDIO_VIDEO_PARAMETER_PARSE_FAIL);
                exit();
                return null;
            }

            if (null == channel) {
                Log.w(TAG, "getConfig > channel is null");
                onResult(IJT808ExtensionProtocol.RESULT_FAIL_FAILURE_AUDIO_VIDEO_PARAMETER_PARSE_FAIL);
                exit();
                return null;
            }

            mConfig = new PeergineConfig()
                    .setServerAddress("connect.peergine.com:7781")
                    .setDevCollectingEndId(token);
        }
        Log.d(TAG, "getConfig > " + mConfig.toString());
        return mConfig;
    }

    protected boolean onPeergineEvent(String sAct, String sData, String sRenID) {
        if (sAct.equals("Login")) {
            if (sData.equals("0")) {
                onResult(IJT808ExtensionProtocol.RESULT_SUCCESS);
                isLoginSuccess = true;
            } else {
                if ("8".equals(sData)) {//用户无效，也有可能是授权到期
                    onResult(IJT808ExtensionProtocol.RESULT_FAIL_FAILURE_AUDIO_VIDEO_PARAMETER_PARSE_FAIL);
                }
                if ("12".equals(sData)) {//操作超时，可能是网络连接不稳定
                    if (!isLoginSuccess) {//false表示没登录成功过，不是重新登录
                        onResult(IJT808ExtensionProtocol.RESULT_FAIL_FAILURE_OTHER);
                    }
                } else {
                    onResult(IJT808ExtensionProtocol.RESULT_FAIL_FAILURE_AUDIO_VIDEO_SERVER_EXCEPTION);
                }
            }
            return true;
        }
        return false;
    }

    protected int getResult() {
        return mResult;
    }

    @Override
    protected void onDestroy() {
        exit();
        super.onDestroy();
    }

    public void recover() {
        isLoginSuccess = false;
        isRecovering = true;
    }

    protected void exit() {
        mTakePhotoByScreenshotResultCallback = null;
        try {
            playExit();
            setAudioVideoRequestCallback(null);
            setDispatchEventCallback(null);
            if (IJT808ExtensionProtocol.MEDIA_TYPE_VIDEO == getTypeCode()) {
                //ModuleApplication.getInstance().rebootModule(200);
            }
        } catch (Exception e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }
    }

    protected void playExit() {
        if (IJT808ExtensionProtocol.RESULT_SUCCESS == getResult()) {
            playTitleByResId(R.string.media_request_close_success);
        }
    }

    protected void playTitleByResId(int resId) {
        playTitleByMediaTypeAndResId(getTypeCode(), resId);
    }

    protected void playTitleByMediaTypeAndResId(int typeCode, int resId) {
        if (null == getAudioVideoRequestCallback()) {
            Log.w(TAG, "playTitleByMediaTypeAndResId > process fail : AudioVideoRequestCallback is null");
            Log.w(TAG, "playTitleByMediaTypeAndResId > play fail : content = " + getString(resId));
            return;
        }
        String content = getAudioVideoRequestCallback().getTitleByMediaType(typeCode, resId);
        play(content);
    }
}
