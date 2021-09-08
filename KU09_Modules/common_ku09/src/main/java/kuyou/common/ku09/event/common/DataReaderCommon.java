package kuyou.common.ku09.event.common;

import android.os.Bundle;

import kuyou.common.ipc.RemoteEvent;
import kuyou.common.ku09.event.common.basic.IEventDataKey;
import kuyou.common.ku09.event.common.basic.IEventDataReaderCommon;

/**
 * action :事件[远程事件扩展，添加事件类型][抽象]
 * <p>
 * remarks:  <br/>
 * author: wuguoxian <br/>
 * date: 21-7-27 <br/>
 * </p>
 */
public class DataReaderCommon implements IEventDataReaderCommon<RemoteEvent>, IEventDataKey {

    protected static final String VAL_NONE = "none";

    private volatile static DataReaderCommon sInstance;

    public static DataReaderCommon getInstance() {
        if (sInstance == null) {
            synchronized (DataReaderCommon.class) {
                if (sInstance == null) {
                    sInstance = new DataReaderCommon();
                }
            }
        }
        return sInstance;
    }

    public static int getEventType(RemoteEvent event) {
        return event.getData().getInt(KEY_INITIATE_TYPE);
    }

    public static int getEventType(Bundle data) {
        return data.getInt(KEY_INITIATE_TYPE);
    }

    @Override
    public RemoteEvent setEventType(RemoteEvent item, int val) {
        item.getData().putInt(KEY_EVENT_TYPE, val);
        return item;
    }

    @Override
    public RemoteEvent setPowerStatus(RemoteEvent item, int val) {
        item.getData().putInt(KEY_POWER_STATUS, val);
        return item;
    }

    @Override
    public RemoteEvent setResult(RemoteEvent item, int val) {
        item.getData().putInt(KEY_RESULT_CODE, val);
        return item;
    }

    @Override
    public RemoteEvent setResult(RemoteEvent item, boolean val) {
        item.getData().putInt(KEY_RESULT_CODE,
                val ? EventCommonResult.ResultCode.SUCCESS : EventCommonResult.ResultCode.FAIL);
        return item;
    }
}
