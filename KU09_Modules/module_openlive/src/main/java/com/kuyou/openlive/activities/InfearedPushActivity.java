package com.kuyou.openlive.activities;

import android.content.pm.ActivityInfo;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.kuyou.openlive.R;
import com.kuyou.openlive.activities.base.OrdinaryPushActivity;
import com.kuyou.openlive.ui.TemperatureFollowView;
import com.kuyou.openlive.ui.VideoGridContainer;
import com.kuyou.openlive.utils.InfearedCameraControl;
import com.thermal.seekware.SeekCamera;
import com.thermal.seekware.SeekCameraManager;
import com.thermal.seekware.SeekImage;
import com.thermal.seekware.SeekImageView;
import com.thermal.seekware.SeekUtility;

import kuyou.common.ku09.event.openlive.EventLaserLightRequest;
import kuyou.common.ku09.JT808ExtensionProtocol.IAudioVideo;

/**
 * action :红外推流界面，使用屏幕共享实现数据推送
 * <p>
 * author: wuguoxian <br/>
 * date: 20-10-26 <br/>
 * </p>
 */
public class InfearedPushActivity extends OrdinaryPushActivity implements SeekCamera.StateCallback {

    private final SeekCamera.ColorLut[] SEEK_CAMERA_COLOR_LUTS = {
            SeekCamera.ColorLut.TYRIAN,
            SeekCamera.ColorLut.IRON2,
            SeekCamera.ColorLut.RECON,
            SeekCamera.ColorLut.WHITEHOT,
            SeekCamera.ColorLut.BLACKHOT
    };

    private int myPalette = 0;
    private SeekCamera mSeekCamera;
    private SeekCameraManager mSeekCameraManager;
    private SeekImageView mSeekImageView;
    private TemperatureFollowView mTemperatureFollowView;

    @Override
    public int getTypeCode() {
        return IAudioVideo.MEDIA_TYPE_INFEARED;
    }

    @Override
    protected int getContentViewResId() {
        return R.layout.activity_infrared_live;
    }

    @Override
    protected VideoGridContainer getVideoGridContainer() {
        return findViewById(R.id.live_video_grid_layout);
    }

    @Override
    protected String getItemTypeName() {
        return "红外视频";
    }

    @Override
    protected void initLiveConfig() {
        super.initLiveConfig();
        initSeekCamera();
        bindVideoService();
    }

    @Override
    protected void onResume() {
        super.onResume();
        dispatchEvent(new EventLaserLightRequest()
                .setSwitch(true)
                .setRemote(false));
    }

    @Override
    protected void onDestroy() {
        dispatchEvent(new EventLaserLightRequest()
                .setSwitch(false)
                .setRemote(false));
        super.onDestroy();
        stopScreenShare();
        unbindVideoService();
        InfearedCameraControl.close();
    }

    public void onDestroyDelay(long delay) {
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    InfearedPushActivity.this.finish();
                } catch (Exception e) {
                    Log.e(TAG, android.util.Log.getStackTraceString(e));
                }
            }
        }, delay);
    }

    private void initSeekCamera() {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);
        mSeekImageView = findViewById(R.id.seek_preview);
        mTemperatureFollowView = findViewById(R.id.temp_follow);
        mSeekImageView.setOnFrameAvailableListener(new SeekImageView.OnFrameAvailableListener() {
            @Override
            public void onFrameAvailable(SeekImageView seekImageView, SeekImage seekImage) {
                if (seekImage.getThermography() != null && seekImage.getThermography().getSpotTemp() != null) {
                    SeekUtility.Temperature spotTemp = seekImage.getThermography().getSpotTemp();
                    if (spotTemp != null) {
                        Log.d(TAG, "温度：" + spotTemp.toShortString());
                    }
                    mTemperatureFollowView.updateMaxTemp(seekImage.getThermography().getMaxPoint(), seekImage.getThermography().getMaxTemp());
                    //mTemperatureFollowView.updateMinTemp(seekImage.getThermography().getMinPoint(), seekImage.getThermography().getMinTemp());
                    runOnUiThread(() -> mTemperatureFollowView.invalidate());
                }
            }
        });
        mSeekCameraManager = new SeekCameraManager(this, null, InfearedPushActivity.this);

        SeekUtility.PermissionHandler.requestStoragePermission(this);
    }

    private void SetPalette(int lut) {
        myPalette = lut;
        if (mSeekCamera != null) {
            mSeekCamera.setColorLut(SEEK_CAMERA_COLOR_LUTS[lut]);
        }
    }

    @Override
    public synchronized void onInitialized(SeekCamera seekCamera) {
    }

    @Override
    public synchronized void onOpened(SeekCamera seekCamera) {
        seekCamera.setAspectRatio(SeekCamera.AspectRatio.MATCH_WIDTH);
        //mSeekImageView.setRotation(90f);
        mSeekCamera = seekCamera;
        seekCamera.createSeekCameraCaptureSession(mSeekImageView);
    }

    @Override
    public synchronized void onStarted(SeekCamera seekCamera) {
        SetPalette(0);
    }

    @Override
    public synchronized void onStopped(SeekCamera seekCamera) {

    }

    @Override
    public synchronized void onClosed(SeekCamera seekCamera) {
        mSeekCamera = null;
    }

    @Override
    public synchronized void onMemoryAccess(SeekCamera camera, SeekCamera.MemoryRegion region, final int progress) {

    }

    @Override
    public synchronized void onReboot(SeekCamera seekCamera) {

    }

    @Override
    public synchronized void onError(SeekCamera seekCamera, Exception e) {

    }
}
