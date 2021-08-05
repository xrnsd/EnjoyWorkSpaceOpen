package com.kuyou.avc.ui.base;

import android.util.Log;

import com.kuyou.avc.ModuleApplication;
import com.kuyou.avc.R;
import com.kuyou.avc.handler.PeergineAudioVideoRequestResultHandler;
import com.kuyou.avc.ui.custom.PeergineConfig;

import kuyou.common.ipc.RemoteEvent;
import kuyou.common.ku09.event.avc.EventAudioVideoOperateRequest;
import kuyou.common.ku09.event.avc.EventAudioVideoOperateResult;
import kuyou.common.ku09.event.avc.base.IAudioVideo;
import kuyou.common.ku09.ui.BasePermissionsActivity;

/**
 * action :
 * <p>
 * remarks:  <br/>
 * author: wuguoxian <br/>
 * date: 21-7-23 <br/>
 * </p>
 */
public abstract class BaseAVCActivity extends BasePermissionsActivity {

    protected static final String TAG = "com.kuyou.avc.ui.base > BaseAVCActivity";

    protected static final int RECREATE_COUNT_MAX = 3;
    protected static final String KEY_RECREATE_COUNT = "keyEventData.recreateCount";

    private PeergineConfig mConfig = null;
    private int mResult = -1;

    public abstract int getTypeCode();

    @Override
    protected void dispatchEvent(RemoteEvent event) {
        if (null == ModuleApplication.getInstance()) {
            Log.e(TAG, "dispatchEvent > process fail : ModuleApplication is null");
            Log.e(TAG, "dispatchEvent > process fail : event send fail : " + event.getCode());
            return;
        }
        ModuleApplication.getInstance().dispatchEvent(event);
    }

    protected void onResult(int result) {
        Log.e(TAG, "onResult > result = " + result);
        mResult = result;

        int recreateCount = getIntent().getIntExtra(KEY_RECREATE_COUNT, 0);
        if (IAudioVideo.RESULT_FAIL_FAILURE_AUDIO_VIDEO_SERVER_EXCEPTION == result) {
            if (recreateCount <= RECREATE_COUNT_MAX) {
                exit();
                recreateCount += 1;
                getIntent().putExtra(KEY_RECREATE_COUNT, recreateCount);
                Log.e(TAG, new StringBuilder("onResult > 模块视频服务异常，第").append(recreateCount).append("次重新打开").toString());
                recreate();
                return;
            }
            Log.e(TAG, "onResult > 模块视频服务异常，重新打开多次无效，放弃快速重置，准备重启模块");
            ModuleApplication.getInstance().reboot(500);
        }

        play(PeergineAudioVideoRequestResultHandler.getInstance(getApplicationContext()).
                getTitleByMediaType(getTypeCode(),
                        IAudioVideo.RESULT_SUCCESS == result ? R.string.media_request_open_success : R.string.media_request_open_handle_fail));

        dispatchEvent(new EventAudioVideoOperateResult()
                .setFlowId(EventAudioVideoOperateRequest.getFlowId(getIntent().getExtras()))
                .setToken(EventAudioVideoOperateRequest.getToken(getIntent().getExtras()))
                .setResultCode(result));
    }

    protected PeergineConfig getConfig() {
        if (null == mConfig) {

            String token = EventAudioVideoOperateRequest.getToken(getIntent().getExtras());
            String channel = EventAudioVideoOperateRequest.getChannelId(getIntent().getExtras());

            if (null == token) {
                Log.w(TAG, "getConfig > token is null");
                onResult(IAudioVideo.RESULT_FAIL_FAILURE_AUDIO_VIDEO_PARAMETER_PARSE_FAIL);
                exit();
                return null;
            }

            if (null == channel) {
                Log.w(TAG, "getConfig > channel is null");
                onResult(IAudioVideo.RESULT_FAIL_FAILURE_AUDIO_VIDEO_PARAMETER_PARSE_FAIL);
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
        finish();
    }
    
    protected void onPeerginEvent(String sAct, String sData, String sRenID){
        
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (IAudioVideo.RESULT_SUCCESS == mResult) {
            play(PeergineAudioVideoRequestResultHandler.getInstance(getApplicationContext())
                    .getTitleByMediaType(getTypeCode(), R.string.media_request_close_success));
        }
        if (IAudioVideo.MEDIA_TYPE_VIDEO == getTypeCode()) {
            //ModuleApplication.getInstance().reboot(200);
        }
    }
}
