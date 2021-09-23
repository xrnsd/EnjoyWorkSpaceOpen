package com.kuyou.vc.protocol.info;

import com.kuyou.vc.protocol.CodecVoice;
import com.kuyou.vc.protocol.basic.IOnParseListener;

/**
 * action :协议编解码项[语音控制][紧急呼叫]
 * <p>
 * remarks:  <br/>
 * author: wuguoxian <br/>
 * date: 21-1-25 <br/>
 * </p>
 */
public class InfoCallEg extends InfoBase {
    @Override
    public int getCmdCode() {
        return CodecVoice.CMD.CALL_EG;
    }

    @Override
    public String getPayloadDef() {
        return "CallEg";
    }

    @Override
    protected String[] getCmdContexts() {
        return new String[]{"紧急呼叫"};
    }

    @Override
    public String getTitle() {
        return "开始紧急呼叫";
    }

    @Override
    public boolean perform(IOnParseListener listener) {
        super.perform(listener);
        return listener.onCallEg();
    }
}
