package org.yzh.protocol.t808;

import org.yzh.framework.orm.annotation.Field;
import org.yzh.framework.orm.annotation.Message;
import org.yzh.framework.orm.model.AbstractMessage;
import org.yzh.framework.orm.model.DataType;
import org.yzh.protocol.basics.Header;
import org.yzh.protocol.commons.JT808;

import java.time.LocalDateTime;

/**
 * @author yezhihao
 * @home https://gitee.com/yezhihao/jt808-server
 */
@Message(JT808.终端拍照上报)
public class T0F01 extends AbstractMessage<Header> {
    private long mediaId;
    private int mediaFormatCode;
    private int eventType;
    private String shootTime;
    private String imgUri;

    public T0F01() {
    }

    public T0F01(String mobileNo) {
        super(new Header(mobileNo, JT808.终端拍照上报));
    }

    @Field(index = 0, type = DataType.DWORD, desc = "多媒体ID")
    public long getMediaId() {
        return mediaId;
    }

    public void setMediaId(long id) {
        this.mediaId = id;
    }

    @Field(index = 1, type = DataType.BYTE, desc = "多媒体格式编码")
    public int getMediaFormatCode() {
        return mediaFormatCode;
    }

    public void setMediaFormatCode(int type) {
        this.mediaFormatCode = type;
    }

    @Field(index = 2, type = DataType.BYTE, desc = "事件项编码")
    public int getEventType() {
        return eventType;
    }

    public void setEventType(int type) {
        this.eventType = type;
    }

    @Field(index = 3, type = DataType.BCD8421,length = 6, desc = "拍摄时间")
    public String getShootTime() {
        return shootTime;
    }

    public void setShootTime(String type) {
        this.shootTime = type;
    }

    @Field(index = 4, type = DataType.STRING, desc = "图片存放地址")
    public String getImgUri() {
        return imgUri;
    }

    public void setImgUri(String type) {
        this.imgUri = type;
    }
}