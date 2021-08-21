package com.kuyou.vc.protocol;

import android.content.Context;
import android.util.Log;

import com.kuyou.vc.protocol.basic.VoiceControl;
import com.unisound.client.SpeechConstants;
import com.unisound.client.SpeechUnderstander;
import com.unisound.client.SpeechUnderstanderListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import kuyou.common.file.FileUtils;

/**
 * action :语音控制[软件实现]
 * <p>
 * author: wuguoxian <br/>
 * date: 20-11-3 <br/>
 * <p>
 */
public class VoiceControlSoft extends VoiceControl {

    private final String TAG = "com.kuyou.vc.protocol.base > VoiceControlSoft";

    private final String TAG_WAKEUP = "wakeup";
    private final String TARGET_DEST_DIR = "/YunZhiSheng/asrfix/";
    private final String FILE_NAME_CLG = "snapshot.dat";
    private final String FILE_NAME_CLG_GRAMMAR = "snapshot";
    private final String FILE_NAME_CLG_SLOT = "Name"; //*.jsgf中slot的名称
    private String mFilePathJsgfdat;

    private boolean mWakeUpModelLoaded = false;
    private boolean mVocabInserted = false;

    private StringBuffer mStringBufferCommand;
    private SpeechUnderstander mMixSpeechUnderstander;

    @Override
    public int getType() {
        return TYPE.SOFT;
    }

    @Override
    public void init(Context context) {
        super.init(context);
        if (null != mStringBufferCommand)
            return;

        Log.d(TAG, "init: ");
        mStringBufferCommand = new StringBuffer();
        mFilePathJsgfdat = context.getFilesDir() + TARGET_DEST_DIR;
        FileUtils.copyFile(context, FILE_NAME_CLG, mFilePathJsgfdat);
        mFilePathJsgfdat += FILE_NAME_CLG;
        mMixSpeechUnderstander = new SpeechUnderstander(context,
                Config.appKey, Config.secret);
        mMixSpeechUnderstander.setOption(SpeechConstants.ASR_SERVICE_MODE,
                SpeechConstants.ASR_SERVICE_MODE_LOCAL);
        mMixSpeechUnderstander.setOption(SpeechConstants.ASR_OPT_PRINT_LOG, true);
        mMixSpeechUnderstander.setOption(SpeechConstants.ASR_DOMAIN, "general");
        //设置获得在线结果超时时间
        mMixSpeechUnderstander.setOption(SpeechConstants.ASR_NET_TIMEOUT, 10000);
        mMixSpeechUnderstander.setListener(new SpeechUnderstanderListener() {
            public void asrEnd() {
                mMixSpeechUnderstander.cancel();
                start();
            }

            @Override
            public void onResult(int type, String jsonResult) {
                switch (type) {
                    case SpeechConstants.ASR_RESULT_LOCAL:
                        String cmd = parseLocalAsrResult(jsonResult);
                        if (cmd.equals("none")) {
                            Log.d(TAG, "onResult > 识别出无效指令");
                            break;
                        }
                        stop();
                        disPatchVoiceCommand(mStringBufferCommand.toString());
                        break;
                    case SpeechConstants.ASR_RESULT_NET:
//                    if(jsonResult != null){
//                        //processAsrResult(jsonResult);
//                    }
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onEvent(int type, int timeMs) {
                switch (type) {
                    case SpeechConstants.ASR_EVENT_INIT_DONE:
                        List<String> wakeupList = new ArrayList<>();
                        wakeupList.add("你好,小安");

                        for (String val : wakeupList) {
                            Log.d(TAG, "设置唤醒关键词: " + val);
                        }
                        mMixSpeechUnderstander.setWakeupWord(wakeupList);
                        break;
                    case SpeechConstants.ASR_EVENT_LOADGRAMMAR_DONE:
                        start();
                        break;
                    case SpeechConstants.ASR_EVENT_SPEECH_DETECTED:
                        Log.d(TAG, "说话中");
                        break;
                    case SpeechConstants.ASR_EVENT_VAD_TIMEOUT:
                        Log.d(TAG, "说话停止");
                        stop();
                        break;
                    case SpeechConstants.ASR_EVENT_SPEECH_END:
                        Log.d(TAG, "正在识别");
                        break;
                    case SpeechConstants.ASR_EVENT_LOCAL_END:
                        if (mStringBufferCommand != null && mStringBufferCommand.length() > 0) {
                            String result = mStringBufferCommand.toString();
                            mStringBufferCommand.delete(0, mStringBufferCommand.length());
                        }
                        Log.d(TAG, "======================  识别结束 ==============================");
                        break;
                    case SpeechConstants.ASR_EVENT_NET_END:
                        break;
                    case SpeechConstants.ASR_EVENT_RECOGNITION_END:
                        asrEnd();
                        break;
                    case SpeechConstants.WAKEUP_EVENT_RECOGNITION_SUCCESS:
                        Log.d(TAG, "唤醒成功");
                        play("在呢");
                        mMixSpeechUnderstander.cancel();

                        if (!mVocabInserted || !mWakeUpModelLoaded) {
                            Log.e(TAG, "recognize > 混合识别开启失败:mVocabInserted=" + mVocabInserted);
                            Log.e(TAG, "recognize > 混合识别开启失败:mWakeUpModelLoaded=" + mWakeUpModelLoaded);
                            return;
                        }
                        Log.d(TAG, "recognize > 正在开启混合识别 , 请说出指令");
                        mMixSpeechUnderstander.start(FILE_NAME_CLG_GRAMMAR);//开启混合识别
                        break;
                    case SpeechConstants.WAKEUP_EVENT_SET_WAKEUPWORD_DONE:
                        mMixSpeechUnderstander.loadCompiledJsgf(FILE_NAME_CLG_GRAMMAR, mFilePathJsgfdat);

                        StringBuffer sb = new StringBuffer();
                        sb.append(FILE_NAME_CLG_GRAMMAR).append("#").append(FILE_NAME_CLG_SLOT);
                        mVocabInserted = true;
                        break;
                    case SpeechConstants.ASR_EVENT_VOLUMECHANGE:
                        //mVolume.setProgress((Integer)mMixSpeechUnderstander.getOption(SpeechConstants.GENERAL_UPDATE_VOLUME));
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onError(int type, String errorMSG) {
                Log.e(TAG, "onError " + type + " " + errorMSG);
            }
        });
        //设置识别窗口,前开始,后结束
        mMixSpeechUnderstander.setOption(SpeechConstants.ASR_VAD_TIMEOUT_FRONTSIL, 1000);
        mMixSpeechUnderstander.setOption(SpeechConstants.ASR_VAD_TIMEOUT_BACKSIL, 2000);
        //识别引擎初始化
        mMixSpeechUnderstander.init("");
    }

    @Override
    protected List<String> getCommandList() {
        List<String> cmdList = new ArrayList<>();
        cmdList.clear();
        cmdList.add("音量调大");
        cmdList.add("音量调小");
        cmdList.add("拍一张照");
        return cmdList;
    }

    @Override
    public void onWakeup() {

    }

    @Override
    public void start() {
        if (mVocabInserted && mWakeUpModelLoaded) {
            Log.d(TAG, "开始语音唤醒监听");
            mMixSpeechUnderstander.start(TAG_WAKEUP);
        }
    }

    @Override
    public void stop() {
        Log.d(TAG, "stopRecord : 停止录音");
        mMixSpeechUnderstander.stop();
    }

    @Override
    public void onSleep() {

    }

    private String parseLocalAsrResult(String jsonResult) {
        try {
            JSONObject json = new JSONObject(jsonResult);
            JSONArray jsonArray = json.getJSONArray("local_asr");
            JSONObject jsonObject = jsonArray.getJSONObject(0);
            String status = jsonObject.getString("result_type");
            if (status.equals("full")) {
                String result = (String) jsonObject.get("recognition_result");
                mStringBufferCommand.delete(0, mStringBufferCommand.length());
                mStringBufferCommand.append(result);
                String cmd = mStringBufferCommand.toString();
                Log.d(TAG, "parseLocalAsrResult > 识别结果为: " + cmd);
                return cmd;
            }
        } catch (JSONException e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }
        return "none";
    }
}
