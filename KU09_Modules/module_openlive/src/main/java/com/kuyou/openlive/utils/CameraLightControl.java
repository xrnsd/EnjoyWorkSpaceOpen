package com.kuyou.openlive.utils;

import android.content.Context;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import kuyou.common.file.FileUtils;
import kuyou.common.utils.CommonUtils;

/**
 * action :
 * <p>
 * remarks:  <br/>
 * author: wuguoxian <br/>
 * date: 21-4-28 <br/>
 * </p>
 */
public class CameraLightControl {

    protected final String TAG = "com.kuyou.openlive.camera > " + this.getClass().getSimpleName();

    private CameraManager mCameraManager;
    private CameraManager.TorchCallback mTorchCallback;
    private boolean isFlashLightOn = false;
    private Context mContext = null;

    private static CameraLightControl sMain;

    private CameraLightControl(Context context) {

    }

    public static CameraLightControl getInstance(Context context) {
        if (null == sMain) {
            sMain = new CameraLightControl(context);
        }
        sMain.initTorchCallback(context);
        return sMain;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void initTorchCallback(Context context) {
        if (null != mTorchCallback) {
            if (!CommonUtils.isContextExisted(mContext)) {
                mCameraManager.unregisterTorchCallback(mTorchCallback);
                mTorchCallback = null;
            } else {
                return;
            }
        }
        mContext = context;
        mTorchCallback = new CameraManager.TorchCallback() {
            @Override
            public void onTorchModeChanged(@NonNull String cameraId, boolean enabled) {
                super.onTorchModeChanged(cameraId, enabled);
                Log.e(TAG, "onTorchModeChanged cameraId=" + cameraId + ";enabled=" + enabled);
                isFlashLightOn = enabled;
            }
        };
        if (null == mCameraManager) {
            mCameraManager = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);
        }
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                mCameraManager.registerTorchCallback(mTorchCallback, null);
            }
        });
    }

    public boolean isFlashLightOn() {
        Log.d(TAG, "isFlashLightOn > isFlashLightOn ="+isFlashLightOn);
        return isFlashLightOn;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public boolean switchFlashLight(boolean val) {
        Log.d(TAG, " switchFlashLight > val = " + val);
        try {
            String[] ids = mCameraManager.getCameraIdList();
            for (String id : ids) {
                CameraCharacteristics c = mCameraManager.getCameraCharacteristics(id);
                //?????????????????????????????????????????????
                Boolean flashAvailable = c.get(CameraCharacteristics.FLASH_INFO_AVAILABLE);
                /*
                 * ???????????????????????????
                 * CameraCharacteristics.LENS_FACING_FRONT ???????????????
                 * CameraCharacteristics.LENS_FACING_BACK ???????????????
                 * CameraCharacteristics.LENS_FACING_EXTERNAL ??????????????????
                 */
                Integer lensFacing = c.get(CameraCharacteristics.LENS_FACING);
                if (flashAvailable != null && flashAvailable
                        && lensFacing != null && lensFacing == CameraCharacteristics.LENS_FACING_BACK) {
                    //????????????????????????
                    mCameraManager.setTorchMode(id, val);
                    break;
                }
            }
            return true;
        } catch (Exception e) {
            Log.e(TAG, Log.getStackTraceString(e));
            mCameraManager.unregisterTorchCallback(mTorchCallback);
            mTorchCallback = null;
        }
        return false;
    }

    public boolean switchLaserLight(boolean val) {
        Log.d(TAG, "open");
        final String devPath = "/sys/kernel/lactl/attr/camera";
        return FileUtils.writeInternalAntennaDevice(devPath, val ? "camera_pwr_on" : "camera_pwr_off");
    }
}
