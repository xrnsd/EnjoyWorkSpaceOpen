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
public class InfoCallHome extends InfoBase {
    @Override
    public int getCmdCode() {
        return CodecVoice.CMD.CALL_HOME;
    }

    @Override
    public String getPayloadDef() {
        return "CallHome";
    }

    @Override
    protected String[] getCmdContexts() {
        return new String[]{"呼叫总部"};
    }

    @Override
    public String getTitle() {
        return "开始呼叫总部";
    }

    @Override
    public boolean perform(IOnParseListener listener) {
        super.perform(listener);
        return listener.onCallHome();
    }
}
