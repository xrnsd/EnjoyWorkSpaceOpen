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
@Message(JT808.拍照指令下发)
public class T8F02 extends AbstractMessage<Header> {

    private LocalDateTime dateTime;

    public T8F02() {
    }

    public T8F02(String mobileNo) {
        super(new Header(mobileNo, JT808.拍照指令下发));
    }

    @Field(index = 0, type = DataType.BCD8421, desc = "命令时间")
    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }
}