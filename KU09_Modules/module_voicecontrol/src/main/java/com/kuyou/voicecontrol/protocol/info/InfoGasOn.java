package com.kuyou.voicecontrol.protocol.info;

import com.kuyou.voicecontrol.protocol.CodecVoice;
import com.kuyou.voicecontrol.protocol.base.IOnParseListener;

/**
 * action :
 * <p>
 * remarks:  <br/>
 * author: wuguoxian <br/>
 * date: 21-1-25 <br/>
 * </p>
 */
public class InfoGasOn extends InfoBase {
    @Override
    public int getCmdCode() {
        return CodecVoice.CMD.GAS_ON;
    }

    @Override
    public String getPayloadDef() {
        return "GasOn";
    }

    @Override
    protected String[] getCmdContexts() {
        return new String[]{"开启气体探测" , "打开气体探测" , "开始气体探测"};
    }

    @Override
    public String geTitle() {
        return "已为您打开气体探测";
    }

    @Override
    public boolean perform(IOnParseListener listener) {
        super.perform(listener);
        return listener.onGas(true);
    }
}
