package com.kuyou.avc.handler.thermal.basic;

/**
 * action :红外回调
 * <p>
 * remarks:  <br/>
 * author: wuguoxian <br/>
 * date: 21-8-23 <br/>
 * </p>
 */
public interface IPeergineCameraCaptureCallBack {
    public void onPreviewFrame(byte[] data);
}
