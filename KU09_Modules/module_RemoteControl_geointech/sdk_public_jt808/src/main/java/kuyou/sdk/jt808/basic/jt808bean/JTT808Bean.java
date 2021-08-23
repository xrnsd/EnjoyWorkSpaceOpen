package kuyou.sdk.jt808.base.jt808bean;

import java.util.Arrays;

import kuyou.common.bytes.ByteUtils;
import kuyou.common.bytes.HexUtil;


/**
 * 解析获注册返回的数据内容的808实体数据
 *
 * @author CCB
 */
public class JTT808Bean {

    //消息id
    private int msgId;
    //消息体属性
    private int msgBodyAttributes;
    //手机号
    private String phoneNumber;
    //消息流水号
    private int msgFlowNumber;
    //应答流水号
    private long replyFlowNumber;
    //应答结果
    private int replyResult;
    //数据包返回的消息id
    private int returnMsgId;
    //鉴权码
    private byte[] authenticationCode;

    public int getMsgId() {
        return msgId;
    }

    public void setMsgId(byte[] msgId) {
        this.msgId = ByteUtils.bytes2Int(msgId);
    }

    public int getMsgBodyAttributes() {
        return msgBodyAttributes;
    }

    public void setMsgBodyAttributes(byte[] msgBodyAttributes) {
        this.msgBodyAttributes = ByteUtils.bytes2Int(msgBodyAttributes);
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(byte[] phoneNumber) {
        this.phoneNumber = HexUtil.ByteToString(phoneNumber);
    }

    public int getMsgFlowNumber() {
        return msgFlowNumber;
    }

    public void setMsgFlowNumber(byte[] msgFlowNumber) {
        this.msgFlowNumber = ByteUtils.bytes2Int(msgFlowNumber);
    }

    public long getReplyFlowNumber() {
        return replyFlowNumber;
    }

    public void setReplyFlowNumber(byte[] replyFlowNumber) {
        this.replyFlowNumber = ByteUtils.bytes2Int(replyFlowNumber);
    }

    public int getReplyResult() {
        return replyResult;
    }

    public void setReplyResult(byte replyResult) {
        this.replyResult = ByteUtils.byte2Int(replyResult);
    }

    public byte[] getAuthenticationCode() {
        return authenticationCode;
    }

    public void setAuthenticationCode(byte[] authenticationCode) {
        this.authenticationCode = authenticationCode;
    }

    public int getReturnMsgId() {
        return returnMsgId;
    }

    public void setReturnMsgId(byte[] returnMsgId) {
        this.returnMsgId = ByteUtils.fourBytes2Int(returnMsgId);
    }

    @Override
    public String toString() {
        return new StringBuilder(1024)
                .append("msgId=").append(String.format("0x%04x", msgId))
                .append(", msgBodyAttributes=").append(msgBodyAttributes)
                .append(", phoneNumber=").append(phoneNumber)
                .append(", msgFlowNumber=").append(msgFlowNumber)
                .append(", replyFlowNumber=").append(replyFlowNumber)
                .append(", replyResult=").append(replyResult)
                .append(", returnMsgId=").append(returnMsgId)
                .append(", authenticationCode=").append(Arrays.toString(authenticationCode))
                .toString();
    }
}
