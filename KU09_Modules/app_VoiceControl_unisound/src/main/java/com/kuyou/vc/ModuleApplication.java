package com.kuyou.vc;

import com.kuyou.vc.handler.UnisoundVoiceControlHandler;
import com.kuyou.vc.protocol.basic.VoiceControl;

import kuyou.common.ku09.BasicModuleApplication;
import kuyou.common.ku09.protocol.basic.IKeyConfig;
import kuyou.common.ku09.event.vc.EventVoiceWakeupRequest;
import kuyou.common.ku09.handler.KeyHandler;

/**
 * action :语音控制模块
 * <p>
 * remarks:  <br/>
 * author: wuguoxian <br/>
 * date: 20-11-3 <br/>
 * <p>
 */
public class ModuleApplication extends BasicModuleApplication {

    //INIT_CHECK_COUNT_MAX * INIT_CHECK_FREQ 不能大于模块服务看门狗的60000默认频度
    public static final int INIT_CHECK_COUNT_MAX = 6;
    public static final int INIT_CHECK_FREQ = 10 * 1000;

    private KeyHandler mKeyHandler;
    private UnisoundVoiceControlHandler mUnisoundVoiceControlHandler;

    @Override
    protected void initRegisterEventHandlers() {
        registerEventHandler(getKeyHandler());
        registerEventHandler(getVoiceControlHandler());
    }

    @Override
    protected String isReady() {
        String statusSuper = super.isReady();
        StringBuilder status = new StringBuilder();
        if (null != statusSuper) {
            status.append(statusSuper);
        }
        if (!getVoiceControlHandler().isReady()) {
            status.append(",VoiceControl初始化异常");
        }
        return status.toString();
    }

    @Override
    protected long getFeedTimeLong() {
        if (null != isReady())
            return INIT_CHECK_COUNT_MAX * INIT_CHECK_FREQ;
        return super.getFeedTimeLong();
    }

    protected KeyHandler getKeyHandler() {
        if (null == mKeyHandler) {
            mKeyHandler = new KeyHandler() {
                @Override
                protected void onKeyLongClick(int keyCode) {
                    super.onKeyLongClick(keyCode);
                    if (IKeyConfig.VOICE_CONTROL == keyCode) {
                        dispatchEvent(new EventVoiceWakeupRequest()
                                .setRemote(false));
                    }
                }
            };
        }
        return mKeyHandler;
    }

    protected UnisoundVoiceControlHandler getVoiceControlHandler() {
        if (null == mUnisoundVoiceControlHandler) {
            mUnisoundVoiceControlHandler = new UnisoundVoiceControlHandler();
            mUnisoundVoiceControlHandler.init(VoiceControl.TYPE.HARDWARE);
        }
        return mUnisoundVoiceControlHandler;
    }
}
