package kuyou.common.ku09;

import android.app.Application;
import android.app.HelmetModuleManageServiceManager;
import android.app.IHelmetModuleCommonCallback;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.os.RemoteException;
import android.os.StrictMode;
import android.util.Log;

import androidx.annotation.NonNull;

import org.greenrobot.eventbus.Subscribe;

import java.util.List;

import kuyou.common.ipc.RemoteEvent;
import kuyou.common.ipc.RemoteEventBus;
import kuyou.common.ipc.RemoteEventHandler;
import kuyou.common.ku09.config.DevicesConfig;
import kuyou.common.ku09.event.IDispatchEventCallBack;
import kuyou.common.ku09.event.common.EventKeyClick;
import kuyou.common.ku09.event.common.EventKeyDoubleClick;
import kuyou.common.ku09.event.common.EventKeyLongClick;
import kuyou.common.ku09.event.common.EventPowerChange;
import kuyou.common.ku09.event.common.base.EventKey;
import kuyou.common.ku09.event.tts.EventTtsPlayRequest;
import kuyou.common.ku09.key.IKeyEventListener;
import kuyou.common.log.LogcatHelper;
import kuyou.common.utils.SystemPropertiesUtils;

/**
 * action :实现HelmetModuleManageServiceManager相关接口
 * <p>
 * author: wuguoxian <br/>
 * date: 20-11-4 <br/>
 * 1 HelmetModuleManageServiceManager相关AIDL接口都实现都封装在KU09_Modules各自的Application里面
 * 2 BaseApplication 实现了模块活动保持,按键事件分发,等模块间公共接口
 * <p>
 */
public abstract class BaseApplication extends Application implements
        IDispatchEventCallBack,
        IPowerStatusListener,
        IKeyEventListener,
        IModuleManager,
        RemoteEventBus.IFrameLiveListener {

    protected String TAG = "kuyou.common.ku09 > BaseApplication";

    protected HelmetModuleManageServiceManager mHelmetModuleManageServiceManager;

    @Override
    public final void onCreate() {
        super.onCreate();
        init();
    }

    protected abstract List<Integer> getEventDispatchList();

    protected void init() {
        TAG = new StringBuilder(getPackageName()).append(" > ModuleApplication").toString();
        TAG_THREAD_WATCH_DOG = getApplicationName() + TAG_THREAD_WATCH_DOG;

        //事件分发相关
        RemoteEventHandler handler = RemoteEventHandler.getInstance()
                .setLocalModulePackageName(getPackageName())
                .setEventDispatchList(getEventDispatchList());
        RemoteEventBus.getInstance(getApplicationContext())
                .setFrameLiveListener(getIpcFrameLiveListener())
                .register(this, handler);

        //log相关
        initLogcatLocal();

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        initHelmetModuleManageServiceManager();
        initKeepAliveConfig();
        initKeyHandlers();

        initCallBack();
    }

    /**
     * action:初始化异常log本地保存
     */
    protected void initLogcatLocal() {
        final String key = "persist.hm.log.save";
        if (!SystemPropertiesUtils.get(key, "0").equals("1")) {
            Log.d(TAG, "initLogcatLocal > LogcatHelper is disable");
            return;
        }
        LogcatHelper.getInstance(getApplicationContext())
                .setSaveLogDirPath("/" + getApplicationName())
                .setLogSizeMax(1024 * 1024 * 10) //100M
                .start("logcat *:d *:w | grep \"(" + android.os.Process.myPid() + ")\"");
    }

    /**
     * action:初始化模块服务回调
     */
    protected void initCallBack() {

        Log.i(TAG, new StringBuilder()
                .append("======================================================\n    ")
                .append("\n模块：").append(getApplicationName())
                .append("\n状态：").append("模块启动")
                .append("\n操作：").append("注册回调")
                .append("    \n\n======================================================")
                .toString());
    }

    protected abstract String getApplicationName();

    protected void initHelmetModuleManageServiceManager() {
        if (null != mHelmetModuleManageServiceManager) {
            return;
        }
        mHelmetModuleManageServiceManager = (HelmetModuleManageServiceManager) getSystemService("helmet_module_manage_service");
    }

    // =========================== 设备配置 ==============================

    private DevicesConfig mDevicesConfig;

    public DevicesConfig getDevicesConfig() {
        if (null == mDevicesConfig) {
            mDevicesConfig = new DevicesConfig();
        }
        return mDevicesConfig;
    }

    // ============================ 模块活动保持 ============================

    protected static final int MSG_WATCHDOG_2_FEED = 1;
    private static final int FLAG_FEED_TIME_LONG = 25 * 1000;

    private static final boolean IS_ENABLE_KEEP_ALIVE = true;
    private String TAG_THREAD_WATCH_DOG = ".HandlerThread.KeepAlive.Client";
    private HandlerThread mHandlerThreadKeepAliveClient;
    private Handler mHandlerKeepAliveClient;

    private void initKeepAliveConfig() {
        if (!isEnableWatchDog()) {
            Log.d(TAG, "initKeepAliveConfig > watch dog is disable");
            return;
        }
        if (null != mHandlerThreadKeepAliveClient) {
            Log.w(TAG, "initKeepAliveConfig > mHandlerThreadKeepAliveClient has been initialized");
            return;
        }
        mHandlerThreadKeepAliveClient = new HandlerThread(TAG_THREAD_WATCH_DOG);
        mHandlerThreadKeepAliveClient.start();
        mHandlerKeepAliveClient = new Handler(mHandlerThreadKeepAliveClient.getLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                mHandlerKeepAliveClient.removeMessages(MSG_WATCHDOG_2_FEED);
                handleMessageAliveClient(msg);
                if (isAutoFeedWatchDog())
                    mHandlerKeepAliveClient.sendEmptyMessageDelayed(MSG_WATCHDOG_2_FEED, getFeedTimeLong());
            }
        };
        mHelmetModuleManageServiceManager.feedWatchDog(getPackageName(), System.currentTimeMillis());
        if (isAutoFeedWatchDog())
            mHandlerKeepAliveClient.sendEmptyMessageDelayed(MSG_WATCHDOG_2_FEED, getFeedTimeLong());
    }

    /**
     * action:模块活动保持 > 是否开启
     */
    protected boolean isEnableWatchDog() {
        return IS_ENABLE_KEEP_ALIVE;
    }

    /**
     * action:模块活动保持 > 相关状态检测的流程是否连续进行
     */
    protected boolean isAutoFeedWatchDog() {
        return true;
    }

    /**
     * action:模块活动保持 > 相关状态检测的流程的处理
     */
    protected void handleMessageAliveClient(@NonNull Message msg) {
        Log.d(TAG, TAG_THREAD_WATCH_DOG + " > MSG_WATCHDOG_2_FEED ");

        String status = isReady();

        if (null == status || status.replaceAll(" ", "").length() == 0) {
            //提醒boss自己还没挂,和运行状态
            mHelmetModuleManageServiceManager.feedWatchDog(getPackageName(), System.currentTimeMillis());
        } else {
            onDogBitesLazyBug(-1, status);
        }
    }

    /**
     * action:模块活动保持 > 相关状态检测的流程的周期长度,单位毫秒
     */
    protected long getFeedTimeLong() {
        return FLAG_FEED_TIME_LONG;
    }

    /**
     * action:模块活动保持 > 开启相关状态检测的流程
     */
    public void sendMsgFeedWatchDog() {
        if (mHandlerKeepAliveClient.hasMessages(MSG_WATCHDOG_2_FEED))
            return;
        Log.d(TAG, " sendMsgFeedWatchDog > 开启模块活动保持 ");
        mHandlerKeepAliveClient.sendEmptyMessageDelayed(MSG_WATCHDOG_2_FEED, FLAG_FEED_TIME_LONG);
    }

    /**
     * action:模块活动保持 > 模块返回活动状态
     */
    protected String isReady() {
        boolean isReady = RemoteEventBus.getInstance(getApplicationContext()).isRegister(getPackageName());
        if (!isReady) {
            return "远程模块框架未初始化完成";
        }
        return null;
    }

    /**
     * action:模块活动保持 > 模块在偷懒,抓起来打一顿
     *
     * @param flag 重启的等待时间,毫秒
     */
    protected void onDogBitesLazyBug(int flag, String stasMsg) {
        StringBuilder logInfo = new StringBuilder()
                .append("======================================================\n")
                .append("\n模块：").append(getApplicationName())
                .append("\n异常状态:").append(stasMsg)
                .append("\n操作：");
        if (-1 != flag) {
            flag = Math.abs(flag) < 5000 ? 5000 : flag;
            logInfo.append("在").append(flag).append("毫秒后重启");
        } else {
            logInfo.append("重启模块");
        }
        Log.e(TAG, logInfo.append("\n\n======================================================").toString());

        getHelmetModuleManageServiceManager().feedWatchDog(getPackageName(), -flag);
        android.os.Process.killProcess(android.os.Process.myPid());
    }

    public Handler getHandlerKeepAliveClient() {
        return mHandlerKeepAliveClient;
    }

    // ============== 基础事件相关 ============================
    protected RemoteEventBus.IFrameLiveListener getIpcFrameLiveListener() {
        return BaseApplication.this;
    }

    @Override
    public void onIpcFrameResisterSuccess() {

    }

    @Override
    public void onIpcFrameUnResister() {

    }

    private int mPowerStatus = EventPowerChange.POWER_STATUS.BOOT_READY;

    protected void initKeyHandlers() {
        mHelmetModuleManageServiceManager.registerHelmetModuleCommonCallback(new IHelmetModuleCommonCallback.Stub() {

            @Override
            public void onPowerStatus(int status) throws RemoteException {
                BaseApplication.this.dispatchEvent(new EventPowerChange().setPowerStatus(status));
            }

            @Override
            public void onKeyClick(int keyCode) throws RemoteException {
                BaseApplication.this.dispatchEvent(new EventKeyClick(keyCode));
            }

            @Override
            public void onKeyDoubleClick(int keyCode) throws RemoteException {
                BaseApplication.this.dispatchEvent(new EventKeyDoubleClick(keyCode));
            }

            @Override
            public void onKeyLongClick(int keyCode) throws RemoteException {
                BaseApplication.this.dispatchEvent(new EventKeyLongClick(keyCode));
            }
        });
    }

    //本地事件
    @Subscribe
    public void onModuleEvent(RemoteEvent event) {
        switch (event.getCode()) {
            case EventPowerChange.Code.POWER_CHANGE:
                final int val = EventPowerChange.getPowerStatus(event);
                if (val == getPowerStatus()) {
                    return;
                }
                getPowerStatusListener().onPowerStatus(val);
                break;
            case EventKey.Code.KEY_CLICK:
                getKeyListener().onKeyClick(EventKey.getKeyCode(event));
                break;
            case EventKey.Code.KEY_LONG_CLICK:
                getKeyListener().onKeyLongClick(EventKey.getKeyCode(event));
                break;
            case EventKey.Code.KEY_DOUBLE_CLICK:
                getKeyListener().onKeyDoubleClick(EventKey.getKeyCode(event));
                break;
            default:
                break;
        }
    }

    protected IPowerStatusListener getPowerStatusListener() {
        return BaseApplication.this;
    }

    @Override
    public void onPowerStatus(int status) {
        Log.d(TAG, "onPowerStatus > status = " + status);
        mPowerStatus = status;
    }

    protected int getPowerStatus() {
        return mPowerStatus;
    }

    protected IKeyEventListener getKeyListener() {
        return BaseApplication.this;
    }

    @Override
    public void onKeyClick(int keyCode) {

    }

    @Override
    public void onKeyDoubleClick(int keyCode) {
    }

    @Override
    public void onKeyLongClick(int keyCode) {
    }

    //============================ 公开接口 ============================

    public android.app.HelmetModuleManageServiceManager getHelmetModuleManageServiceManager() {
        return mHelmetModuleManageServiceManager;
    }

    public void play(String content) {
        if (null == content || content.length() <= 0) {
            Log.e(TAG, "play > process fail : content is invalid");
            return;
        }
        Log.d(TAG, "play > content= " + content);
        dispatchEvent(new EventTtsPlayRequest(content));
    }

    @Override
    public void dispatchEvent(RemoteEvent event) {
        RemoteEventBus.getInstance().dispatch(event);
    }

    @Override
    public void reboot(int delayedMillisecond) {
        getHelmetModuleManageServiceManager().rebootModule(
                getPackageName(),
                android.os.Process.myPid(),
                delayedMillisecond);
    }
}
