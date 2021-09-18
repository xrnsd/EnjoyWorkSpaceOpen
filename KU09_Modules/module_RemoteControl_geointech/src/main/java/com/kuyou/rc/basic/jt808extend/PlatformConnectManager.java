package com.kuyou.rc.basic.jt808extend;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import kuyou.sdk.jt808.basic.exceptions.SocketManagerException;
import kuyou.sdk.jt808.basic.protocol.JT808ReaderProtocol;
import kuyou.sdk.jt808.basic.socketbean.PulseData;
import kuyou.sdk.jt808.basic.socketbean.SendDataBean;
import kuyou.sdk.jt808.oksocket.client.impl.client.action.ActionDispatcher;
import kuyou.sdk.jt808.oksocket.client.sdk.OkSocket;
import kuyou.sdk.jt808.oksocket.client.sdk.client.ConnectionInfo;
import kuyou.sdk.jt808.oksocket.client.sdk.client.OkSocketOptions;
import kuyou.sdk.jt808.oksocket.client.sdk.client.action.SocketActionAdapter;
import kuyou.sdk.jt808.oksocket.client.sdk.client.connection.IConnectionManager;

public class PlatformConnectManager {

    private static final String TAG = "com.kuyou.rc.basic.jt808extend > PlatformConnectManager";

    private static PlatformConnectManager INSTANCE;
    private static final Object SingleInstanceLocker = new Object();
    private ConnectionInfo info;
    private IConnectionManager mManager;

    private SocketActionAdapter mSocketActionAdapter;

    public static PlatformConnectManager getInstance() {
        if (INSTANCE == null) {
            synchronized (SingleInstanceLocker) {
                if (INSTANCE == null) {
                    INSTANCE = new PlatformConnectManager();
                }
            }
        }
        return INSTANCE;
    }

    public void initManager(String ip, int prot, int heartbeatInterval) {
        if (null != info) {
            return;
        }
        info = new ConnectionInfo(ip, prot);
        final Handler handler = new Handler(Looper.getMainLooper());
        OkSocketOptions.Builder builder = new OkSocketOptions.Builder();
        builder.setPulseFrequency(heartbeatInterval); //心跳间隔
        builder.setCallbackThreadModeToken(new OkSocketOptions.ThreadModeToken() {
            @Override
            public void handleCallbackEvent(ActionDispatcher.ActionRunnable runnable) {
                handler.post(runnable);
            }
        });
        builder.setReaderProtocol(new JT808ReaderProtocol());
        builder.setMaxReadDataMB(6)
                .setConnectTimeoutSecond(4)
                .setWritePackageBytes(100)
                .setReadPackageBytes(70)
                .setPulseFeedLoseTimes(7);
        mManager = OkSocket.open(info).option(builder.build());
    }

    public IConnectionManager getManager() {
        return mManager;
    }

    /**
     * 连接和回调
     * 如果是已连接 ， 则断开连接
     *
     * @param ip
     * @param port
     * @param adapter
     * @throws
     */
    public void connect(String ip, int port, int heartbeatInterval, SocketActionAdapter adapter) throws Exception {
        //调用通道进行连接
        initManager(ip, port, heartbeatInterval);
        if (mManager != null) {
            Log.d(TAG, new StringBuilder("connect > ")
                    .append("\nserverUrl = ").append(ip)
                    .append("\nserverPort = ").append(port).toString());
            if (!mManager.isConnect()) {
                if (mSocketActionAdapter != null)
                    mManager.unRegisterReceiver(mSocketActionAdapter);
                mSocketActionAdapter = adapter;
                mManager.registerReceiver(adapter);
                mManager.connect();
            }
        } else {
            throw new SocketManagerException("请先初始化");
        }

    }

    public boolean isClean() {
        if (null == mSocketActionAdapter) {
            return true;
        }
        return null != mManager && mManager.isClean();
    }

    public boolean isConnect() {
        return null != mManager && mManager.isConnect();
    }

    public void send(byte[] body) {
        if (null == mManager) {
            Log.e(TAG, "send > process fail : mManager is null");
            return;
        }
        mManager.send(new SendDataBean(body));
    }

    public OkSocketOptions getOption() {
        return mManager.getOption();
    }

    public void openPulse() {
        OkSocket.open(info)
                .getPulseManager()
                .setPulseSendable(new PulseData())//只需要设置一次,下一次可以直接调用pulse()
                .pulse();//开始心跳,开始心跳后,心跳管理器会自动进行心跳触发
    }

    /**
     * action: 上报自定义心跳数据
     */
    public boolean openPulse(PulseData data) {
        try {
            getManager()
                    .getPulseManager()
                    .setPulseSendable(data)//只需要设置一次,下一次可以直接调用pulse()
                    .pulse();//开始心跳,开始心跳后,心跳管理器会自动进行心跳触发
            Log.d(TAG, " 开启心跳成功 ");
            return true;
        } catch (Exception e) {
            Log.e(TAG, " 开启心跳失败 ");
            Log.e(TAG, Log.getStackTraceString(e));
        }
        return false;
    }

    public void feedPulse() {
        if (mManager != null)
            mManager.getPulseManager().feed();
    }

    public void disconnect() {
        Log.d(TAG, "disconnect > 主动断开服务器连接 ----------------------------- ");
        mManager.unRegisterReceiver(mSocketActionAdapter);
        mManager.disconnect();
        mSocketActionAdapter = null;
    }

}
