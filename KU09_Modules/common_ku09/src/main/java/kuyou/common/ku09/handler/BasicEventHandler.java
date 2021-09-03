package kuyou.common.ku09.handler;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import kuyou.common.ipc.RemoteEvent;
import kuyou.common.ku09.basic.ILiveControlCallback;
import kuyou.common.ku09.config.IDeviceConfig;
import kuyou.common.ku09.event.common.basic.IEventBusDispatchCallback;
import kuyou.common.ku09.event.tts.EventTextToSpeechPlayRequest;
import kuyou.common.ku09.status.StatusProcessBusProxy;
import kuyou.common.ku09.status.basic.IStatusProcessBus;
import kuyou.common.ku09.status.basic.IStatusProcessBusCallback;
import kuyou.common.ku09.status.basic.IStatusProcessBusProxy;

/**
 * action :业务处理器[抽象]
 * <p>
 * remarks:  <br/>
 * author: wuguoxian <br/>
 * date: 21-7-23 <br/>
 * </p>
 */
public abstract class BasicEventHandler {

    protected final String TAG = "kuyou.common.ku09.handler > BasicEventHandler";

    private Context mContext;

    protected Context getContext() {
        return mContext;
    }

    public void setContext(Context context) {
        mContext = context;
    }

    //协处理器 相关

    /**
     * action: 嵌套的协处理器列表 <br/>
     * remarks: 协处理里面有嵌套的协处理器时请重载此方法，已保证正常初始化
     */
    public List<BasicEventHandler> getSubEventHandlers() {
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

    public BasicEventHandler setModuleLiveControlCallback(ILiveControlCallback callback) {
        mModuleLiveControlCallback = callback;
        return BasicEventHandler.this;
    }

    protected void rebootModule(int delayedMillisecond) {
        if (null == mModuleLiveControlCallback) {
            Log.e(TAG, "reboot > process fail : mModuleLiveControlCallback is null");
            return;
        }
        mModuleLiveControlCallback.rebootModule(delayedMillisecond);
    }

    // StatusProcessBus 相关

    private IStatusProcessBusProxy mStatusProcessBus;

    final public void setStatusProcessBus(IStatusProcessBus spb) {
        if (null != mStatusProcessBus) {
            return;
        }
        mStatusProcessBus = new StatusProcessBusProxy(spb) {
            @Override
            protected void onReceiveStatusProcessNotice(int statusCode, boolean isRemove) {
                BasicEventHandler.this.onReceiveStatusProcessNotice(statusCode, isRemove);
            }
        };
        initStatusProcessBusCallbackList();
    }

    public IStatusProcessBusProxy getStatusProcessBus() {
        return mStatusProcessBus;
    }

    protected void initStatusProcessBusCallbackList() {
    }

    protected void registerStatusProcessBusCallback(int statusCode, IStatusProcessBusCallback callback) {
        if (null == getStatusProcessBus()) {
            Log.e(TAG, "registerStatusProcessBusCallback > process fail : getStatusProcessBus is null");
            return;
        }
        getStatusProcessBus().registerStatusProcessBusCallback(statusCode, callback);
    }

    protected void onReceiveStatusProcessNotice(int statusCode, boolean isRemove) {
    }

    //RemoteEventBus 相关

    private IEventBusDispatchCallback mEventBusDispatchCallBack;
    private List<Integer> mHandleLocalEventCodeList = null,
            mHandleRemoteEventCodeList = null;

    public boolean onReceiveEventNotice(RemoteEvent event) {
        return false;
    }

    protected void initHandleEventCodeList() {
    }

    protected BasicEventHandler registerHandleEvent(int eventCode, boolean isRemote) {
        if (null == mHandleLocalEventCodeList) {
            mHandleLocalEventCodeList = new ArrayList<>();
            mHandleRemoteEventCodeList = new ArrayList<>();
        }
        if (isRemote) {
            mHandleRemoteEventCodeList.add(eventCode);
        } else {
            mHandleLocalEventCodeList.add(eventCode);
        }
        return BasicEventHandler.this;
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
            initHandleEventCodeList();
        }
        return mHandleLocalEventCodeList;
    }

    public List<Integer> getHandleRemoteEventCodeList() {
        if (null == mHandleRemoteEventCodeList) {
            mHandleRemoteEventCodeList = new ArrayList<>();
            initHandleEventCodeList();
        }
        return mHandleRemoteEventCodeList;
    }

    public BasicEventHandler setDispatchEventCallBack(IEventBusDispatchCallback dispatchEventCallBack) {
        mEventBusDispatchCallBack = dispatchEventCallBack;
        return BasicEventHandler.this;
    }

    protected IEventBusDispatchCallback getDispatchEventCallBack() {
        return mEventBusDispatchCallBack;
    }

    protected void dispatchEvent(RemoteEvent event) {
        if (null == mEventBusDispatchCallBack) {
            Log.e(TAG, "dispatchEvent > process fail : mDispatchEventCallBack is null");
            return;
        }
        mEventBusDispatchCallBack.dispatchEvent(event);
    }

    protected void play(String content) {
        if (null == content || content.length() <= 0) {
            Log.e(TAG, "play > process fail : content is invalid");
            return;
        }
        dispatchEvent(new EventTextToSpeechPlayRequest(content));
    }
}
