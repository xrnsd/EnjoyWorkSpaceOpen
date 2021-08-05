package com.kuyou.voicecontrol.protocol.info;

import com.kuyou.voicecontrol.protocol.base.IOnParseListener;

/**
 * action :
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
