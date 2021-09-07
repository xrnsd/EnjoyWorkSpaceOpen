package com.kuyou.rc.protocol.jt808extend.item;

import android.util.Log;

import com.kuyou.rc.protocol.jt808extend.basic.InstructionParserListener;
import com.kuyou.rc.protocol.jt808extend.basic.SicBasic;

import java.util.Arrays;

import kuyou.common.bytes.ByteUtils;
import kuyou.common.ku09.event.avc.basic.EventAudioVideoCommunication;
import kuyou.common.ku09.event.rc.basic.EventRemoteControl;
import kuyou.common.ku09.protocol.IJT808ExtensionProtocol;
import kuyou.sdk.jt808.basic.jt808coding.JTT808Coding;

/**
 * action :JT808扩展的单项指令编解码器[音视频]
 * <p>
 * remarks:  <br/>
 * author: wuguoxian <br/>
 * date: 21-8-9 <br/>
 * </p>
 */
public class SicAudioVideo extends SicBasic implements IJT808ExtensionProtocol {
    protected final String TAG = "com.kuyou.rc.protocol.jt808extend.item > SicAudioVideo";

    private int mPlatformType = IJT808ExtensionProtocol.PLATFORM_TYPE_PEERGIN;
    private int mChannelId = -1;
    private int mMediaType = -1;
    private int mEventType = -1;
    private int mResult = -1;

    private String mToken = null;

    @Override
    public String getTitle() {
        return "音视频请求";
    }

    @Override
    public int getFlag() {
        return S2C_REQUEST_AUDIO_VIDEO_PARAMETERS;
    }

    @Override
    public boolean isMatchEventCode(int eventCode) {
        return eventCode == EventRemoteControl.Code.AUDIO_VIDEO_PARAMETERS_APPLY_REQUEST
                || eventCode == EventRemoteControl.Code.AUDIO_VIDEO_PARAMETERS_APPLY_RESULT
                || eventCode == EventAudioVideoCommunication.Code.AUDIO_VIDEO_OPERATE_RESULT;
    }

    @Override
    public void parse(byte[] data, InstructionParserListener listener) {
        super.parse(data, listener);
        Log.d(TAG, "parse > ");
        byte[] bytes = getMsgContentAndParseMsgHeader(data);
        try {
            setChannelId(ByteUtils.fourBytes2Int(Arrays.copyOfRange(bytes, 0, 4)));

            setMediaType(ByteUtils.byte2Int(bytes[4]));

            setEventType(ByteUtils.byte2Int(bytes[5]));

            setToken(new String(Arrays.copyOfRange(bytes, 6, bytes.length)));

            Log.d(TAG, toString());
            //打开音频模块
            if (null != listener)
                listener.onRemote2LocalExpand(SicAudioVideo.this);
        } catch (Exception e) {
            if (null != listener)
                listener.onRemote2LocalExpandFail(e);
        }
    }

    @Override
    public byte[] getBody(final int config) {
        switch (config) {
            case BodyConfig.REQUEST:
                byte mediaTypeByte = ByteUtils.int2Byte(getMediaType());
                byte eventTypeByte = ByteUtils.int2Byte(getEventType());
                byte[] tokenBytes = null;

                if (IJT808ExtensionProtocol.PLATFORM_TYPE_AGORA == getPlatformType()) {
                    if (-1 != getChannelId()) {
                        tokenBytes = String.valueOf(getChannelId()).getBytes();
                    }
                }

                if (IJT808ExtensionProtocol.PLATFORM_TYPE_PEERGIN == getPlatformType()) {
                    tokenBytes = getDeviceConfig().getCollectingEndId().getBytes();
                }

                byte[] body = ByteUtils.byteMergerAll(
                        new byte[]{mediaTypeByte, eventTypeByte},
                        tokenBytes
                );
                return getPackToJt808(C2S_REQUEST_PHOTO_UPLOAD, body);

            case BodyConfig.RESULT:
                byte[] flowBytes = ByteUtils.int2Word(Long.valueOf(getFlowNumber()).intValue());
                byte[] tokenBytesResult = getToken().getBytes();
                byte[] resultBytes = new byte[]{ByteUtils.int2Byte(getResult())};

                byte[] bodyResult = ByteUtils.byteMergerAll(
                        flowBytes,
                        resultBytes,
                        tokenBytesResult
                );
                return JTT808Coding.generate808(C2S_RESULT_AUDIO_VIDEO_PARAMETERS, getRemoteControlDeviceConfig(), bodyResult);
            default:
                break;
        }
        Log.e(TAG, "getBody > process fail : config is invalid");
        return new byte[0];
    }

    public int getPlatformType() {
        return mPlatformType;
    }

    public SicAudioVideo setPlatformType(int platformType) {
        mPlatformType = platformType;
        return SicAudioVideo.this;
    }

    public SicAudioVideo setChannelId(int val) {
        mChannelId = val;
        return SicAudioVideo.this;
    }

    public int getChannelId() {
        return mChannelId;
    }

    public SicAudioVideo setMediaType(int val) {
        mMediaType = val;
        return SicAudioVideo.this;
    }

    public int getMediaType() {
        return mMediaType;
    }

    public SicAudioVideo setEventType(int val) {
        mEventType = val;
        return SicAudioVideo.this;
    }

    public int getEventType() {
        return mEventType;
    }

    public SicAudioVideo setToken(String val) {
        if (null == val || val.length() <= 0)
            val = TOKEN_NULL;
        mToken = val;
        return SicAudioVideo.this;
    }

    public String getToken() {
        return mToken;
    }

    public int getResult() {
        return mResult;
    }

    public SicAudioVideo setResult(int result) {
        mResult = result;
        return SicAudioVideo.this;
    }

    public boolean isClose() {
        return IJT808ExtensionProtocol.EVENT_TYPE_CLOSE == getEventType();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(1024);
        sb.append("mPlatformType = ").append(mPlatformType);
        sb.append("\nmResult = ").append(mResult);
        sb.append("\nmChannelId = ").append(mChannelId);
        sb.append("\nmMediaType = ").append(mMediaType);
        sb.append("\nmEventType = ").append(mEventType);
        sb.append("\nmToken = ").append(mToken);
        return sb.toString();
    }
}
