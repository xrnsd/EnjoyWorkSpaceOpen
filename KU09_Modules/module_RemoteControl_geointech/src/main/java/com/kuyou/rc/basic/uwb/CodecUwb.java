package com.kuyou.rc.basic.uwb;

import android.content.Context;

import com.kuyou.rc.basic.uwb.basic.IModuleInfoListener;
import com.kuyou.rc.basic.uwb.basic.InfoUwb;

import kuyou.common.protocol.Codec;

/**
 * action :编解码器[UWB模块]
 * <p>
 * remarks:  <br/>
 * author: wuguoxian <br/>
 * date: 21-4-1 <br/>
 * </p>
 */
public class CodecUwb extends Codec<IModuleInfoListener> {

    private static CodecUwb sMain;

    private CodecUwb(Context context) {
        setAutoLoadAllInfoCallBack(new IAutoLoadAllInfoCallBack() {
            @Override
            public Class<?> getInfoClass() {
                return InfoUwb.class;
            }

            @Override
            public Context getApplicationContext() {
                return context.getApplicationContext();
            }
        });
        loadAllInfo();
    }

    public static CodecUwb getInstance(Context context) {
        if (null == sMain) {
            sMain = new CodecUwb(context);
        }
        return sMain;
    }

    @Override
    protected int getFlagIndex() {
        return 3;
    }
}
