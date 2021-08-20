package kuyou.common.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;

import kuyou.common.protocol.flow.ILifeListener;

/**
 * action :调试用基础UI节目实现
 * <p>
 * remarks:  <br/>
 * author: wuguoxian <br/>
 * date: 21-1-13 <br/>
 * </p>
 */
public abstract class ActivityBase extends Activity {
    protected final String TAG = "kuyou.common.ui > " + this.getClass().getSimpleName();

    private ProgressDialog mProgressDialog;

    protected abstract void initViews();

    protected abstract void initConfigs();

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        initViews();
        initConfigs();
        if (null != getLiftListener())
            getLiftListener().onCreate(bundle);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (null != getLiftListener())
            getLiftListener().onStart();

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (null != getLiftListener())
            getLiftListener().onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (null != getLiftListener())
            getLiftListener().onPause();

    }

    @Override
    protected void onStop() {
        super.onStop();
        if (null != getLiftListener())
            getLiftListener().onStop();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != getLiftListener())
            getLiftListener().onDestroy();

    }

    protected void showProcess(Context context, String title) {
        if (null == mProgressDialog)
            mProgressDialog = ProgressDialog.show(context, title, "初始化中，请稍等...");
        mProgressDialog.show();
    }

    protected void dismissProgress() {
        if (null == mProgressDialog)
            return;
        mProgressDialog.dismiss();
    }

    protected ILifeListener getLiftListener() {
        return null;
    }
}
