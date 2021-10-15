package kuyou.common.ku09.handler;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import kuyou.common.ipc.RemoteEvent;
import kuyou.common.ku09.event.common.basic.IEventBusDispatchCallback;
import kuyou.common.ku09.event.tts.EventTextToSpeechPlayRequest;
import kuyou.common.ku09.protocol.basic.IDeviceConfig;
import kuyou.common.ku09.protocol.basic.ILiveControlCallback;
import kuyou.common.status.StatusProcessBusImpl;
import kuyou.common.status.basic.IStatusProcessBus;

/**
 * action :业务处理器[抽象]
 * <p>
 * remarks:  <br/>
 * author: wuguoxian <br/>
 * date: 21-7-23 <br/>
 * </p>
 */
public abstract class BasicAssistHandler {

    protected final String TAG = "kuyou.common.ku09.handler > BasicEventHandler";

    private Context mContext;

    protected Handler mMainThreadHandler = new Handler(Looper.getMainLooper());

    protected Context getContext() {
        return mContext;
    }

    public void setContext(Context context) {
        mContext = context;
    }

    //协处理器 相关
    public void start() {

    }

    public void stop() {

    }

    /**
     * action: 嵌套的协处理器列表 <br/>
     * remarks: 协处理里面有嵌套的协处理器时请重载此方法，已保证正常初始化
     */
    public List<BasicAssistHandler> getSubEventHandlers() {
        return null;
    }

    //设备配置相关 相关

    private IDeviceConfig mDeviceConfig;

    protected IDeviceConfig getDeviceConfig() {
        return mDeviceConfig;
    }

    public void setDevicesConfig(IDeviceConfig config) {
        mDeviceConfig = config;
    }

    //模块控制 相关
    private ILiveControlCallback mModuleLiveControlCallback;

    public BasicAssistHandler setLiveControlCallback(ILiveControlCallback callback) {
        mModuleLiveControlCallback = callback;
        return BasicAssistHandler.this;
    }

    protected void rebootModule(int delayedMillisecond) {
        if (null == mModuleLiveControlCallback) {
            Log.e(TAG, "reboot > process fail : mModuleLiveControlCallback is null");
            return;
        }
        mModuleLiveControlCallback.rebootModule(delayedMillisecond);
    }

    // StatusProcessBus 相关

    private IStatusProcessBus mStatusProcessBus;

    public IStatusProcessBus getStatusProcessBus() {
        initStatusProcessBus();
        return mStatusProcessBus;
    }

    public void initStatusProcessBus() {
        if (null != mStatusProcessBus) {
            return;
        }
        mStatusProcessBus = new StatusProcessBusImpl() {
            @Override
            protected void onReceiveProcessStatusNotice(int statusCode, boolean isRemove) {
                BasicAssistHandler.this.onReceiveProcessStatusNotice(statusCode, isRemove);
            }
        };
        initReceiveProcessStatusNotices();
    }

    protected void initReceiveProcessStatusNotices() {
    }

    protected void onReceiveProcessStatusNotice(int statusCode, boolean isRemove) {

    }

    //RemoteEventBus 相关

    private IEventBusDispatchCallback mEventBusDispatchCallBack;
    private List<Integer> mHandleLocalEventCodeList = null,
            mHandleRemoteEventCodeList = null;

    public boolean onReceiveEventNotice(RemoteEvent event) {
        return false;
    }

    protected void initReceiveEventNotices() {
    }

    protected BasicAssistHandler registerHandleEvent(int eventCode, boolean isRemote) {
        if (null == mHandleLocalEventCodeList) {
            mHandleLocalEventCodeList = new ArrayList<>();
            mHandleRemoteEventCodeList = new ArrayList<>();
        }
        if (isRemote) {
            mHandleRemoteEventCodeList.add(eventCode);
        } else {
            mHandleLocalEventCodeList.add(eventCode);
        }
        return BasicAssistHandler.this;
    }

    protected boolean unRegisterHandleEvent(int eventCode) {
        if (-1 != getHandleLocalEventCodeList().indexOf(eventCode)) {
            getHandleLocalEventCodeList().remove(Integer.valueOf(eventCode));
            return true;
        }
        if (-1 != getHandleRemoteEventCodeList().indexOf(eventCode)) {
            getHandleRemoteEventCodeList().remove(Integer.valueOf(eventCode));
            return true;
        }
        return false;
    }

    public List<Integer> getHandleLocalEventCodeList() {
        if (null == mHandleLocalEventCodeList) {
            mHandleLocalEventCodeList = new ArrayList<>();
            initReceiveEventNotices();
        }
        return mHandleLocalEventCodeList;
    }

    public List<Integer> getHandleRemoteEventCodeList() {
        if (null == mHandleRemoteEventCodeList) {
            mHandleRemoteEventCodeList = new ArrayList<>();
            initReceiveEventNotices();
        }
        return mHandleRemoteEventCodeList;
    }

    public BasicAssistHandler setDispatchEventCallBack(IEventBusDispatchCallback dispatchEventCallBack) {
        mEventBusDispatchCallBack = dispatchEventCallBack;
        return BasicAssistHandler.this;
    }

    protected IEventBusDispatchCallback getDispatchEventCallBack() {
        return mEventBusDispatchCallBack;
    }

    protected void dispatchEvent(RemoteEvent event) {
        if (null == mEventBusDispatchCallBack) {
            Log.e(TAG, "dispatchEvent > process fail : mDispatchEventCallBack is null");
            return;
        }
        //确保在主线程处理
        if (Looper.myLooper() == Looper.getMainLooper()) {
            mEventBusDispatchCallBack.dispatchEvent(event);
            return;
        }
        mMainThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                BasicAssistHandler.this.mEventBusDispatchCallBack.dispatchEvent(event);
            }
        });
    }

    protected void play(String content) {
        if (null == content || content.length() <= 0) {
            Log.e(TAG, "play > process fail : content is invalid");
            return;
        }
        dispatchEvent(new EventTextToSpeechPlayRequest(content));
    }
}
