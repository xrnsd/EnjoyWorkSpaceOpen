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
public class InfoVideoOn extends InfoBase {
    @Override
    public int getCmdCode() {
        return CodecVoice.CMD.CAMERA_ON;
    }

    @Override
    public String getPayloadDef() {
        return "CameraOn";
    }

    @Override
    protected String[] getCmdContexts() {
        return new String[]{"开启摄录" , "打开摄录" , "开始摄录"};
    }

    @Override
    public String getTitle() {
        return "正在为您打开视频通话";
    }

    @Override
    public boolean perform(IOnParseListener listener) {
        super.perform(listener);
        return listener.onVideo(true);
    }
}
