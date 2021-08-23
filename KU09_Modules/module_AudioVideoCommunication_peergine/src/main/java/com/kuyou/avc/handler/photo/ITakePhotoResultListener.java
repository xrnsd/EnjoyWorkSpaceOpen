package com.kuyou.avc.handler.photo;

import android.os.Bundle;

import kuyou.common.ipc.RemoteEvent;

/**
 * action :接口[拍照结果]
 * <p>
 * remarks:  <br/>
 * author: wuguoxian <br/>
 * date: 21-8-13 <br/>
 * </p>
 */
public interface ITakePhotoResultListener {
    public void onTakePhotoResult(boolean result, String info, Bundle data);
}
