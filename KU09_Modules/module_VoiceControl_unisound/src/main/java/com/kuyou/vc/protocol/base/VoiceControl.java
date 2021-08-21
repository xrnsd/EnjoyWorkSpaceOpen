package com.kuyou.vc.protocol.base;

import android.content.Context;
import android.util.Log;

import com.kuyou.vc.protocol.CodecVoice;
import com.kuyou.vc.protocol.info.InfoBase;

import java.util.List;

/**
 * action :协议编解码器[语音控制][抽象]
 * <p>
 * remarks:  <br/>
 * author: wuguoxian <br/>
 * date: 21-1-22 <br/>
 * </p>
 */
public abstract class VoiceControl implements InfoBase.IParseFinishListener {
    protected final String TAG = this.getClass().getSimpleName() + "_123456";

    public static interface TYPE {
        /**
         * action:打印调试log<br/>
         * remark:<br/>
         * created: wgx 2018-9-20<br/>
         */
        public static final int SOFT = 0;
        /**
         * action:关闭全局异常捕获<br/>
         * remark:<br/>
         * created: wgx 2018-9-20<br/>
         */
        public static final int HARDWARE = 1;
    }

    public static interface ICallBack {
        public void onPlay(String text);
    }

    private int mTypeCode = TYPE.SOFT;
    private Context mContext;
    private List<String> mCommandList = null;

    private CodecVoice mCodecVoice;
    protected ICallBack mCallBack;

    public void init(Context context) {
        mContext = context;
        mTypeCode = getPolicy();
        mCommandList = getCommandList();
        mCodecVoice = CodecVoice.getInstance();
        mCodecVoice.initInfo(context, VoiceControl.this);
    }

    public void setType(int code) {
        mTypeCode = code;
    }

    public int getType() {
        return mTypeCode;
    }

    public boolean isEnableSoftwareIdentification() {
        return TYPE.SOFT == mTypeCode;
    }

    public boolean isEnableHardwareIdentification() {
        return TYPE.HARDWARE == mTypeCode;
    }

    public void setListener(IOnParseListener listener) {
        if (null == mCodecVoice) {
            Log.e(TAG, "setListener > process fail : mCodecVoice is null");
            return;
        }
        mCodecVoice.setListener(listener);
    }

    public void setCallBack(ICallBack callBack) {
        mCallBack = callBack;
    }

    protected ICallBack getCallBack() {
        return mCallBack;
    }

    protected boolean isValidCommand(String command) {
        if (null == mCommandList
                || mCommandList.size() <= 0) {
            Log.e(TAG, "isValidCommand > process fail : mCommandList is invalid");
            return false;
        }
        if (null == command || !mCommandList.contains(command.replaceAll(" ", ""))) {
            Log.d(TAG, " isValidCommand > invalid cmd: " + command);
            return false;
        }
        return true;
    }

    protected boolean disPatchVoiceCommand(byte[] command) {
        if (null == mCodecVoice) {
            Log.e(TAG, "disPatchVoiceCommand > process fail : mVoiceHandler is null");
            return false;
        }
        mCodecVoice.handle(command);
        return true;
    }

    protected boolean disPatchVoiceCommand(String command) {
        if (!isValidCommand(command)) {
            return false;
        }
        if (null == mCodecVoice) {
            Log.e(TAG, "disPatchVoiceCommand > process fail : mVoiceHandler is null");
            return false;
        }
        mCodecVoice.handle(command);
        return true;
    }

    @Override
    public void onParseFinish(String text) {
        play(text);
    }

    protected void play(String text) {
        if (null == getCallBack()) {
            Log.e(TAG, "play > process fail : getCallback() is null");
            return;
        }
        getCallBack().onPlay(text);
    }

    public abstract void onWakeup();

    public abstract void onSleep();

    /**
     * 启动语音唤醒
     */
    public abstract void start();

    /**
     * 停止录音,停止语音识别
     */
    public abstract void stop();

    protected abstract int getPolicy();

    protected abstract List<String> getCommandList();
}
