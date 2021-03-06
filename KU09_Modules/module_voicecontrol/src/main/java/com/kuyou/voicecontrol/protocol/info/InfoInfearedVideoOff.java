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
public class InfoInfearedVideoOff extends InfoBase {
    @Override
    public int getCmdCode() {
        return CodecVoice.CMD.THERMAL_CAMERA_OFF;
    }

    @Override
    public String getPayloadDef() {
        return "IROff";
    }

    @Override
    protected String[] getCmdContexts() {
        return new String[]{"结束红外" , "关掉红外" , "关闭红外"};
    }

    @Override
    public String geTitle() {
        //因为功能项存在提示，所以关闭语言控制的提示
        return "";//"已为您关闭红外";
    }

    @Override
    public boolean perform(IOnParseListener listener) {
        super.perform(listener);
        return listener.onThermalCamera(false);
    }
}
