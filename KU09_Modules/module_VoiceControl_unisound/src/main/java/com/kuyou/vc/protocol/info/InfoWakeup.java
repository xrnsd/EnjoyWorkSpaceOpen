package com.kuyou.vc.protocol.info;

import com.kuyou.vc.protocol.CodecVoice;
import com.kuyou.vc.protocol.base.IOnParseListener;

/**
 * action :协议编解码项[语音控制][唤醒]
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
    public String getTitle() {
        return "在呢";
    }

    @Override
    public boolean perform(IOnParseListener listener) {
        super.perform(listener);
        return listener.onWakeup(true);
    }
}
