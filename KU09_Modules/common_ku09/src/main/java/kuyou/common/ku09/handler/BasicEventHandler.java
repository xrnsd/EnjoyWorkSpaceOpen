package kuyou.common.ku09.handler;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import kuyou.common.ipc.RemoteEvent;
import kuyou.common.ku09.IModuleManager;
import kuyou.common.ku09.event.IDispatchEventCallback;
import kuyou.common.ku09.event.tts.EventTextToSpeechPlayRequest;

/**
 * action :业务处理器[抽象]
 * <p>
 * remarks:  <br/>
 * author: wuguoxian <br/>
 * date: 21-7-23 <br/>
 * </p>
 */
public abstract class BasicEventHandler {
    protected final String TAG = "kuyou.common.ku09 > BaseHandler";

    private Context mContext;
    private IModuleManager mModuleManager;
    private IDispatchEventCallback mDispatchEventCallBack;
    private List<Integer> mHandleLocalEventCodeList = null;
    private List<Integer> mHandleRemoteEventCodeList = null;

    protected Context getContext() {
        return mContext;
    }

    public void setContext(Context context) {
        mContext = context;
    }

    public abstract boolean onModuleEvent(RemoteEvent event);

    protected abstract void initHandleEventCodeList();

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

    public BasicEventHandler setDispatchEventCallBack(IDispatchEventCallback dispatchEventCallBack) {
        mDispatchEventCallBack = dispatchEventCallBack;
        return BasicEventHandler.this;
    }

    protected IDispatchEventCallback getDispatchEventCallBack() {
        return mDispatchEventCallBack;
    }

    public BasicEventHandler setModuleManager(IModuleManager moduleManager) {
        mModuleManager = moduleManager;
        return BasicEventHandler.this;
    }

    protected void dispatchEvent(RemoteEvent event) {
        if (null == mDispatchEventCallBack) {
            Log.e(TAG, "dispatchEvent > process fail : mDispatchEventCallBack is null");
            return;
        }
        mDispatchEventCallBack.dispatchEvent(event);
    }

    protected void play(String content) {
        if (null == content || content.length() <= 0) {
            Log.e(TAG, "play > process fail : content is invalid");
            return;
        }
        dispatchEvent(new EventTextToSpeechPlayRequest(content));
    }

    protected void reboot(int delayedMillisecond) {
        if (null == mModuleManager) {
            Log.e(TAG, "reboot > process fail : mModuleManager is null");
            return;
        }
        mModuleManager.reboot(delayedMillisecond);
    }
}
