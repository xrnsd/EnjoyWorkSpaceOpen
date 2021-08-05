package com.kuyou.jt808.info;

import android.util.Log;

import java.util.Arrays;

import kuyou.common.ku09.bytes.ByteUtils;
import kuyou.sdk.jt808.base.jt808coding.JTT808Coding;

import kuyou.common.ku09.JT808ExtensionProtocol.IAudioVideo;

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

    public byte[] getApplyAudioVideoParametersMsgByMediaTypeCode(int mediaTypeCode, boolean val) {
        return getRequestMsgBytes(mediaTypeCode, val ? EVENT_TYPE_LOCAL_INITIATED : EVENT_TYPE_CLOSE, val ? -1 : getChannelId());
    }

    public byte[] getRequestMsgBytes(int mediaType, int eventType, int channelId) {
        byte mediaTypeByte = ByteUtils.int2Byte(mediaType);
        byte eventTypeByte = ByteUtils.int2Byte(eventType);
        if (-1 != channelId) {
            byte[] mediaIdByte = ByteUtils.int2DWord(channelId);
            byte[] body = ByteUtils.byteMergerAll(
                    new byte[]{mediaTypeByte, eventTypeByte},
                    mediaIdByte
            );
            return JTT808Coding.generate808(MSG_ID_0F02, getConfig(), body);
        } else {
            return JTT808Coding.generate808(MSG_ID_0F02, getConfig(), new byte[]{mediaTypeByte, eventTypeByte});
        }
    }

    private static final int CODE_RES_808_2_LIVE_SUCCESS = 0;
    private static final int CODE_RES_808_2_LIVE_FAIL = 1;

    private static final int CODE_RESULT_LIVE_2_808_AGORA_REJECTED = 1; //声网拒绝
    private static final int CODE_RESULT_LIVE_2_808_PLATFORM_REJECTED = 2; // 平台拒绝
    private static final int CODE_RESULT_LIVE_2_808_DEVICE_REJECTED = 3; // 终端拒绝
    private static final int CODE_RESULT_LIVE_2_808_TIMEOUT = 4; //超时
    private static final int CODE_RESULT_LIVE_2_808_OTHER = 5; //其它
    private static final int CODE_RESULT_LIVE_2_808_NONE = 6; //成功加入频道

    public byte[] getResultMsgBytes(int type, String msg) {
        int result = CODE_RES_808_2_LIVE_FAIL;

        switch (type) {
            case CODE_RESULT_LIVE_2_808_AGORA_REJECTED:
            case CODE_RESULT_LIVE_2_808_PLATFORM_REJECTED:
            case CODE_RESULT_LIVE_2_808_DEVICE_REJECTED:
            case CODE_RESULT_LIVE_2_808_TIMEOUT:
            case CODE_RESULT_LIVE_2_808_OTHER:
                result = CODE_RES_808_2_LIVE_FAIL;
                break;
            case CODE_RESULT_LIVE_2_808_NONE:
                result = CODE_RES_808_2_LIVE_SUCCESS;
                break;
            default:
                break;
        }

        byte[] flowNumResult = ByteUtils.int2Word(getMsgFlowNumber());
        byte[] channelId = ByteUtils.int2DWord(getChannelId());
        byte resultByte = ByteUtils.int2Byte(result);
        byte typeByte = ByteUtils.int2Byte(type);

        byte[] body = ByteUtils.byteMergerAll(
                flowNumResult,
                channelId,
                result == CODE_RES_808_2_LIVE_FAIL ? new byte[]{resultByte, typeByte} : new byte[]{resultByte}
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

    public void setEventType(int val) {
        mEventType = val;
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
            mRequestOpenLiveListener.requestOpenLive(AudioVideoInfo.this);
    }

    public void setRequestOpenLiveListener(IRequestOpenLiveListener listener) {
        mRequestOpenLiveListener = listener;
    }

    public static interface IRequestOpenLiveListener {
        public void requestOpenLive(AudioVideoInfo info);
    }
}
