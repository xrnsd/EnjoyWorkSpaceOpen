package kuyou.common.camera;

import android.content.Context;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import kuyou.common.utils.CommonUtils;

/**
 * action :
 * <p>
 * remarks:  <br/>
 * author: wuguoxian <br/>
 * date: 21-9-9 <br/>
 * </p>
 */
public class CameraUtil {
    protected static final String TAG = "kuyou.common.camera > CameraUtil";
    
    private volatile static CameraUtil sInstance;

    private CameraUtil(Context context) {
        mCameraManager = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);
    }

    public static CameraUtil getInstance(Context context) {
        if (sInstance == null) {
            synchronized (CameraUtil.class) {
                if (sInstance == null) {
                    context=context.getApplicationContext();
                    sInstance = new CameraUtil(context);
                }
            }
        }
        return sInstance;
    }

    private CameraManager mCameraManager;
    private Context mContext = null;

    /**
     * action:确认相机可用
     *
     * @param lensFacingFlag:查看下面 <br/>
     *                            CameraCharacteristics.LENS_FACING_FRONT 前置摄像头 <br/>
     *                            CameraCharacteristics.LENS_FACING_BACK 后只摄像头 <br/>
     *                            CameraCharacteristics.LENS_FACING_EXTERNAL 外部的摄像头 <br/>
     *                            
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public boolean isCameraAvailable(int lensFacingFlag) {
        try {
            String[] ids = mCameraManager.getCameraIdList();
            for (String id : ids) {
                CameraCharacteristics c = mCameraManager.getCameraCharacteristics(id);

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

                if (null != lensFacing
                        && lensFacingFlag == lensFacing) {
                    return true;
                }
            }
        } catch (Exception e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }
        return false;
    }
}
