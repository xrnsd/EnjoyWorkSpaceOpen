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
public class InfoShoot extends InfoBase {
    @Override
    public int getCmdCode() {
        return CodecVoice.CMD.SHOOT;
    }

    @Override
    public String getPayloadDef() {
        return "Shoot";
    }

    @Override
    protected String[] getCmdContexts() {
        return new String[]{"开始拍照"};
    }

    @Override
    public String getTitle() {
        return "正在为您拍照";
    }

    @Override
    public boolean perform(IOnParseListener listener) {
        super.perform(listener);
        return listener.onShoot();
    }
}
