package com.kuyou.avc.ui.basic;

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
public interface ITakePhotoByCameraResultListener {
    public void onTakePhotoResult(boolean result, String info, Bundle data);
}
