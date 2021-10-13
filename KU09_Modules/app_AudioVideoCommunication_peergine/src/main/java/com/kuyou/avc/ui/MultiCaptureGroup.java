package com.kuyou.avc.ui;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.kuyou.avc.R;
import com.kuyou.avc.ui.basic.MultiCapture;

import kuyou.common.ku09.protocol.basic.IJT808ExtensionProtocol;

/**
 * action :群组通话[基于Peergine,采集端]
 * <p>
 * remarks:  <br/>
 * author: wuguoxian <br/>
 * date: 21-7-23 <br/>
 * </p>
 */
public class MultiCaptureGroup extends MultiCapture {

    protected final static String TAG = "com.kuyou.avc.ui > MultiCaptureGroup";

    private Runnable mRunnableWaitingForRenderConnectTimeout = null;
    private Handler mHandlerWaitingForRenderConnectTimeout = null;

    @Override
    protected int getContentViewResId() {
        return R.layout.main_capture;
    }

    @Override
    public int getTypeCode() {
        return IJT808ExtensionProtocol.MEDIA_TYPE_GROUP;
    }

    @Override
    protected void onStart() {
        super.onStart();
        //getHandlerWaitingForRenderConnectTimeout().postDelayed(mRunnableWaitingForRenderConnectTimeout, 15*1000);
        getHandlerWaitingForRenderConnectTimeout().postDelayed(mRunnableWaitingForRenderConnectTimeout, 90 * 1000);
        liveStart();
    }

    @Override
    public void exit() {
        if (getTransfering()) {
            btnRefuse();
            setTransfering(false);
        }
        liveStop();
        super.exit();
    }

    public Handler getHandlerWaitingForRenderConnectTimeout() {
        Log.d(TAG, "getHandlerWaitingForRenderConnectTimeout > ");
        if (null == mHandlerWaitingForRenderConnectTimeout) {
            mHandlerWaitingForRenderConnectTimeout = new Handler(Looper.getMainLooper());
            mRunnableWaitingForRenderConnectTimeout = new Runnable() {
                @Override
                public void run() {
                    String content = "群组成员已全部离线，即将关闭群组";
                    Log.d(TAG, "runnableWaitingForRenderConnectTimeout > run > " + content);
                    MultiCaptureGroup.this.play(content);
                    MultiCaptureGroup.this.performOperate();
                }
            };
        }
        return mHandlerWaitingForRenderConnectTimeout;
    }

    protected void performOperate() {
        if (null == getAudioVideoRequestCallback()) {
            Log.e(TAG, "performOperate > process fail : getAudioVideoRequestCallback() is null");
            return;
        }
        getAudioVideoRequestCallback().performOperate();
    }

    @Override
    protected void refreshRenderList(boolean isAdd, String renderId) {
        super.refreshRenderList(isAdd, renderId);
        final int size = getRenderList().size();
        if (0 < size) {
            Log.i(TAG, "refreshRenderList > 群组不空，清除超时回调");
            getHandlerWaitingForRenderConnectTimeout().removeCallbacks(mRunnableWaitingForRenderConnectTimeout);
        } else if (0 == size) {
            Log.i(TAG, "refreshRenderList > 群组为空，开始倒计时");
            //getHandlerWaitingForRenderConnectTimeout().postDelayed(mRunnableWaitingForRenderConnectTimeout, 5*1000);
            getHandlerWaitingForRenderConnectTimeout().postDelayed(mRunnableWaitingForRenderConnectTimeout, 30 * 1000);
        }
    }
}
