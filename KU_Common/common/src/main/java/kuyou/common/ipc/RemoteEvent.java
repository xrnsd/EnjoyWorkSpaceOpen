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

    protected final static String TAG = "com.kuyou.ipc > RemoteEvent";
    protected final static String KEY_EVENT_CODE = "event.code";
    protected final static String KEY_EVENT_START_PACKAGE_NAME = "event.start.package.name";

    protected final static int NONE = -1;

    private boolean isRemote = false;
    private boolean isDispatch2Myself = false;
    private boolean isEnableConsumeSeparately = true;

    private Bundle mData = null;

    public abstract int getCode();

    public boolean isRemote() {
        return isRemote;
    }

    public RemoteEvent setRemote(boolean val) {
        isRemote = val;
        return RemoteEvent.this;
    }

    public boolean isDispatch2Myself() {
        return isDispatch2Myself;
    }

    public RemoteEvent setPolicyDispatch2Myself(boolean val) {
        isDispatch2Myself = val;
        return RemoteEvent.this;
    }
    
    //是否允许被单独消费，默认是
    public boolean isEnableConsumeSeparately() {
        return isEnableConsumeSeparately;
    }

    public RemoteEvent setEnableConsumeSeparately(boolean enableConsumeSeparately) {
        isEnableConsumeSeparately = enableConsumeSeparately;
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
        return data.getInt(KEY_EVENT_CODE, -1);
    }

    public static String getStartPackageNameByData(RemoteEvent event) {
        return getStartPackageNameByData(event.getData());
    }

    public static String getStartPackageNameByData(Bundle data) {
        return data.getString(KEY_EVENT_START_PACKAGE_NAME);
    }
}
