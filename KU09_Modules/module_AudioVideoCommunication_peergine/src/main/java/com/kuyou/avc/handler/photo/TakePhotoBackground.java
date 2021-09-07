package com.kuyou.avc.handler.photo;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.Image;
import android.media.ImageReader;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;

import androidx.core.app.NotificationCompat;

import com.kuyou.avc.R;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import kuyou.common.ku09.event.avc.EventPhotoTakeRequest;
import kuyou.common.ku09.event.avc.EventPhotoTakeResult;
import kuyou.common.ku09.protocol.IJT808ExtensionProtocol;

public class TakePhotoBackground extends Service {

    protected static final String TAG = "com.kuyou.avc.photo > TakePhotoBackground";

    private static Intent sTakePhotoServer = null;

    private static ITakePhotoByCameraResultListener sTakePhotoResultListener;

    private CameraViews mCameraViews;
    private WindowManager mWindowManager;
    private LayoutParams Params;

    private Bundle mData;
    private String mFileName, mFileFormatType, mImgStorageDir;

    public static void perform(Context context, Bundle data, ITakePhotoByCameraResultListener listener) {
        sTakePhotoResultListener = listener;
        sTakePhotoServer = new Intent(context, TakePhotoBackground.class);
        sTakePhotoServer.putExtras(data);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.getApplicationContext().startForegroundService(sTakePhotoServer);
        } else {
            context.getApplicationContext().startService(sTakePhotoServer);
        }
        Log.d(TAG, "perform > 开始后台拍照");
    }

    public static void stop(Context context) {
        if (null == sTakePhotoServer) {
            Log.e(TAG, "stop > process fail : sTakePhotoServer is null");
            return;
        }
        context.getApplicationContext().stopService(sTakePhotoServer);
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mData = intent.getExtras();
        setFileName(EventPhotoTakeRequest.getFileName(mData));
        setFileFormatType(EventPhotoTakeRequest.getFileFormatType(mData));
        setImgStorageDir(EventPhotoTakeRequest.getImgStorageDir(mData));

        if (Build.VERSION.SDK_INT >= 23 && !Settings.canDrawOverlays(getApplicationContext())) {
            Log.e(TAG, "onCreate > process fail : 悬浮窗权限未开启");
        } else {
            showWindow(mData);
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String NOTIFICATION_CHANNEL_ID = "com.example.simpleapp";
            String channelName = "My Background Service";
            NotificationChannel chan = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_NONE);
            chan.setLightColor(Color.BLUE);
            chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            assert manager != null;
            manager.createNotificationChannel(chan);

            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
            Notification notification = notificationBuilder.setOngoing(true)
                    .setSmallIcon(R.drawable.ic_launcher_background)
                    .setContentTitle("App is running in background")
                    .setPriority(NotificationManager.IMPORTANCE_MIN)
                    .setCategory(Notification.CATEGORY_SERVICE)
                    .build();
            startForeground(2, notification);
        }
    }

    public Bundle getData() {
        return mData;
    }

    private boolean isLocalDeviceSendInitiate() {
        return IJT808ExtensionProtocol.EVENT_TYPE_LOCAL_DEVICE_INITIATE == EventPhotoTakeResult.getEventType(getData());
    }

    protected void onResult(boolean result, String info) {
        if (null == sTakePhotoResultListener) {
            Log.e(TAG, "onResult > process fail : sTakePhotoResultListener is null");
            finish();
            return;
        }
        sTakePhotoResultListener.onTakePhotoResult(result, info, getData());
        finish();
    }

    private void finish() {
        if (null == sTakePhotoServer) {
            Log.e(TAG, "finish > stop camera take background service > process fail :sTakePhotoServer is null ");
        } else {
            Log.d(TAG, "finish > stop camera take background service");
            mCameraViews.close();
            stopService(sTakePhotoServer);
        }
    }

    private void showWindow(Bundle configData) {
        Log.d(TAG, "showWindow > ");
        //创建MyWindow的实例
        mCameraViews = new CameraViews(getApplicationContext()).setTakeResultListener(new CameraViews.ITakeResultListener() {
            @Override
            public void onResult(ImageReader reader, String info) {
                if (null == reader) {
                    TakePhotoBackground.this.onResult(false, info);
                    return;
                }
                ImageSaver imageSaver = new ImageSaver();
                imageSaver.setImage(reader.acquireLatestImage());
                imageSaver.setDirImageStorage(EventPhotoTakeRequest.getImgStorageDir(getData()));
                imageSaver.setImageFileName(EventPhotoTakeRequest.getFileName(getData()));
                imageSaver.setOnResultListener(new onResultListener() {
                    @Override
                    public void onResult(boolean result, String info) {
                        TakePhotoBackground.this.onResult(result, info);
                    }
                });
                new Thread(imageSaver).start();
            }
        }).initConfig(configData);

        //窗口管理者
        mWindowManager = (WindowManager) getSystemService(Service.WINDOW_SERVICE);
        //窗口布局参数
        Params = new LayoutParams();
        //布局坐标,以屏幕左上角为(0,0)
        Params.x = 0;
        Params.y = 0;

        //布局类型
        Params.type = LayoutParams.TYPE_APPLICATION_OVERLAY; // 系统提示类型,重要

        //布局flags
        Params.flags = LayoutParams.FLAG_NOT_FOCUSABLE; // 不能抢占聚焦点
        Params.flags = Params.flags | LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH;
        Params.flags = Params.flags | LayoutParams.FLAG_LAYOUT_NO_LIMITS; // 排版不受限制
        Params.flags |= LayoutParams.FLAG_HARDWARE_ACCELERATED;

        //布局的gravity
        Params.gravity = Gravity.LEFT | Gravity.TOP;

        //布局的宽和高
        Params.width = 1;
        Params.height = 1;

        mCameraViews.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {

                    case MotionEvent.ACTION_MOVE:
                        Params.x = (int) event.getRawX() - mCameraViews.getWidth() / 2;
                        Params.y = (int) event.getRawY() - mCameraViews.getHeight() / 2;
                        //更新布局位置
                        mWindowManager.updateViewLayout(mCameraViews, Params);

                        break;
                }
                return false;
            }
        });

        if (!mCameraViews.isAttachedToWindow()) {
            mWindowManager.addView(mCameraViews, Params);
        }
    }

    public String getFileName() {
        return mFileName;
    }

    public void setFileName(String fileName) {
        mFileName = fileName;
    }

    public String getFileFormatType() {
        return mFileFormatType;
    }

    public void setFileFormatType(String fileFormatType) {
        mFileFormatType = fileFormatType;
    }

    public String getImgStorageDir() {
        return mImgStorageDir;
    }

    public void setImgStorageDir(String imgStorageDir) {
        mImgStorageDir = imgStorageDir;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mCameraViews = null;
    }

    public static interface onResultListener {
        public void onResult(boolean result, String info);
    }


    public class ImageSaver implements Runnable {
        protected final String TAG = "com.kuyou.avc.ui > TakePhoto > ImageSaver ";

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
