package com.kuyou.vc.protocol.base;

import android.util.Log;

/**
 * action :编解码结果监听器[语音控制]
 * <p>
 * remarks:  <br/>
 * author: wuguoxian <br/>
 * date: 21-1-25 <br/>
 * </p>
 */
public class IOnParseListener {
    protected final String TAG = this.getClass().getSimpleName() + "_123456";

    public boolean onWakeup(boolean switchStatus) {
        Log.d(TAG, "onWakeup > " + (switchStatus ? "语言模块已唤醒" : "语言模块已休眠"));
        return false;
    }

    public boolean onShoot( ) {
        Log.d(TAG, "onShoot > 语言模块发起请求：拍照");
        return false;
    }

    public boolean onCallEg( ) {
        Log.d(TAG, "onShoot > 语言模块发起请求：紧急呼叫");
        return false;
    }

    public boolean onCallHome( ) {
        Log.d(TAG, "onShoot > 语言模块发起请求：呼叫总部");
        return false;
    }

    public boolean onCallEnd( ) {
        Log.d(TAG, "onShoot > 语言模块发起请求：结束通话");
        return false;
    }

    public boolean onVideo(boolean switchStatus) {
        Log.d(TAG, "onShoot > 语言模块发起请求：" + (switchStatus ? "开启录像" : "关闭录像"));
        return false;
    }

    public boolean onFlashlight(boolean switchStatus) {
        Log.d(TAG, "onShoot > 语言模块发起请求：" + (switchStatus ? "开启手电筒" : "关闭手电筒"));
        return false;
    }

    public boolean onThermalCamera(boolean switchStatus) {
        Log.d(TAG, "onShoot > 语言模块发起请求：" + (switchStatus ? "开启红外" : "关闭红外"));
        return false;
    }

    public boolean onGas(boolean switchStatus) {
        Log.d(TAG, "onGas > 语言模块发起请求：" + (switchStatus ? "开启气体探测" : "关闭气体探测"));
        return false;
    }

    public boolean onVolumeChange(int configCode) {
        Log.d(TAG, "onShoot > 语言模块发起请求：调整音量");
        return false;
    }
}
