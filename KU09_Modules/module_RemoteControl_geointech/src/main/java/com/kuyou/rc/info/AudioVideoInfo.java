package com.kuyou.rc.info;

import android.util.Log;

import java.util.Arrays;

import kuyou.common.bytes.ByteUtils;
import kuyou.common.ku09.event.avc.base.IAudioVideo;
import kuyou.sdk.jt808.base.jt808coding.JTT808Coding;

/**
 * action :0x8F03 ,0x0F03
 * <p>
 * author: wuguoxian <br/>
 * date: 20-10-19 <br/>
 * <p>
 */
public class AudioVideoInfo extends MsgInfo implements IAudioVideo {

    private int mChannelId = -1;
    private int mMediaType = -1;
    private int mEventType = -1;

    protected final String TOKEN_NULL = "none";
    private String mToken = null;

    @Override
    public boolean parse(byte[] bytes) {
        super.parse(bytes);
        bytes = getMsgContent();
        try {
            setChannelId(ByteUtils.fourBytes2Int(Arrays.copyOfRange(bytes, 0, 4)));

            setMediaType(ByteUtils.byte2Int(bytes[4]));

            setEventType(ByteUtils.byte2Int(bytes[5]));

            setToken(new String(Arrays.copyOfRange(bytes, 6, bytes.length)));

            Log.d(TAG, toString());

            //打开音频模块
            requestOpenLive();
            return true;
        } catch (Exception e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }
        Log.d(TAG, toString());
        return false;
    }

    public byte[] getApplyAudioVideoParametersMsgByMediaTypeCode(int platformType, int mediaTypeCode, boolean isSwitch) {
        return getRequestMsgBytes(platformType, mediaTypeCode, getEventType(), isSwitch ? -1 : getChannelId());
    }

    public byte[] getRequestMsgBytes(int platformType, int mediaType, int eventType, int channelId) {
        byte mediaTypeByte = ByteUtils.int2Byte(mediaType);
        byte eventTypeByte = ByteUtils.int2Byte(eventType);

        switch (platformType) {
            case IAudioVideo.PLATFORM_TYPE_AGORA:
                if (-1 != channelId) {
                    byte[] tokenBytes = String.valueOf(channelId).getBytes();
                    byte[] body = ByteUtils.byteMergerAll(
                            new byte[]{mediaTypeByte, eventTypeByte},
                            tokenBytes
                    );
                    return JTT808Coding.generate808(MSG_ID_0F02, getConfig(), body);
                }
                return JTT808Coding.generate808(MSG_ID_0F02, getConfig(), new byte[]{mediaTypeByte, eventTypeByte});
            case IAudioVideo.PLATFORM_TYPE_PEERGIN:
                byte[] tokenBytes = getConfig().getCollectingEndId().getBytes();
                byte[] body = ByteUtils.byteMergerAll(
                        new byte[]{mediaTypeByte, eventTypeByte},
                        tokenBytes
                );
                return JTT808Coding.generate808(MSG_ID_0F02, getConfig(), body);
            default:
                Log.e(TAG, "getRequestMsgBytes > process fail : invalid platformType = " + platformType);
                return null;
        }
    }

    /**
     * MSG_ID_0F03
     * <p>
     * String token,int flowId,int result
     */
    public byte[] getResultMsgBytes(String token, int... config) {
        final int flowId = config[0], result = config[1];
        byte[] flowBytes = ByteUtils.int2Word(flowId);
        byte[] tokenBytes = token.getBytes();
        byte[] resultBytes = new byte[]{
                ByteUtils.int2Byte(result)};

        byte[] body = ByteUtils.byteMergerAll(
                flowBytes,
                resultBytes,
                tokenBytes
        );
        return JTT808Coding.generate808(MSG_ID_0F03, getConfig(), body);
    }

    public int getMsgFlowNumber() {
        return mMsgHeader.getMsgFlowNumber();
    }

    public void setChannelId(int val) {
        mChannelId = val;
    }

    public int getChannelId() {
        return mChannelId;
    }

    public void setMediaType(int val) {
        mMediaType = val;
    }

    public int getMediaType() {
        return mMediaType;
    }

    public AudioVideoInfo setEventType(int val) {
        mEventType = val;
        return AudioVideoInfo.this;
    }

    public int getEventType() {
        return mEventType;
    }

    public void setToken(String val) {
        if (null == val || val.length() <= 0)
            val = TOKEN_NULL;
        mToken = val;
    }

    public String getToken() {
        return mToken;
    }

    public boolean isClose() {
        return EVENT_TYPE_CLOSE == getEventType();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(1024);
        sb.append("mChannelId = ").append(mChannelId);
        sb.append("\nmMediaType = ").append(mMediaType);
        sb.append("\nmEventType = ").append(mEventType);
        sb.append("\nsetToken = ").append(ByteUtils.bytes2Hex(mToken.getBytes()));
        sb.append("\nmToken = ").append(mToken);
        return sb.toString();
    }

    IRequestOpenLiveListener mRequestOpenLiveListener;

    private void requestOpenLive() {
        if (null != mRequestOpenLiveListener)
            mRequestOpenLiveListener.onAudioVideoOperateRequest(AudioVideoInfo.this);
    }

    public void setRequestOpenLiveListener(IRequestOpenLiveListener listener) {
        mRequestOpenLiveListener = listener;
    }

    public static interface IRequestOpenLiveListener {
        public void onAudioVideoOperateRequest(AudioVideoInfo info);
    }
}
