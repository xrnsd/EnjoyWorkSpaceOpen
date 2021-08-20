package com.kuyou.vc.protocol.info;

import com.kuyou.vc.protocol.CodecVoice;
import com.kuyou.vc.protocol.base.IOnParseListener;

/**
 * action :协议编解码项[语音控制][硬件实现][打开红外热成像视频]
 * <p>
ftAdb * author: wuguoxian <br/>
 * date: 21-1-25 <br/>
 * </p>
 */
public class InfoInfearedVideoOn extends InfoBase {
    @Override
    public int getCmdCode() {
        return CodecVoice.CMD.THERMAL_CAMERA_ON;
    }

    @Override
    public String getPayloadDef() {
        return "IROn";
    }

    @Override
    protected String[] getCmdContexts() {
        return new String[]{"开启红外" , "打开红外" , "开始红外"};
    }

    @Override
    public String getTitle() {
        return "正在为您打开红外视频";
    }

    @Override
    public boolean perform(IOnParseListener listener) {
        super.perform(listener);
        return listener.onThermalCamera(true);
    }
}
