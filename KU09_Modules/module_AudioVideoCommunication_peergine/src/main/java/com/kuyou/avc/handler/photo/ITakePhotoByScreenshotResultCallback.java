package com.kuyou.avc.handler.photo;

import android.os.Bundle;

import kuyou.common.ipc.RemoteEvent;

/**
 * action :
 * <p>
 * remarks:  <br/>
 * author: wuguoxian <br/>
 * date: 21-8-24 <br/>
 * </p>
 */
public interface ITakePhotoByScreenshotResultCallback {
    public Bundle getEventData();
    public void onScreenshotResult(boolean result, String info, Bundle data);
}
