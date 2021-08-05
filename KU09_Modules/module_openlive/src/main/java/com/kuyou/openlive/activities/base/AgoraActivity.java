package com.kuyou.openlive.activities.base;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.projection.MediaProjectionManager;
import android.os.IBinder;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.SurfaceView;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;

import kuyou.common.ku09.JT808ExtensionProtocol.IAudioVideo;
import com.kuyou.openlive.ModuleApplication;
import com.kuyou.openlive.R;
import com.kuyou.openlive.rtc.Constants;
import com.kuyou.openlive.rtc.EventHandler;
import com.kuyou.openlive.stats.LocalStatsData;
import com.kuyou.openlive.stats.RemoteStatsData;
import com.kuyou.openlive.stats.StatsData;
import com.kuyou.openlive.ui.VideoGridContainer;

import io.agora.advancedvideo.externvideosource.ExternalVideoInputManager;
import io.agora.advancedvideo.externvideosource.ExternalVideoInputService;
import io.agora.advancedvideo.externvideosource.IExternalVideoInputService;
import io.agora.rtc.IRtcEngineEventHandler;
import io.agora.rtc.RtcEngine;
import io.agora.rtc.video.CameraCapturerConfiguration;
import io.agora.rtc.video.VideoCanvas;
import io.agora.rtc.video.VideoEncoderConfiguration;

/**
 * action :
 * <p>
 * author: wuguoxian <br/>
 * date: 20-11-26 <br/>
 * <p>
 */
public abstract class AgoraActivity extends BaseLiveActivity implements EventHandler {

    protected VideoGridContainer mVideoGridContainer;

    @Override
    protected void initViews() {
        super.initViews();
        mVideoGridContainer = getVideoGridContainer();
        if (null != mVideoGridContainer) {
            mVideoGridContainer.setStatsManager(statsManager());
        }
    }

    @Override
    protected void initLiveConfig() {
        setAppScreenBrightness(255);

        registerRtcEventHandler(this);

        VideoEncoderConfiguration configuration = new VideoEncoderConfiguration(
                Constants.VIDEO_DIMENSIONS[config().getVideoDimenIndex()],
                VideoEncoderConfiguration.FRAME_RATE.FRAME_RATE_FPS_15,
                VideoEncoderConfiguration.STANDARD_BITRATE,
                VideoEncoderConfiguration.ORIENTATION_MODE.ORIENTATION_MODE_FIXED_PORTRAIT
        );
        configuration.mirrorMode = Constants.VIDEO_MIRROR_MODES[config().getMirrorEncodeIndex()];
        rtcEngine().setVideoEncoderConfiguration(configuration);

        rtcEngine().setCameraCapturerConfiguration(new CameraCapturerConfiguration(
                CameraCapturerConfiguration.CAPTURER_OUTPUT_PREFERENCE.CAPTURER_OUTPUT_PREFERENCE_PERFORMANCE,
                CameraCapturerConfiguration.CAMERA_DIRECTION.CAMERA_REAR));

        joinChannel();

        startBroadcast();
    }

    @Override
    protected VideoGridContainer getVideoGridContainer() {
        return null;
    }

    protected void joinChannel() {
        // Initialize token, extra info here before joining channel
        // 1. Users can only see each other after they join the
        // same channel successfully using the same app id.
        // 2. One token is only valid for the channel name and uid that
        // you use to generate this token.
        String token = getString(R.string.agora_access_token);
        if (getIntent().hasExtra(ModuleApplication.KEY_TOKEN)) {
            token = getIntent().getStringExtra(ModuleApplication.KEY_TOKEN);
        }
        if (TextUtils.isEmpty(token) || TextUtils.equals(token, "#YOUR ACCESS TOKEN#")) {
            token = null; // default, no token
        }

        String channelName = config().getChannelName();
        if (getIntent().hasExtra(ModuleApplication.KEY_CHANNEL_ID)) {
            channelName = getIntent().getStringExtra(ModuleApplication.KEY_CHANNEL_ID);
        }
        rtcEngine().joinChannel(token, channelName, "", 0);

        Log.d(TAG, new StringBuilder(32)
                .append("  joinChannel > ")
                .append("\n token = ").append(token)
                .append("\n channelName = ").append(channelName)
                .toString());
    }

    protected void startBroadcast() {
        rtcEngine().setClientRole(io.agora.rtc.Constants.CLIENT_ROLE_BROADCASTER);
        if (null != mVideoGridContainer) {
            SurfaceView surface = prepareRtcVideo(0, true);
            mVideoGridContainer.addUserVideoSurface(0, surface, true);
        }
    }

    protected void stopBroadcast() {
        rtcEngine().setClientRole(io.agora.rtc.Constants.CLIENT_ROLE_AUDIENCE);
        removeRtcVideo(0, true);
        if (null != mVideoGridContainer) {
            mVideoGridContainer.removeUserVideo(0, true);
        }
    }

    protected void setAppScreenBrightness(int birghtessValue) {
        Window window = getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.screenBrightness = birghtessValue / 255.0f;
        window.setAttributes(lp);
    }

    protected SurfaceView prepareRtcVideo(int uid, boolean local) {
        // Render local/remote video on a SurfaceView

        SurfaceView surface = RtcEngine.CreateRendererView(getApplicationContext());
        if (local) {
            rtcEngine().setupLocalVideo(
                    new VideoCanvas(
                            surface,
                            VideoCanvas.RENDER_MODE_HIDDEN,
                            0,
                            Constants.VIDEO_MIRROR_MODES[config().getMirrorLocalIndex()]
                    )
            );
        } else {
            rtcEngine().setupRemoteVideo(
                    new VideoCanvas(
                            surface,
                            VideoCanvas.RENDER_MODE_HIDDEN,
                            uid,
                            Constants.VIDEO_MIRROR_MODES[config().getMirrorRemoteIndex()]
                    )
            );
        }
        return surface;
    }

    protected void removeRtcVideo(int uid, boolean local) {
        if (local) {
            rtcEngine().setupLocalVideo(null);
        } else {
            rtcEngine().setupRemoteVideo(new VideoCanvas(null, VideoCanvas.RENDER_MODE_HIDDEN, uid));
        }
    }

    @Override
    protected void onDestroy() {
        setAppScreenBrightness(0);//防止后台看到红外退出后的桌面界面,因为屏幕共享关闭有延时
        rtcEngine().leaveChannel();
        stopBroadcast();
        removeRtcEventHandler(this);
        statsManager().clearAllData();
        super.onDestroy();
    }

    //============= InfearedPushActivity 和 OrdinaryPushActivity 通用部分 =========================
    @Override
    public void onRtcStats(IRtcEngineEventHandler.RtcStats stats) {
        if (!statsManager().isEnabled())
            return;

        LocalStatsData data = (LocalStatsData) statsManager().getStatsData(0);
        if (data == null)
            return;

        data.setLastMileDelay(stats.lastmileDelay);
        data.setVideoSendBitrate(stats.txVideoKBitRate);
        data.setVideoRecvBitrate(stats.rxVideoKBitRate);
        data.setAudioSendBitrate(stats.txAudioKBitRate);
        data.setAudioRecvBitrate(stats.rxAudioKBitRate);
        data.setCpuApp(stats.cpuAppUsage);
        data.setCpuTotal(stats.cpuAppUsage);
        data.setSendLoss(stats.txPacketLossRate);
        data.setRecvLoss(stats.rxPacketLossRate);
    }

    @Override
    public void onNetworkQuality(int uid, int txQuality, int rxQuality) {
        if (!statsManager().isEnabled())
            return;

        StatsData data = statsManager().getStatsData(uid);
        if (data == null)
            return;

        data.setSendQuality(statsManager().qualityToString(txQuality));
        data.setRecvQuality(statsManager().qualityToString(rxQuality));
    }

    @Override
    public void onRemoteVideoStats(IRtcEngineEventHandler.RemoteVideoStats stats) {
        if (!statsManager().isEnabled())
            return;

        RemoteStatsData data = (RemoteStatsData) statsManager().getStatsData(stats.uid);
        if (data == null)
            return;

        data.setWidth(stats.width);
        data.setHeight(stats.height);
        data.setFramerate(stats.rendererOutputFrameRate);
        data.setVideoDelay(stats.delay);
    }

    @Override
    public void onRemoteAudioStats(IRtcEngineEventHandler.RemoteAudioStats stats) {
        if (!statsManager().isEnabled())
            return;

        RemoteStatsData data = (RemoteStatsData) statsManager().getStatsData(stats.uid);
        if (data == null)
            return;

        data.setAudioNetDelay(stats.networkTransportDelay);
        data.setAudioNetJitter(stats.jitterBufferDelay);
        data.setAudioLoss(stats.audioLossRate);
        data.setAudioQuality(statsManager().qualityToString(stats.quality));
    }

    // ====================  屏幕共享  ==========================================
    private static final int DEFAULT_SHARE_FRAME_RATE = 15;
    private static final int PROJECTION_REQ_CODE = 1 << 2;
    private static final int DEFAULT_VIDEO_TYPE = ExternalVideoInputManager.TYPE_SCREEN_SHARE;

    protected RelativeLayout mPreviewLayout;
    protected RelativeLayout videoContainer;

    protected IExternalVideoInputService mService;
    protected DisplayMetrics metrics;

    private VideoInputServiceConnection mServiceConnection;
    private int mCurVideoSource = DEFAULT_VIDEO_TYPE;

    private class VideoInputServiceConnection implements ServiceConnection {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            mService = (IExternalVideoInputService) iBinder;
            openScreenShare();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mService = null;
        }
    }

    private boolean hasLocalVideoSharePreview() {
        return mCurVideoSource == ExternalVideoInputManager.TYPE_LOCAL_VIDEO && mPreviewLayout != null &&
                mPreviewLayout.getChildCount() > 0;
    }

    private void requestMediaProjection() {
        MediaProjectionManager mpm = (MediaProjectionManager)
                getSystemService(Context.MEDIA_PROJECTION_SERVICE);
        Intent intent = mpm.createScreenCaptureIntent();
        startActivityForResult(intent, PROJECTION_REQ_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PROJECTION_REQ_CODE && resultCode == RESULT_OK) {
            startScreenShare(data);
        }
    }

    private void openScreenShare() {
        removeLocalPreview();
        requestMediaProjection();
    }

    private void removeLocalPreview() {
        if (hasLocalVideoSharePreview()) {
            mPreviewLayout.removeAllViews();
        }
    }

    /**
     * 使用屏幕共享推送
     */
    protected void bindVideoService() {
        Intent intent = new Intent();
        intent.setClass(this, ExternalVideoInputService.class);
        mServiceConnection = new VideoInputServiceConnection();
        bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
    }

    protected void unbindVideoService() {
        try {
            if (mServiceConnection != null) {
                unbindService(mServiceConnection);
            }
        } catch (Exception e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }
    }

    protected void startScreenShare(Intent data) {
        if (null == mService)
            return;
        metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        data.putExtra(ExternalVideoInputManager.FLAG_SCREEN_WIDTH, metrics.widthPixels);
        data.putExtra(ExternalVideoInputManager.FLAG_SCREEN_HEIGHT, metrics.heightPixels);
        data.putExtra(ExternalVideoInputManager.FLAG_SCREEN_DPI, (int) metrics.density);
        data.putExtra(ExternalVideoInputManager.FLAG_FRAME_RATE, DEFAULT_SHARE_FRAME_RATE);

        setVideoConfig(ExternalVideoInputManager.TYPE_SCREEN_SHARE, metrics.widthPixels, metrics.heightPixels);
        try {
            mService.setExternalVideoInput(ExternalVideoInputManager.TYPE_SCREEN_SHARE, data);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    protected void stopScreenShare() {
        if (null == mService)
            return;
        try {
            mService.setExternalVideoInput(ExternalVideoInputManager.TYPE_AR_CORE, new Intent());
        } catch (RemoteException e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }
        if (null != metrics)
            setVideoConfig(-1, metrics.widthPixels, metrics.heightPixels);
    }

    protected void setVideoConfig(int sourceType, int width, int height) {
        VideoEncoderConfiguration.ORIENTATION_MODE mode;
        switch (sourceType) {
            case ExternalVideoInputManager.TYPE_LOCAL_VIDEO:
                mode = VideoEncoderConfiguration.ORIENTATION_MODE.ORIENTATION_MODE_FIXED_LANDSCAPE;
                break;
            case ExternalVideoInputManager.TYPE_SCREEN_SHARE:
                //mode = VideoEncoderConfiguration.ORIENTATION_MODE.ORIENTATION_MODE_FIXED_PORTRAIT;
                //break;
            default:
                mode = VideoEncoderConfiguration.ORIENTATION_MODE.ORIENTATION_MODE_ADAPTIVE;
                break;

        }

        rtcEngine().setVideoEncoderConfiguration(new VideoEncoderConfiguration(
                new VideoEncoderConfiguration.VideoDimensions(width, height),
                VideoEncoderConfiguration.FRAME_RATE.FRAME_RATE_FPS_10,
                VideoEncoderConfiguration.STANDARD_BITRATE, mode
        ));
    }
}

