package com.kuyou.avc.handler.thermal;

import android.content.Context;
import android.util.Log;

import com.kuyou.avc.handler.thermal.basic.IPeergineCameraCaptureCallBack;
import com.thermal.seekware.SeekCamera;
import com.thermal.seekware.SeekCameraManager;
import com.thermal.seekware.SeekImage;
import com.thermal.seekware.SeekImageView;

import kuyou.common.ipc.RemoteEvent;
import kuyou.common.ku09.basic.ThermalCameraControl;
import kuyou.common.ku09.handler.BasicAssistHandler;

/**
 * action :协处理器[红外]
 * <p>
 * remarks:  <br/>
 * author: wuguoxian <br/>
 * date: 21-8-23 <br/>
 * </p>
 */
public class SeekThermalCameraControlHandler extends BasicAssistHandler implements SeekCamera.StateCallback {
    protected final String TAG = "com.kuyou.avc.handler.infeare > SeekThermalImagingCameraHandler";

    private SeekCamera mSeekCamera;
    private SeekCameraManager mSeekCameraManager;

    private SeekImageView mSeekImageView;
    //private TemperatureFollowView mTemperatureFollowView;
    private SeekImageView.OnFrameAvailableListener mFrameAvailableListener;

    private IPeergineCameraCaptureCallBack mPeergineCameraCaptureCallBack;

    private int myPalette = 0;

    public SeekThermalCameraControlHandler(Context context) {
        setContext(context.getApplicationContext());
        initSeekCamera(getContext());
    }

    private void initSeekCamera(Context context) {
        mSeekImageView = new SeekImageView(context);
        //mTemperatureFollowView = findViewById(R.id.temp_follow);
        mSeekImageView.setOnFrameAvailableListener(new SeekImageView.OnFrameAvailableListener() {
            @Override
            public void onFrameAvailable(SeekImageView seekImageView, SeekImage seekImage) {
                onPreviewFrame(seekImageView, seekImage);
//                if (seekImage.getThermography() != null && seekImage.getThermography().getSpotTemp() != null) {
//                    SeekUtility.Temperature spotTemp = seekImage.getThermography().getSpotTemp();
//                    if (spotTemp != null) {
//                        Log.d(TAG, "温度：" + spotTemp.toShortString());
//                    }
//                    mTemperatureFollowView.updateMaxTemp(seekImage.getThermography().getMaxPoint(), seekImage.getThermography().getMaxTemp());
//                    //mTemperatureFollowView.updateMinTemp(seekImage.getThermography().getMinPoint(), seekImage.getThermography().getMinTemp());
//                    runOnUiThread(() -> mTemperatureFollowView.invalidate());
//                }
            }
        });
        mSeekCameraManager = new SeekCameraManager(context, null, SeekThermalCameraControlHandler.this);
    }

    public void start() {
        ThermalCameraControl.open();
    }

    public void stop() {
        setFrameAvailableListener(null);
        setPeergineCameraCaptureCallBack(null);
        ThermalCameraControl.close();
    }

    public void screenshot() {

    }

    public SeekImageView getSeekImageView() {
        return mSeekImageView;
    }

    protected SeekImageView.OnFrameAvailableListener getFrameAvailableListener() {
        return mFrameAvailableListener;
    }

    public SeekThermalCameraControlHandler setFrameAvailableListener(SeekImageView.OnFrameAvailableListener listener) {
        mFrameAvailableListener = listener;
        mSeekImageView.setOnFrameAvailableListener(listener);
        return SeekThermalCameraControlHandler.this;
    }

    protected void onPreviewFrame(SeekImageView seekImageView, SeekImage seekImage) {
        if (null == getPeergineCameraCaptureCallBack()) {
            Log.e(TAG, "onPreviewFrame > process fail : PeergineCameraCaptureCallBack is null");
        }

        byte[] data = seekImage.getFilteredBuffer().array();
        getPeergineCameraCaptureCallBack().onPreviewFrame(data);
    }

    protected IPeergineCameraCaptureCallBack getPeergineCameraCaptureCallBack() {
        return mPeergineCameraCaptureCallBack;
    }

    public SeekThermalCameraControlHandler setPeergineCameraCaptureCallBack(IPeergineCameraCaptureCallBack callback) {
        mPeergineCameraCaptureCallBack = callback;
        return SeekThermalCameraControlHandler.this;
    }

    private void setPalette(int lut) {
        myPalette = lut;
        if (mSeekCamera != null) {
            mSeekCamera.setColorLut(SEEK_CAMERA_COLOR_LUTS[lut]);
        }
    }

    @Override
    protected void initReceiveEventNotices() {
    }

    @Override
    public boolean onReceiveEventNotice(RemoteEvent event) {
        return false;
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
        setPalette(0);
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

    private final SeekCamera.ColorLut[] SEEK_CAMERA_COLOR_LUTS = {
            SeekCamera.ColorLut.TYRIAN,
            SeekCamera.ColorLut.IRON2,
            SeekCamera.ColorLut.RECON,
            SeekCamera.ColorLut.WHITEHOT,
            SeekCamera.ColorLut.BLACKHOT
    };
}
