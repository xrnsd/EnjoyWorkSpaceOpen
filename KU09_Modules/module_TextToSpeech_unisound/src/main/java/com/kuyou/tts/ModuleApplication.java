package com.kuyou.tts;

import android.app.IHelmetModuleTTSCallback;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;

import androidx.annotation.NonNull;

import com.kuyou.tts.base.TtsManager;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import kuyou.common.BuildConfig;
import kuyou.common.ipc.RemoteEvent;
import kuyou.common.ku09.BaseApplication;
import kuyou.common.ku09.event.common.EventPowerChange;
import kuyou.common.ku09.event.tts.EventTtsPlayRequest;
import kuyou.common.ku09.event.tts.ModuleEventTts;

/**
 * action :语音合成相关实现
 * <p>
 * author: wuguoxian <br/>
 * date: 20-11-2 <br/>
 * <p>
 */
public class ModuleApplication extends BaseApplication {

    private final static int MSG_PLAY = 1;
    private final static int MSG_RESET = 2;

    private static ModuleApplication sMain;

    private static TtsManager mTTSPlayer;
    private static boolean isInitFinish = false, isPlaying = false;
    private static Handler mHandlerPlay;
    private static Queue<String> mPendingPlaylist;

    private String mPlayText = null, mPlayTextOld = null;

    @Override
    protected void init() {
        super.init();
        sMain = ModuleApplication.this;
    }

    @Override
    protected void initCallBack() {
        super.initCallBack();
        mHelmetModuleManageServiceManager.registerHelmetModuleTTSCallback(new IHelmetModuleTTSCallback.Stub() {
            @Override
            public void onRequestTtsPlay(String text) throws RemoteException {
                ModuleApplication.this.onRequestTtsPlay(text);
            }
        });
    }

    @Override
    protected List<Integer> getEventDispatchList() {
        List<Integer> list = new ArrayList<>();

        list.add(ModuleEventTts.Code.TEXT_PLAY);

        return list;
    }

    @Override
    protected String getApplicationName() {
        return "TextToSpeech_unisound";
    }

    @Override
    protected long getFeedTimeLong() {
        return 60 * 1000;
    }

    @Override
    protected String isReady() {
        String statusSuper = super.isReady();
        StringBuilder status = new StringBuilder();
        if (null != statusSuper) {
            status.append(statusSuper);
        }
        if(null == mTTSPlayer){
            status.append(",TTS初始化异常");
        }
        return status.toString();
    }

    public static ModuleApplication getInstance() {
        return sMain;
    }

    public void initTts() {
        initTts(getApplicationContext());
    }

    private void initTts(Context context) {
        if (null != mHandlerPlay)
            return;
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
                            Log.d(TAG, "  MSG_PLAY > mPlayText=" + mPlayText);
                        }
                        break;
                    case MSG_RESET:
                        mPlayTextOld = null;
                        Log.d(TAG, "  MSG_RESET > mPlayTextOld content has been reset");
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
                isPlaying = false;
                mHandlerPlay.sendEmptyMessage(MSG_PLAY);
                mHandlerPlay.sendEmptyMessageDelayed(MSG_RESET, 2000);
            }
        });
    }

    /**
     * action: TTS语音合成
     * TTS模块重载
     */
    protected void onRequestTtsPlay(String text) {
        if (null == text || text.length() <= 0) {
            Log.d(TAG, "onRequestTtsPlay > text is null ");
            return;
        }
        if (null == mPendingPlaylist) {
            Log.d(TAG, "onRequestTtsPlay > tts module is init fail > auto restart tts");
            getHelmetModuleManageServiceManager().feedWatchDog(getPackageName(), System.currentTimeMillis() + 60 * 1000);
            android.os.Process.killProcess(android.os.Process.myPid());
            return;
        }
        if (null != mPlayTextOld && mPlayTextOld.equals(text)) {
            Log.d(TAG, " onRequestTtsPlay > 重复 tts 已取消 > text=" + text);
            return;
        }
        //@{ added by wgx Usefulness: 
        if (getPowerStatus() == EventPowerChange.POWER_STATUS.SHUTDOWN) {
            mHandlerPlay.removeMessages(MSG_PLAY);
            synchronized (mPendingPlaylist) {
                mPendingPlaylist.clear();
            }
            Log.w(TAG, "onRequestTtsPlay > system is shutting down > cancel play text = " + text);
            return;
        }
        //}@ end wgx
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
        } else {
            Log.i(TAG, new StringBuilder()
                    .append("handle text= ").append(text)
                    .toString());
        }
        if (isInitFinish
                && !isPlaying
                && !mHandlerPlay.hasMessages(MSG_PLAY))
            mHandlerPlay.sendEmptyMessage(MSG_PLAY);
    }

    @Override
    public void play(String text) {
        Log.d(TAG, "play > text=" + text);
        onRequestTtsPlay(text);
    }

    @Override
    public void onPowerStatus(int status) {
        if (status == EventPowerChange.POWER_STATUS.SHUTDOWN) {
            onRequestTtsPlay("关机");
        }
        super.onPowerStatus(status);
    }

    @Override
    public void onModuleEvent(RemoteEvent event) {
        super.onModuleEvent(event);
        switch (event.getCode()) {
            case ModuleEventTts.Code.TEXT_PLAY:
                onRequestTtsPlay(EventTtsPlayRequest.getPlayContent(event));
                break;
            default:
                break;
        }
    }
}
