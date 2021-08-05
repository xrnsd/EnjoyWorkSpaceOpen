package com.kuyou.jt808;

import android.app.Application;
import android.app.TtsServiceManager;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.StrictMode;
import android.util.Log;

import androidx.annotation.NonNull;

import com.cuichen.jt808_sdk.oksocket.client.sdk.client.ConnectionInfo;
import com.cuichen.jt808_sdk.oksocket.client.sdk.client.action.ISocketActionListener;
import com.cuichen.jt808_sdk.oksocket.client.sdk.client.action.SocketActionAdapter;
import com.cuichen.jt808_sdk.oksocket.core.iocore.interfaces.IPulseSendable;
import com.cuichen.jt808_sdk.oksocket.core.iocore.interfaces.ISendable;
import com.cuichen.jt808_sdk.oksocket.core.pojo.OriginalData;
import com.cuichen.jt808_sdk.sdk.SocketManagerTest;
import com.kuyou.jt808.protocol.AuthenticationInfo;
import com.kuyou.jt808.protocol.ImageInfo;

import java.util.ArrayList;
import java.util.List;

public class Jt808Application extends Application {

    public static Jt808Application mC;

    @Override
    public void onCreate() {
        super.onCreate();
        mC = this;
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        builder.detectFileUriExposure();
        initTtsServiceManager();
        connect();
    }

    //@{ added by wgx Usefulness:
    private static final String SERVER_ADDRESS = "centos40.geointech.cn";
    private static final int SERVER_PORT = 8110;

    private SocketManagerTest socketManager;
    private List<ISocketActionListener> mISocketActionListenerList = new ArrayList<>();

    public static Jt808Application getInstance() {
        return mC;
    }

    private void initSocketManager() {
        if (null != socketManager) {
            return;
        }
        socketManager = SocketManagerTest.getInstance();
        socketManager.init();
    }

    public SocketManagerTest getSocketManager() {
        initSocketManager();
        return socketManager;
    }

    public void connect() {
        connect(SERVER_ADDRESS, SERVER_PORT);
    }

    public void connect(String serverUrl, int serverPort) {
        initSocketManager();
        try {
            socketManager.disconnect();
            socketManager.connect(serverUrl, serverPort, new SocketActionAdapter() {
                @Override
                public void onSocketIOThreadStart(String action) {
                    Jt808Application.getInstance().onSocketIOThreadStart(action);
                }

                @Override
                public void onSocketIOThreadShutdown(String action, Exception e) {
                    Jt808Application.getInstance().onSocketIOThreadShutdown(action, e);
                }

                @Override
                public void onSocketDisconnection(ConnectionInfo info, String action, Exception e) {
                    Jt808Application.getInstance().onSocketDisconnection(info, action, e);
                }

                @Override
                public void onSocketConnectionSuccess(ConnectionInfo info, String action) {
                    //android.util.Log.d(TAG,"  ");
                    Log.d("123456", " onSocketConnectionSuccess =" + socketManager.isConnect());
                    new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            socketManager.send(AuthenticationInfo.getInstance().getAuthenticationMsgBytes());
                        }
                    }, 2000);
                    Jt808Application.getInstance().onSocketConnectionSuccess(info, action);
                }

                @Override
                public void onSocketConnectionFailed(ConnectionInfo info, String action, Exception e) {
                    Jt808Application.getInstance().onSocketConnectionFailed(info, action, e);
                }

                @Override
                public void onSocketReadResponse(ConnectionInfo info, String action, OriginalData data) {
                    Jt808Application.getInstance().onSocketReadResponse(info, action, data);
                }

                @Override
                public void onSocketWriteResponse(ConnectionInfo info, String action, ISendable data) {
                    Jt808Application.getInstance().onSocketWriteResponse(info, action, data);
                }

                @Override
                public void onPulseSend(ConnectionInfo info, IPulseSendable data) {
                    Jt808Application.getInstance().onPulseSend(info, data);
                }
            });
        } catch (Exception e) {
            Log.e("123456", Log.getStackTraceString(e));
        }
    }

    public void setSocketActionListener(ISocketActionListener listener) {
        mISocketActionListenerList.add(listener);
    }

    private void onSocketIOThreadStart(String action) {
        for (ISocketActionListener listener : mISocketActionListenerList) {
            listener.onSocketIOThreadStart(action);
        }
    }

    private void onSocketIOThreadShutdown(String action, Exception e) {
        for (ISocketActionListener listener : mISocketActionListenerList) {
            listener.onSocketIOThreadShutdown(action, e);
        }
    }

    private void onSocketDisconnection(ConnectionInfo info, String action, Exception e) {
        for (ISocketActionListener listener : mISocketActionListenerList) {
            listener.onSocketDisconnection(info, action, e);
        }
    }

    private void onSocketConnectionSuccess(ConnectionInfo info, String action) {
        for (ISocketActionListener listener : mISocketActionListenerList) {
            listener.onSocketConnectionSuccess(info, action);
        }
    }

    private void onSocketConnectionFailed(ConnectionInfo info, String action, Exception e) {
        for (ISocketActionListener listener : mISocketActionListenerList) {
            listener.onSocketConnectionFailed(info, action, e);
        }
    }

    private void onSocketReadResponse(ConnectionInfo info, String action, OriginalData data) {
        for (ISocketActionListener listener : mISocketActionListenerList) {
            listener.onSocketReadResponse(info, action, data);
        }
    }

    private void onSocketWriteResponse(ConnectionInfo info, String action, ISendable data) {
        for (ISocketActionListener listener : mISocketActionListenerList) {
            listener.onSocketWriteResponse(info, action, data);
        }
    }

    private void onPulseSend(ConnectionInfo info, IPulseSendable data) {
        for (ISocketActionListener listener : mISocketActionListenerList) {
            listener.onPulseSend(info, data);
        }
    }

    //@{ added by wgx Usefulness:
    TtsServiceManager mTtsServiceManager;
    List<ILiveResultListener> mILiveResultListenerList = new ArrayList<>();

    public static interface ILiveResultListener {
        public void onLiveResult(int resultCode, String msg);
    }

    public void setLiveResultListener(ILiveResultListener listener) {
        getTtsServiceManager();
        mILiveResultListenerList.add(listener);
    }

    private void onLiveResultLocal(int resultCode, String msg) {
        Log.d("123456", "808 >  onLiveResult > resultCode= " + resultCode);
        Log.d("123456", "808 >  onLiveResult > msg= " + msg);
        for (ILiveResultListener listener : mILiveResultListenerList) {
            listener.onLiveResult(resultCode, msg);
        }
    }

    private void initTtsServiceManager() {
        if (null != mTtsServiceManager) {
            return;
        }
        mTtsServiceManager = (TtsServiceManager) getSystemService("usc_tts");
        mTtsServiceManager.register808Callback(new android.app.I808Callback.Stub() {
            @Override
            public void onLiveResult(int resultCode, String msg) {
                onLiveResultLocal(resultCode, msg);
            }

            @Override
            public void onCameraShootKey() {
                ImageInfo.performShoot();
            }

        });
        initWatchDogConfig();
    }

    public TtsServiceManager getTtsServiceManager() {
        initTtsServiceManager();
        return mTtsServiceManager;
    }

    // ==================== 模块保活 ==============================
    private static final boolean IS_ENABLE_KEEP_ALIVE = true;
    private static final String TAG_THREAD_WATCH_DOG = "808.HandlerThread.WatchDog.Client";
    private static final int MSG_WATCHDOG_2_FEED = 1;
    private static final int FLAG_FEED_TIME_LONG = 25 * 1000;
    private HandlerThread mHandlerThreadWatchDogClient;
    private Handler mHandlerWatchDogClient;

    private void initWatchDogConfig() {
        if(!IS_ENABLE_KEEP_ALIVE){
            return;
        }
        if (null != mHandlerThreadWatchDogClient)
            return;
        mHandlerThreadWatchDogClient = new HandlerThread(TAG_THREAD_WATCH_DOG);
        mHandlerThreadWatchDogClient.start();
        mHandlerWatchDogClient = new Handler(mHandlerThreadWatchDogClient.getLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                Log.d("123456", TAG_THREAD_WATCH_DOG + " > MSG_WATCHDOG_2_FEED ");
                mHandlerWatchDogClient.removeMessages(MSG_WATCHDOG_2_FEED);
                //提醒boss自己还没挂
                mTtsServiceManager.feedWatchDog(getPackageName(), System.currentTimeMillis());
                mHandlerWatchDogClient.sendEmptyMessageDelayed(MSG_WATCHDOG_2_FEED, FLAG_FEED_TIME_LONG);
            }
        };
        mHandlerWatchDogClient.sendEmptyMessage(MSG_WATCHDOG_2_FEED);
    }
    //}@ end wgx
}
