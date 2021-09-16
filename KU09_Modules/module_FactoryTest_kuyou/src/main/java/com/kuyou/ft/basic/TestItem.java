package com.kuyou.ft.basic;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.kuyou.ft.R;
import com.kuyou.ft.basic.event.EventTestItemResult;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import kuyou.common.ipc.RemoteEvent;
import kuyou.common.ipc.RemoteEventBus;
import kuyou.common.ku09.protocol.basic.IDeviceConfig;
import kuyou.common.status.StatusProcessBusCallbackImpl;
import kuyou.common.status.StatusProcessBusImpl;
import kuyou.common.status.basic.IStatusProcessBus;
import kuyou.common.status.basic.IStatusProcessBusCallback;

/**
 * action :测试功能项[抽象]
 * <p>
 * remarks:  <br/>
 * author: wuguoxian <br/>
 * date: 21-3-9 <br/>
 * </p>
 */
public abstract class TestItem extends Activity implements View.OnClickListener {
    protected final String TAG = "com.kuyou.ft.basic > " + this.getClass().getSimpleName();

    protected final static String KEY_TEST_PROCESS_TYPE = "test.process.type";

    public static final int POLICY_TEST = (1 << 0);
    public static final int POLICY_TEST_AUTO = (1 << 1);
    public static final int POLICY_TEST_AGING = (1 << 2);

    protected final static int PS_TIMING = 2048;
    protected TextView mTvTiming;
    private int mTiming = -1;

    protected ViewGroup mContent, mBtns;
    protected Button mBtnItem, mBtnSuccess, mBtnFailed;
    protected TextView mTvTitle;

    private Handler mHandlerAging;
    private List<Runnable> mRunnableListAging;
    private IStatusProcessBus mStatusProcessBus;

    private IDeviceConfig mDeviceConfig;

    public int getTestPolicy() {
        int policy = 0;
        policy |= POLICY_TEST;
        policy |= POLICY_TEST_AUTO;
        policy |= POLICY_TEST_AGING;
        return policy;
    }

    public abstract int getTestId();

    public abstract int getSubContentId();

    public abstract String getTestTitle(Context context);

    public IStatusProcessBus getStatusProcessBus() {
        initStatusProcessBus();
        return mStatusProcessBus;
    }

    public void initStatusProcessBus() {
        if (null != mStatusProcessBus) {
            return;
        }
        mStatusProcessBus = new StatusProcessBusImpl() {
            @Override
            protected void onReceiveProcessStatusNotice(int statusCode, boolean isRemove) {
                TestItem.this.onReceiveProcessStatusNotice(statusCode, isRemove);
            }
        };
        initReceiveProcessStatusNotices();
    }

    protected void initReceiveProcessStatusNotices() {
        if (-1 != getTimingFlag()) {
            mTiming = getTimingFlag() - 1;
            getStatusProcessBus().registerStatusNoticeCallback(PS_TIMING, new StatusProcessBusCallbackImpl(true, 1000)
                    .setNoticeHandleLooperPolicy(IStatusProcessBusCallback.LOOPER_POLICY_MAIN)
                    .setEnableReceiveRemoveNotice(true));
        }
    }

    protected void onReceiveProcessStatusNotice(int statusCode, boolean isRemove) {
        if (PS_TIMING == statusCode && -1 != getTimingFlag()) {
            runOnUiThread(() -> refreshTiming(isRemove));
        }
    }

    protected void refreshTiming(boolean isRemove) {
        if (null == mTvTiming) {
            return;
        }
        if (isRemove || mTiming <= 0) {
            mTvTiming.setVisibility(View.GONE);
            return;
        }
        mTvTiming.setText(String.valueOf(mTiming));
        mTiming -= 1;
        return;
    }

    protected int getTimingFlag() {
        return -1;
    }

    public void onReceiveEventNotice(RemoteEvent event) {

    }

    protected IDeviceConfig getDeviceConfig() {
        return mDeviceConfig;
    }

    public void setDeviceConfig(IDeviceConfig deviceConfig) {
        mDeviceConfig = deviceConfig;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initWindowConfig();
        initViews();
        setTitle(getTestTitle(TestItem.this));
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
        if (isTestModeAging()) {
            onAging();
        }
    }

    @Override
    public void finish() {
        resetAging();
        super.finish();
    }

    @Override
    protected void onDestroy() {
        resetAging();
        super.onDestroy();
    }

    @Override
    public boolean onKeyDown(int paramInt, KeyEvent paramKeyEvent) {
        Log.i(TAG, "paramInt" + paramInt);
        switch (paramInt) {
            case KeyEvent.KEYCODE_MENU:
                if (exitAgingTest()) {
                    return true;
                }
                Log.i(TAG, "key_menu pressed");
                return true;
            case KeyEvent.KEYCODE_BACK:
                if (exitAgingTest()) {
                    return true;
                }
                Log.i(TAG, "key_back pressed");
                return true;
            case KeyEvent.KEYCODE_HOME:
                if (exitAgingTest()) {
                    return true;
                }
                Log.i(TAG, "key_home pressed");
                return true;
            case KeyEvent.KEYCODE_SEARCH:
                Log.i(TAG, "key_search pressed");
                return true;
            case KeyEvent.KEYCODE_VOLUME_UP:
                Log.i(TAG, "key_volumeUp pressed");
                return true;
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                Log.i(TAG, "key_volumeDown pressed");
                return true;
            case KeyEvent.KEYCODE_POWER:
                Log.i(TAG, "key_power pressed");
                return true;
            default:
                return true;
        }
    }

    @Override
    public void onClick(View v) {
        try {
            switch (v.getId()) {
                case R.id.success:
                    onResult(true);
                    break;
                case R.id.failed:
                    onResult(false);
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }
    }

    protected void dispatchEvent(RemoteEvent event) {
        RemoteEventBus.getInstance().dispatch(event);
    }

    protected void initWindowConfig() {
        requestWindowFeature(1);
        getWindow().addFlags(1024);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    protected void setTestResultTitle(String result) {
        if (null == mTvTitle) {
            Log.e(TAG, "setTestResultTitle > process fail : mTvTitle null");
            return;
        }
        runOnUiThread(() -> mTvTitle.setText(result));
    }

    protected void initViews() {
        if (-1 == getSubContentId() || 0 == getSubContentId()) {
            return;
        }
        setContentView(R.layout.test_item);
        mContent = (ViewGroup) LayoutInflater.from(TestItem.this).inflate(getSubContentId(),
                findViewById(R.id.content), true);
        mBtns = findViewById(R.id.btns);

        mBtnSuccess = findViewById(R.id.success);
        mBtnFailed = findViewById(R.id.failed);

        mBtnSuccess.setOnClickListener(TestItem.this);
        mBtnFailed.setOnClickListener(TestItem.this);
    }

    /**
     * 测试结果
     */
    protected void onResult(boolean status) {
        Log.d(TAG, "onResult > status = " + status);
        EventBus.getDefault().post(new EventTestItemResult(getTestId(), status));
        finish();
    }

    protected int getHardwareModuleTypeId() {
        return -1;
    }

    public boolean isEnableTest() {
        if (-1 != getHardwareModuleTypeId()
                && null != getDeviceConfig()
                && !getDeviceConfig().isHardwareModuleCarry(getHardwareModuleTypeId())) {
            return false;
        }
        return (getTestPolicy() & POLICY_TEST) != 0;
    }

    public boolean isEnableTestByFlag(int flag) {
        return (getTestPolicy() & flag) != 0;
    }

    protected View.OnClickListener getListener() {
        return TestItem.this;
    }

    /**
     * 占据的列数，默认为2
     */
    public int getOccupyColumns() {
        return 2;
    }

    public Button getButton(Context context, Class<?> cls, int testProcessType) {
        if (null != mBtnItem)
            return mBtnItem;
        mBtnItem = (Button) LayoutInflater.from(context).inflate(R.layout.test_entrance_item, null);
        mBtnItem.setId(getTestId());
        mBtnItem.setText(getTestTitle(context));
        mBtnItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent targetItem = new Intent();
                targetItem.setClass(context, cls);
                targetItem.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                targetItem.putExtra(KEY_TEST_PROCESS_TYPE, testProcessType);
                context.startActivity(targetItem);
            }
        });
        return mBtnItem;
    }

    protected boolean isTestModeAuto() {
        Intent intent = getIntent();
        if (!intent.hasExtra(KEY_TEST_PROCESS_TYPE))
            return false;
        int flag = intent.getIntExtra(KEY_TEST_PROCESS_TYPE, -1);
        return flag == POLICY_TEST_AUTO;
    }

    protected boolean isTestModeAging() {
        Intent intent = getIntent();
        if (!intent.hasExtra(KEY_TEST_PROCESS_TYPE))
            return false;
        int flag = intent.getIntExtra(KEY_TEST_PROCESS_TYPE, -1);
        return flag == POLICY_TEST_AGING;
    }

    protected void onAging() {
        mBtnSuccess.setVisibility(View.INVISIBLE);
        mBtnFailed.setVisibility(View.INVISIBLE);
    }

    protected void onAgingTestItem(Runnable runnable, int delay) {
        if (null == mHandlerAging) {
            mHandlerAging = new Handler(Looper.getMainLooper());
            mRunnableListAging = new ArrayList<>();
        }
        mRunnableListAging.add(runnable);
        mHandlerAging.postDelayed(runnable, delay);
    }

    private void resetAging() {
        if (null == mHandlerAging || mRunnableListAging.size() == 0)
            return;
        for (Runnable run : mRunnableListAging) {
            mHandlerAging.removeCallbacks(run);
        }
        mHandlerAging = null;
    }

    private boolean exitAgingTest() {
        if (!isEnableTestByFlag(POLICY_TEST_AGING) || !isTestModeAging())
            return false;
        EventBus.getDefault().post(new EventTestItemResult(R.id.test_aging, false));
        finish();
        return true;
    }
}
