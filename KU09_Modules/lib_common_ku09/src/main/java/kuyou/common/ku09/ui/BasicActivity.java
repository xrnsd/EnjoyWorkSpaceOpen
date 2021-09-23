package kuyou.common.ku09.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import kuyou.common.ipc.RemoteEvent;
import kuyou.common.ipc.RemoteEventBus;
import kuyou.common.ku09.event.tts.EventTextToSpeechPlayRequest;

/**
 * action :模块UI界面通用基础实现[抽象]
 * <p>
 * author: wuguoxian <br/>
 * date: 20-11-25 <br/>
 * <p>
 */
public abstract class BasicActivity extends AppCompatActivity {
    protected final String TAG = "kuyou.common.ku09.ui > BaseActivity";

    private static final String KEY_WATCH_DOG_FLAG = "isLaunchByWatchDog";
    private static final String KEY_WATCH_DOG_RESTART_FLAG = "isLaunchByWatchDogRestart";

    protected boolean isEnableContentView() {
        return true;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //EventBus.getDefault().register(this);
        if (-1 != getContentViewResId()) {
            super.setContentView(getContentViewResId());
        } else if (isEnableContentView()) {
            throw new RuntimeException("getContentViewResId is none");
        }
        if (isEnableContentView()) {
            initViews();
        } else {
            init();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        autoHideOrRestart(this);
    }

    protected void init() {

    }

    protected void initViews() {

    }

    @Override
    public void setContentView(int layoutResID) {
        throw new RuntimeException("setContentView is diable > please use getContentViewResId()");
    }

    @Override
    public void setContentView(View view) {
        throw new RuntimeException("setContentView is diable > please use getContentViewResId()");
    }

    protected int getContentViewResId() {
        return -1;
    }

    protected void autoHideOrRestart(Activity context) {
        if (null == context || null == context.getIntent()) {
            return;
        }
        if (context.getIntent().hasExtra(KEY_WATCH_DOG_RESTART_FLAG)) {
            Log.d(TAG, "autoHideOrRestart > restart ");
            android.os.Process.killProcess(android.os.Process.myPid());
            return;
        }
        if (context.getIntent().hasExtra(KEY_WATCH_DOG_FLAG)) {
            Log.d(TAG, "autoHideOrRestart > auto hide ");
            context.onBackPressed();
        }
    }

    protected void dispatchEvent(RemoteEvent event) {
        RemoteEventBus.getInstance().dispatch(event);
    }

    protected void play(String content) {
        //Log.d(TAG, "play > content= " + content);
        dispatchEvent(new EventTextToSpeechPlayRequest(content));
    }
}
