package com.kuyou.rc.basic.jt808extend.item;

import com.kuyou.rc.basic.jt808extend.InstructionParserListener;

import java.util.Arrays;

import kuyou.common.bytes.ByteUtils;
import kuyou.common.ku09.protocol.basic.IJT808ExtensionProtocol;

/**
 * action :JT808扩展的单项指令编解码器[服务器接收报警处理后的应答]
 * <p>
 * remarks:  <br/>
 * author: wuguoxian <br/>
 * date: 21-8-9 <br/>
 * </p>
 */
public class SicAlarmReply extends SicBasic {
    protected final static String TAG = "com.kuyou.rc.basic.jt808extend.item > SicAlarmReply";

    private int mAlarmType = -1;
    private int mEventType = -1;
    private long mFlowNumReply = -1;

    @Override
    public int getFlag() {
        return IJT808ExtensionProtocol.S2C_ALARM_REPLY;
    }

    @Override
    public String getTitle() {
        return "服务器接收报警处理后的应答";
    }

    @Override
    public byte[] getBody(final int config) {
        return new byte[]{};
    }

    @Override
    public void parse(byte[] data, InstructionParserListener listener) {
        super.parse(data, listener);

        final int indexFlag = 12;
        setFlowNumReply(ByteUtils.bytes2Word(Arrays.copyOfRange(data, indexFlag + 0, indexFlag + 2)));
        setAlarmType(ByteUtils.byte2Int(data[indexFlag + 2]));
        setEventType(ByteUtils.byte2Int(data[indexFlag + 3]));

        //Log.d(TAG, "parse > " + toString());
        listener.onRemote2LocalExpand(SicAlarmReply.this);
    }

    public long getFlowNumReply() {
        return mFlowNumReply;
    }

    public void setFlowNumReply(long flowNumReply) {
        mFlowNumReply = flowNumReply;
    }

    public int getAlarmType() {
        return mAlarmType;
    }

    public void setAlarmType(int alarmType) {
        mAlarmType = alarmType;
    }

    public int getEventType() {
        return mEventType;
    }

    public void setEventType(int eventType) {
        mEventType = eventType;
    }

    public static String getAlarmInfo(int alarmTypeCode, int eventType) {
        StringBuilder info = new StringBuilder(128);

        switch (alarmTypeCode) {
            case IJT808ExtensionProtocol.ALARM_FLAG_CAP_OFF:
                info.append("报警类型:脱帽");
                break;
            case IJT808ExtensionProtocol.ALARM_FLAG_SOS:
                info.append("报警类型:SOS");
                break;
            case IJT808ExtensionProtocol.ALARM_FLAG_NEAR_POWER:
                info.append("报警类型:近电");
                break;
            case IJT808ExtensionProtocol.ALARM_FLAG_ENTRY_AND_EXIT:
                info.append("报警类型:进出");
                break;
            case IJT808ExtensionProtocol.ALARM_FLAG_FALL:
                info.append("报警类型:跌倒");
                break;
            case IJT808ExtensionProtocol.ALARM_FLAG_GAS:
                info.append("报警类型:气体");
                break;
            case IJT808ExtensionProtocol.ALARM_FLAG_GAS_METHANE:
                info.append("报警类型:甲烷气体");
                break;
            case IJT808ExtensionProtocol.ALARM_FLAG_GAS_SULFUR_HEXAFLUORIDE:
                info.append("报警类型:六氟化硫气体");
                break;
            case IJT808ExtensionProtocol.ALARM_FLAG_GAS_CARBON_MONOXIDE:
                info.append("报警类型:一氧化碳气体");
                break;
            default:
                info.append("报警类型:未知,alarmTypeCode = ").append(alarmTypeCode);
                break;
        }
        info.append("\n");

        switch (eventType) {
            case IJT808ExtensionProtocol.ALARM_REPLY_NORMAL:
                info.append("处理类型:未处理[默认]");
                break;
            case IJT808ExtensionProtocol.ALARM_REPLY_PLATFORM_PROCESSED:
                info.append("处理类型:平台已处理");
                break;
            case IJT808ExtensionProtocol.ALARM_REPLY_PLATFORM_PROCESSED_CLOSE_ALARM:
                info.append("处理类型:平台已处理,关闭报警");
                break;
            case IJT808ExtensionProtocol.ALARM_REPLY_PLATFORM_PROCESSED_CLOSE_CONTINUOUS_ALARM:
                info.append("处理类型:平台已处理,关闭本次连续报警");
                break;
            default:
                info.append("处理类型:未知");
                break;
        }
        return info.toString();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(1024);
        String alarmInfo = getAlarmInfo(getAlarmType(), getEventType());
        if (alarmInfo.length() > 0)
            sb.append("\n").append(alarmInfo);
        return sb.toString();
    }
}
