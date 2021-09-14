package com.kuyou.ft.basic.camera;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.os.Environment;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;

import androidx.annotation.RequiresApi;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * 该类为调用照相机拍照
 * 使用该类实现拍照只需要2步
 * 1.在onCreat新建线程实例化该类
 * 2.点击拍照时调用该类的takePhoto()方法
 * ps：实例化TakePhotoListener()可以实现将得到的照片与保存路径显示在ui界面
 */
public class CameraSurfaceViewCallback implements SurfaceHolder.Callback {
    
    protected final static String TAG = "com.kuyou.ft.basic.camera > CameraSurfaceViewCallback";

    private boolean isHasSurface = false,
            isEnableTake = false,
            isCameraPreviewing = false;

    private int mCurrentCamIndex = 0,
            mPostRotate = 0;

    private Camera mCamera;
    private Activity mContext;
    private TakePhotoListener mTakePhotoListener;

    public CameraSurfaceViewCallback(Activity activity, int CameraId, TakePhotoListener lis) {
        mContext = activity;
        mTakePhotoListener = lis;
        mCurrentCamIndex = CameraId;
    }

    public void setPostRotate(int val) {
        mPostRotate = val;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.d(TAG, "SurfaceViewCallback: surfaceCreated");
        if (!isHasSurface) {
            this.mCamera = openFrontFacingCameraGingerbread();
            this.mCamera.setPreviewCallback(new Camera.PreviewCallback() {
                @RequiresApi(api = 19)
                @Override
                public void onPreviewFrame(byte[] bytes, Camera camera) {
                    if (isEnableTake) {
                        getSurfacePic(bytes, camera);
                        isEnableTake = false;
                    }
                }
            });
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        if (isCameraPreviewing) {
            mCamera.stopPreview();
            isCameraPreviewing = false;
        }
        try {
            mCamera.setPreviewDisplay(holder);
            mCamera.startPreview();
            isCameraPreviewing = true;
            setCameraDisplayOrientation(mContext, mCurrentCamIndex, mCamera);
        } catch (Exception e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        if (!isCameraPreviewing)
            return;
        holder.removeCallback(this);
        if (null == mCamera)
            return;
        mCamera.setPreviewCallback(null);
        mCamera.stopPreview();
        mCamera.lock();
        mCamera.release();
        mCamera = null;

    }

    private void setCameraDisplayOrientation(Activity activity, int cameraId, Camera camera) {
        Camera.CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo(cameraId, info);
        int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();

        /** 度图片顺时针旋转的角度。有效值为0、90、180和270*/
        /** 起始位置为0（横向）*/
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }
        Log.d(TAG, "setCameraDisplayOrientation > degrees= " + degrees);
        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;  // compensate the mirror
        } else {
            /** 背面*/
            result = (info.orientation - degrees + 360) % 360;
        }
        camera.setDisplayOrientation(result);
    }


    /**
     * 初始化相机
     */
    private Camera openFrontFacingCameraGingerbread() {
        Camera cam = null;
        cam = Camera.open(mCurrentCamIndex);
        return cam;
    }

    public void takePhoto() {
        this.isEnableTake = true;
    }

    @RequiresApi(api = 19)
    public void getSurfacePic(byte[] data, Camera camera) {
        Camera.Size size = camera.getParameters().getPreviewSize();
        YuvImage image = new YuvImage(data, ImageFormat.NV21, size.width, size.height, null);
        if (image != null) {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            image.compressToJpeg(new Rect(0, 0, size.width, size.height), 80, stream);

            Bitmap bmp = BitmapFactory.decodeByteArray(stream.toByteArray(), 0, stream.size());
            /** 因为图片会放生旋转，因此要对图片进行旋转到和手机在一个方向上*/
            rotateMyBitmap(bmp);
        }
    }

    @RequiresApi(api = 19)
    public void rotateMyBitmap(Bitmap bmp) {
        String imgFilename = "IMG" + new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date()) + ".jpg";
        File imgFile = new File(Environment.getExternalStorageDirectory(), imgFilename);
        Matrix matrix = new Matrix();
        Log.d(TAG, "rotateMyBitmap > mPostRotate=" + mPostRotate);
        matrix.postRotate(mPostRotate);
        Bitmap bitmap = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), matrix, true);
        mTakePhotoListener.onSuccess(imgFile, bitmap);/**把得到的照片在UI上显示*/
        BufferedOutputStream buff = null;
        try {
            buff = new BufferedOutputStream(new FileOutputStream(imgFile));
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, buff);
            Log.d(TAG, "onPreviewFrame: suf");
        } catch (FileNotFoundException e) {
            Log.e(TAG, Log.getStackTraceString(e));
        } finally {
            if (buff != null) {
                try {
                    buff.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
