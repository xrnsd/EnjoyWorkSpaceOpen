package com.kuyou.tts.basic;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import androidx.annotation.NonNull;

import com.unisound.client.SpeechConstants;
import com.unisound.client.SpeechSynthesizer;
import com.unisound.client.SpeechSynthesizerListener;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import kuyou.common.file.AssetsCopy;

public class TTSManager extends SpeechSynthesizer implements TTSConfig {
    private static final String TAG = "com.kuyou.tts.base > SpeechSynthesizer";

    private static final int MSG_SYNTHESIZED_TTS = 2033;
    private static final int SYNTHESIZED_TTS_INTERVAL = 1500;//TTS合成间隔
    private Queue<String> mQueueUnsynthesized = new LinkedList<String>();
    private String mSpeechSynthesisContent = null;
    private Handler mHandlerUnsynthesized = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (MSG_SYNTHESIZED_TTS == msg.what) {
                mSpeechSynthesisContent = null;
                mSpeechSynthesisContent = mQueueUnsynthesized.poll();
                if (null != mSpeechSynthesisContent) {
                    TTSManager.super.playText(mSpeechSynthesisContent);
                }
            }
        }
    };

    //用于多次回调使用
    public static interface ISynthesizerListener {
        public void onInitFinish();

        public void onInitFail(int type, String errorMSG);

        public void onPlayBegin(String speechSynthesisContent);

        public void onPlayEnd(String speechSynthesisContent);
    }

    private List<ISynthesizerListener> mSynthesizerListeners = new ArrayList<ISynthesizerListener>();

    public void addSynthesizerListener(ISynthesizerListener listener) {
        if (!mSynthesizerListeners.contains(listener))
            mSynthesizerListeners.add(listener);

        InputStream abpath = getClass().getResourceAsStream("/assets/文件名");
    }

    private static TTSManager sMain;

    public static TTSManager getInstance(Context context, ISynthesizerListener listener) {
        if (null == sMain) {
            sMain = new TTSManager(context, APP_KEY, SECRET);

        }
        sMain.addSynthesizerListener(listener);
        return sMain;
    }

    private TTSManager(Context context, String appKey, String secret) {
        super(context, appKey, secret);
        initialize(context);
    }

    private void initialize(Context context) {
        initModelFileConfig(context);

        setOption(SpeechConstants.TTS_SERVICE_MODE, SpeechConstants.TTS_SERVICE_MODE_LOCAL);
        // 设置前端模型
        File modelFile = new File(FILE_PATH_MODEL_FRONTEND);
        if (modelFile.exists())
            setOption(SpeechConstants.TTS_KEY_FRONTEND_MODEL_PATH, modelFile.getPath());
        // 设置后端模型
        modelFile = new File(FILE_PATH_MODEL_BACKEND);
        if (modelFile.exists())
            setOption(SpeechConstants.TTS_KEY_BACKEND_MODEL_PATH, modelFile.getPath());
        // 设置回调监听
        setTTSListener(new SpeechSynthesizerListener() {
            @Override
            public void onEvent(int type) {
                switch (type) {
                    // 初始化成功回调
                    case SpeechConstants.TTS_EVENT_INIT:
                        onInitFinish();
                        break;
                    // 开始合成回调
                    case SpeechConstants.TTS_EVENT_SYNTHESIZER_START:
                        break;
                    // 合成结束回调
                    case SpeechConstants.TTS_EVENT_SYNTHESIZER_END:
                        break;
                    // 开始缓存回调
                    case SpeechConstants.TTS_EVENT_BUFFER_BEGIN:
                        break;
                    // 缓存完毕回调
                    case SpeechConstants.TTS_EVENT_BUFFER_READY:
                        break;
                    // 开始播放回调
                    case SpeechConstants.TTS_EVENT_PLAYING_START:
                        break;
                    // 播放完成回调
                    case SpeechConstants.TTS_EVENT_PLAYING_END:
                        onPlayEnd();
                        break;
                    // 暂停回调
                    case SpeechConstants.TTS_EVENT_PAUSE:
                        break;
                    // 恢复回调
                    case SpeechConstants.TTS_EVENT_RESUME:
                        break;
                    // 停止回调
                    case SpeechConstants.TTS_EVENT_STOP:
                        break;
                    // 释放资源回调
                    case SpeechConstants.TTS_EVENT_RELEASE:
                        break;
                    default:
                        break;
                }

            }

            @Override
            public void onError(int type, String errorMSG) {
                onInitFail(type, errorMSG);
            }
        });
        init("");
    }

    @Override
    public int playText(String s) {
        return 0;//super.playText(s);
    }

    @Override
    public void stop() {
        try {
            mQueueUnsynthesized.clear();
        } catch (Exception e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }
        super.stop();
    }

    public void play(String text) {
        synchronized (mQueueUnsynthesized) {
            mQueueUnsynthesized.offer(text);
        }
        if (!mHandlerUnsynthesized.hasMessages(MSG_SYNTHESIZED_TTS)) {
            mHandlerUnsynthesized.sendEmptyMessage(MSG_SYNTHESIZED_TTS);
        }
    }

    public void onInitFinish() {
        Log.d(TAG, "onInitFinish");
        for (ISynthesizerListener listener : mSynthesizerListeners) {
            listener.onInitFinish();
        }
    }

    // 语音合成错误回调
    public void onInitFail(int type, String errorMSG) {
        Log.e(TAG, "onInitFail > errorMSG=" + errorMSG);
        for (ISynthesizerListener listener : mSynthesizerListeners) {
            listener.onInitFail(type, errorMSG);
        }
    }

    public void onPlayBegin() {
        Log.d(TAG, "onPlayBegin");
        for (ISynthesizerListener listener : mSynthesizerListeners) {
            listener.onPlayBegin(mSpeechSynthesisContent);
        }
    }

    public void onPlayEnd() {
        Log.d(TAG, "onPlayEnd");
        for (ISynthesizerListener listener : mSynthesizerListeners) {
            listener.onPlayEnd(mSpeechSynthesisContent);
        }

        //处理待合成语音
        mHandlerUnsynthesized.sendEmptyMessageDelayed(MSG_SYNTHESIZED_TTS, SYNTHESIZED_TTS_INTERVAL);
    }

    @Override
    public void initModelFileConfig(Context context) {
        File modelStorageDirPath = new File(DIR_PATH_MODEL);
        if (modelStorageDirPath.exists())
            return;
        modelStorageDirPath.mkdirs();

        AssetsCopy.copy(context, FILE_NAME_MODEL_FRONTEND, DIR_PATH_MODEL);
        AssetsCopy.copy(context, FILE_NAME_MODEL_BACKEND, DIR_PATH_MODEL);
    }
}
