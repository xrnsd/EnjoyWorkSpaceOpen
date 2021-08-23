package com.kuyou.avc.handler.photo;

import android.app.Service;
import android.content.Context;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.media.ImageReader;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.TextureView;
import android.view.WindowManager;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;

import com.kuyou.avc.R;

import java.util.Arrays;

import kuyou.common.ku09.event.avc.EventPhotoTakeRequest;

public class CameraViews extends LinearLayout {

    protected static final String TAG = "com.kuyou.avc.photo > CameraViews";

    private TextureView mTextureView;
    private WindowManager mWindowManager;

    protected Bundle mData;
    private TextureView.SurfaceTextureListener mSurfaceTextureListener = null;
    private CameraManager mCameraManager = null;
    private CameraDevice.StateCallback mCameraDeviceStateCallback = null;
    private CameraDevice mCameraDeviceOpened = null;
    private Surface mTextureSurface = null, mSurfaceSurface = null;
    private CameraCaptureSession.StateCallback mCameraCaptureSessionStateCallback = null;
    private CameraCaptureSession cameraCaptureSession = null;
    private CaptureRequest.Builder mCaptureRequestBuilder = null, mCaptureRequestBuilderImageReader = null;
    private CaptureRequest mCaptureRequest = null;
    private ImageReader mImageReader = null;

    public CameraViews(Context context) {
        super(context);
    }

    public CameraViews initConfig(Bundle data) {
        setData(data);
        Log.d(TAG, "initConfig > ");
        LayoutInflater.from(getContext()).inflate(R.layout.take_photo, this);

        mTextureView = (TextureView) findViewById(R.id.texture_view_camera2);
        mWindowManager = (WindowManager) getContext().getSystemService(Service.WINDOW_SERVICE);

        mSurfaceTextureListener = new TextureView.SurfaceTextureListener() {
            @Override
            public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
                mTextureSurface = new Surface(mTextureView.getSurfaceTexture());
                openCamera();
            }

            @Override
            public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
            }

            @Override
            public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
                return false;
            }

            @Override
            public void onSurfaceTextureUpdated(SurfaceTexture surface) {
            }
        };
        mTextureView.setSurfaceTextureListener(mSurfaceTextureListener);
        mImageReader = ImageReader.newInstance(EventPhotoTakeRequest.getImgWidth(getData()),
                EventPhotoTakeRequest.getImgHeight(getData()), ImageFormat.JPEG, 2);
        mImageReader.setOnImageAvailableListener(new ImageReader.OnImageAvailableListener() {
            @Override
            public void onImageAvailable(ImageReader reader) {
                onResult(reader, null);
            }
        }, null);
        mSurfaceSurface = mImageReader.getSurface();
        return CameraViews.this;
    }

    public CameraViews setData(Bundle data) {
        mData = data;
        return CameraViews.this;
    }

    private void openCamera() {
        mCameraManager = (CameraManager) getContext().getSystemService(Context.CAMERA_SERVICE);  // 初始化
        mCameraDeviceStateCallback = new CameraDevice.StateCallback() {
            @Override
            public void onOpened(@NonNull CameraDevice camera) {
                mCameraDeviceOpened = camera;
                try {
                    mCaptureRequestBuilder = mCameraDeviceOpened.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
                    mCaptureRequestBuilder.addTarget(mTextureSurface);
                    mCaptureRequest = mCaptureRequestBuilder.build();
                    mCameraCaptureSessionStateCallback = new CameraCaptureSession.StateCallback() {
                        @Override
                        public void onConfigured(@NonNull CameraCaptureSession session) {
                            cameraCaptureSession = session;
                            try {
                                session.setRepeatingRequest(mCaptureRequest, null, null);
                                CameraViews.this.take();
                            } catch (CameraAccessException e) {
                                Log.e(TAG, "onConfigured > process fail : ");
                                onResult(null, "CameraCaptureSession config fail :" + e.toString());
                            }
                        }

                        @Override
                        public void onConfigureFailed(@NonNull CameraCaptureSession session) {
                        }
                    };
                    mCameraDeviceOpened.createCaptureSession(Arrays.asList(mTextureSurface, mSurfaceSurface), mCameraCaptureSessionStateCallback, null);
                } catch (CameraAccessException e) {
                    e.printStackTrace();
                    onResult(null, "camera open fail :" + e.toString());
                }
            }

            @Override
            public void onDisconnected(@NonNull CameraDevice camera) {
            }

            @Override
            public void onError(@NonNull CameraDevice camera, int error) {
            }
        };
        try {
            mCameraManager.openCamera(mCameraManager.getCameraIdList()[0], mCameraDeviceStateCallback, null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * action:拍照
     */
    protected void take() {
        //B4.1 配置request的参数 拍照模式(这行代码要调用已启动的相机 opened_camera，所以不能放在外面
        try {
            mCaptureRequestBuilderImageReader = mCameraDeviceOpened.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
        mCaptureRequestBuilderImageReader.set(CaptureRequest.JPEG_ORIENTATION, 90);
        mCaptureRequestBuilderImageReader.set(CaptureRequest.CONTROL_AF_TRIGGER, CameraMetadata.CONTROL_AF_TRIGGER_START);
        //B4.2 配置request的参数 的目标对象
        mCaptureRequestBuilderImageReader.addTarget(mSurfaceSurface);
        try {
            cameraCaptureSession.capture(mCaptureRequestBuilderImageReader.build(), null, null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
            onResult(null, e.toString());
        }
    }

    public Bundle getData() {
        return mData;
    }

    public void close() {
        // 先把相机的session关掉
        if (cameraCaptureSession != null) {
            cameraCaptureSession.close();
        }
        // 再关闭相机
        if (null != mCameraDeviceOpened) {
            mCameraDeviceOpened.close();
        }
        // 最后关闭ImageReader
        if (null != mImageReader) {
            mImageReader.close();
        }
    }

    private ITakeResultListener mTakeResultListener;

    public CameraViews setTakeResultListener(ITakeResultListener takeResultListener) {
        mTakeResultListener = takeResultListener;
        return CameraViews.this;
    }

    protected void onResult(ImageReader reader, String info) {
        if (null == mTakeResultListener) {
            Log.e(TAG, "onResult > process fail : mTakeResultListener is null");
            return;
        }
        Log.d(TAG, "onResult > ");
        mTakeResultListener.onResult(reader, info);
    }

    public static interface ITakeResultListener {
        public void onResult(ImageReader reader, String info);
    }
}
