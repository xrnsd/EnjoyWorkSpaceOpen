package com.kuyou.avc.handler.thermal.basic;

import android.view.View;

/**
 * action :
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
