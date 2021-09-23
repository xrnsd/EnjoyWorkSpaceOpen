package kuyou.common.ku09.handler;

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
 * action :手电筒控制
 * <p>
 * remarks:  <br/>
 * author: wuguoxian <br/>
 * date: 21-4-28 <br/>
 * </p>
 */
public class CameraLightControl {

    protected static final String TAG = "kuyou.common.ku09.handler > CameraLightControl";

    private volatile static CameraLightControl sInstance;

    private CameraLightControl(Context context) {

    }

    public static CameraLightControl getInstance(Context context) {
        if (sInstance == null) {
            synchronized (CameraLightControl.class) {
                if (sInstance == null) {
                    context=context.getApplicationContext();
                    sInstance = new CameraLightControl(context);
                    sInstance.initTorchCallback(context);
                }
            }
        }
        return sInstance;
    }

    private CameraManager mCameraManager;
    private CameraManager.TorchCallback mTorchCallback;
    private boolean isFlashLightOn = false;
    private Context mContext = null;

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
        Log.d(TAG, "isFlashLightOn > isFlashLightOn =" + isFlashLightOn);
        return isFlashLightOn;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public boolean switchFlashLight(boolean val) {
        Log.d(TAG, " switchFlashLight > val = " + val);
        try {
            String[] ids = mCameraManager.getCameraIdList();
            for (String id : ids) {
                CameraCharacteristics c = mCameraManager.getCameraCharacteristics(id);
                //查询该摄像头组件是否包含闪光灯
                Boolean flashAvailable = c.get(CameraCharacteristics.FLASH_INFO_AVAILABLE);
                if (null == flashAvailable || !flashAvailable) {
                    continue;
                }
                /*
                 * 获取相机面对的方向
                 * CameraCharacteristics.LENS_FACING_FRONT 前置摄像头
                 * CameraCharacteristics.LENS_FACING_BACK 后只摄像头
                 * CameraCharacteristics.LENS_FACING_EXTERNAL 外部的摄像头
                 */
                Integer lensFacing = c.get(CameraCharacteristics.LENS_FACING);

                if (null == lensFacing
                        || CameraCharacteristics.LENS_FACING_BACK != lensFacing)
                    continue;

                //打开或关闭手电筒
                mCameraManager.setTorchMode(id, val);
                onSwitch(true);
                return true;
            }
        } catch (Exception e) {
            Log.e(TAG, Log.getStackTraceString(e));
            mCameraManager.unregisterTorchCallback(mTorchCallback);
            mTorchCallback = null;
        }
        onSwitch(false);
        return false;
    }

    public boolean switchLaserLight(boolean val) {
        Log.d(TAG, "open");
        final String devPath = "/sys/kernel/lactl/attr/camera";
        boolean result = FileUtils.writeInternalAntennaDevice(devPath, val ? "camera_pwr_on" : "camera_pwr_off");
        onSwitch(result);
        return result;
    }

    private IOnSwitchListener mOnSwitchListener;

    protected IOnSwitchListener getOnSwitchListener() {
        return mOnSwitchListener;
    }

    protected void onSwitch(boolean isSuccess) {
        if (null == mOnSwitchListener) {
            Log.e(TAG, "onSwitch > process fail : mOnSwitchListener is null");
            return;
        }
        mOnSwitchListener.onSwitch(isSuccess);
    }

    public CameraLightControl setOnSwitchListener(IOnSwitchListener onSwitchListener) {
        mOnSwitchListener = onSwitchListener;
        return CameraLightControl.this;
    }

    public static interface IOnSwitchListener {
        public void onSwitch(boolean isSuccess);
    }
}
