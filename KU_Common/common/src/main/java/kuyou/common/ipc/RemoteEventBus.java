package kuyou.common.ipc;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import kuyou.common.ipc.base.IRemoteConfig;
import kuyou.common.ipc.base.IRemoteEventHandler;

/**
 * action :事件远程分发器
 * <p>
 * remarks:  <br/>
 * author: wuguoxian <br/>
 * date: 21-3-27 <br/>
 * </p>
 */
public class RemoteEventBus implements IRemoteConfig {

    protected String mTagLog = "kuyou.common.ipc > RemoteEventBus";

    private Context mContext;
    private static RemoteEventBus sMain;
    private IRemoteEventHandler mRemoteEventHandler;
    private FrameEventHandler mFrameEventHandler;

    private boolean mIsBound = false;
    private IRemoteService mService = null;
    private ServiceConnection mConnection = null;

    private RemoteEventBus(Context context) {
        mContext = context.getApplicationContext();
        mFrameEventHandler = FrameEventHandler.getInstance();
    }

    public static RemoteEventBus getInstance(Context context) {
        if (null == sMain) {
            sMain = new RemoteEventBus(context);
        }
        return sMain;
    }

    public static RemoteEventBus getInstance() {
        if (null == sMain) {
            throw new NullPointerException("RemoteEventBus is null\nplease perform method \"RemoteEventBus.getInstance(Context context)\"");
        }
        if (null == sMain.mContext) {
            throw new NullPointerException("context is null");
        }
        return sMain;
    }

    protected Context getContext() {
        return mContext;
    }

    public IRemoteEventHandler getRemoteEventHandler() {
        return mRemoteEventHandler;
    }

    public RemoteEventBus register(IRegisterConfig config) {
        startIPCDaemon(getContext());

        EventBus.getDefault().register(config.getLocalEventDispatchHandler());

        mRemoteEventHandler = RemoteEventHandler.getInstance()
                .setLocalModulePackageName(getContext().getPackageName())
                .setEventDispatchList(config.getEventDispatchList());

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                bindIPCService(mContext);
            }
        }, 1000);

        if (null != mRemoteEventHandler.getLocalModulePackageName()) {
            mTagLog = new StringBuilder(mTagLog).append(" > ").append(
                    mRemoteEventHandler.getLocalModulePackageName()).
                    toString();
        }

        setFrameLiveListener(config.getFrameLiveListener());

        Log.d(mTagLog, "register > ");

        return RemoteEventBus.this;
    }

    private void dispatchRemoteEventFrameStatus(int code) {
        mFrameEventHandler.dispatchFrameStatus(code);
    }

    private void startIPCDaemon(Context context) {
        Intent intent = new Intent();
        intent.setAction(ACTION_FLAG_FRAME_EVENT);
        intent.setPackage("com.kuyou.ipc");
        intent.addFlags(Intent.FLAG_RECEIVER_FOREGROUND);
        context.sendBroadcast(intent);
    }

    private void bindIPCService(Context context) {
        mConnection = new ServiceConnection() {
            public void onServiceConnected(ComponentName className, IBinder service) {
                Log.d(mTagLog, "onServiceConnected: ");
                dispatchRemoteEventFrameStatus(Code.BIND_IPC_SERVICE_SUCCESS);
                mIsBound = true;
                mService = IRemoteService.Stub.asInterface(service);

                try {
                    mService.registerCallback(mContext.getApplicationContext().getPackageName(),
                            new IRemoteServiceCallBack.Stub() {
                                @Override
                                public void onReceiveEvent(Bundle data) {
                                    Log.d(mTagLog, "onReceiveEvent > data = " + data);
                                    mRemoteEventHandler.remoteEvent2LocalEvent(data);
                                }

                                @Override
                                public List<String> getReceiveEventFlag() {
                                    return null;
                                }
                            });
                    RemoteEventBus.this.onResisterSuccess();
                } catch (Exception e) {
                    Log.e(mTagLog, Log.getStackTraceString(e));
                    RemoteEventBus.this.onUnResister();
                }
            }

            public void onServiceDisconnected(ComponentName className) {
                dispatchRemoteEventFrameStatus(Code.UNBIND_IPC_SERVICE);
                Log.d(mTagLog, "onServiceDisconnected: ");
                mIsBound = false;
                mService = null;
                RemoteEventBus.this.onUnResister();
            }
        };

        Intent intent = new Intent();
        intent.setPackage("com.kuyou.ipc");
        intent.setAction("kuyou.common.ipc.FrameRemoteService");
        context.bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }

    public boolean isRegister(String packageName) {
        if (null != mService) {
            try {
                return mService.getRegisterModules().contains(packageName);
            } catch (Exception e) {
                Log.e(mTagLog, Log.getStackTraceString(e));
            }
        }
        return true;
    }

    public void dispatch(RemoteEvent event) {
        if (!event.isRemote()) {
            EventBus.getDefault().post(event);
            //Log.d(mTagLog, "dispatch > is not remote ");
            return;
        }
        if (mService == null) {
            //Log.e(mTagLog, "dispatch > IPC service is not bind");
            return;
        }
        try {
            //Log.d(mTagLog, "dispatch > event " + event.getCode());
            mService.sendEvent(event.getData());
        } catch (Exception e) {
            Log.e(mTagLog, Log.getStackTraceString(e));
        }
    }

    private IFrameLiveListener mFrameLiveListener;

    public RemoteEventBus setFrameLiveListener(IFrameLiveListener frameLiveListener) {
        mFrameLiveListener = frameLiveListener;
        return RemoteEventBus.this;
    }

    protected void onResisterSuccess() {
        if (null == mFrameLiveListener) {
            //Log.d(mTagLog, "onResisterSuccess > mFrameLiveListener is null");
            return;
        }
        mFrameLiveListener.onIpcFrameResisterSuccess();
    }

    protected void onUnResister() {
        if (null == mFrameLiveListener) {
            //Log.d(mTagLog, "onUnResister > mFrameLiveListener is null");
            return;
        }
        mFrameLiveListener.onIpcFrameUnResister();
    }

    public static interface IFrameLiveListener {
        public void onIpcFrameResisterSuccess();

        public void onIpcFrameUnResister();
    }

    public static interface IRegisterConfig {
        /**
         * action:指定IPC框架状态监听器
         */
        public IFrameLiveListener getFrameLiveListener();

        /**
         * action:指定想要接收的远程事件ID列表
         */
        public List<Integer> getEventDispatchList();

        /**
         * action:指定远程事件转发到本地后的处理器
         */
        public Object getLocalEventDispatchHandler();
    }
}
