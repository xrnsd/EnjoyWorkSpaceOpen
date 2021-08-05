package org.yzh.protocol.t808;

import org.yzh.framework.orm.annotation.Field;
import org.yzh.framework.orm.annotation.Message;
import org.yzh.framework.orm.model.AbstractMessage;
import org.yzh.framework.orm.model.DataType;
import org.yzh.protocol.basics.Header;
import org.yzh.protocol.commons.JT808;

/**
 * @author yezhihao
 * @home https://gitee.com/yezhihao/jt808-server
 */
@Message(JT808.终端音视频请求)
public class T0F02 extends AbstractMessage<Header> {
    private int mediaType;
    private int eventType;
    private int mediaId;

    public T0F02() {
    }

    public T0F02(String mobileNo) {
        super(new Header(mobileNo, JT808.终端音视频请求));
    }

    @Field(index = 0, type = DataType.BYTE, desc = "多媒体类型")
    public int getMediaType() {
        return mediaType;
    }

    public void setMediaType(int id) {
        this.mediaType = id;
    }

    @Field(index = 1, type = DataType.BYTE, desc = "事件项编码")
    public int getEventType() {
        return eventType;
    }

    public void setEventType(int type) {
        this.eventType = type;
    }

    @Field(index = 2, type = DataType.DWORD, desc = "多媒体ID")
    public int getMediaId() {
        return mediaId;
    }

    public void setMediaId(int id) {
        this.mediaId = id;
    }
}