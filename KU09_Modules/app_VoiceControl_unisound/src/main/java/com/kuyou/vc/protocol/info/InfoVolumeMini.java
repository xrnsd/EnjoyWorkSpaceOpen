package com.kuyou.vc.protocol.info;

import com.kuyou.vc.protocol.CodecVoice;

/**
 * action :协议编解码项[语音控制][静音]
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
    public String getTitle() {
        return "已为您打开静音模式";
    }

    @Override
    public int getCmdCode() {
        return CodecVoice.CMD.VOLUME_MINI;
    }
}
