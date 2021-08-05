package com.kuyou.vc.protocol.info;

import com.kuyou.vc.protocol.CodecVoice;
import com.kuyou.vc.protocol.base.IOnParseListener;

/**
 * action :
 * <p>
 * remarks:  <br/>
 * author: wuguoxian <br/>
 * date: 21-1-25 <br/>
 * </p>
 */
public class InfoFlashlightOff extends InfoBase {
    @Override
    public int getCmdCode() {
        return CodecVoice.CMD.FLASHLIGHT_OFF;
    }

    @Override
    public String getPayloadDef() {
        return "FlashlightOff";
    }

    @Override
    protected String[] getCmdContexts() {
        return new String[]{"关灯"};
    }

    @Override
    public String geTitle() {
        return "已为您关闭手电筒";
    }

    @Override
    public boolean perform(IOnParseListener listener) {
        super.perform(listener);
        return listener.onFlashlight(false);
    }
}
