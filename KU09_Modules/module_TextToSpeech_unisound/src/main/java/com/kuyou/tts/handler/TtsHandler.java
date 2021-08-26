package com.kuyou.tts.handler;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import androidx.annotation.NonNull;

import com.kuyou.tts.basic.TtsManager;

import java.util.LinkedList;
import java.util.Queue;

import kuyou.common.BuildConfig;
import kuyou.common.ipc.RemoteEvent;
import kuyou.common.ku09.IPowerStatusListener;
import kuyou.common.ku09.event.common.EventPowerChange;
import kuyou.common.ku09.event.tts.EventTTSModuleLiveExit;
import kuyou.common.ku09.event.tts.EventTextToSpeech;
import kuyou.common.ku09.event.tts.EventTextToSpeechPlayRequest;
import kuyou.common.ku09.handler.BasicEventHandler;

/**
 * action :
 * <p>
 * remarks:  <br/>
 * author: wuguoxian <br/>
 * date: 21-8-21 <br/>
 * </p>
 */
public class TtsHandler extends BasicEventHandler implements IPowerStatusListener {

    protected final String TAG = "com.kuyou.tts.handler > TtsHandler";

    private final static int MSG_PLAY = 1;
    private final static int MSG_RESET = 2;

    private Context mContext;
    private TtsManager mTTSPlayer;
    private Handler mHandlerPlay;
    private Queue<String> mPendingPlaylist;

    private boolean isInitFinish = false, isPlaying = false;
    private String mPlayText = null, mPlayTextOld = null;

    public TtsHandler(Context context) {
        setContext(context.getApplicationContext());
    }

    public void initTts() {
        if (null != mHandlerPlay)
            return;
        Context context = getContext();
        Log.d(TAG, "initTts");
        mPendingPlaylist = new LinkedList<>();
        mHandlerPlay = new Handler() {
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case MSG_PLAY:
                        mHandlerPlay.removeMessages(MSG_PLAY);
                        if (!isInitFinish
                                || getPowerStatus() == EventPowerChange.POWER_STATUS.SHUTDOWN) {
                            break;
                        }
                        mPlayText = null;
                        synchronized (mPendingPlaylist) {
                            if (mPendingPlaylist.size() > 0)
                                mPlayText = mPendingPlaylist.poll();
                        }
                        if (null == mPlayText) {
                            break;
                        }
                        if (null != mTTSPlayer) {
                            isPlaying = true;
                            mTTSPlayer.play(mPlayText);
                            Log.i(TAG, "handleMessage > MSG_PLAY > text = " + mPlayText);
                        }
                        break;
                    case MSG_RESET:
                        mPlayTextOld = null;
                        Log.d(TAG, "handleMessage > MSG_RESET");
                        break;
                    default:
                        break;
                }
            }
        };
        // 初始化语音合成对象
        mTTSPlayer = TtsManager.getInstance(context, new TtsManager.ISynthesizerListener() {
            @Override
            public void onInitFinish() {
                isInitFinish = true;
                if (mPendingPlaylist.size() > 0 && !mHandlerPlay.hasMessages(MSG_PLAY)) {
                    mHandlerPlay.sendEmptyMessage(MSG_PLAY);
                }
            }

            @Override
            public void onInitFail(int type, String errorMSG) {
                isInitFinish = true;
                Log.d(TAG, " onInitFail >  errorMSG=" + errorMSG);
            }

            @Override
            public void onPlayBegin(String speechSynthesisContent) {
            }

            @Override
            public void onPlayEnd(String speechSynthesisContent) {
                if (EventPowerChange.POWER_STATUS.SHUTDOWN == getPowerStatus()) {
                    dispatchEvent(new EventTTSModuleLiveExit()
                            .setExitType(EventTTSModuleLiveExit.ExitType.SHUTDOWN)
                            .setRemote(false));
                    return;
                }
                isPlaying = false;
                mHandlerPlay.sendEmptyMessage(MSG_PLAY);
                mHandlerPlay.sendEmptyMessageDelayed(MSG_RESET, 2000);
            }
        });
    }

    public boolean isReady() {
        return null != mTTSPlayer;
    }

    /**
     * action: TTS语音合成
     * TTS模块重载
     */
    public void onRequestTtsPlay(String text) {
        if (null == text || text.length() <= 0) {
            Log.w(TAG, "onRequestTtsPlay > text is null ");
            return;
        }
        if (null == mPendingPlaylist) {
            Log.w(TAG, "onRequestTtsPlay > tts module is init fail > auto restart tts");
            dispatchEvent(new EventTTSModuleLiveExit()
                    .setExitType(EventTTSModuleLiveExit.ExitType.SHUTDOWN)
                    .setRemote(false));
            return;
        }
        if (null != mPlayTextOld && mPlayTextOld.equals(text)) {
            Log.w(TAG, " onRequestTtsPlay > 重复 tts 已取消 > text=" + text);
            return;
        }
        if (getPowerStatus() == EventPowerChange.POWER_STATUS.SHUTDOWN) {
            Log.w(TAG, "onRequestTtsPlay > system is ready shut down > cancel play text = " + text);
            return;
        }
        if (!mPendingPlaylist.offer(text)) {
            Log.e(TAG, " onRequestTtsPlay > mPendingPlaylist add item fail ======================== ");
        } else {
            mPlayTextOld = text;
        }
        if (BuildConfig.DEBUG) {
            Log.d(TAG, new StringBuilder("onRequestTtsPlay >")
                    .append(" \n handle text= ").append(text)
                    .append(" \n mPendingPlaylist.size()= ").append(mPendingPlaylist.size())
                    .append(" \n isInitFinish = ").append(isInitFinish)
                    .append(" \n isPlaying = ").append(isPlaying)
                    .toString());
        }
        if (isInitFinish
                && !isPlaying
                && !mHandlerPlay.hasMessages(MSG_PLAY))
            mHandlerPlay.sendEmptyMessage(MSG_PLAY);
    }

    private int mPowerStatus = EventPowerChange.POWER_STATUS.BOOT_READY;

    protected int getPowerStatus() {
        return mPowerStatus;
    }

    @Override
    public void onPowerStatus(int status) {
        if (EventPowerChange.POWER_STATUS.SHUTDOWN == status) {
            synchronized (mPendingPlaylist) {
                mHandlerPlay.removeMessages(MSG_PLAY);
                mPendingPlaylist.clear();
                mHandlerPlay.removeMessages(MSG_RESET);
            }
            mTTSPlayer.play("关机");
        }
        mPowerStatus = status;
    }

    @Override
    protected void initHandleEventCodeList() {
        registerHandleEvent(EventTextToSpeech.Code.MODULE_INIT_REQUEST, false);
        registerHandleEvent(EventTextToSpeech.Code.TEXT_PLAY, true);
    }

    @Override
    public boolean onModuleEvent(RemoteEvent event) {
        switch (event.getCode()) {
            case EventTextToSpeech.Code.TEXT_PLAY:
                onRequestTtsPlay(EventTextToSpeechPlayRequest.getPlayContent(event));
                break;
            case EventTextToSpeech.Code.MODULE_INIT_REQUEST:
                initTts();
                break;
            default:
                return false;
        }
        return true;
    }
}
