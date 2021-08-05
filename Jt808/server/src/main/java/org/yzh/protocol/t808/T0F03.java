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
@Message(JT808.应答终端音视频请求)
public class T0F03 extends AbstractMessage<Header> {
    private int flowId;
    private int mediaId;
    private int result;
    private int failReason;

    public T0F03() {
    }

    public T0F03(String mobileNo) {
        super(new Header(mobileNo, JT808.应答终端音视频请求));
    }

    @Field(index = 0, type = DataType.WORD, desc = "应答流水号")
    public int getFlowId() {
        return flowId;
    }

    public void setFlowId(int id) {
        this.flowId = id;
    }

    @Field(index = 1, type = DataType.DWORD, desc = "多媒体ID")
    public int getMediaId() {
        return mediaId;
    }

    public void setMediaId(int id) {
        this.mediaId = id;
    }

    @Field(index = 2, type = DataType.BYTE, desc = "结果")
    public int getResult() {
        return result;
    }

    public void setResult(int type) {
        this.result = type;
    }

    @Field(index = 3, type = DataType.BYTE, desc = "失败原因")
    public int getFailReason() {
        return failReason;
    }

    public void setFailReason(int type) {
        this.failReason = type;
    }
}