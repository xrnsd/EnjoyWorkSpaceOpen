package com.kuyou.ipc;

import android.app.Application;
import android.content.Intent;

import kuyou.common.ipc.FrameRemoteService;

/**
 * action :
 * <p>
 * remarks:  <br/>
 * author: wuguoxian <br/>
 * date: 21-7-22 <br/>
 * </p>
 */
public class ModuleApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        startService(new Intent(this, FrameRemoteService.class));
    }
}
