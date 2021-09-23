package com.kuyou.avc.handler.thermal;

import android.util.Log;
import android.view.View;

import com.kuyou.avc.basic.thermal.IPeergineCameraCaptureCallBack;
import com.kuyou.avc.basic.thermal.IPeergineCameraCaptureHandler;
import com.peergine.plugin.android.pgDevVideoIn;

import kuyou.common.ipc.RemoteEvent;
import kuyou.common.ku09.event.common.basic.IEventBusDispatchCallback;
import kuyou.common.ku09.handler.BasicAssistHandler;

/**
 * action :协处理器[Peergine][红外]
 * <p>
 * remarks:  <br/>
 * author: wuguoxian <br/>
 * date: 21-8-23 <br/>
 * </p>
 */
public class PeergineThermalCameraCaptureHandler extends BasicAssistHandler
        implements IPeergineCameraCaptureHandler {

    protected final String TAG = "com.kuyou.avc.handler.thermal > PeergineThermalCameraCaptureHandler";

    private int m_iDevID = -1;
    private int m_iCameraFormat = pgDevVideoIn.PG_DEV_VIDEO_IN_FMT_MJPEG;

    private SeekThermalCameraControlHandler mThermalCameraControlHandler;

    protected SeekThermalCameraControlHandler getThermalCameraControlHandler() {
        if (null == mThermalCameraControlHandler) {
            mThermalCameraControlHandler = new SeekThermalCameraControlHandler(getContext());
            mThermalCameraControlHandler.setPeergineCameraCaptureCallBack(new IPeergineCameraCaptureCallBack() {
                @Override
                public void onPreviewFrame(byte[] data) {
                    PeergineThermalCameraCaptureHandler.this.onPreviewFrame(data);
                }
            });
        }
        return mThermalCameraControlHandler;
    }

    @Override
    public View getView() {
        return getThermalCameraControlHandler().getSeekImageView();
    }

    @Override
    public BasicAssistHandler setDispatchEventCallBack(IEventBusDispatchCallback dispatchEventCallBack) {
        getThermalCameraControlHandler().setDispatchEventCallBack(dispatchEventCallBack);
        return super.setDispatchEventCallBack(dispatchEventCallBack);
    }

    @Override
    public boolean start(int... vals) {
        m_iDevID = vals[0];
        getThermalCameraControlHandler().start();
        return false;
    }

    @Override
    public void stop() {
        getThermalCameraControlHandler().stop();
    }

    @Override
    public void screenshot() {

    }

    protected void onPreviewFrame(byte[] data) {
        try {
            pgDevVideoIn.CaptureProcExt(m_iDevID, data, 0, data.length, m_iCameraFormat, 0);
        } catch (Exception e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }
    }

    @Override
    protected void initReceiveEventNotices() {
    }

    @Override
    public boolean onReceiveEventNotice(RemoteEvent event) {
        return false;
    }
}
