package com.kuyou.jt808.protocol;

import android.util.Log;

import com.cuichen.jt808_sdk.sdk.SocketConfig;
import com.cuichen.jt808_sdk.sdk.jt808coding.JTT808Coding;
import com.cuichen.jt808_sdk.sdk.jt808utils.ByteUtil;

import java.util.Arrays;

/**
 * action :0x8F03 ,0x0F03
 * <p>
 * author: wuguoxian <br/>
 * date: 20-10-19 <br/>
 * <p>
 */
public class AudioVideoInfo extends MsgInfo {

    private int mChannelId = -1;
    private int mMediaType = -1;
    private int mEventType = -1;
    private String mToken = null;

    private static final String KEY_TOKEN = "token";
    private static final String KEY_MEDIA_TYPE = "mediaType";
    private static final String KEY_CHANNEL_ID = "channelId";

    public static final int MEDIA_TYPE_AUDIO = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;
    public static final int MEDIA_TYPE_INFEARED = 3;

    public static final int EVENT_TYPE_OPEN = 1;
    public static final int EVENT_TYPE_CLOSE = 255;

    public AudioVideoInfo() {
        super();
    }

    @Override
    public boolean parse(byte[] bytes) {
        super.parse(bytes);
        bytes = getMsgContent();
        try {
            setChannelId(ByteUtil.fourBytes2Int(Arrays.copyOfRange(bytes, 0, 4)));

            setMediaType(ByteUtil.byte2Int(bytes[4]));

            setEventType(ByteUtil.byte2Int(bytes[5]));

            setToken(new String(Arrays.copyOfRange(bytes, 6, bytes.length)));

            Log.d(TAG, toString());

            //打开音频模块
            getTtsServiceManager().openLive(getMediaType(),getToken(),String.valueOf(getChannelId()));
            return true;
        } catch (Exception e) {
            Log.e("123456", Log.getStackTraceString(e));
        }
        Log.d(TAG, toString());
        return false;
    }
    public static final String ACTION_MEDIA_STATUS = "action.media.status";

    public byte[] getRequestOpenAudioParameterMsg() {
        return getRequestMsgBytes(MEDIA_TYPE_AUDIO, EVENT_TYPE_OPEN, -1);
    }

    public byte[] getRequestOpenVideoParameterMsg() {
        return getRequestMsgBytes(MEDIA_TYPE_VIDEO, EVENT_TYPE_OPEN, -1);
    }

    public byte[] getRequestCloseVideoParameterMsg() {
        return getRequestMsgBytes(MEDIA_TYPE_VIDEO, EVENT_TYPE_CLOSE, getChannelId());
    }

    public byte[] getRequestMsgBytes(int mediaType, int eventType, int channelId) {
        byte mediaTypeByte = ByteUtil.int2Byte(mediaType);
        byte eventTypeByte = ByteUtil.int2Byte(eventType);
        if (-1 != channelId) {
            byte[] mediaIdByte = ByteUtil.int2DWord(channelId);
            byte[] body = ByteUtil.byteMergerAll(
                    new byte[]{mediaTypeByte, eventTypeByte},
                    mediaIdByte
            );
            return JTT808Coding.generate808(MSG_ID_0F02, SocketConfig.getmPhont(), body);
        } else {
            return JTT808Coding.generate808(MSG_ID_0F02, SocketConfig.getmPhont(), new byte[]{mediaTypeByte, eventTypeByte});
        }
    }
    
    private static final int CODE_RES_808_2_LIVE_SUCCESS = 0;
    private static final int CODE_RES_808_2_LIVE_FAIL = 1;

    private static final int CODE_ERR_808_2_LIVE_AGORA_REJECTED = 1; //声网拒绝
    private static final int CODE_ERR_808_2_LIVE_PLATFORM_REJECTED = 2; // 平台拒绝
    private static final int CODE_ERR_808_2_LIVE_DEVICE_REJECTED = 3; // 终端拒绝
    private static final int CODE_ERR_808_2_LIVE_TIMEOUT = 4; //超时
    private static final int CODE_ERR_808_2_LIVE_OTHER = 5; //其它
    private static final int CODE_ERR_808_2_LIVE_NONE = 6; //成功加入频道

    public byte[] getResultMsgBytes(int type, String msg) {
        int result = CODE_RES_808_2_LIVE_FAIL;

        switch (type) {
            case CODE_ERR_808_2_LIVE_AGORA_REJECTED:
            case CODE_ERR_808_2_LIVE_PLATFORM_REJECTED:
            case CODE_ERR_808_2_LIVE_DEVICE_REJECTED:
            case CODE_ERR_808_2_LIVE_TIMEOUT:
            case CODE_ERR_808_2_LIVE_OTHER:
                result = CODE_RES_808_2_LIVE_FAIL;
                break;
            case CODE_ERR_808_2_LIVE_NONE:
                result = CODE_RES_808_2_LIVE_SUCCESS;
                break;
            default:
                break;
        }

        byte[] flowNumResult = ByteUtil.int2Word(getMsgFlowNumber());
        byte[] channelId = ByteUtil.int2DWord(getChannelId());
        byte resultByte = ByteUtil.int2Byte(result);
        byte typeByte = ByteUtil.int2Byte(type);

        byte[] body = ByteUtil.byteMergerAll(
                flowNumResult,
                channelId,
                result == CODE_RES_808_2_LIVE_FAIL ? new byte[]{resultByte, typeByte} : new byte[]{resultByte}
        );

        return JTT808Coding.generate808(MSG_ID_0F03, SocketConfig.getmPhont(), body);
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
        if(null==val||val.length()<=0)
            val="none";
        mToken = val;
    }

    public String getToken() {
        return mToken;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(1024);
        sb.append("mChannelId = ").append(mChannelId);
        sb.append("\nmMediaType = ").append(mMediaType);
        sb.append("\nmEventType = ").append(mEventType);
        sb.append("\nsetToken = ").append(ByteUtil.bytes2Hex(mToken.getBytes()));
        sb.append("\nmToken = ").append(mToken);
        return sb.toString();
    }
}
