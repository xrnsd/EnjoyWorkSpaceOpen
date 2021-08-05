package kuyou.common.ku09;

import android.content.Context;
import android.util.Log;

import kuyou.common.ipc.RemoteEvent;
import kuyou.common.ku09.event.IDispatchEventCallBack;
import kuyou.common.ku09.event.tts.EventTtsPlayRequest;

/**
 * action :
 * <p>
 * remarks:  <br/>
 * author: wuguoxian <br/>
 * date: 21-7-23 <br/>
 * </p>
 */
public abstract class BaseHandler {
    protected final String TAG = "kuyou.common.ku09 > BaseHandler";

    private IDispatchEventCallBack mDispatchEventCallBack;
    private IModuleManager mModuleManager;
    private Context mContext;

    protected Context getContext() {
        return mContext;
    }

    public void setContext(Context context) {
        mContext = context;
    }

    public abstract boolean onModuleEvent(RemoteEvent event);

    public BaseHandler setDispatchEventCallBack(IDispatchEventCallBack dispatchEventCallBack) {
        mDispatchEventCallBack = dispatchEventCallBack;
        return BaseHandler.this;
    }

    public BaseHandler setModuleManager(IModuleManager moduleManager) {
        mModuleManager = moduleManager;
        return BaseHandler.this;
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
        dispatchEvent(new EventTtsPlayRequest(content));
    }

    protected void reboot(int delayedMillisecond) {
        if (null == mModuleManager) {
            Log.e(TAG, "reboot > process fail : mModuleManager is null");
            return;
        }
        mModuleManager.reboot(delayedMillisecond);
    }
}
