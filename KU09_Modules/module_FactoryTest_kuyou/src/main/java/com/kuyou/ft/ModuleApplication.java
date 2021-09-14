package com.kuyou.ft;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.kuyou.ft.item.TestItemHwSwInfo;

import kuyou.common.ku09.BasicModuleApplication;

/**
 * action :
 * <p>
 * remarks:  <br/>
 * author: wuguoxian <br/>
 * date: 21-9-14 <br/>
 * </p>
 */
public class ModuleApplication extends BasicModuleApplication implements Application.ActivityLifecycleCallbacks {

    @Override
    protected void init() {
        super.init();
        registerActivityLifecycleCallbacks(ModuleApplication.this);
    }

    @Override
    protected void initRegisterEventHandlers() {

    }

    @Override
    public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {
        if (activity instanceof TestItemHwSwInfo) {
            TestItemHwSwInfo info = (TestItemHwSwInfo) activity;
            info.buildVersionContent(ModuleApplication.this.getDeviceConfig());
        }
    }

    @Override
    public void onActivityStarted(@NonNull Activity activity) {

    }

    @Override
    public void onActivityResumed(@NonNull Activity activity) {

    }

    @Override
    public void onActivityPaused(@NonNull Activity activity) {

    }

    @Override
    public void onActivityStopped(@NonNull Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(@NonNull Activity activity) {

    }
}
