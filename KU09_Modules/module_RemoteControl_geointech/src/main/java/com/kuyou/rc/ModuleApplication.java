package com.kuyou.rc;

import android.app.IHelmetModule808Callback;
import android.os.Handler;
import android.os.Looper;
import android.os.RemoteException;
import android.util.Log;

import com.kuyou.rc.business.HelmetSocketManager;
import com.kuyou.rc.business.Jt808Codec;
import com.kuyou.rc.handler.AlarmHandler;
import com.kuyou.rc.handler.KeyHandler;
import com.kuyou.rc.handler.LocationReportHandler;
import com.kuyou.rc.handler.PlatformInteractiveCommandHandler;
import com.kuyou.rc.info.AuthenticationInfo;
import com.kuyou.rc.info.ImageInfo;
import com.kuyou.rc.info.LocationInfo;
import com.kuyou.rc.location.AMapLocationProvider;
import com.kuyou.rc.location.HMLocationProvider;
import com.kuyou.rc.utils.UploadUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import kuyou.common.bytes.ByteUtils;
import kuyou.common.ipc.RemoteEvent;
import kuyou.common.ku09.BaseApplication;
import kuyou.common.ku09.IPowerStatusListener;
import kuyou.common.ku09.event.avc.EventAudioVideoOperateResult;
import kuyou.common.ku09.event.avc.EventPhotoTakeResult;
import kuyou.common.ku09.event.avc.base.EventAudioVideoCommunication;
import kuyou.common.ku09.event.avc.base.IAudioVideo;
import kuyou.common.ku09.event.common.EventNetworkConnect;
import kuyou.common.ku09.event.common.EventNetworkDisconnect;
import kuyou.common.ku09.event.rc.EventAudioVideoParametersApplyRequest;
import kuyou.common.ku09.event.rc.EventAudioVideoParametersApplyResult;
import kuyou.common.ku09.event.rc.EventAuthenticationRequest;
import kuyou.common.ku09.event.rc.EventAuthenticationResult;
import kuyou.common.ku09.event.rc.EventConnectResult;
import kuyou.common.ku09.event.rc.EventLocationReportRequest;
import kuyou.common.ku09.event.rc.EventLocationStartReportRequest;
import kuyou.common.ku09.event.rc.EventPhotoUploadRequest;
import kuyou.common.ku09.event.rc.EventPhotoUploadResult;
import kuyou.common.ku09.event.rc.base.EventRemoteControl;
import kuyou.common.ku09.event.rc.base.EventResult;
import kuyou.common.ku09.key.IKeyEventListener;
import kuyou.common.utils.NetworkUtils;
import kuyou.sdk.jt808.base.RemoteControlDeviceConfig;

/**
 * action :808模块和其他模块通信等
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
    protected RemoteControlDeviceConfig mConfig;

    private PlatformInteractiveCommandHandler mPlatformInteractiveCommandHandler;
    private HMLocationProvider mLocationProvider;
    private LocationReportHandler mLocationReportHandler;

    private AlarmHandler mAlarmHandler;
    private KeyHandler mKeyHandler;

    @Override
    protected String getApplicationName() {
        return "RemoteControl_geointech";
    }

    @Override
    protected List<Integer> getEventDispatchList() {
        List<Integer> list = new ArrayList<>();

        list.add(EventAudioVideoCommunication.Code.PHOTO_TAKE_RESULT);
        list.add(EventRemoteControl.Code.AUDIO_AND_VIDEO_PARAMETERS_APPLY_REQUEST);
        list.add(EventRemoteControl.Code.PHOTO_UPLOAD_REQUEST);

        return list;
    }

    @Override
    protected void init() {
        super.init();
        sApplication = ModuleApplication.this;
        initLocation();
        getAlarmHandler().setLocationProvider(getLocationProvider());
        connect();
    }

    @Override
    protected IPowerStatusListener getPowerStatusListener() {
        return getKeyHandler();
    }

    @Override
    protected IKeyEventListener getKeyListener() {
        return getKeyHandler();
    }

    @Override
    protected void initCallBack() {
        super.initCallBack();
        mHelmetModuleManageServiceManager.registerHelmetModule808Callback(new IHelmetModule808Callback.Stub() {
            @Override
            public int getAudioVideoParameterApplyStatus() throws RemoteException {
                return ModuleApplication.this.mPlatformInteractiveCommandHandler.getRequestAudioVideoParametersStatus();
            }
        });
    }

    private void initLocation() {
        if (null != mLocationProvider) {
            return;
        }

        //位置上报事件发生器，定期发出位置上报请求
        mLocationReportHandler = LocationReportHandler.getInstance(getHandlerKeepAliveClient().getLooper());
        mLocationReportHandler.setReportLocationFreq(getConfig().getHeartbeatInterval());
        mLocationReportHandler.setLocationReportCallBack(new LocationReportHandler.IOnLocationReportCallBack() {
            @Override
            public void onLocationReport() {
                dispatchEvent(new EventLocationReportRequest());
            }
        });

        //位置提供器
        //mLocationProvider = NormalFilterLocationProvider.getInstance(getApplicationContext());
        mLocationProvider = AMapLocationProvider.getInstance(getApplicationContext());
//        mLocationProvider = new HMLocationProvider(getApplicationContext()).enableLocalBasePosition(mHelmetModuleManageServiceManager);
        mLocationProvider.setRemoteControlDeviceConfig(getConfig());
    }

    protected void initHelmetSocketManager() {
        if (null != mHelmetSocketManager) {
            return;
        }
        mHelmetSocketManager = HelmetSocketManager.getInstance(getConfig());
        mHelmetSocketManager.init();
    }

    public KeyHandler getKeyHandler() {
        if (null == mKeyHandler) {
            mKeyHandler = KeyHandler.getInstance();
        }
        return mKeyHandler;
    }

    public AlarmHandler getAlarmHandler() {
        if (null == mAlarmHandler) {
            mAlarmHandler = new AlarmHandler();
        }
        return mAlarmHandler;
    }

    @Override
    protected String isReady() {
        String status = super.isReady();
        if (null != status) {
            return status;
        }
        boolean isNetworkAvailableNow = NetworkUtils.isNetworkAvailable(getApplicationContext());
        if (!isNetworkAvailableNow) {
            Log.w(TAG, "isReady > 未联网,放弃平台链接状态检查和模块自动重置 ");
            return null;
        }
        if (!isNetworkAvailable && isNetworkAvailableNow) {
            dispatchEvent(new EventNetworkConnect());
        } else if (isNetworkAvailable && !isNetworkAvailableNow) {
            dispatchEvent(new EventNetworkDisconnect());
        }
        isNetworkAvailable = isNetworkAvailableNow;
        if (null == mHelmetSocketManager || !mHelmetSocketManager.isConnect()) {
            Log.w(TAG, "isReady > 未连接平台,尝试链接平台 ");
            connect();
            return null;
        }

        //模块没有正常定位
        if (!getLocationProvider().isValidLocation()) {
            if (!IS_ENABLE_FAKE_LOCATION) {
                Log.w(TAG, "isReady >  ");
                return "未正常定位,尝试复位";
            }
        }

        //联网后以socketManager连接状态为准
        if (!is808Connected()) {
            return "平台连接异常";
        }
        return null;
    }

    protected Jt808Codec getCodec() {
        if (null == mPlatformInteractiveCommandHandler) {
            mPlatformInteractiveCommandHandler = new PlatformInteractiveCommandHandler(ModuleApplication.this, getConfig());
        }
        return mPlatformInteractiveCommandHandler;
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
        return getLocationProvider().getLocationInfo();
    }

    public RemoteControlDeviceConfig getConfig() {
        if (null == mConfig) {
            mConfig = new RemoteControlDeviceConfig() {
                @Override
                public String getDevId() {
                    return ModuleApplication.this.getDevicesConfig().getDevId();
                }

                @Override
                public String getUwbId() {
                    return ModuleApplication.this.getDevicesConfig().getUwbId();
                }

                @Override
                public String getCollectingEndId() {
                    return ModuleApplication.this.getDevicesConfig().getCollectingEndId();
                }

                @Override
                public int getHeartbeatInterval() {
                    return ModuleApplication.this.getDevicesConfig().getHeartbeatInterval();
                }

                @Override
                public String getRemoteControlServerAddress() {
                    return ModuleApplication.this.getDevicesConfig().getRemoteControlServerAddress();
                }

                @Override
                public int getRemoteControlServerPort() {
                    return ModuleApplication.this.getDevicesConfig().getRemoteControlServerPort();
                }

                @Override
                public String getAuthenticationCode() {
                    return ModuleApplication.this.getDevicesConfig().getAuthenticationCode();
                }

                @Override
                public String getRemotePhotoServerAddress() {
                    return ModuleApplication.this.getDevicesConfig().getRemotePhotoServerAddress();
                }

                @Override
                public String getDirPathStoragePhoto() {
                    return ModuleApplication.this.getDevicesConfig().getDirPathStoragePhoto();
                }
            };
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
                Log.w(TAG, "connect > process fail : network is unavailable");
                return;
            }
            if (mHelmetSocketManager.isConnect()) {
                Log.e(TAG, "connect > process fail : HelmetSocketManager is connected");
                return;
            }
            String serverUrl = getConfig().getRemoteControlServerAddress();
            int serverPort = getConfig().getRemoteControlServerPort();
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
        Log.d(TAG, "sendToOnlinePlatform >" + ByteUtils.bytes2Hex(msg));
        getHelmetSocketManager().send(msg);
    }

    @Override
    public void onIpcFrameResisterSuccess() {
        super.onIpcFrameResisterSuccess();
        getKeyHandler().setDispatchEventCallBack(ModuleApplication.this);
        getAlarmHandler().setDispatchEventCallBack(ModuleApplication.this);
    }

    @Override
    public void onModuleEvent(RemoteEvent event) {
        super.onModuleEvent(event);
        getAlarmHandler().onModuleEvent(event);

        switch (event.getCode()) {
            case EventRemoteControl.Code.CONNECT_RESULT:
                if (EventConnectResult.isResultSuccess(event)) {
                    Log.i(TAG, "onModuleEvent > 连接服务器成功");
                    dispatchEvent(new EventAuthenticationRequest()
                            .setRemote(false));
                    return;
                }
                if (EventConnectResult.getResultCode(event) == EventResult.ResultCode.DIS) {
                    Log.w(TAG, "onModuleEvent > 服务器连接断开");
                    return;
                }
                Log.w(TAG, "onModuleEvent > 连接服务器失败");
                break;

            case EventRemoteControl.Code.AUTHENTICATION_REQUEST:
                Log.d(TAG, "onModuleEvent > 开始鉴权 ");
                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        AuthenticationInfo.getInstance().setConfig(getConfig());
                        sendToOnlinePlatform(AuthenticationInfo.getInstance().getAuthenticationMsgBytes());
                    }
                }, 2000);
                break;

            case EventRemoteControl.Code.AUTHENTICATION_RESULT:
                if (EventAuthenticationResult.isResultSuccess(event)) {
                    Log.d(TAG, "onModuleEvent > 鉴权成功 ");
                    dispatchEvent(new EventLocationStartReportRequest()
                            .setRemote(false));
                    return;
                }
                Log.w(TAG, "onModuleEvent > 鉴权失败 ");
                break;

            case EventRemoteControl.Code.LOCATION_START_REPORT_REQUEST:
                Log.d(TAG, "onModuleEvent > 开始上报位置 ");
                mLocationReportHandler.start();
                break;

            case EventRemoteControl.Code.LOCATION_REPORT_REQUEST:
                sendToOnlinePlatform(getLocationInfo().getReportLocationMsgBody());
                break;

            case EventAudioVideoCommunication.Code.PHOTO_TAKE_RESULT:
                Log.d(TAG, "onModuleEvent > 拍照状态上传");
                if (!EventPhotoTakeResult.isResultSuccess(event)) {
                    byte[] msg = mPlatformInteractiveCommandHandler.getImageInfo()
                            .setEventType(EventPhotoTakeResult.getEventType(event))
                            .setResult(ImageInfo.ResultCode.LOCAL_DEVICE_SHOOT_FAIL)
                            .getResultMsgBytes();
                    sendToOnlinePlatform(msg);
                }
                break;

            case EventRemoteControl.Code.PHOTO_UPLOAD_REQUEST:
                Log.d(TAG, "onModuleEvent > 开始上传照片");
                final String filePath = EventPhotoUploadRequest.getImgFilePath(event);
                File imgFile = new File(filePath);
                if (!imgFile.exists()) {
                    Log.e(TAG, "onModuleEvent > 开始上传照片 > process fail : img is`not exists = " + filePath);
                    dispatchEvent(new EventPhotoUploadResult()
                            .setResult(false)
                            .setEventType(EventPhotoUploadRequest.getEventType(event))
                            .setRemote(false));
                    return;
                }
                UploadUtil.getInstance()
                        .setOnUploadCallBack(new UploadUtil.OnUploadCallBack() {
                            @Override
                            public UploadUtil.UploadConfig getConfig() {
                                return new UploadUtil.UploadConfig()
                                        .setStrDeviceId(ModuleApplication.this.getConfig().getDevId())
                                        .setStrServerUrl(ModuleApplication.this.getConfig().getRemotePhotoServerAddress())
                                        .setFileImageLocal(imgFile);
                            }

                            @Override
                            public void onUploadFinish(int resultCode) {
                                boolean isUploadSuccess = UploadUtil.ResultCode.UPLOAD_SUCCESS == resultCode;
                                if (isUploadSuccess) {
                                    play("拍照成功");
                                }
                                dispatchEvent(new EventPhotoUploadResult()
                                        .setResult(isUploadSuccess)
                                        .setEventType(EventPhotoUploadRequest.getEventType(event))
                                        .setRemote(false));
                            }
                        })
                        .uploadImageBySubThread();
                break;

            case EventRemoteControl.Code.PHOTO_UPLOAD_RESULT:
                boolean isUploadSuccess = EventPhotoUploadResult.isResultSuccess(event);
                if (isUploadSuccess) {
                    Log.d(TAG, "onModuleEvent > 照片上传成功");
                } else {
                    Log.w(TAG, "onModuleEvent > 照片上传失败");
                }
                byte[] PhotoUploadResultMsg = mPlatformInteractiveCommandHandler.getImageInfo()
                        .setEventType(EventPhotoUploadRequest.getEventType(event))
                        .setResult(isUploadSuccess ? ImageInfo.ResultCode.SUCCESS : ImageInfo.ResultCode.LOCAL_DEVICE_UPLOAD_FAIL)
                        .getResultMsgBytes();
                sendToOnlinePlatform(PhotoUploadResultMsg);
                break;

            case EventRemoteControl.Code.AUDIO_AND_VIDEO_PARAMETERS_APPLY_REQUEST:
                Log.i(TAG, "onModuleEvent > 申请音视频参数和操作");
                final int eventType = EventAudioVideoParametersApplyRequest.getEventType(event);
                boolean isSwitch = IAudioVideo.EVENT_TYPE_CLOSE != eventType;

                //处理：在未链接平台情况下申请打开参数
                if (isSwitch && !getHelmetSocketManager().isConnect()) {
                    Log.w(TAG, "onModuleEvent > 申请视频参数和操作 > 未链接平台");
                    dispatchEvent(new EventAudioVideoParametersApplyResult()
                            .setResult(false)
                            .setRemote(true));
                    play("打开失败，请检查网络链接");
                    return;
                }

                //处理：在链接平台情况下申请打开参数,添加自动超时，记录状态
                int platformType = EventAudioVideoParametersApplyRequest.getPlatformType(event);
                int mediaTypeCode = EventAudioVideoParametersApplyRequest.getMediaType(event);
                if (isSwitch) {
                    mPlatformInteractiveCommandHandler.clearRequestAudioVideoParametersFlag();
                    mPlatformInteractiveCommandHandler.addRequestAudioVideoParametersFlag(mediaTypeCode, getHandlerKeepAliveClient(), 30000);
                } else {
                    mPlatformInteractiveCommandHandler.clearRequestAudioVideoParametersFlag();
                }
                //处理：通知平台
                byte[] PlatformDirectiveAVCMsg = mPlatformInteractiveCommandHandler
                        .getAudioVideoInfo()
                        .setEventType(eventType)
                        .getApplyAudioVideoParametersMsgByMediaTypeCode(platformType, mediaTypeCode, isSwitch);
                sendToOnlinePlatform(PlatformDirectiveAVCMsg);
                break;

            case EventAudioVideoCommunication.Code.AUDIO_VIDEO_OPERATE_RESULT:
                Log.w(TAG, "onModuleEvent > 返回平台音视频参数下发的请求处理结果");
                byte[] LocalDeviceHandleAVCResultMsg = mPlatformInteractiveCommandHandler
                        .getAudioVideoInfo().getResultMsgBytes(
                                EventAudioVideoOperateResult.getToken(event),
                                EventAudioVideoOperateResult.getFlowId(event),
                                EventAudioVideoOperateResult.getResult(event));
                sendToOnlinePlatform(LocalDeviceHandleAVCResultMsg);
                break;
            default:
                break;
        }
    }
}
