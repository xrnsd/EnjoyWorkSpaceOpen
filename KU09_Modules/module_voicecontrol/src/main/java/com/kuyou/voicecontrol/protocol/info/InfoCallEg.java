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
    public String geTitle() {
        return "开始紧急呼叫";
    }

    @Override
    public boolean perform(IOnParseListener listener) {
        super.perform(listener);
        return listener.onCallEg();
    }
}
