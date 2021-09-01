package com.kuyou.avc.ui;

import android.view.View;

import com.kuyou.avc.R;
import com.kuyou.avc.handler.thermal.PeergineThermalCameraCaptureHandler;
import com.kuyou.avc.ui.basic.MultiCapExter2;

import kuyou.common.ku09.event.common.basic.IEventBusDispatchCallback;
import kuyou.common.ku09.protocol.IJT808ExtensionProtocol;

/**
 * action :红外[基于Peergine]
 * <p>
 * remarks:  <br/>
 * author: wuguoxian <br/>
 * date: 21-7-23 <br/>
 * </p>
 */
public class MultiCaptureThermal extends MultiCapExter2 {

    private PeergineThermalCameraCaptureHandler mPeergineThermalCameraCaptureHandler;

    public PeergineThermalCameraCaptureHandler getPeergineThermalCameraCaptureHandler() {
        if (null == mPeergineThermalCameraCaptureHandler) {
            mPeergineThermalCameraCaptureHandler = new PeergineThermalCameraCaptureHandler();
        }
        return mPeergineThermalCameraCaptureHandler;
    }

    @Override
    protected void initViews() {
        super.initViews();
    }

    @Override
    public void setDispatchEventCallback(IEventBusDispatchCallback dispatchEventCallback) {
        getPeergineThermalCameraCaptureHandler().setDispatchEventCallBack(dispatchEventCallback);
        super.setDispatchEventCallback(dispatchEventCallback);
    }

    @Override
    protected int getContentViewResId() {
        return R.layout.main_capexter;
    }

    @Override
    protected View getPreviewView() {
        return getPeergineThermalCameraCaptureHandler().getView();
    }

    @Override
    public int getTypeCode() {
        return IJT808ExtensionProtocol.MEDIA_TYPE_THERMAL;
    }

    @Override
    protected void liveStart() {
        getPeergineThermalCameraCaptureHandler().start(1234);
        super.liveStart();
    }

    @Override
    protected void liveStop() {
        getPeergineThermalCameraCaptureHandler().stop();
        super.liveStop();
    }
}
