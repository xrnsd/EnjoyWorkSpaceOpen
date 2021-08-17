package kuyou.common.ku09;

import android.app.Application;
import android.app.HelmetModuleManageServiceManager;
import android.app.IHelmetModuleCommonCallback;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;

import androidx.annotation.NonNull;

import org.greenrobot.eventbus.Subscribe;

import java.util.List;

import kuyou.common.ipc.RemoteEvent;
import kuyou.common.ipc.RemoteEventBus;
import kuyou.common.ku09.config.DevicesConfig;
import kuyou.common.ku09.event.IDispatchEventCallBack;
import kuyou.common.ku09.event.common.EventKeyClick;
import kuyou.common.ku09.event.common.EventKeyDoubleClick;
import kuyou.common.ku09.event.common.EventKeyLongClick;
import kuyou.common.ku09.event.common.EventPowerChange;
import kuyou.common.ku09.event.common.base.EventKey;
import kuyou.common.ku09.event.tts.EventTextToSpeechPlayRequest;
import kuyou.common.ku09.key.IKeyEventListener;
import kuyou.common.log.LogcatHelper;
import kuyou.common.utils.DebugUtil;
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
        IModuleManager {

    protected String TAG = "kuyou.common.ku09 > BaseApplication";

    protected HelmetModuleManageServiceManager mHelmetModuleManageServiceManager;

    @Override
    public final void onCreate() {
        super.onCreate();
        init();
    }

    // =========================== 初始化 ==============================

    protected void init() {
        TAG = new StringBuilder(getPackageName()).append(" > ModuleApplication").toString();
        TAG_THREAD_WATCH_DOG = getApplicationName() + TAG_THREAD_WATCH_DOG;

        //模块间IPC框架初始化
        RemoteEventBus.getInstance(getApplicationContext())
                .register(new RemoteEventBus.IRegisterConfig() {
                    @Override
                    public RemoteEventBus.IFrameLiveListener getFrameLiveListener() {
                        return BaseApplication.this.getIpcFrameLiveListener();
                    }

                    @Override
                    public List<Integer> getEventDispatchList() {
                        return BaseApplication.this.getEventDispatchList();
                    }

                    @Override
                    public Object getLocalEventDispatchHandler() {
                        return BaseApplication.this;
                    }
                });
        //StrictMode相关
        initStrictModePolicy();

        //log相关
        initLogcatLocal();

        //初始化模块状态控制系统服务
        initHelmetModuleManageServiceManager();
        initCallBack();

        //初始化模块状态看门狗
        initKeepAliveConfig();

        //初始化按键协处理器
        initKeyHandlers();
    }

    /**
     * action:初始化严格模式配置
     */
    protected void initStrictModePolicy() {
        final String key = "persist.hm.strict.mode";
        if (!SystemPropertiesUtils.get(key, "0").equals("1")) {
            Log.d(TAG, "initLogcatLocal > LogcatHelper is disable");
            return;
        }
        DebugUtil.startStrictModeThreadPolicy();
        DebugUtil.startStrictModeVmPolicy();
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

    protected void initHelmetModuleManageServiceManager() {
        if (null != mHelmetModuleManageServiceManager) {
            return;
        }
        mHelmetModuleManageServiceManager = (HelmetModuleManageServiceManager) getSystemService("helmet_module_manage_service");
    }

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

    // ============================ 模块状态看门狗 ============================

    protected static final int MSG_WATCHDOG_2_FEED = 1;
    //public static final int MSG_REPORT_LOCATION = 2;
    public static final int MSG_IPC_FRAME_INIT_FINISH = 3;

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
                mHandlerKeepAliveClient.removeMessages(msg.what);
                switch (msg.what) {
                    case MSG_WATCHDOG_2_FEED:
                        handleMessageAliveClient();
                        if (isAutoFeedWatchDog())
                            mHandlerKeepAliveClient.sendEmptyMessageDelayed(MSG_WATCHDOG_2_FEED, getFeedTimeLong());
                        break;
                    case MSG_IPC_FRAME_INIT_FINISH:
                        Log.d(TAG, "handleMessage > MSG_IPC_FRAME_INIT_FINISH");
                        break;
                    default:
                        break;
                }
            }
        };
        mHelmetModuleManageServiceManager.feedWatchDog(getPackageName(), System.currentTimeMillis());
        if (isAutoFeedWatchDog())
            mHandlerKeepAliveClient.sendEmptyMessageDelayed(MSG_WATCHDOG_2_FEED, getFeedTimeLong());
    }

    /**
     * action:模块状态看门狗 > 是否开启
     */
    protected boolean isEnableWatchDog() {
        return IS_ENABLE_KEEP_ALIVE;
    }

    /**
     * action:模块状态看门狗 > 相关状态检测的流程是否连续进行
     */
    protected boolean isAutoFeedWatchDog() {
        return true;
    }

    /**
     * action:模块状态看门狗 > 相关状态检测的流程的处理
     */
    protected void handleMessageAliveClient() {
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
     * action:模块状态看门狗 > 相关状态检测的流程的周期长度,单位毫秒
     */
    protected long getFeedTimeLong() {
        return FLAG_FEED_TIME_LONG;
    }

    /**
     * action:模块状态看门狗 > 开启相关状态检测的流程
     */
    public void sendMsgFeedWatchDog() {
        if (mHandlerKeepAliveClient.hasMessages(MSG_WATCHDOG_2_FEED))
            return;
        Log.d(TAG, " sendMsgFeedWatchDog > 开启模块活动保持 ");
        mHandlerKeepAliveClient.sendEmptyMessageDelayed(MSG_WATCHDOG_2_FEED, FLAG_FEED_TIME_LONG);
    }

    /**
     * action:模块状态看门狗 > 模块返回活动状态
     */
    protected String isReady() {
        boolean isReady = RemoteEventBus.getInstance(getApplicationContext()).isRegister(getPackageName());
        if (!isReady) {
            return "远程模块框架未初始化完成";
        }
        return null;
    }

    /**
     * action:模块状态看门狗 > 模块在偷懒,抓起来打一顿
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

    @Override
    public void reboot(int delayedMillisecond) {
        getHelmetModuleManageServiceManager().rebootModule(
                getPackageName(),
                android.os.Process.myPid(),
                delayedMillisecond);
    }

    // ============================ 事件相关 ============================

    private int mPowerStatus = EventPowerChange.POWER_STATUS.BOOT_READY;
    private IPowerStatusListener mPowerStatusListener;
    private IKeyEventListener mKeyEventListener;

    protected abstract List<Integer> getEventDispatchList();

    @Override
    public void dispatchEvent(RemoteEvent event) {
        RemoteEventBus.getInstance().dispatch(event);
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

    public void play(String content) {
        if (null == content || content.length() <= 0) {
            Log.e(TAG, "play > process fail : content is invalid");
            return;
        }
        Log.d(TAG, "play > content= " + content);
        dispatchEvent(new EventTextToSpeechPlayRequest(content));
    }

    protected IPowerStatusListener getPowerStatusListener() {
        if (null == mPowerStatusListener) {
            mPowerStatusListener = new IPowerStatusListener() {
                @Override
                public void onPowerStatus(int status) {
                    BaseApplication.this.onPowerStatus(status);
                }
            };
        }
        return mPowerStatusListener;
    }

    protected void onPowerStatus(int status) {
        Log.d(TAG, "onPowerStatus > status = " + status);
        mPowerStatus = status;
    }

    protected int getPowerStatus() {
        return mPowerStatus;
    }

    protected IKeyEventListener getKeyListener() {
        if (null == mKeyEventListener) {
            mKeyEventListener = new IKeyEventListener() {
                @Override
                public void onKeyClick(int keyCode) {
                    BaseApplication.this.onKeyClick(keyCode);
                }

                @Override
                public void onKeyDoubleClick(int keyCode) {
                    BaseApplication.this.onKeyDoubleClick(keyCode);
                }

                @Override
                public void onKeyLongClick(int keyCode) {
                    BaseApplication.this.onKeyLongClick(keyCode);
                }
            };
        }
        return mKeyEventListener;
    }

    protected void onKeyClick(int keyCode) {
    }

    protected void onKeyDoubleClick(int keyCode) {
    }

    protected void onKeyLongClick(int keyCode) {
    }

    protected RemoteEventBus.IFrameLiveListener getIpcFrameLiveListener() {
        return new RemoteEventBus.IFrameLiveListener() {
            @Override
            public void onIpcFrameResisterSuccess() {
//                if (null != getHandlerKeepAliveClient()) {
//                    getHandlerKeepAliveClient().sendEmptyMessage(MSG_IPC_FRAME_INIT_FINISH);
//                } else {
//                    Log.e(TAG, "onIpcFrameResisterSuccess > process fail : HandlerKeepAliveClient is null");
//                }
                BaseApplication.this.onIpcFrameResisterSuccess();
            }

            @Override
            public void onIpcFrameUnResister() {
                BaseApplication.this.onIpcFrameUnResister();
            }
        };
    }

    protected void onIpcFrameResisterSuccess() {
    }

    protected void onIpcFrameUnResister() {

    }

    // =========================== 设备配置等==============================

    protected abstract String getApplicationName();

    private DevicesConfig mDevicesConfig;

    public DevicesConfig getDevicesConfig() {
        if (null == mDevicesConfig) {
            mDevicesConfig = new DevicesConfig();
        }
        return mDevicesConfig;
    }

    public HelmetModuleManageServiceManager getHelmetModuleManageServiceManager() {
        return mHelmetModuleManageServiceManager;
    }
}
