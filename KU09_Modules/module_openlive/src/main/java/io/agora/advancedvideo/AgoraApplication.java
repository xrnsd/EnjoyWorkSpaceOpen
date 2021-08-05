package io.agora.advancedvideo;

import android.app.Activity;
import android.app.Application;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.TextureView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import kuyou.common.file.FileUtils;
import kuyou.common.ku09.BaseApplication;
import com.kuyou.openlive.rtc.Constants;
import com.kuyou.openlive.R;
import com.kuyou.openlive.rtc.AgoraEventHandler;
import com.kuyou.openlive.rtc.EngineConfig;
import com.kuyou.openlive.rtc.EventHandler;
import com.kuyou.openlive.stats.StatsManager;
import com.kuyou.openlive.utils.PrefManager;

import io.agora.rtc.RtcEngine;


public abstract class AgoraApplication extends BaseApplication implements Application.ActivityLifecycleCallbacks {
    protected RtcEngine mRtcEngine;
    protected EngineConfig mGlobalConfig = new EngineConfig();
    protected AgoraEventHandler mHandler = new AgoraEventHandler();
    protected StatsManager mStatsManager = new StatsManager();

    private TextureView mLocalPreview;

    @Override
    protected void init() {
        super.init();
        initConfigListener();
        registerActivityLifecycleCallbacks(this);
        try {
            mRtcEngine = RtcEngine.create(getApplicationContext(), getString(R.string.private_app_id), mHandler);
            // Sets the channel profile of the Agora RtcEngine.
            // The Agora RtcEngine differentiates channel profiles and applies different optimization algorithms accordingly. For example, it prioritizes smoothness and low latency for a video call, and prioritizes video quality for a video broadcast.
            mRtcEngine.setChannelProfile(io.agora.rtc.Constants.CHANNEL_PROFILE_LIVE_BROADCASTING);
            mRtcEngine.enableVideo();
            //mRtcEngine.setLogFile(FileUtils.initializeLogFile(this));
        } catch (Exception e) {
            Log.e(TAG, Log.getStackTraceString(e));
            e.printStackTrace();
        }

        initConfig();
        initLocalPreview();
    }

    private void initConfigListener(){
        mHandler.setOnAgoraError(new AgoraEventHandler.onAgoraError() {
            @Override
            public void onError(int err) {
                if (io.agora.rtc.IRtcEngineEventHandler.ErrorCode.ERR_INVALID_APP_ID == err) {
                    Log.d(TAG, "onError > App ID 无效");
                }
            }
            @Override
            public void onConnectionStateChanged(int state, int reason) {
                String stateMsg = null;
                switch (state) {
                    case io.agora.rtc.Constants.CONNECTION_STATE_DISCONNECTED:
                        stateMsg = "网络连接断开";
                        break;
                    case io.agora.rtc.Constants.CONNECTION_STATE_CONNECTING:
                        stateMsg = "建立网络连接中";
                        break;
                    case io.agora.rtc.Constants.CONNECTION_STATE_CONNECTED:
                        stateMsg = "网络已连接";
                        break;
                    case io.agora.rtc.Constants.CONNECTION_STATE_RECONNECTING:
                        stateMsg = "重新建立网络连接中";
                        break;
                    case io.agora.rtc.Constants.CONNECTION_STATE_FAILED:
                        stateMsg = "网络连接失败";
                        break;
                    default:
                        Log.d(TAG, "onConnectionStateChanged > state=" + state);
                        break;
                }
                if (null != stateMsg)
                    Log.d(TAG, "onConnectionStateChanged > stateMsg=" + stateMsg);

                String reasonMsg = null;
                switch (reason) {
                    case io.agora.rtc.Constants.CONNECTION_CHANGED_CONNECTING:
                        reasonMsg = "建立网络连接中";
                        break;
                    case io.agora.rtc.Constants.CONNECTION_CHANGED_JOIN_SUCCESS:
                        reasonMsg = "成功加入频道";
                        break;
                    case io.agora.rtc.Constants.CONNECTION_CHANGED_INTERRUPTED:
                        reasonMsg = "网络连接中断";
                        break;
                    case io.agora.rtc.Constants.CONNECTION_CHANGED_BANNED_BY_SERVER:
                        reasonMsg = "网络连接被服务器禁止";
                        break;
                    case io.agora.rtc.Constants.CONNECTION_CHANGED_JOIN_FAILED:
                        reasonMsg = "加入频道失败";
                        break;
                    case io.agora.rtc.Constants.CONNECTION_CHANGED_LEAVE_CHANNEL:
                        reasonMsg = "离开频道";
                        break;
                    case io.agora.rtc.Constants.CONNECTION_CHANGED_INVALID_APP_ID:
                        reasonMsg = "不是有效的 APP ID。请更换有效的 APP ID 重新加入频道";
                        break;
                    case io.agora.rtc.Constants.CONNECTION_CHANGED_INVALID_CHANNEL_NAME:
                        reasonMsg = "不是有效的频道名。请更换有效的频道名重新加入频道";
                        break;
                    case io.agora.rtc.Constants.CONNECTION_CHANGED_INVALID_TOKEN:
                        reasonMsg = "生成的 Token 无效。一般有以下原因：\n" +
                                "在控制台上启用了 App Certificate，但加入频道未使用 Token。当启用了 App Certificate，必须使用 Token\n" +
                                "在调用 joinChannel 加入频道时指定的 uid 与生成 Token 时传入的 uid 不一致";
                        break;
                    case io.agora.rtc.Constants.CONNECTION_CHANGED_TOKEN_EXPIRED:
                        reasonMsg = "当前使用的 Token 过期，不再有效，需要重新在你的服务端申请生成 Token";
                        break;
                    case io.agora.rtc.Constants.CONNECTION_CHANGED_REJECTED_BY_SERVER:
                        reasonMsg = "此用户被服务器禁止";
                        break;
                    case io.agora.rtc.Constants.CONNECTION_CHANGED_SETTING_PROXY_SERVER:
                        reasonMsg = "由于设置了代理服务器，SDK 尝试重连";
                        break;
                    case io.agora.rtc.Constants.CONNECTION_CHANGED_RENEW_TOKEN:
                        reasonMsg = "客户端 IP 地址变更，可能是由于网络类型，或网络运营商的 IP 或端口发生改变引起";
                        break;
                    case io.agora.rtc.Constants.CONNECTION_CHANGED_KEEP_ALIVE_TIMEOUT:
                        reasonMsg = "SDK 和服务器连接保活超时，进入自动重连状态";
                        break;
                    default:
                        Log.d(TAG, "onConnectionStateChanged > reason=" + reason);
                        break;
                }
                if (null != reasonMsg)
                    Log.d(TAG, "onConnectionStateChanged > reasonMsg=" + reasonMsg);
                AgoraApplication.this.onConnectionStateChanged(state,reason);
            }
        });
    }
    
    protected void exitChannel(){
        
    }

    private void initConfig() {
        SharedPreferences pref = PrefManager.getPreferences(getApplicationContext());
        mGlobalConfig.setVideoDimenIndex(pref.getInt(
                Constants.PREF_RESOLUTION_IDX, Constants.DEFAULT_PROFILE_IDX));

        boolean showStats = pref.getBoolean(Constants.PREF_ENABLE_STATS, false);
        mGlobalConfig.setIfShowVideoStats(showStats);
        mStatsManager.enableStats(showStats);

        mGlobalConfig.setMirrorLocalIndex(pref.getInt(Constants.PREF_MIRROR_LOCAL, 0));
        mGlobalConfig.setMirrorRemoteIndex(pref.getInt(Constants.PREF_MIRROR_REMOTE, 0));
        mGlobalConfig.setMirrorEncodeIndex(pref.getInt(Constants.PREF_MIRROR_ENCODE, 0));
    }

    protected void onConnectionStateChanged(int state, int reason){

    }

    private void initLocalPreview() {
        mLocalPreview = new TextureView(this);
    }

    public TextureView localPreview() {
        return mLocalPreview;
    }

    public EngineConfig engineConfig() {
        return mGlobalConfig;
    }

    public RtcEngine rtcEngine() {
        return mRtcEngine;
    }

    public StatsManager statsManager() {
        return mStatsManager;
    }

    public void registerEventHandler(EventHandler handler) {
        mHandler.addHandler(handler);
    }

    public void removeEventHandler(EventHandler handler) {
        mHandler.removeHandler(handler);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        RtcEngine.destroy();
    }

    @Override
    public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {

    }

    @Override
    public void onActivityStarted(@NonNull Activity activity) {

    }

    @Override
    public void onActivityResumed(@NonNull Activity activity) {

    }

    @Override
    public void onActivityPaused(@NonNull Activity activity) {

    }

    @Override
    public void onActivityStopped(@NonNull Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(@NonNull Activity activity) {

    }
}
