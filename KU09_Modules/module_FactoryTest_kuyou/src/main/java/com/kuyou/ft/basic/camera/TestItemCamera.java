package com.kuyou.ft.basic.camera;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;

import com.kuyou.ft.R;
import com.kuyou.ft.basic.TestItemBasic;

/**
 * action :相机测试[抽象]
 * <p>
 * remarks:  <br/>
 * author: wuguoxian <br/>
 * date: 21-3-09 <br/>
 * </p>
 */
public abstract class TestItemCamera extends TestItemBasic {

    protected Button mBtnTakePhoto;
    protected TextView mUrlPhoto;
    protected ImageView mImgVwPhoto;
    protected SurfaceView mSurfaceView;
    protected SurfaceHolder mSurfaceHolder;
    protected CameraSurfaceViewCallback mCameraSurfaceViewCallback;

    @Override
    protected void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);
        startCamera(getCameraId());
    }

    @Override
    public int getSubContentId() {
        return R.layout.test_item_camera;
    }

    @Override
    protected void initViews() {
        super.initViews();
        mUrlPhoto = findViewById(R.id.camera_url);
        mImgVwPhoto = findViewById(R.id.camera_img);
        mBtnTakePhoto = findViewById(R.id.take_photo);
        mSurfaceView = findViewById(R.id.camera_SV);
        mBtnTakePhoto.setOnClickListener(TestItemCamera.this);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        try {
            switch (v.getId()) {
                case R.id.take_photo:
                    mCameraSurfaceViewCallback.takePhoto();

                    //mSurfaceView.setVisibility(View.GONE);
                    mImgVwPhoto.setVisibility(View.VISIBLE);
                    mUrlPhoto.setVisibility(View.VISIBLE);
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }
    }

    /**
     * 相机ID
     */
    protected abstract int getCameraId();

    /**
     * 偏转角度
     */
    protected abstract int getPostRotate();

    @Override
    protected void onResult(boolean status) {
        if (null != mSurfaceHolder)
            mCameraSurfaceViewCallback.surfaceDestroyed(mSurfaceHolder);
        super.onResult(status);
    }

    @Override
    protected void onAging() {
        super.onAging();
        onAgingTestItem(() -> onResult(false), 8000);//超时视为失败
    }

    protected void startCamera(int cameraId) {
        new Thread("surface") {
            @Override
            public void run() {
                Log.d(TAG, "SurfaceViewCallback run: ");
                mCameraSurfaceViewCallback = new CameraSurfaceViewCallback(TestItemCamera.this, cameraId, new TakePhotoListener() {
                    @Override
                    public void onSuccess(File bitFile, Bitmap bitmap) {
                        watchImg(bitFile, bitmap);
                    }

                    @Override
                    public void onFail(String error) {
                        mSurfaceView.setVisibility(View.VISIBLE);
                        mUrlPhoto.setVisibility(View.GONE);
                        mImgVwPhoto.setVisibility(View.GONE);
                    }
                });
                mCameraSurfaceViewCallback.setPostRotate(getPostRotate());
                mSurfaceHolder = mSurfaceView.getHolder();
                mSurfaceHolder.addCallback(mCameraSurfaceViewCallback);
                if (isTestModeAging()) {
                    onAgingTestItem(() -> mBtnTakePhoto.performClick(), 2000);
                }
            }
        }.start();
    }

    protected void watchImg(File url, Bitmap imgBit) {
        Log.d(TAG, "watchImg > ");

        mUrlPhoto.setText("图片路径:" + url.getPath());

        mImgVwPhoto.setImageBitmap(imgBit);
        if (isTestModeAging()) {
            onAgingTestItem(() -> onResult(true), 2000);
        }
    }
}