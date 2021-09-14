package com.kuyou.ft.item;

import android.content.Context;

import com.kuyou.ft.R;
import com.kuyou.ft.basic.camera.TestItemCamera;

public class TestItemCameraBack extends TestItemCamera {

    @Override
    public int getTestId() {
        return R.id.test_bcamera;
    }

    @Override
    public String getTestTitle(Context context) {
        return context.getString(R.string.bcamera_test);
    }

    @Override
    protected int getCameraId() {
        return 0;
    }

    @Override
    protected int getPostRotate() {
        return 90;
    }
}