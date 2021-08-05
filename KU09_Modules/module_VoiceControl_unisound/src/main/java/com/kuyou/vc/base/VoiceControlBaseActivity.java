package com.kuyou.vc.base;

import android.Manifest;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import androidx.annotation.NonNull;

import com.kuyou.vc.definition.IVoiceControlCustomConfig;

import kuyou.common.ku09.ui.BasePermissionsActivity;

public abstract class VoiceControlBaseActivity extends BasePermissionsActivity implements IVoiceControlCustomConfig {
    protected final String TAG = "com.kuyou.voicecontrol.base > VoiceControlBaseActivity";

    protected final int MSG_INIT_MODULE = 1111, MSG_REQUEST_PERMISSION = 1112;
    private Handler mHandlerIniter;
    private int mInitCount = 0;

    @Override
    protected void initViews() {
        super.initViews();
        moduleInit(0);
    }

    @Override
    protected String[] getPermissions() {
        return new String[]{
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.READ_CONTACTS
        };
    }

    private void moduleInit(final int delayed) {
        if (null == mHandlerIniter) {
            mHandlerIniter = new Handler(Looper.getMainLooper()) {
                @Override
                public void handleMessage(@NonNull Message msg) {
                    super.handleMessage(msg);
                    mHandlerIniter.removeMessages(msg.what);
                    switch (msg.what) {
                        case MSG_INIT_MODULE:
                            Log.d(TAG, " moduleInit > handleMessage > MSG_INIT_MODULE");
                            if (checkSelfPermission()) {
                                //VoiceControlApplication.getInstance().init();
                                return;
                            } else if (!mHandlerIniter.hasMessages(MSG_REQUEST_PERMISSION)) {
                                mHandlerIniter.sendEmptyMessageDelayed(MSG_REQUEST_PERMISSION, getPermissions().length * 2000);
                            }
                            if (mInitCount >= INIT_CHECK_COUNT_MAX) {
                                return;
                            }
                            mInitCount += 1;
                            mHandlerIniter.sendEmptyMessageDelayed(MSG_INIT_MODULE, INIT_CHECK_FREQ);
                            break;
                        case MSG_REQUEST_PERMISSION:
                            Log.d(TAG, " moduleInit > handleMessage > MSG_REQUEST_PERMISSION");
                            requestPermission(getPermissions());
                            break;
                        default:
                            break;
                    }
                }
            };
        }
        if (delayed < 0) {
            mHandlerIniter.removeMessages(MSG_REQUEST_PERMISSION);
            mHandlerIniter.removeMessages(MSG_INIT_MODULE);
            //VoiceControlApplication.getInstance().init();
            return;
        }
        //mHandlerIniter.sendEmptyMessageDelayed(MSG_INIT_MODULE,delayed);
    }
}
