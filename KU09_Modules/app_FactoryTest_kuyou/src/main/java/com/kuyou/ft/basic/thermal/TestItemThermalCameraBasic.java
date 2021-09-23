package com.kuyou.ft.basic.thermal;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.hardware.usb.UsbDevice;
import android.util.Log;
import android.view.View;

import com.kuyou.ft.R;
import com.kuyou.ft.basic.TestItemBasic;
import com.thermal.seekware.SeekCamera;
import com.thermal.seekware.SeekCameraManager;
import com.thermal.seekware.SeekImage;
import com.thermal.seekware.SeekImageView;
import com.thermal.seekware.SeekUtility;

import kuyou.common.ku09.handler.ThermalCameraControl;
import kuyou.common.ku09.handler.UsbDeviceHandler;
import kuyou.common.status.StatusProcessBusCallbackImpl;
import kuyou.common.status.basic.IStatusProcessBusCallback;

/**
 * action :
 * <p>
 * remarks:  <br/>
 * author: wuguoxian <br/>
 * date: 21-9-15 <br/>
 * </p>
 */
public abstract class TestItemThermalCameraBasic extends TestItemBasic {

    protected final static int OPEN_TIMEING_FLAG = 5;

    protected final static int PS_OPEN_TIME_OUT = 0;
    protected final static int PS_SHOW = 1;

    private final SeekCamera.ColorLut[] SEEK_CAMERA_COLOR_LUTS = {
            SeekCamera.ColorLut.TYRIAN,
            SeekCamera.ColorLut.IRON2,
            SeekCamera.ColorLut.RECON,
            SeekCamera.ColorLut.WHITEHOT,
            SeekCamera.ColorLut.BLACKHOT
    };

    private boolean isConfirm = false;
    private int myPalette = 0;

    private SeekCamera mSeekCamera;
    private SeekCameraManager mSeekCameraManager;
    private SeekImageView mSeekImageView;

    @Override
    public int getSubContentId() {
        return R.layout.test_item_thermal;
    }

    @Override
    protected void initViews() {
        super.initViews();
        mTvTitle = findViewById(R.id.tv_test_title);
        mTvTitle.setText(getString(R.string.title_thermal_camera_normal, OPEN_TIMEING_FLAG));
        mTvTiming = findViewById(R.id.tv_time);

        UsbDeviceHandler.register(getApplicationContext()).setUsbDeviceListener(new UsbDeviceHandler.IUsbDeviceListener() {
            @Override
            public void onUsbDevice(UsbDevice device, boolean attached) {
                if (device.getProductName().contains("Thermal") && attached) {
                    getStatusProcessBus().stop(PS_OPEN_TIME_OUT);
                    TestItemThermalCameraBasic.this.initSeekCamera();
                } else {
                    Log.d(TestItemThermalCameraBasic.this.TAG, "onUsbDevice > " + device.getProductName());
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        showOpenUsbConfirmDialog(TestItemThermalCameraBasic.this);
    }

    @Override
    protected void onDestroy() {
        ThermalCameraControl.close();
        UsbDeviceHandler.unregister(getApplicationContext());
        super.onDestroy();
    }

    @Override
    protected void initReceiveProcessStatusNotices() {
        super.initReceiveProcessStatusNotices();

        getStatusProcessBus().registerStatusNoticeCallback(PS_OPEN_TIME_OUT, new StatusProcessBusCallbackImpl(false, OPEN_TIMEING_FLAG * 1000)
                .setNoticeHandleLooperPolicy(IStatusProcessBusCallback.LOOPER_POLICY_MAIN));

        getStatusProcessBus().registerStatusNoticeCallback(PS_SHOW, new StatusProcessBusCallbackImpl(false, 0)
                .setNoticeHandleLooperPolicy(IStatusProcessBusCallback.LOOPER_POLICY_MAIN));
    }

    @Override
    protected void onReceiveProcessStatusNotice(int statusCode, boolean isRemove) {
        super.onReceiveProcessStatusNotice(statusCode, isRemove);
        switch (statusCode) {
            case PS_OPEN_TIME_OUT:
                getStatusProcessBus().stop(PS_TIMING);
                Log.d(TAG, "onReceiveProcessStatusNotice > 超时失败");
                setTestResultTitle(
                        getString(R.string.title_thermal_camera_not_found));
                ThermalCameraControl.close();
                mBtnSuccess.setEnabled(false);
                break;

            case PS_SHOW:
                getStatusProcessBus().stop(PS_OPEN_TIME_OUT);
                getStatusProcessBus().stop(PS_TIMING);
                Log.d(TAG, "onReceiveProcessStatusNotice > 已打开");
                mTvTitle.setVisibility(View.GONE);
                mSeekImageView.setVisibility(View.VISIBLE);

                mSeekCamera.setAspectRatio(SeekCamera.AspectRatio.MATCH_WIDTH);
                //mSeekImageView.setRotation(90f);
                mSeekCamera.createSeekCameraCaptureSession(mSeekImageView);
                break;

            default:
                break;
        }
    }

    @Override
    protected int getTimingFlag() {
        return OPEN_TIMEING_FLAG;
    }

    protected void showOpenUsbConfirmDialog(Context context) {
        if (isConfirm) {
            return;
        }
        AlertDialog.Builder b = new AlertDialog.Builder(context);
        b.setTitle(R.string.test_factory_reset_title);
        b.setMessage(R.string.test_factory_reset_open_thermal_readme);
        b.setPositiveButton(R.string.test_factoryreset_ok,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        TestItemThermalCameraBasic.this.open();
                    }
                });
        b.setNegativeButton(R.string.test_factoryreset_cancel,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        isConfirm = true;
                        TestItemThermalCameraBasic.this.onResult(false);
                    }
                });
        b.create().show();
    }

    protected void open() {
        SeekUtility.PermissionHandler.requestStoragePermission(this);

        getStatusProcessBus().start(PS_OPEN_TIME_OUT);
        getStatusProcessBus().start(PS_TIMING);
        isConfirm = true;
        ThermalCameraControl.open();
    }

    private void initSeekCamera() {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);
        mSeekImageView = findViewById(R.id.seek_preview);
        mSeekImageView.setOnFrameAvailableListener(new SeekImageView.OnFrameAvailableListener() {
            @Override
            public void onFrameAvailable(SeekImageView seekImageView, SeekImage seekImage) {
                if (seekImage.getThermography() != null && seekImage.getThermography().getSpotTemp() != null) {
                    SeekUtility.Temperature spotTemp = seekImage.getThermography().getSpotTemp();
                    if (spotTemp != null) {
                        Log.d(TAG, "温度：" + spotTemp.toShortString());
                    }
                }
            }
        });
        mSeekCameraManager = new SeekCameraManager(this, null, new SeekCamera.StateCallback() {
            @Override
            public void onInitialized(SeekCamera seekCamera) {

            }

            @Override
            public void onOpened(SeekCamera seekCamera) {
                TestItemThermalCameraBasic.this.mSeekCamera = seekCamera;
                TestItemThermalCameraBasic.this.getStatusProcessBus().start(PS_SHOW);
            }

            @Override
            public void onStarted(SeekCamera seekCamera) {

            }

            @Override
            public void onStopped(SeekCamera seekCamera) {

            }

            @Override
            public void onClosed(SeekCamera seekCamera) {
                TestItemThermalCameraBasic.this.mSeekCamera = null;
            }

            @Override
            public void onMemoryAccess(SeekCamera seekCamera, SeekCamera.MemoryRegion memoryRegion, int i) {

            }

            @Override
            public void onReboot(SeekCamera seekCamera) {

            }

            @Override
            public void onError(SeekCamera seekCamera, Exception e) {
                Log.e(TestItemThermalCameraBasic.this.TAG, Log.getStackTraceString(e));
            }
        });
    }

    private void setPalette(int lut) {
        myPalette = lut;
        if (mSeekCamera != null) {
            mSeekCamera.setColorLut(SEEK_CAMERA_COLOR_LUTS[lut]);
        }
    }
}
