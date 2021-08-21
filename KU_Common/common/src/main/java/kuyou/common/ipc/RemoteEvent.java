package kuyou.common.ipc;

import android.os.Bundle;

/**
 * action :IPC框架传递的事件
 * <p>
 * remarks:  <br/>
 * author: wuguoxian <br/>
 * date: 21-3-29 <br/>
 * </p>
 */
public abstract class RemoteEvent {

    public final static String KEY_EVENT_CODE = "event.code";
    public final static String KEY_EVENT_START_PACKAGE_NAME = "event.start.package.name";
    public final static int NONE = -1;

    protected final String TAG = "com.kuyou.ipc > " + this.getClass().getSimpleName();

    private boolean isRemote = false;

    private Bundle mData = null;
    
    protected String mStartPackageName = null;

    public abstract int getCode();

    public String getStartPackageName() {
        return mStartPackageName;
    }

    public RemoteEvent setStartPackageName(String val){
        mStartPackageName = val;
        getData().putString(KEY_EVENT_START_PACKAGE_NAME, val);
        return RemoteEvent.this;
    }

    public boolean isRemote() {
        return isRemote;
    }

    public RemoteEvent setRemote(boolean val) {
        isRemote = val;
        return RemoteEvent.this;
    }

    public Bundle getData() {
        if (null == mData) {
            mData = new Bundle();
            applyCode();
        }
        return mData;
    }

    public RemoteEvent setData(Bundle data) {
        mData = data;
        applyCode();
        return RemoteEvent.this;
    }

    protected void applyCode() {
        getData().putInt(KEY_EVENT_CODE, getCode());
    }

    public static int getCodeByData(Bundle data) {
        return data.getInt(KEY_EVENT_CODE,-1);
    }

    public static String getStartPackageNameByData(RemoteEvent event) {
        return event.getData().getString(KEY_EVENT_START_PACKAGE_NAME);
    }

    public static String getStartPackageNameByData(Bundle data) {
        return data.getString(KEY_EVENT_START_PACKAGE_NAME);
    }
}
