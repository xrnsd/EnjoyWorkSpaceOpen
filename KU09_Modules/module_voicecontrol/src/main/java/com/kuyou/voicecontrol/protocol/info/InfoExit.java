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
public class InfoExit extends InfoBase {
    @Override
    public int getCmdCode() {
        return CodecVoice.CMD.EXIT;
    }

    @Override
    public String getPayloadDef() {
        return "exitUni";
    }

    @Override
    protected String[] getCmdContexts() {
        return new String[]{""};
    }

    @Override
    public String geTitle() {
        return "有需要再叫我";
    }

    @Override
    public boolean perform(IOnParseListener listener) {
        super.perform(listener);
        return listener.onWakeup(false);
    }
}
