package com.kuyou.openlive.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.media.Image;
import android.media.ImageReader;
import android.os.Bundle;
import android.util.Log;
import android.view.Surface;
import android.view.TextureView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.kuyou.openlive.R;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;

import kuyou.common.ku09.event.jt808.EventPhotoUploadRequest;
import kuyou.common.ku09.event.openlive.EventPhotoTakeRequest;
import kuyou.common.ku09.event.openlive.EventPhotoTakeResult;
import kuyou.common.ku09.ui.BaseActivity;

/**
 * action :相机测试[抽象]
 * <p>
 * remarks:  <br/>
 * author: wuguoxian <br/>
 * date: 21-3-09 <br/>
 * </p>
 */
public class TakePhoto extends BaseActivity {

    private static final String TAG = "com.kuyou.openlive > TakePhoto >";

    protected Bundle mData;

    private TextureView mTextureView = null;
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

    @Override
    protected int getContentViewResId() {
        return R.layout.take_photo;
    }

    @Override
    protected void initViews() {
        mData = getIntent().getExtras();
        mTextureView = findViewById(R.id.texture_view_camera2);
        initConfig();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 如果 textureView可用，就直接打开相机
        if (mTextureView.isAvailable()) {
            openCamera();
        } else {
            // 否则，就开启它的可用时监听。
            mTextureView.setSurfaceTextureListener(mSurfaceTextureListener);
        }
    }

    @Override
    protected void onPause() {
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
        // 最后交给父View去处理
        super.onPause();
    }

    private Bundle getData() {
        if (null == mData) {
            mData = getIntent().getExtras();
        }
        return mData;
    }

    private void initConfig() {
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
                ImageSaver imageSaver = new ImageSaver();
                imageSaver.setImage(reader.acquireLatestImage());
                imageSaver.setDirImageStorage(EventPhotoTakeRequest.getImgStorageDir(getData()));
                imageSaver.setImageFileName(EventPhotoTakeRequest.getFileName(getData()));
                imageSaver.setOnResultListener(new onResultListener() {
                    @Override
                    public void onResult(boolean result, String info) {
                        TakePhoto.this.onResult(result, info);
                    }
                });
                new Thread(imageSaver).start();
            }
        }, null);
        mSurfaceSurface = mImageReader.getSurface();
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
            onResult(false, e.toString());
        }
    }

    private void openCamera() {
        mCameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);  // 初始化
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
                                TakePhoto.this.take();
                            } catch (CameraAccessException e) {
                                e.printStackTrace();
                                onResult(false, "CameraCaptureSession config fail :" + e.toString());
                            }
                        }

                        @Override
                        public void onConfigureFailed(@NonNull CameraCaptureSession session) {
                        }
                    };
                    mCameraDeviceOpened.createCaptureSession(Arrays.asList(mTextureSurface, mSurfaceSurface), mCameraCaptureSessionStateCallback, null);
                } catch (CameraAccessException e) {
                    e.printStackTrace();
                    onResult(false, "camera open fail :" + e.toString());
                }
            }

            @Override
            public void onDisconnected(@NonNull CameraDevice camera) {
            }

            @Override
            public void onError(@NonNull CameraDevice camera, int error) {
            }
        };
        checkPermission();
        try {
            mCameraManager.openCamera(mCameraManager.getCameraIdList()[0], mCameraDeviceStateCallback, null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void checkPermission() {
        // 检查是否申请了权限
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 1);
            }
        }
    }

    public static void open(Context context, Bundle data) {
        Log.d(TAG, "open > ");
        Intent intent = new Intent();
        intent.setClass(context, TakePhoto.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtras(data);
        context.startActivity(intent);
    }

    protected void onResult(boolean result, String info) {
        if (result) {
            dispatchEvent(new EventPhotoUploadRequest()
                    .setImgFilePath(info)
                    .setRemote(true));
            Log.d(TAG, "onSuccess > 拍照成功");
        } else {
            dispatchEvent(new EventPhotoTakeResult()
                    .setData(getData())
                    //.setErrorInfo(error)
                    .setRemote(true)
                    .setResult(false));
            Log.d(TAG, "onFail > 拍照失败");
            play("拍照失败");
        }
        finish();
    }

    public static interface onResultListener {
        public void onResult(boolean result, String info);
    }

    public class ImageSaver implements Runnable {
        protected final String TAG = "com.kuyou.openlive.camera > ImageSaver ";

        private Image mImage = null;
        private File mFileImage = null;
        private String mDirImageStorage = null;
        private onResultListener mOnResultListener = null;

        public ImageSaver() {
        }

        public void setImage(Image image) {
            mImage = image;
        }

        public void setImageFileName(String fileName) {
            mFileImage = new File(mDirImageStorage, fileName);
        }

        public void setDirImageStorage(String dirPath) {
            File dirImageStorage = new File(dirPath);
            if (!dirImageStorage.exists())
                dirImageStorage.mkdirs();
            mDirImageStorage = dirPath;
        }

        public void setOnResultListener(onResultListener onResultListener) {
            mOnResultListener = onResultListener;
        }

        @Override
        public void run() {
            ByteBuffer buffer = mImage.getPlanes()[0].getBuffer();
            int length = buffer.remaining();
            byte[] bytes = new byte[length];
            buffer.get(bytes);
            mImage.close();
            Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, length);

            //Matrix matrix = new Matrix();
            //matrix.postRotate(0);
            //Bitmap bitmap = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), matrix, true);
            BufferedOutputStream buff = null;
            try {
                buff = new BufferedOutputStream(new FileOutputStream(mFileImage));
                bmp.compress(Bitmap.CompressFormat.JPEG, 100, buff);
                Log.d(TAG, "onPreviewFrame: suf");
                mOnResultListener.onResult(true, mFileImage.getPath());
                return;
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
            mOnResultListener.onResult(false, "img file save fail");
        }
    }
}