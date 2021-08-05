package org.yzh.protocol.t808;

import org.yzh.framework.orm.annotation.Field;
import org.yzh.framework.orm.annotation.Fs;
import org.yzh.framework.orm.annotation.Message;
import org.yzh.framework.orm.model.AbstractMessage;
import org.yzh.framework.orm.model.DataType;
import org.yzh.protocol.basics.Header;
import org.yzh.protocol.commons.JT808;

/**
 * @author yezhihao
 * @home https://gitee.com/yezhihao/jt808-server
 */
@Message(JT808.音视频参数下发)
public class T8F03 extends AbstractMessage<Header> {
    private int sign;
    private int mediaId;
    private int mediaType;
    private int eventType;
    private String mToken;

    public T8F03() {
    }

    public T8F03(String mobileNo) {
        super(new Header(mobileNo, JT808.音视频参数下发));
    }

    @Field(index = 0, type = DataType.DWORD, desc = "声网频道ID")
    public int getMediaId() {
        return mediaId;
    }

    public void setMediaId(int id) {
        this.mediaId = id;
    }

    @Field(index = 1, type = DataType.BYTE, desc = "多媒体类型")
    public int getMediaType() {
        return mediaType;
    }

    public void setMediaType(int type) {
        this.mediaType = type;
    }

    @Field(index = 2, type = DataType.BYTE, desc = "事件项编码")
    public int getEventType() {
        return eventType;
    }

    public void setEventType(int type) {
        this.eventType = type;
    }

    @Field(index = 3, type = DataType.STRING, desc = "Token")
    public String getToken() {
        return mToken;
    }

    public void setToken(String content) {
        this.mToken = content;
    }
}