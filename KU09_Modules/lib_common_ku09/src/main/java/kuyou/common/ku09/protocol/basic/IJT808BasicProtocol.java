package kuyou.common.ku09.protocol.basic;

/**
 * action :KU09
 * <p>
 * remarks:  <br/>
 * author: wuguoxian <br/>
 * date: 21-3-23 <br/>
 * 标准协议指令名称项定义说明[部分]
 * </p>
 */
public interface IJT808BasicProtocol {

    // ========================  BASIC PROTOCOL ================================
    /**
     * action : 终端通用应答
     * flow：client > server
     */
    public final static int C2S_REPLY = 0x0001;
    /**
     * action : 鉴权
     * flow：client > server
     */
    public final static int C2S_AUTHENTICATION = 0x0102;
}
