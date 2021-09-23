package com.kuyou.vc.protocol.info;

import com.kuyou.vc.protocol.CodecVoice;
import com.kuyou.vc.protocol.basic.IOnParseListener;

/**
 * action :协议编解码项[语音控制][硬件实现][打开手电筒]
 * <p>
 * remarks:  <br/>
 * author: wuguoxian <br/>
 * date: 21-1-25 <br/>
 * </p>
 */
public class InfoFlashlightOn extends InfoBase {
    @Override
    public int getCmdCode() {
        return CodecVoice.CMD.FLASHLIGHT_ON;
    }

    @Override
    public String getPayloadDef() {
        return "FlashlightOn";
    }

    @Override
    protected String[] getCmdContexts() {
        return new String[]{"开灯"};
    }

    @Override
    public String getTitle() {
        return "已为您打开手电筒";
    }

    @Override
    public boolean perform(IOnParseListener listener) {
        super.perform(listener);
        return listener.onFlashlight(true);
    }
}
