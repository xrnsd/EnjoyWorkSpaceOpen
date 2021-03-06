package kuyou.common.ku09.event.common.base;

import androidx.annotation.NonNull;

import kuyou.common.ipc.RemoteEvent;

/**
 * action :
 * <p>
 * remarks:  <br/>
 * author: wuguoxian <br/>
 * date: 21-3-23 <br/>
 * </p>
 */
public abstract class EventKey extends ModuleEventCommon {
    public static final String KEY_CODE = "key.code";

    public EventKey() {

    }

    public EventKey(int keyCode) {
        getData().putInt(KEY_CODE, keyCode);
    }

    @NonNull
    @Override
    public String toString() {
        return new StringBuilder()
                .append("getCode = ").append(getCode())
                .append("data.keyCode = ").append(getData().getInt(KEY_CODE))
                .toString();
    }

    public static int getKeyCode(RemoteEvent event) {
        return event.getData().getInt(KEY_CODE);
    }
}
