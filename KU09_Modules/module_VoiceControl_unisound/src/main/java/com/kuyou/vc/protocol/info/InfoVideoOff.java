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
public class InfoVideoOff extends InfoBase {
    @Override
    public int getCmdCode() {
        return CodecVoice.CMD.CAMERA_OFF;
    }

    @Override
    public String getPayloadDef() {
        return "CameraOff";
    }

    @Override
    protected String[] getCmdContexts() {
        return new String[]{"结束摄录" , "关掉摄录" , "关闭摄录"};
    }

    @Override
    public String getTitle() {
        //因为功能项存在提示，所以关闭语言控制的提示
        return "";//"已为您关闭摄录";
    }

    @Override
    public boolean perform(IOnParseListener listener) {
        super.perform(listener);
        return listener.onVideo(false);
    }
}
