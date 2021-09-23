package com.kuyou.vc.protocol.info;

import com.kuyou.vc.protocol.CodecVoice;
import com.kuyou.vc.protocol.basic.IOnParseListener;

/**
 * action :协议编解码项[语音控制][硬件实现][打开气体检测]
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
    public String getTitle() {
        return "已为您打开气体探测";
    }

    @Override
    public boolean perform(IOnParseListener listener) {
        super.perform(listener);
        return listener.onGas(true);
    }
}
