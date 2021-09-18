package com.kuyou.rc.basic.jt808extend.item;

import com.kuyou.rc.basic.jt808extend.InstructionParserListener;

import java.util.Arrays;

import kuyou.common.ku09.protocol.basic.IDeviceConfig;
import kuyou.common.ku09.protocol.basic.IJT808ExtensionProtocol;
import kuyou.sdk.jt808.basic.RemoteControlDeviceConfig;
import kuyou.sdk.jt808.basic.jt808bean.JTT808Bean;
import kuyou.sdk.jt808.basic.jt808coding.JTT808Coding;

/**
 * action :JT808扩展的单项指令编解码器[抽象]
 * <p>
 * remarks:  <br/>
 * author: wuguoxian <br/>
 * date: 21-8-9 <br/>
 * </p>
 */
public abstract class SicBasic extends kuyou.common.protocol.Info<InstructionParserListener> implements IJT808ExtensionProtocol {

    public static interface BodyConfig {
        public final static int REQUEST = 0;
        public final static int RESULT = 1;
    }

    protected JTT808Bean mMsgHeader;
    protected IDeviceConfig mDeviceConfig;
    protected RemoteControlDeviceConfig mRemoteControlDeviceConfig;
    private int mBodyConfig = -1;
    private long mFlowId = -1;

    public int getBodyConfig() {
        return mBodyConfig;
    }

    public SicBasic setBodyConfig(int bodyConfig) {
        mBodyConfig = bodyConfig;
        return SicBasic.this;
    }

    public boolean isMatchEventCode(int eventCode) {
        return false;
    }

    public abstract byte[] getBody(final int config);

    @Override
    public byte[] getBody() {
        return getBody(BodyConfig.RESULT);
    }

    @Override
    public int getFlag() {
        return -1;
    }

    @Override
    public int getCmdCode() {
        return -1;
    }

    @Override
    public void parse(byte[] data, InstructionParserListener listener) {
        if (null == listener) {
            throw new NullPointerException("parse > process fail : listener is null");
        }
    }

    @Override
    public boolean isSuccess() {
        return false;
    }

    @Override
    public void reset() {
        mBodyConfig = -1;
    }

    protected byte[] getMsgContentAndParseMsgHeader(byte[] bytes) {
        mMsgHeader = new JTT808Bean();
        byte[] msgId = Arrays.copyOfRange(bytes, 0, 2);
        byte[] msgBodyAttributes = Arrays.copyOfRange(bytes, 2, 4);
        byte[] phone = Arrays.copyOfRange(bytes, 4, 10);
        byte[] msgFlowNum = Arrays.copyOfRange(bytes, 10, 12);
        mMsgHeader.setMsgId(msgId);
        mMsgHeader.setMsgBodyAttributes(msgBodyAttributes);
        mMsgHeader.setPhoneNumber(phone);
        mMsgHeader.setMsgFlowNumber(msgFlowNum);
        setFlowNumber(mMsgHeader.getMsgFlowNumber());

        return Arrays.copyOfRange(bytes, 12, bytes.length);
    }

    protected byte[] getPackToJt808(int instruction, byte[] body) {
        return JTT808Coding.generate808(instruction, getRemoteControlDeviceConfig(), body);
    }

    public RemoteControlDeviceConfig getRemoteControlDeviceConfig() {
        if (null == mRemoteControlDeviceConfig) {
            mRemoteControlDeviceConfig = new RemoteControlDeviceConfig() {
                @Override
                public String getDevId() {
                    return SicBasic.this.getDeviceConfig().getDevId();
                }
            };
        }
        return mRemoteControlDeviceConfig;
    }

    public IDeviceConfig getDeviceConfig() {
        return mDeviceConfig;
    }

    public SicBasic setDeviceConfig(IDeviceConfig config) {
        mDeviceConfig = config;
        return SicBasic.this;
    }

    public long getFlowNumber() {
        return mFlowId;
    }

    public SicBasic setFlowNumber(long flowId) {
        mFlowId = flowId;
        return SicBasic.this;
    }
}
