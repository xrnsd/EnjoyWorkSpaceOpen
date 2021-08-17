package com.kuyou.vc.protocol.info;

import com.kuyou.vc.protocol.CodecVoice;
import com.kuyou.vc.protocol.base.IOnParseListener;

/**
 * action :协议编解码项[语音控制][呼叫总部]
 * <p>
 * remarks:  <br/>
 * author: wuguoxian <br/>
 * date: 21-1-25 <br/>
 * </p>
 */
public class InfoCallEnd extends InfoBase {
    @Override
    public int getCmdCode() {
        return CodecVoice.CMD.CALL_HOME;
    }

    @Override
    public String getPayloadDef() {
        return "CallEnd";
    }

    @Override
    protected String[] getCmdContexts() {
        return new String[]{"呼叫总部"};
    }

    @Override
    public String getTitle() {
        return "已为您结束通话";
    }

    @Override
    public boolean perform(IOnParseListener listener) {
        super.perform(listener);
        return listener.onCallEnd();
    }
}
