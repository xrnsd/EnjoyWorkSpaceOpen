package com.kuyou.vc.protocol.info;

import com.kuyou.vc.protocol.CodecVoice;
import com.kuyou.vc.protocol.basic.IOnParseListener;

/**
 * action :协议编解码项[语音控制][关闭气体探测]
 * <p>
 * remarks:  <br/>
 * author: wuguoxian <br/>
 * date: 21-1-25 <br/>
 * </p>
 */
public class InfoGasOff extends InfoBase {
    @Override
    public int getCmdCode() {
        return CodecVoice.CMD.GAS_OFF;
    }

    @Override
    public String getPayloadDef() {
        return "GasOff";
    }

    @Override
    protected String[] getCmdContexts() {
        return new String[]{"关闭气体探测" , "关掉气体探测" , "结束气体探测"};
    }

    @Override
    public String getTitle() {
        return "已为您关掉气体探测";
    }

    @Override
    public boolean perform(IOnParseListener listener) {
        super.perform(listener);
        return listener.onGas(false);
    }
}
