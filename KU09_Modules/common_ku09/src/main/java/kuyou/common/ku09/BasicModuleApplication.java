package kuyou.common.ku09;

import android.app.Application;
import android.app.HelmetModuleManageServiceManager;
import android.app.IHelmetModuleCommonCallback;
import android.os.RemoteException;
import android.util.Log;
import android.view.KeyEvent;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import kuyou.common.exception.IGlobalExceptionControl;
import kuyou.common.exception.UncaughtExceptionManager;
import kuyou.common.ipc.RemoteEvent;
import kuyou.common.ipc.RemoteEventBus;
import kuyou.common.ku09.protocol.basic.ILiveControlCallback;
import kuyou.common.ku09.protocol.DeviceConfigImpl;
import kuyou.common.ku09.protocol.basic.IDeviceConfig;
import kuyou.common.ku09.event.common.EventKeyClick;
import kuyou.common.ku09.event.common.EventKeyDoubleClick;
import kuyou.common.ku09.event.common.EventKeyLongClick;
import kuyou.common.ku09.event.common.EventPowerChange;
import kuyou.common.ku09.event.common.basic.IEventBusDispatchCallback;
import kuyou.common.ku09.event.tts.EventTextToSpeechPlayRequest;
import kuyou.common.ku09.handler.BasicAssistHandler;
import kuyou.common.status.StatusProcessBusCallbackImpl;
import kuyou.common.status.StatusProcessBusImpl;
import kuyou.common.status.basic.IStatusProcessBus;
import kuyou.common.status.basic.IStatusProcessBusCallback;
import kuyou.common.log.LogcatHelper;
import kuyou.common.utils.CommonUtils;
import kuyou.common.utils.DebugUtil;
import kuyou.common.utils.SystemPropertiesUtils;

/**
 * action :模块通用基础实现[抽象]
 * <p>
 * remarks:  <br/>
 * author: wuguoxian <br/>
 * date: 20-11-4 <br/>
 * 已实现列表：<br/>
 * 1 IPC框架配置 <br/>
 * 2 log保存 <br/>
 * 3 模块活动保持 <br/>
 * 4 设备基础配置 <br/>
 * 5 业务协处理器实现 <br/>
 * <p>
 */
public abstract class BasicModuleApplication extends Application {

    protected String TAG = "kuyou.common.ku09 > BasicModuleApplication";

    protected HelmetModuleManageServiceManager mHelmetModuleManageServiceManager;

    @Override
    public final void onCreate() {
        super.onCreate();
        init();
    }

    // =========================== 初始化 ==============================

    protected void init() {
        TAG = new StringBuilder(getPackageName()).append(" > ModuleApplication").toString();

        //模块间IPC框架初始化
        RemoteEventBus.getInstance(getApplicationContext())
                .register(new RemoteEventBus.IRegisterConfig() {
                    @Override
                    public RemoteEventBus.IFrameLiveListener getFrameLiveListener() {
                        return BasicModuleApplication.this.getIpcFrameLiveListener();
                    }

                    @Override
                    public List<Integer> getEventDispatchList() {
                        return BasicModuleApplication.this.getEventDispatchList();
                    }

                    @Override
                    public Object getLocalEventDispatchHandler() {
                        return BasicModuleApplication.this;
                    }
                });

        //StrictMode相关
        initStrictModePolicy();

        //log相关
        initLogcatLocal();
        initExceptionLogLocal();

        //初始化模块状态维护相关
        initHelmetModuleManageServiceManager();
        initModuleSystemServiceCallBack();
        getProcessStatusBus();

        //初始化按键协处理器
        initKeyHandlers();
    }

    /**
     * action:初始化严格模式配置
     */
    protected void initStrictModePolicy() {
        final String key = "persist.kub.hm.sm";
        if (!SystemPropertiesUtils.get(key, "0").equals("1")) {
            Log.d(TAG, "initLogcatLocal > LogcatHelper is disable");
            return;
        }
        DebugUtil.startStrictModeVmPolicy();
        DebugUtil.startStrictModeThreadPolicy();
    }

    /**
     * action:初始化log本地保存
     */
    protected void initLogcatLocal() {
        final String key = "persist.kud.hm.ls";
        if (!SystemPropertiesUtils.get(key, "0").equals("1")) {
            Log.d(TAG, "initLogcatLocal > LogcatHelper is disable");
            return;
        }
        if (BuildConfig.IS_ENABLE_CONFUSE) {
            LogcatHelper.getInstance(getApplicationContext())
                    .setSaveLogDirPath(new StringBuilder()
                            .append("/kuyou/logcat/")
                            .append(getApplicationName())
                            .toString())
                    .setLogSizeMax(1024 * 1024 * 10) //100M
                    .start("logcat \"*:i*:w*:e\" | grep \"(" + android.os.Process.myPid() + ")\"");
            return;
        }
        LogcatHelper.getInstance(getApplicationContext())
                .setSaveLogDirPath(new StringBuilder()
                        .append("/kuyou/logcat/")
                        .append(getApplicationName())
                        .toString())
                .setLogSizeMax(1024 * 1024 * 10) //100M
                .start("logcat \"*:d*:i*:w*:e\" | grep \"(" + android.os.Process.myPid() + ")\"");
    }

    /**
     * action:初始化异常log本地保存
     */
    protected void initExceptionLogLocal() {
        final String key = "persist.kud.hm.els";
        if (!SystemPropertiesUtils.get(key, "0").equals("1")) {
            Log.d(TAG, "initExceptionLogLocal > exception info auto save is disable");
            return;
        }
        UncaughtExceptionManager
                .getInstance(new IGlobalExceptionControl() {
                    @Override
                    public Application getApplication() {
                        return BasicModuleApplication.this;
                    }

                    @Override
                    public int getPolicy() {
                        int flags = 0;
                        flags |= IGlobalExceptionControl.POLICY_ENABLE_EXIT_APP;
                        flags |= IGlobalExceptionControl.POLICY_ENABLE_CRASH_PROMPT;
                        return flags;
                    }
                }).setSaveExceptionLogDirPath("/kuyou/logcat/" + getApplicationName());
    }

    /**
     * action:初始化模块系统服务回调
     */
    protected void initModuleSystemServiceCallBack() {

        StringBuilder statusInfo = new StringBuilder().append("======================================================\n    ");

        statusInfo.append("\n模块：").append(getApplicationName());
        statusInfo.append("\n版本：").append(BuildConfig.BUILD_DATE);

        long timeNow = System.currentTimeMillis() + 8 * 3600 * 1000;
        long time = timeNow;
        try {
            time = CommonUtils.formatDate2Stamp(BuildConfig.BUILD_DATE, BuildConfig.BUILD_DATE_PATTERN);
        } catch (Exception e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }
        if (timeNow > time && timeNow - time < 600000) {
            statusInfo.append("\n版本状态：").append("新鲜");
        } else {
            statusInfo.append("\n版本状态：").append("原始");
        }
        statusInfo.append("\n状态：").append("模块启动");
        statusInfo.append("\n操作：").append("注册回调");

        Log.i(TAG, statusInfo.append("    \n\n======================================================")
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
                BasicModuleApplication.this.getEventBusDispatchCallback().dispatchEvent(new EventPowerChange()
                        .setPowerStatus(status)
                        .setEnableConsumeSeparately(false)
                        .setRemote(false));
                //Log.d(TAG, "onPowerStatus > status " + status);
            }

            @Override
            public void onKeyClick(int keyCode) throws RemoteException {
                BasicModuleApplication.this.getEventBusDispatchCallback().dispatchEvent(new EventKeyClick(keyCode));
            }

            @Override
            public void onKeyDoubleClick(int keyCode) throws RemoteException {
                BasicModuleApplication.this.getEventBusDispatchCallback().dispatchEvent(new EventKeyDoubleClick(keyCode));
            }

            @Override
            public void onKeyLongClick(int keyCode) throws RemoteException {
                if (KeyEvent.KEYCODE_POWER == keyCode) {
                    BasicModuleApplication.this.getEventBusDispatchCallback().dispatchEvent(new EventPowerChange()
                            .setPowerStatus(EventPowerChange.POWER_STATUS.SHUTDOWN)
                            .setEnableConsumeSeparately(false)
                            .setRemote(false));
                    return;
                }
                BasicModuleApplication.this
                        .getEventBusDispatchCallback()
                        .dispatchEvent(new EventKeyLongClick(keyCode));
            }
        });
    }

    // ============================ 模块状态看门狗 ============================

    private static final int FLAG_FEED_TIME_LONG = 25 * 1000;
    private static final int PS_FEED = 1;
    private IStatusProcessBus mStatusProcessBus;

    protected IStatusProcessBus getProcessStatusBus() {
        if (null == mStatusProcessBus) {
            mStatusProcessBus = new StatusProcessBusImpl() {
                @Override
                protected void onReceiveProcessStatusNotice(int statusCode, boolean isRemove) {
                    Log.d(BasicModuleApplication.this.TAG, "onFeedWatchDog > MSG_WATCHDOG_2_FEED ");
                    String status = BasicModuleApplication.this.isReady();

                    if (null == status || status.replaceAll(" ", "").length() == 0) {
                        //提醒boss自己还没挂,和运行状态
                        BasicModuleApplication.this.getHelmetModuleManageServiceManager()
                                .feedWatchDog(getPackageName(), System.currentTimeMillis());
                    } else {
                        //模块在偷懒,抓起来打一顿
                        StringBuilder logInfo = new StringBuilder()
                                .append("======================================================\n")
                                .append("\n模块：").append(getApplicationName())
                                .append("\n异常状态:").append(status)
                                .append("\n操作：").append("重启模块")
                                .append("\n\n======================================================");

                        Log.e(TAG, logInfo.toString());

                        BasicModuleApplication.this.getHelmetModuleManageServiceManager()
                                .feedWatchDog(getPackageName(), 1);
                        android.os.Process.killProcess(android.os.Process.myPid());
                        return;
                    }
                }
            };
            mStatusProcessBus.registerStatusNoticeCallback(PS_FEED,
                    new StatusProcessBusCallbackImpl(true, getFeedTimeLong())
                            .setNoticeHandleLooperPolicy(IStatusProcessBusCallback.LOOPER_POLICY_MAIN));

            mStatusProcessBus.start(PS_FEED);

            if (null != getHelmetModuleManageServiceManager()) {
                getHelmetModuleManageServiceManager().feedWatchDog(getPackageName(), System.currentTimeMillis());
            } else {
                Log.e(TAG, "getProcessStatusBus > process fail : 马上启动看门狗失败，安全帽模块服务未获取");
            }
        }
        return mStatusProcessBus;
    }

    /**
     * action:模块状态看门狗 > 相关状态检测的流程的周期长度,单位毫秒 <br/>
     *
     * @return : 模块看门狗喂食频度，不能大于60000,60*1000为模块服务看门狗的默认吃饭频度 <br/>
     */
    protected long getFeedTimeLong() {
        return FLAG_FEED_TIME_LONG;
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

    // ============================ 安全帽模块生命管理 ============================

    private ILiveControlCallback mLiveControlCallback;

    protected ILiveControlCallback getLiveControlCallback() {
        if (null == mLiveControlCallback) {
            mLiveControlCallback = new ILiveControlCallback() {
                @Override
                public void rebootModule(int delayedMillisecond) {
                    BasicModuleApplication.this.getHelmetModuleManageServiceManager().rebootModule(
                            getPackageName(),
                            android.os.Process.myPid(),
                            delayedMillisecond);
                }

                @Override
                public void rebootDevice(boolean isAutoBoot) {
                    SystemPropertiesUtils.set("ctl.start", isAutoBoot ? "system_shutdwon" : "system_reboot");
                }
            };
        }
        return mLiveControlCallback;
    }

    // ============================ 模块间IPC，模块与系统服务 ,模块事件的各种协处理器 ============================

    private RemoteEventBus.IFrameLiveListener mFrameLiveListener;
    private IEventBusDispatchCallback mEventBusDispatchCallback;
    private List<BasicAssistHandler> mEventHandlerList = null;

    /**
     * action:注册事件处理器
     **/
    protected abstract void initRegisterEventHandlers();

    /**
     * action:远程事件的监听列表
     **/
    protected List<Integer> getEventDispatchList() {
        if (0 == getEventHandlerList().size()) {
            Log.e(TAG, "getEventDispatchList > process fail : handlers is null");
            return null;
        }
        List<BasicAssistHandler> subHandlerList = new ArrayList<>();
        List<Integer> codeList = getAssistHandlerList(getEventHandlerList(), subHandlerList);
        if (subHandlerList.size() > 0) {
            getEventHandlerList().addAll(subHandlerList);
        }
        return codeList;
    }

    protected IEventBusDispatchCallback getEventBusDispatchCallback() {
        if (null == mEventBusDispatchCallback) {
            mEventBusDispatchCallback = new IEventBusDispatchCallback() {
                @Override
                public void dispatchEvent(RemoteEvent event) {
                    RemoteEventBus.getInstance().dispatch(event);
                }
            };
        }
        return mEventBusDispatchCallback;
    }

    private List<Integer> getAssistHandlerList(List<BasicAssistHandler> handlerList, List<BasicAssistHandler> subHandlerList) {
        List<Integer> remoteEventCodeList = new ArrayList<>();
        for (BasicAssistHandler handler : handlerList) {
            handler.setContext(getApplicationContext());
            handler.setDispatchEventCallBack(getEventBusDispatchCallback());
            handler.setLiveControlCallback(getLiveControlCallback());
            handler.setDevicesConfig(getDeviceConfig());
            handler.initStatusProcessBus();

            List<BasicAssistHandler> sub = handler.getSubEventHandlers();
            if (null != sub && sub.size() > 0) {
                subHandlerList.addAll(sub);
                remoteEventCodeList.addAll(getAssistHandlerList(sub, subHandlerList));
            }
            remoteEventCodeList.addAll(handler.getHandleRemoteEventCodeList());
        }
        return remoteEventCodeList;
    }

    /**
     * action:模块间IPC框架状态监听器
     **/
    protected RemoteEventBus.IFrameLiveListener getIpcFrameLiveListener() {
        if (null == mFrameLiveListener) {
            mFrameLiveListener = new RemoteEventBus.IFrameLiveListener() {
                @Override
                public void onIpcFrameResisterSuccess() {
                    Log.d(TAG, "onIpcFrameResisterSuccess > ");
                }

                @Override
                public void onIpcFrameUnResister() {
                    Log.d(TAG, "onIpcFrameUnResister > ");
                }
            };
        }
        return mFrameLiveListener;
    }

    protected BasicModuleApplication registerEventHandler(BasicAssistHandler handler) {
        getEventHandlerList().add(handler);
        return BasicModuleApplication.this;
    }

    protected List<BasicAssistHandler> getEventHandlerList() {
        if (null == mEventHandlerList) {
            mEventHandlerList = new ArrayList<>();
            initRegisterEventHandlers();
        }
        return mEventHandlerList;
    }

    //本地事件
    @Subscribe
    public void onReceiveEventNotice(RemoteEvent event) {
        for (BasicAssistHandler handler : getEventHandlerList()) {
            if (handler.onReceiveEventNotice(event)) {
//                Log.d(TAG, "已消费 event = " + event.getCode());
//                Log.d(TAG, "EventHandler = " + handler.getClass().getSimpleName());
                if (event.isEnableConsumeSeparately()) {
                    return;
                }
            }
        }
        Log.i(TAG, "onReceiveEventNotice > unable to consumption event = " + event.getCode());
    }

    public void play(String content) {
        if (null == content || content.length() <= 0) {
            Log.e(TAG, "play > process fail : content is invalid");
            return;
        }
        getEventBusDispatchCallback().dispatchEvent(new EventTextToSpeechPlayRequest(content));
    }

    // =========================== 设备配置等==============================
    private String mAppName = null;

    protected String getApplicationName() {
        if (null == mAppName) {
            int resId = getResources().getIdentifier("app_name", "string", getPackageName());
            if (0 < resId)
                mAppName = getApplicationContext().getString(resId);
        }
        return mAppName;
    }

    private IDeviceConfig mDeviceConfig;

    public IDeviceConfig getDeviceConfig() {
        if (null == mDeviceConfig) {
            mDeviceConfig = new DeviceConfigImpl();
        }
        return mDeviceConfig;
    }

    public HelmetModuleManageServiceManager getHelmetModuleManageServiceManager() {
        initHelmetModuleManageServiceManager();
        return mHelmetModuleManageServiceManager;
    }
}
