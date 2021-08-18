package com.kuyou.avc;

import android.util.Log;

import com.kuyou.avc.handler.FlashlightHandler;
import com.kuyou.avc.handler.KeyHandler;
import com.kuyou.avc.handler.PeergineAudioVideoHandler;
import com.kuyou.avc.handler.base.AudioVideoRequestResultHandler;
import com.kuyou.avc.util.InfearedCameraControl;

import java.util.ArrayList;
import java.util.List;

import kuyou.common.ipc.RemoteEvent;
import kuyou.common.ipc.RemoteEventBus;
import kuyou.common.ku09.BaseApplication;
import kuyou.common.ku09.event.avc.base.EventAudioVideoCommunication;
import kuyou.common.ku09.event.common.base.EventCommon;
import kuyou.common.ku09.event.rc.base.EventRemoteControl;
import kuyou.common.ku09.key.IKeyEventListener;

/**
 * action :
 * <p>
 * remarks:  <br/>
 * author: wuguoxian <br/>
 * date: 21-7-12 <br/>
 * </p>
 */
public class ModuleApplication extends BaseApplication {

    private static final String TAG = "com.kuyou.avc > ModuleApplication";

    private AudioVideoRequestResultHandler mAudioVideoRequestHandler;
    private FlashlightHandler mFlashlightHandler;
    private KeyHandler mKeyHandler;

    private static ModuleApplication sMain;

    public static ModuleApplication getInstance() {
        return sMain;
    }

    @Override
    protected String getApplicationName() {
        return "AudioVideoCommunication_peergine";
    }

    @Override
    protected void init() {
        super.init();
        sMain = ModuleApplication.this;

        initHandlers();

        InfearedCameraControl.close();
    }

    private void initHandlers() {
        if (null != mAudioVideoRequestHandler)
            return;

        mAudioVideoRequestHandler = PeergineAudioVideoHandler.getInstance(getApplicationContext());
        mAudioVideoRequestHandler.setDispatchEventCallBack(ModuleApplication.this)
                .setModuleManager(ModuleApplication.this);
        mAudioVideoRequestHandler.setHandlerKeepAliveClient(getHandlerKeepAliveClient())
                .setDevicesConfig(getDevicesConfig());

        registerActivityLifecycleCallbacks(mAudioVideoRequestHandler);

        mKeyHandler = KeyHandler.getInstance(getApplicationContext());
        mKeyHandler.setAudioVideoRequestResult(mAudioVideoRequestHandler)
                .setDispatchEventCallBack(ModuleApplication.this)
                .setModuleManager(ModuleApplication.this);

        mFlashlightHandler = FlashlightHandler.getInstance(getApplicationContext());
    }

    @Override
    protected RemoteEventBus.IFrameLiveListener getIpcFrameLiveListener() {
        return PeergineAudioVideoHandler.getInstance(getApplicationContext());
    }

    @Override
    protected List<Integer> getEventDispatchList() {
        List<Integer> list = new ArrayList<>();

        list.add(EventCommon.Code.NETWORK_CONNECTED);
        list.add(EventCommon.Code.NETWORK_DISCONNECT);

        list.add(EventRemoteControl.Code.PHOTO_UPLOAD_RESULT);
        list.add(EventRemoteControl.Code.AUDIO_VIDEO_PARAMETERS_APPLY_RESULT);

        list.add(EventAudioVideoCommunication.Code.AUDIO_VIDEO_OPERATE_REQUEST);
        list.add(EventAudioVideoCommunication.Code.PHOTO_TAKE_REQUEST);
        list.add(EventAudioVideoCommunication.Code.FLASHLIGHT_REQUEST);

        return list;
    }

    @Override
    protected long getFeedTimeLong() {
        return 30 * 1000;
    }

    @Override
    protected IKeyEventListener getKeyListener() {
        return mKeyHandler;
    }

    @Override
    public void onModuleEvent(RemoteEvent event) {
        super.onModuleEvent(event);
        initHandlers();
        mKeyHandler.onModuleEvent(event);
        if (mAudioVideoRequestHandler.onModuleEvent(event))
            return;
        if (mFlashlightHandler.onModuleEvent(event))
            return;
    }
}
