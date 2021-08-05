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
public class InfoVolumeDownload extends InfoVolume {

    @Override
    protected int getConfigCode() {
        return Config.TRUN_DOWN;
    }

    @Override
    public String getPayloadDef() {
        return "volumeDownUni";
    }

    @Override
    protected String[] getCmdContexts() {
        return new String[]{"音量调小"};
    }

    @Override
    public String geTitle() {
        return "已为您降低音量";
    }

    @Override
    public int getCmdCode() {
        return CodecVoice.CMD.VOLUME_DOWNLOAD;
    }
}
