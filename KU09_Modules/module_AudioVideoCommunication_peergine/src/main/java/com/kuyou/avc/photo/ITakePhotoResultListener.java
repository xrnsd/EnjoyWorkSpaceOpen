package com.kuyou.avc.photo;

import android.os.Bundle;

import kuyou.common.ipc.RemoteEvent;

/**
 * action :
 * <p>
 * remarks:  <br/>
 * author: wuguoxian <br/>
 * date: 21-8-13 <br/>
 * </p>
 */
public interface ITakePhotoResultListener {
    public void onTakePhotoResult(boolean result, String info, Bundle data);
}
