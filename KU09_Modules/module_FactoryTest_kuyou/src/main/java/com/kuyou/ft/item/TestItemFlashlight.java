package com.kuyou.ft.item;

import android.annotation.TargetApi;
import android.content.Context;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.os.Build;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import com.kuyou.ft.R;
import com.kuyou.ft.basic.TestItemBasic;

public class TestItemFlashlight extends TestItemBasic {

    private boolean isopen = false;

    private Button mBtnOn, mBtnOff;
    private CameraManager mCameraManager;//add chh

    @Override
    public int getTestId() {
        return R.id.test_flash_light;
    }

    @Override
    public int getSubContentId() {
        return R.layout.test_item_flash_light;
    }

    @Override
    public String getTestTitle(Context context) {
        return context.getString(R.string.test_flash_light);
    }

    @Override
    protected void initWindowConfig() {
        //super.initWindowConfig();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    @Override
    protected void initViews() {
        super.initViews();
        mBtnOn = findViewById(R.id.open_flashlight);
        mBtnOff = findViewById(R.id.close_flashlight);
        mBtnOn.setOnClickListener(TestItemFlashlight.this);
        mBtnOff.setOnClickListener(TestItemFlashlight.this);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.open_flashlight:
                switchFlashLight(getApplicationContext(), true);
                break;
            case R.id.close_flashlight:
                switchFlashLight(getApplicationContext(), false);
                break;
            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        if (isopen) {
            switchFlashLight(getApplicationContext(), false);
        }
        super.onDestroy();
    }

    @TargetApi(25)
    public void switchFlashLight(Context context, boolean openOrClose) {
        //判断API是否大于24（安卓7.0系统对应的API）
        if (Build.VERSION.SDK_INT >= 21) {
            try {
                //获取CameraManager
                mCameraManager = (android.hardware.camera2.CameraManager) context.getSystemService("camera");
                //获取当前手机所有摄像头设备ID
                String[] ids = mCameraManager.getCameraIdList();
                for (String id : ids) {
                    CameraCharacteristics c = mCameraManager.getCameraCharacteristics(id);
                    //查询该摄像头组件是否包含闪光灯
                    Boolean flashAvailable = c.get(CameraCharacteristics.FLASH_INFO_AVAILABLE);
                    Integer lensFacing = c.get(CameraCharacteristics.LENS_FACING);
                    if (flashAvailable != null && flashAvailable
                            && lensFacing != null && lensFacing == CameraCharacteristics.LENS_FACING_BACK) {
                        //打开或关闭手电筒
                        mCameraManager.setTorchMode(id, openOrClose);
                    }
                }
                isopen = openOrClose;
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onAging() {
        super.onAging();
        onAgingTestItem(() -> switchFlashLight(getApplicationContext(), true), 0);
        onAgingTestItem(() -> switchFlashLight(getApplicationContext(), false), 2500 * 1);
        onAgingTestItem(() -> onResult(true), 2500 * 2);
    }
}

