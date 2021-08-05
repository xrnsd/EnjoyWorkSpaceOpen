package kuyou.common.ku09.key;

import android.view.KeyEvent;

/**
 * action :
 * <p>
 * remarks:  <br/>
 * author: wuguoxian <br/>
 * date: 21-3-23 <br/>
 * </p>
 */
public interface KeyConfig {
    public final static int CALL = KeyEvent.KEYCODE_ALT_LEFT;
    public final static int VOICE_CONTROL = KeyEvent.KEYCODE_SHIFT_LEFT;
    public final static int CAMERA = KeyEvent.KEYCODE_CAMERA;
    public final static int FLASHLIGHT = KeyEvent.KEYCODE_ALT_RIGHT;
    public final static int POWER = KeyEvent.KEYCODE_POWER;

    public final static int ALARM_NEAR_POWER = KeyEvent.KEYCODE_F3;
    public final static int ALARM_GAS = KeyEvent.KEYCODE_F4;
    public final static int ALARM_GAS_OFF = KeyEvent.KEYCODE_F6;
}
