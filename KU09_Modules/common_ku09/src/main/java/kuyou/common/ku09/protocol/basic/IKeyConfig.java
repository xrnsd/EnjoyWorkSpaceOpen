package kuyou.common.ku09.protocol.basic;

import android.view.KeyEvent;

/**
 * action :终端自定义实体按键的实际键值定义，模拟按键[其他硬件模块产生的事件]的实际键值定义
 * <p>
 * remarks:  <br/>
 * author: wuguoxian <br/>
 * date: 21-3-23 <br/>
 * </p>
 */
public interface IKeyConfig {
    //实体按键
    public final static int CALL = KeyEvent.KEYCODE_ALT_LEFT;
    public final static int VOICE_CONTROL = KeyEvent.KEYCODE_SHIFT_LEFT;
    public final static int CAMERA = KeyEvent.KEYCODE_CAMERA;
    public final static int FLASHLIGHT = KeyEvent.KEYCODE_ALT_RIGHT;
    public final static int POWER = KeyEvent.KEYCODE_POWER;

    //模拟按键[其他硬件模块产生的事件]
    public final static int ALARM_NEAR_POWER = KeyEvent.KEYCODE_F3;
    public final static int ALARM_GAS = KeyEvent.KEYCODE_F4;
    public final static int ALARM_GAS_OFF = KeyEvent.KEYCODE_F6;
}
