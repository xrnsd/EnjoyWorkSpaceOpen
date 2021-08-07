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

//import org.greenrobot.eventbus.Subscribe;
//import kuyou.common.ku09.event.common.base.EventKey;

/**
 * action :
 * <p>
 * author: wuguoxian <br/>
 * date: 20-11-25 <br/>
 * <p>
 */
public abstract class BaseActivity extends AppCompatActivity {
    protected final String TAG = "kuyou.common.ku09.ui > " + this.getClass().getSimpleName();

    private static final String KEY_WATCH_DOG_FLAG = "isLaunchByWatchDog";
    private static final String KEY_WATCH_DOG_RESTART_FLAG = "isLaunchByWatchDogRestart";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //EventBus.getDefault().register(this);
        if (-1 != getContentViewResId()) {
            super.setContentView(getContentViewResId());
        } else {
            throw new RuntimeException("getContentViewResId is none");
        }
        initViews();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        autoHideOrRestart(this);
    }

//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        EventBus.getDefault().unregister(this);
//    }

//    @Subscribe
//    public void onModuleEvent(RemoteEvent event) {
//        switch (event.getCode()) {
//            case EventKey.Code.KEY_CLICK:
//                onKeyClick(EventKey.getKeyCode(event));
//                break;
//            case EventKey.Code.KEY_DOUBLE_CLICK:
//                onKeyDoubleClick(EventKey.getKeyCode(event));
//                break;
//            case EventKey.Code.KEY_LONG_CLICK:
//                onKeyLongClick(EventKey.getKeyCode(event));
//                break;
//            default:
//                break;
//        }
//    }

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
        Log.d(TAG, "play > content= " + content);
        dispatchEvent(new EventTextToSpeechPlayRequest(content));
    }
}
