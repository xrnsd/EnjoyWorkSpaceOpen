package kuyou.common.ipc;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * action :远程事件接收，本地分发器[抽象]
 * <p>
 * remarks:  <br/>
 * author: wuguoxian <br/>
 * date: 21-3-24 <br/>
 * </p>
 */
public abstract class RemoteEventConverter extends BroadcastReceiver implements IRemoteConfig {

    protected String TAG = null;

    protected List<Integer> mEventDispatchList = null;

    public RemoteEventConverter() {
        super();
        mEventDispatchList = new ArrayList<>();
        initEventDispatchList();
    }

    /**
     * action:初始化需要分发的的event_code
     */
    protected abstract void initEventDispatchList();

    protected List<Integer> getEventDispatchList() {
        return mEventDispatchList;
    }

    protected void addEventCodeLocalDisPatch(int eventCode) {
        getEventDispatchList().add(eventCode);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (null == TAG) {
            TAG = new StringBuilder()
                    .append(" com.kuyou.ipc.frame > ")
                    .append(context.getPackageName()).append(" > ")
                    .append(this.getClass().getSimpleName())
                    .toString();
        }
        switch (intent.getAction()) {
            case ACTION_FALG_ENVENT:
                onRemote2Local(intent);
                break;
            default:
                Log.e(TAG, "onReceive > process fail : invalid intent = " + intent);
                break;
        }
    }

    protected void onRemote2Local(final Intent intent) {
        if (null == intent || null == intent.getExtras()) {
            Log.w(TAG, "dispatchEvent2Local > process fail : EventIntent is invalid");
            return;
        }
        int eventCode = intent.getExtras().getInt(RemoteEvent.KEY_EVENT_CODE);
        if (-1 == getEventDispatchList().indexOf(eventCode)) {
            Log.d(TAG, "dispatchEvent2Local >  give up eventCode = " + eventCode);
            return;
        }
        Log.d(TAG, "dispatchEvent2Local > eventCode = " + eventCode);
        RemoteEventBus.getInstance().onRemote2LocalFinish(new RemoteEvent() {
            @Override
            public int getCode() {
                return eventCode;
            }
        }.setData(intent.getExtras()));
    }
}
