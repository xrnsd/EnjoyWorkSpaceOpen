package com.kuyou.ft.basic;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;

import com.kuyou.ft.basic.event.EventTestItemResult;
import com.kuyou.ft.util.ClassUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kuyou.common.utils.SystemPropertiesUtils;

/**
 * action :测试流程入口[抽象]
 * <p>
 * remarks:  <br/>
 * author: wuguoxian <br/>
 * date: 21-3-15 <br/>
 * </p>
 */
public abstract class TestEntrance extends Activity {

    protected final static String TAG = "com.kuyou.ft.basic.camera > TestEntrance";

    protected final static String KEY_AGING_MODE = "persist.ft.mode";
    protected static Map<Integer, TestItem> sTestItemList = new HashMap<Integer, TestItem>();

    /**
     * action:当前测试流程的测试类型
     */
    protected abstract int getTestProcessType();

    /**
     * action:测试流程需要的测试项列表
     * 此方法可以控制测试项加载顺序
     * 此方法返回列表不为空:getTestItemListNon返回结果失效
     */
    protected List<Class> getTestItemList() {
        return null;
    }

    /**
     * action:测试流程不需要的测试项列表
     * 此方法需确认:getTestItemList返回结果为空
     */
    protected List<Class> getTestItemListNon() {
        return null;
    }

    protected void onTestItemLoaded(TestItem item) {

    }

    protected void initViews() {

    }

    /**
     * action:自动搜索所有测试项列表
     */
    protected List<Class> getTestItemListAll(Context context) {
        try {
            return ClassUtils.getAllClasses(context, TestItem.class);
        } catch (Exception e) {
            Log.e("123456", Log.getStackTraceString(e));
        }
        return null;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addFlag();
        loadAllTestItem(getApplicationContext());
        EventBus.getDefault().register(this);
        initViews();
    }

    @Override
    protected void onResume() {
        if (getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        clearFlag();
    }

    protected void loadAllTestItem(Context context) {
        TestItem testItem;
        sTestItemList.clear();
        try {
            List<Class> list = getTestItemList();
            if (null == list) {
                list = getTestItemListAll(context);
                List<Class> listNon = getTestItemListNon();
                if (null != listNon) {
                    for (Class item : listNon) {
                        list.remove(item);
                        Log.w(TAG, "loadAllTestItem > 本流程无需测试项 : " + item);
                    }
                }
            }
            for (Class item : list) {
                testItem = (TestItem) item.newInstance();
                if (!testItem.isEnableTest() || !testItem.isEnableTestByFlag(getTestProcessType())) {
                    Log.w(TAG, "loadAllTestItem > 测试项已关闭 : " + testItem.getTestTitle(context));
                    continue;
                }
                testItem.getButton(context, testItem.getClass(), getTestProcessType());
                sTestItemList.put(testItem.getTestId(), testItem);
                onTestItemLoaded(testItem);
                Log.d(TAG, "loadAllTestItem > add item = " + testItem.getTestTitle(context));
            }

            Log.d(TAG, "loadAllTestItem > process finish > count = " + sTestItemList.size());
        } catch (Exception e) {
            Log.e(TAG, "loadAllTestItem > process fail ");
            Log.e(TAG, Log.getStackTraceString(e));
            return;
        }
    }

    @Subscribe
    public void onEventTestItemResult(EventTestItemResult event) {

    }

    private void addFlag() {
        SystemPropertiesUtils.set(KEY_AGING_MODE, getTestProcessType());
    }

    private void clearFlag() {
        SystemPropertiesUtils.set(KEY_AGING_MODE, -1);
    }
}
