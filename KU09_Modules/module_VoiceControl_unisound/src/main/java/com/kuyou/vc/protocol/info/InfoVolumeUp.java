package com.kuyou.vc.protocol.info;

import com.kuyou.vc.protocol.CodecVoice;

/**
 * action :
 * <p>
 * remarks:  <br/>
 * author: wuguoxian <br/>
 * date: 21-1-25 <br/>
 * </p>
 */
public class InfoVolumeUp extends InfoVolume {

    @Override
    protected int getConfigCode() {
        return Config.TRUN_UP;
    }

    @Override
    public String getPayloadDef() {
        return "volumeUpUni";
    }

    @Override
    protected String[] getCmdContexts() {
        return new String[]{"音量调大"};
    }

    @Override
    public String getTitle() {
        return "已为您升高音量";
    }

    @Override
    public int getCmdCode() {
        return CodecVoice.CMD.VOLUME_UP;
    }
}
