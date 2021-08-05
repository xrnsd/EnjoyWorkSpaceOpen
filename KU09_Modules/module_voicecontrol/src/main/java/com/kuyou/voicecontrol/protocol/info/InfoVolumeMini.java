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
public class InfoVolumeMini extends InfoVolume {

    @Override
    protected int getConfigCode() {
        return Config.TRUN_Quiet;
    }

    @Override
    public String getPayloadDef() {
        return "volumeMinUni";
    }

    @Override
    protected String[] getCmdContexts() {
        return new String[]{"开启静音","打开静音"};
    }

    @Override
    public String geTitle() {
        return "已为您打开静音模式";
    }

    @Override
    public int getCmdCode() {
        return CodecVoice.CMD.VOLUME_MINI;
    }
}
