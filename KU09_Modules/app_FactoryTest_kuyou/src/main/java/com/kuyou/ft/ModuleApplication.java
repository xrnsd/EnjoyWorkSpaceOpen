package com.kuyou.ft;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.kuyou.ft.basic.TestEntrance;
import com.kuyou.ft.basic.TestItemBasic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kuyou.common.ipc.RemoteEvent;
import kuyou.common.ku09.BasicModuleApplication;
import kuyou.common.ku09.event.rc.basic.EventRemoteControl;
import kuyou.common.ku09.event.vc.basic.EventVoiceControl;

/**
 * action :工厂测试模块
 * <p>
 * remarks:  <br/>
 * author: wuguoxian <br/>
 * date: 21-9-14 <br/>
 * </p>
 */
public class ModuleApplication extends BasicModuleApplication implements Application.ActivityLifecycleCallbacks {

    protected Map<Integer, TestItemBasic> mItemTestList = new HashMap<>();

    @Override
    protected void init() {
        super.init();
        registerActivityLifecycleCallbacks(ModuleApplication.this);
    }

    protected List<Integer> getEventDispatchList() {
        List<Integer> eventCode = new ArrayList<>();
        eventCode.add(EventRemoteControl.Code.HARDWARE_MODULE_STATUS_DETECTION_RESULT);
        eventCode.add(EventVoiceControl.Code.VOICE_WAKEUP_RESULT);
        return eventCode;
    }

    @Override
    protected void initRegisterEventHandlers() {

    }

    @Override
    public void onReceiveEventNotice(RemoteEvent event) {
        super.onReceiveEventNotice(event);

        for (Map.Entry<Integer, TestItemBasic> entry : getItemTestList().entrySet()) {
            entry.getValue().onReceiveEventNotice(event);
        }
    }

    @Override
    public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {
        addTestItem(activity);
        addEntrance(activity);
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

    public Map<Integer, TestItemBasic> getItemTestList() {
        return mItemTestList;
    }

    public void addTestItem(Activity activity) {
        if (!(activity instanceof TestItemBasic)) {
            return;
        }
        TestItemBasic item = (TestItemBasic) activity;
        synchronized (mItemTestList) {
            mItemTestList.put(item.getTestId(), item);
        }
        item.setDeviceConfig(getDeviceConfig());
        item.initStatusProcessBus();
    }

    public void addEntrance(Activity activity) {
        if (!(activity instanceof TestEntrance)) {
            return;
        }
        TestEntrance entrance = (TestEntrance) activity;
        entrance.setDeviceConfig(getDeviceConfig());
    }
}
