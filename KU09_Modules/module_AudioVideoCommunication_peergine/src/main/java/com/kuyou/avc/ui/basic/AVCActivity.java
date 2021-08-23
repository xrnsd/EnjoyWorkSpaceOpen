package com.kuyou.avc.ui.basic;

import android.util.Log;

import com.kuyou.avc.R;
import com.kuyou.avc.handler.basic.IAudioVideoRequestCallback;

import kuyou.common.ipc.RemoteEvent;
import kuyou.common.ku09.event.IDispatchEventCallback;
import kuyou.common.ku09.event.avc.EventAVCModuleLiveExit;
import kuyou.common.ku09.event.avc.EventAudioVideoOperateRequest;
import kuyou.common.ku09.event.avc.EventAudioVideoOperateResult;
import kuyou.common.ku09.protocol.IJT808ExtensionProtocol;
import kuyou.common.ku09.ui.BasePermissionsActivity;

/**
 * action :音视频通信[抽象]
 * <p>
 * remarks:  <br/>
 * author: wuguoxian <br/>
 * date: 21-7-23 <br/>
 * </p>
 */
public abstract class AVCActivity extends BasePermissionsActivity {

    protected static final String TAG = "com.kuyou.avc.ui.base > BaseAVCActivity";

    protected static final int RECREATE_COUNT_MAX = 3;
    protected static final String KEY_RECREATE_COUNT = "keyEventData.recreateCount";

    private PeergineConfig mConfig = null;
    private int mResult = -1;
    private IVideoCameraResultListener mVideoCameraResultListener;
    private IAudioVideoRequestCallback mAudioVideoRequestCallback;
    private IDispatchEventCallback mDispatchEventCallback;

    public static interface IVideoCameraResultListener {
        public void onScreenshotResult(String result);
    }

    protected void onScreenshotResult(String result) {
        if (null == mVideoCameraResultListener) {
            Log.e(TAG, "onScreenshot > process fail : mVideoCameraResultListener is null");
            return;
        }
        mVideoCameraResultListener.onScreenshotResult(result);
    }

    public abstract int getTypeCode();

    public int screenshot(RemoteEvent event, IVideoCameraResultListener listener) {
        mVideoCameraResultListener = listener;
        return -1024;
    }

    public void setVideoCameraResultListener(IVideoCameraResultListener videoCameraResultListener) {
        mVideoCameraResultListener = videoCameraResultListener;
    }

    protected IDispatchEventCallback getDispatchEventCallback() {
        return mDispatchEventCallback;
    }

    public void setDispatchEventCallback(IDispatchEventCallback dispatchEventCallback) {
        mDispatchEventCallback = dispatchEventCallback;
    }

    protected IAudioVideoRequestCallback getAudioVideoRequestCallback() {
        return mAudioVideoRequestCallback;
    }

    public void setAudioVideoRequestCallback(IAudioVideoRequestCallback audioVideoRequestCallback) {
        mAudioVideoRequestCallback = audioVideoRequestCallback;
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

        playTitleByResId(IJT808ExtensionProtocol.RESULT_SUCCESS == result ? R.string.media_request_open_success : R.string.media_request_open_handle_fail);

        dispatchEvent(new EventAudioVideoOperateResult()
                .setFlowId(EventAudioVideoOperateRequest.getFlowId(getIntent().getExtras()))
                .setToken(EventAudioVideoOperateRequest.getToken(getIntent().getExtras()))
                .setResultCode(result));
    }

    protected PeergineConfig getConfig() {
        if (null == mConfig) {

            String token = EventAudioVideoOperateRequest.getToken(getIntent().getExtras());
            String channel = EventAudioVideoOperateRequest.getChannelId(getIntent().getExtras());

//            if (null == token) {
//                token = "hzjy070607";
//                channel = "1111";
//            }

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

    public void exit() {
        try {
            playExit();
            finish();
            if (IJT808ExtensionProtocol.MEDIA_TYPE_VIDEO == getTypeCode()) {
                //ModuleApplication.getInstance().reboot(200);
            }
        } catch (Exception e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }
    }

    protected void onPeergineEvent(String sAct, String sData, String sRenID) {

    }

    protected int getResult() {
        return mResult;
    }

    protected void playExit() {
        if (IJT808ExtensionProtocol.RESULT_SUCCESS == getResult()) {
            playTitleByResId(R.string.media_request_close_success);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        setVideoCameraResultListener(null);
        setAudioVideoRequestCallback(null);
        setDispatchEventCallback(null);
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
