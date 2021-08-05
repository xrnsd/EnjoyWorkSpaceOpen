package com.kuyou.rc.location.base;

import android.content.Context;

/**
 * action :
 * <p>
 * remarks:  <br/>
 * author: wuguoxian <br/>
 * date: 21-7-28 <br/>
 * </p>
 */
public interface ILocationProviderLiveCallback {
    public void startLocation(boolean isBackground);
    public void stopLocation();
}
