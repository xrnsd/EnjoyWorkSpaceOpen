package com.kuyou.avc.basic.photo;

import android.os.Bundle;

import com.kuyou.avc.ui.basic.ITakePhotoByCameraResultListener;

/**
 * action :拍照回调[截图]
 * <p>
 * remarks:  <br/>
 * author: wuguoxian <br/>
 * date: 21-8-24 <br/>
 * </p>
 */
public interface ITakePhotoByScreenshotResultCallback extends ITakePhotoByCameraResultListener {
    public Bundle getEventData();
}
