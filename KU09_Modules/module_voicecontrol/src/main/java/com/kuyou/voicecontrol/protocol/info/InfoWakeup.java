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
public class InfoWakeup extends InfoBase {
    @Override
    public int getCmdCode() {
        return CodecVoice.CMD.WAKEUP;
    }

    @Override
    public String getPayloadDef() {
        return "wakeup_uni";
    }

    @Override
    protected String[] getCmdContexts() {
        return new String[]{"你好 小安","小安 你好","您好 小安","小安 您好","在吗 小安","小安 在吗"};
    }

    @Override
    public String geTitle() {
        return "在呢";
    }

    @Override
    public boolean perform(IOnParseListener listener) {
        super.perform(listener);
        return listener.onWakeup(true);
    }
}
