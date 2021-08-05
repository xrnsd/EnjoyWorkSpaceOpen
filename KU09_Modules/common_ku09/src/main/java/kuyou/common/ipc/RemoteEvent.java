package kuyou.common.ipc;

import android.os.Bundle;

/**
 * action :
 * <p>
 * remarks:  <br/>
 * author: wuguoxian <br/>
 * date: 21-3-29 <br/>
 * </p>
 */
public abstract class RemoteEvent {

    public final static String KEY_EVENT_CODE = "event.code";
    public final static int NONE = -1;

    protected final String TAG = "kuyou.common.ipc > " + this.getClass().getSimpleName();

    private boolean isRemote = false;

    private Bundle mData = null;

    public abstract int getCode();

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
        return data.getInt(KEY_EVENT_CODE);
    }
}
