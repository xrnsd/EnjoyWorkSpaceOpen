package com.kuyou.ft.basic.camera;

import android.graphics.Bitmap;

import java.io.File;

/**
 * 监听拍照
 * 通过file与bitmap可以在ui界面输出保存路径与获取的图片
 */
public interface TakePhotoListener {

    void onSuccess(File bitFile, Bitmap bitmap);

    void onFail(String error);
}
