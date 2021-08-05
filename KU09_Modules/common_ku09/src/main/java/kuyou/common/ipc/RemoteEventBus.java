package kuyou.common.ipc;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * action :事件远程分发器
 * <p>
 * remarks:  <br/>
 * author: wuguoxian <br/>
 * date: 21-3-27 <br/>
 * </p>
 */
public class RemoteEventBus implements IRemoteConfig {

    protected String TAG = this.getClass().getSimpleName();

    private Context mContext;
    private static RemoteEventBus sMain;
    private List<String> mAllModulePackageNameList = null;

    private RemoteEventBus() {
    }

    public static RemoteEventBus getInstance() {
        if (null == sMain) {
            sMain = new RemoteEventBus();
        }
        return sMain;
    }

    public boolean register(Object subscriber, Context context) {
        TAG = new StringBuilder()
                .append(" com.kuyou.ipc.frame > ")
                .append(context.getPackageName()).append(" > ")
                .append(this.getClass().getSimpleName())
                .toString();

        if (isEventDispatchProcess(context)) {
            return false;
        }

        EventBus.getDefault().register(subscriber);
        mContext = context.getApplicationContext();
        RemoteModuleInfoProvider.addModulePackageName(context);
        context.getContentResolver().registerContentObserver(
                RemoteModuleInfoProvider.MODULE_PACKAGE_NAME_CONTENT_URI, true,
                new OnRemoteModuleListChangeListener(new Handler(Looper.getMainLooper())));

        Log.d(TAG, "addModulePackageName > PackageName = " + context.getPackageName());
        return true;
    }

    protected List<String> getAllModulePackageName() {
        if (null == mAllModulePackageNameList || mAllModulePackageNameList.size() <= 0)
            mAllModulePackageNameList = RemoteModuleInfoProvider.getAllModulePackageName(mContext, false);
        return mAllModulePackageNameList;
    }

    public void dispatch(RemoteEvent event) {
        if (!event.isRemote()) {
            EventBus.getDefault().post(event);
            return;
        }

        List<String> allModule = getAllModulePackageName();
        if (null == allModule || allModule.size() <= 0) {
            Log.e(TAG, "dispatchEvent2Remote > process fail : mAllModulePackageNameList is null");
            return;
        }

        Intent intent = new Intent();
        intent.setAction(ACTION_FALG_ENVENT);
        intent.addFlags(Intent.FLAG_RECEIVER_FOREGROUND);
        intent.putExtras(event.getData());
        StringBuilder dispatchInfo = new StringBuilder("dispatchEvent2Remote > ");
        dispatchInfo.append(" event code = ").append(event.getCode());
        for (String module : allModule) {
            intent.setPackage(module);
            mContext.sendBroadcast(intent);
            dispatchInfo.append(" \n event module = ").append(module);
        }
        Log.d(TAG, dispatchInfo.toString());
    }

    public void onRemote2LocalFinish(RemoteEvent event) {
        EventBus.getDefault().post(event);
    }

    private boolean isEventDispatchProcess(Context context) {
        String processNameMain = context.getPackageName();
        String processName = getCurrentProcessName(context);
        boolean val = !processNameMain.equals(getCurrentProcessName(context));
        if(val){
            Log.w(TAG, "register > isEventDispatchProcess > 非主进程,processName = "+processName);
        }
        return val;
    }

    private String getCurrentProcessName(Context context) {
        int pid = android.os.Process.myPid();
        String processName = "";
        ActivityManager manager = (ActivityManager) context.getSystemService
                (Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningAppProcessInfo process : manager.getRunningAppProcesses()) {
            if (process.pid == pid) {
                processName = process.processName;
            }
        }
        return processName;
    }

    public class OnRemoteModuleListChangeListener extends ContentObserver {
        /**
         * Creates a content observer.
         *
         * @param handler The handler to run {@link #onChange} on, or null if none.
         */
        public OnRemoteModuleListChangeListener(Handler handler) {
            super(handler);
        }

        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            mAllModulePackageNameList = RemoteModuleInfoProvider.getAllModulePackageName(mContext, false);
        }
    }
}
