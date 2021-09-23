package com.kuyou.vc.protocol.info;

import com.kuyou.vc.protocol.basic.IOnParseListener;

/**
 * action :协议编解码项[语音控制][硬件实现][音量控制][抽象]
 * <p>
 * remarks:  <br/>
 * author: wuguoxian <br/>
 * date: 21-1-25 <br/>
 * </p>
 */
public abstract class InfoVolume extends InfoBase {

    public static interface Config {
        public final static int TRUN_UP = 0;
        public final static int TRUN_DOWN = 1;
        public final static int TRUN_MAX = 2;
        public final static int TRUN_Quiet = 3;
    }

    protected abstract int getConfigCode();

    @Override
    public boolean perform(IOnParseListener listener) {
        super.perform(listener);
        return listener.onVolumeChange(getConfigCode());
    }
}
