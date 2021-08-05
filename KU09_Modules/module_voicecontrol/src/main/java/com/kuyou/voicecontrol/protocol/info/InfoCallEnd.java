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
    public String geTitle() {
        return "已为您结束通话";
    }

    @Override
    public boolean perform(IOnParseListener listener) {
        super.perform(listener);
        return listener.onCallEnd();
    }
}
