package org.yzh.protocol.t808;

import io.github.yezhihao.protostar.DataType;
import io.github.yezhihao.protostar.annotation.Field;
import io.github.yezhihao.protostar.annotation.Message;
import org.yzh.protocol.basics.Header;
import org.yzh.protocol.basics.JTMessage;
import org.yzh.protocol.commons.JT808;

/**
 * @author yezhihao
 * @home https://gitee.com/yezhihao/jt808-server
 */
@Message(JT808.平台报警应答)
public class T8F04 extends JTMessage {

    private int FlowNumber;
    private int mAlarmType;
    private int mEventType;

    public T8F04() {
    }

    public T8F04(String mobileNo) {
        super(new Header(mobileNo, JT808.平台报警应答));
    }

    @Field(index = 0, type = DataType.WORD, desc = "报警消息流水号")
    public int getFlowNumber() {
        return FlowNumber;
    }

    public void setFlowNumber(int flowNumber) {
        this.FlowNumber = flowNumber;
    }

    @Field(index = 1, type = DataType.BYTE, desc = "报警类型ID")
    public int getAlarmType() {
        return mAlarmType;
    }

    public void setAlarmType(int val) {
        this.mAlarmType = val;
    }

    @Field(index = 2, type = DataType.BYTE, desc = "事件项编码")
    public int getEventType() {
        return mEventType;
    }

    public void setEventType(int val) {
        this.mEventType = val;
    }
}