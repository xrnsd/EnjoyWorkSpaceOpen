package com.kuyou.voicecontrol.protocol.info;

import com.kuyou.voicecontrol.protocol.CodecVoice;

/**
 * action :
 * <p>
 * remarks:  <br/>
 * author: wuguoxian <br/>
 * date: 21-1-25 <br/>
 * </p>
 */
public class InfoVolumeMax extends InfoVolume {

    @Override
    protected int getConfigCode() {
        return Config.TRUN_MAX;
    }

    @Override
    public String getPayloadDef() {
        return "volumeMaxUni";
    }

    @Override
    protected String[] getCmdContexts() {
        return new String[]{"最大音量"};
    }

    @Override
    public String geTitle() {
        return "已为您打开户外模式";
    }

    @Override
    public int getCmdCode() {
        return CodecVoice.CMD.VOLUME_MAX;
    }
}
