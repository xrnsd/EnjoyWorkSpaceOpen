package com.kuyou.jt808;

import android.app.IHelmetModule808Callback;
import android.app.IHelmetModuleLocationCallback;
import android.content.Context;
import android.location.Location;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Looper;
import android.os.RemoteException;
import android.util.Log;

import com.kuyou.jt808.alarm.ALARM;
import com.kuyou.jt808.business.HelmetSocketManager;
import com.kuyou.jt808.info.AuthenticationInfo;
import com.kuyou.jt808.info.LocationInfo;
import com.kuyou.jt808.location.AmapLocationProvider;
import com.kuyou.jt808.location.HMLocationProvider;
import com.kuyou.jt808.location.LocationReportHandler;

import kuyou.common.ipc.RemoteEvent;
import kuyou.common.ku09.BaseApplication;
import kuyou.common.ku09.event.common.EventPowerChange;
import kuyou.common.ku09.event.jt808.EventAudioVideoParametersApplyRequest;
import kuyou.common.ku09.event.jt808.EventAudioVideoParametersApplyResult;
import kuyou.common.ku09.event.jt808.EventAuthenticationRequest;
import kuyou.common.ku09.event.jt808.EventAuthenticationResult;
import kuyou.common.ku09.event.jt808.EventConnectResult;
import kuyou.common.ku09.event.jt808.EventLocationStartReportRequest;
import kuyou.common.ku09.event.jt808.EventPhotoUploadRequest;
import kuyou.common.ku09.event.jt808.EventPhotoUploadResult;
import kuyou.common.ku09.event.jt808.alarm.EventAlarm;
import kuyou.common.ku09.event.jt808.alarm.EventAlarmGas;
import kuyou.common.ku09.event.jt808.alarm.EventAlarmNearPower;
import kuyou.common.ku09.event.jt808.alarm.EventAlarmSos;
import kuyou.common.ku09.event.jt808.base.EventResult;
import kuyou.common.ku09.event.jt808.base.ModuleEventJt808;
import kuyou.common.ku09.event.openlive.EventPhotoTakeResult;
import kuyou.common.ku09.event.openlive.base.ModuleEventOpenLive;
import kuyou.common.ku09.key.KeyConfig;
import kuyou.common.utils.NetworkUtils;
import kuyou.common.utils.SystemPropertiesUtils;
import kuyou.sdk.jt808.base.Jt808Codec;
import kuyou.sdk.jt808.base.Jt808Config;

/**
 * action :808??????????????????????????????
 * <p>
 * author: wuguoxian <br/>
 * date: 20-10-24 <br/>
 * <p>
 */
public class ModuleApplication extends BaseApplication {

    private static ModuleApplication sApplication;

    public static ModuleApplication getInstance() {
        return sApplication;
    }

    public static boolean IS_ENABLE_FAKE_LOCATION = true;

    protected boolean isNetworkAvailable = false;
    protected HelmetSocketManager mHelmetSocketManager;
    protected DeviceConfig mConfig;

    private OnlinePlatformMessageHandler mOnlinePlatformMessageHandler;
    private HMLocationProvider mLocationProvider;
    private LocationReportHandler mLocationReportHandler;

    private boolean isEnableNearPowerAlarm = true;

    @Override
    protected String getApplicationName() {
        return "808";
    }

    @Override
    protected void init() {
        super.init();
        sApplication = ModuleApplication.this;
        initLocation();
        autoOpenWifi();
        connect();
    }

    @Override
    protected void initCallBack() {
        super.initCallBack();
        mHelmetModuleManageServiceManager.registerHelmetModule808Callback(new IHelmetModule808Callback.Stub() {
            @Override
            public int getAudioVideoParameterApplyStatus() throws RemoteException {
                return ModuleApplication.this.mOnlinePlatformMessageHandler.getRequestAudioVideoParametersStatus();
            }
        });
//        mHelmetModuleManageServiceManager.registerHelmetModuleLocationCallback(new IHelmetModuleLocationCallback.Stub() {
//            @Override
//            public void onLocationChange(Location location) throws RemoteException {
//                Log.d(TAG, "onLocationChange > location+"+location);
//                ModuleApplication.this.getLocationProvider().dispatchEventLocationChange(location);
//            }
//        });
    }

    private void initLocation() {
        if (null != mLocationProvider) {
            return;
        }
        mLocationProvider = AmapLocationProvider.getInstance(getApplicationContext());
        mLocationReportHandler = LocationReportHandler
                .getInstance(getHandlerKeepAliveClient().getLooper(), new LocationReportHandler.IOnLocationReportCallBack() {
                    @Override
                    public void onLocationReport(Location location) {
                        sendToOnlinePlatform(
                                ModuleApplication.this.getLocationInfo().setLocation(location).reportLocation());
                    }
                });
        mLocationReportHandler.setReportLocationFreq(getConfig().getHeartbeatInterval());
        mLocationProvider.setLocationChangeListener(mLocationReportHandler);
    }

    protected void initHelmetSocketManager() {
        if (null != mHelmetSocketManager) {
            return;
        }
        mHelmetSocketManager = HelmetSocketManager.getInstance(getConfig());
        mHelmetSocketManager.init();
    }

    @Override
    protected boolean isReady() {
        isNetworkAvailable = NetworkUtils.isNetworkAvailable(getApplicationContext());
        if (!isNetworkAvailable) {
            Log.w(TAG, "isReady > ?????????,??????????????????????????????????????????????????? ");
            return true;
        }
        if (null == mHelmetSocketManager || !mHelmetSocketManager.isConnect()) {
            Log.w(TAG, "isReady > ???????????????,?????????????????? ");
            connect();
            return true;
        }

        //????????????????????????
        if (!getLocationProvider().isValidLocation()) {
            if (!IS_ENABLE_FAKE_LOCATION) {
                Log.w(TAG, "isReady > ???????????????,???????????? ");
                return false;
            }
        }

        //????????????socketManager??????????????????
        return is808Connected();
    }

    @Override
    public int getAudioVideoParameterApplyStatus() {
        if (null != mHelmetModuleManageServiceManager)
            mHelmetModuleManageServiceManager.getAudioVideoParameterApplyStatus();
        return -1;
    }

    protected Jt808Codec getCodec() {
        if (null == mOnlinePlatformMessageHandler) {
            mOnlinePlatformMessageHandler = new OnlinePlatformMessageHandler(ModuleApplication.this, getConfig());
        }
        return mOnlinePlatformMessageHandler;
    }

    protected boolean is808Connected() {
        if (null != mHelmetSocketManager)
            return mHelmetSocketManager.isConnect();
        return false;
    }

    public HMLocationProvider getLocationProvider() {
        initLocation();
        return mLocationProvider;
    }

    private LocationInfo getLocationInfo() {
        return getLocationProvider().getLocationInfo(getConfig());
    }

    public Jt808Config getConfig() {
        if (null == mConfig) {
            mConfig = new DeviceConfig();
        }
        return mConfig;
    }

    public HelmetSocketManager getHelmetSocketManager() {
        initHelmetSocketManager();
        return mHelmetSocketManager;
    }

    public void connect() {
        initHelmetSocketManager();
        synchronized (mHelmetSocketManager) {
            if (!NetworkUtils.isNetworkAvailable(getApplicationContext())) {
                Log.e(TAG, "connect > process fail : network is unavailable");
                return;
            }
            if (mHelmetSocketManager.isConnect()) {
                Log.e(TAG, "connect > process fail : HelmetSocketManager is connected");
                return;
            }
            String serverUrl = getConfig().getRemoteServerAddress();
            int serverPort = getConfig().getRemoteServerPort();
            Log.d(TAG, new StringBuilder("connect > ")
                    .append("\nserverUrl = ").append(serverUrl)
                    .append("\nserverPort = ").append(serverPort).toString());
            try {
                mHelmetSocketManager.disconnect();
            } catch (Exception e) {
                Log.e(TAG, Log.getStackTraceString(e));
            }
            try {
                mHelmetSocketManager.connect(serverUrl, serverPort, getCodec());
            } catch (Exception e) {
                Log.e(TAG, Log.getStackTraceString(e));
            }
        }
    }

    public void sendToOnlinePlatform(byte[] msg) {
        getHelmetSocketManager().send(msg);
    }

    private void autoOpenWifi() {
        final String key = "persist.aw1.ssid", val_none = "aw1_none";
        if (SystemPropertiesUtils.get(key, val_none).equals(val_none)) {
            Log.w(TAG, "autoOpenWifi > ssid config is null ");
        } else {
            if (NetworkUtils.isNetworkAvailable(getApplicationContext())) {
                Log.e(TAG, "autoOpenWifi > process fail : network is available");
                return;
            }
            WifiManager wifiMgr = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            wifiMgr.setWifiEnabled(true);
        }
    }

    @Override
    protected void onKeyClick(int keyCode) {
        super.onKeyClick(keyCode);
        switch (keyCode) {
            case KeyConfig.ALARM_NEAR_POWER:
                if (!isEnableNearPowerAlarm) {
                    Log.w(TAG, "onKeyClick > ???????????????????????????");
                    return;
                }
                dispatchEvent(new EventAlarmNearPower()
                        .setRemote(false));
                break;
            case KeyConfig.ALARM_GAS:
                dispatchEvent(new EventAlarmGas()
                        .setRemote(false));
                break;
            case KeyConfig.ALARM_GAS_OFF:
                dispatchEvent(new EventAlarmGas().setSwitch(false)
                        .setRemote(false));
                break;
            default:
                break;
        }
    }

    @Override
    protected void onKeyLongClick(int keyCode) {
        super.onKeyLongClick(keyCode);
        switch (keyCode) {
            case KeyConfig.CALL:
                dispatchEvent(new EventAlarmSos()
                        .setRemote(false));
                break;
            default:
                break;
        }
    }

    @Override
    protected void onPowerStatus(int status) {
        super.onPowerStatus(status);
        isEnableNearPowerAlarm = EventPowerChange.POWER_STATUS.CHARGE != status
                && EventPowerChange.POWER_STATUS.SHUTDOWN != status;
        Log.d(TAG, "onPowerStatus > isEnableNearPowerAlarm = "+isEnableNearPowerAlarm);
    }

    @Override
    public void onModuleEvent(RemoteEvent event) {
        super.onModuleEvent(event);
        switch (event.getCode()) {
            case ModuleEventJt808.Code.CONNECT_RESULT:
                if (EventConnectResult.isResultSuccess(event)) {
                    Log.i(TAG, "onModuleEvent > ?????????????????????");
                    dispatchEvent(new EventAuthenticationRequest()
                            .setRemote(false));
                    return;
                }
                if (EventConnectResult.getResultCode(event) == EventResult.ResultCode.DIS) {
                    Log.w(TAG, "onModuleEvent > ?????????????????????");
                    return;
                }
                Log.w(TAG, "onModuleEvent > ?????????????????????");
                break;

            case ModuleEventJt808.Code.AUTHENTICATION_REQUEST:
                Log.d(TAG, "onModuleEvent > ???????????? ");
                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        AuthenticationInfo.getInstance().setConfig(getConfig());
                        sendToOnlinePlatform(AuthenticationInfo.getInstance().getAuthenticationMsgBytes());
                    }
                }, 2000);
                break;

            case ModuleEventJt808.Code.AUTHENTICATION_RESULT:
                if (EventAuthenticationResult.isResultSuccess(event)) {
                    Log.d(TAG, "onModuleEvent > ???????????? ");
                    dispatchEvent(new EventLocationStartReportRequest()
                            .setRemote(false));
                    return;
                }
                Log.w(TAG, "onModuleEvent > ???????????? ");
                break;

            case ModuleEventJt808.Code.LOCATION_START_REPORT_REQUEST:
                Log.d(TAG, "onModuleEvent > ?????????????????? ");
                mLocationReportHandler.start();
                break;

            case ModuleEventJt808.Code.PHOTO_UPLOAD_REQUEST:
                Log.d(TAG, "onModuleEvent > ??????????????????");
                mOnlinePlatformMessageHandler.uploadImg(EventPhotoUploadRequest.getImgFilePath(event));
                break;

            case ModuleEventJt808.Code.PHOTO_UPLOAD_RESULT:
                if (EventPhotoUploadResult.isResultSuccess(event)) {
                    Log.d(TAG, "onModuleEvent > ??????????????????");
                    sendToOnlinePlatform(EventPhotoUploadResult.getMsg(event));
                } else {
                    Log.w(TAG, "onModuleEvent > ??????????????????");
                }
                break;

            case ModuleEventJt808.Code.AUDIO_AND_VIDEO_PARAMETERS_APPLY_REQUEST:
                boolean isSwitch = EventAudioVideoParametersApplyRequest.isSwitch(event);
                //??????????????????????????????????????????????????????
                if (isSwitch && !getHelmetSocketManager().isConnect()) {
                    Log.w(TAG, "onModuleEvent > ??????????????????????????? > ???????????????");
                    dispatchEvent(new EventAudioVideoParametersApplyResult()
                            .setResult(false)
                            .setRemote(true));
                    play("????????????????????????????????????");
                    return;
                }
                Log.i(TAG, "onModuleEvent > ???????????????????????????");
                int mediaTypeCode = EventAudioVideoParametersApplyRequest.getMediaType(event);
                //???????????????????????????????????????????????????,?????????????????????????????????
                if (isSwitch) {
                    mOnlinePlatformMessageHandler.addRequestAudioVideoParametersFlag(mediaTypeCode, getHandlerKeepAliveClient(), 30000);
                } else {
                    mOnlinePlatformMessageHandler.resetRequestAudioVideoParametersFlag();
                }
                //?????????????????????
                byte[] msg = mOnlinePlatformMessageHandler
                        .getAudioVideoInfo()
                        .getApplyAudioVideoParametersMsgByMediaTypeCode(mediaTypeCode, isSwitch);
                sendToOnlinePlatform(msg);
                break;

            // =================    ????????????    ==========================

            case EventAlarm.Code.ALARM_NEAR_POWER:
                play("?????????????????????????????????");
                getLocationInfo().setAlarmFlag(ALARM.FLAG_NEAR_POWER);
                break;

            case EventAlarm.Code.ALARM_CAP_OFF:
                getLocationInfo().setAlarmFlag(ALARM.FLAG_CAP_OFF);
                break;

            case EventAlarm.Code.ALARM_FALL:
                getLocationInfo().setAlarmFlag(ALARM.FLAG_FALL);
                break;

            case EventAlarm.Code.ALARM_GAS:
                getLocationInfo().setAlarmFlag(ALARM.FLAG_GAS);
                break;

            case EventAlarm.Code.ALARM_SOS:
                LocationInfo info = getLocationInfo();

                if (info.isAutoAddSosFlag()) {
                    info.setAutoAddSosFlag(false, ALARM.FLAG_SOS);
                    play("?????????SOS");
                    break;
                }
                info.setAutoAddSosFlag(true, ALARM.FLAG_SOS);
                play("?????????SOS");
                break;

            default:
                break;
        }
    }
}
