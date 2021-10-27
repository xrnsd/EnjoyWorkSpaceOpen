package com.kuyou.avc.basic.thermal;

import android.view.View;

/**
 * action :红外采集接口
 * <p>
 * remarks:  <br/>
 * author: wuguoxian <br/>
 * date: 21-8-23 <br/>
 * </p>
 */
public interface IPeergineCameraCaptureHandler {
    public View getView();
    public boolean start(int... vals);
    public void stop();
    public void screenshot();
}
