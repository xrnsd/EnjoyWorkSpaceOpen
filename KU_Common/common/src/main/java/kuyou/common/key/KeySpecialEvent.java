package kuyou.common.key;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * action :按键事件自定义处理实现
 * <p>
 * remarks:  <br/>
 * author: wuguoxian <br/>
 * date: 20-10-19 <br/>
 * </p>
 */
public class KeySpecialEvent {
    private static final String TAG = "KeySpecialEvent";

    private static final int FLAG_INTERVAL_CLICK = 500;
    private static final int MSG_SINGLE_CLICK_DISPATCH = 1;
    private static final int MSG_SINGLE_LONG_CLICK_DISPATCH = 2;
    private static final int MSG_DOUBLE_CLICK_DISPATCH = 3;

    public static interface OnClickListener {
        public void onKeyClick(int keyCode);

        public void onKeyLongClick(int keyCode);

        public void onKeyDoubleClick(int keyCode);
    }

    private static final Map<Integer, KeySpecialEvent> mKeyCodeHandlerCache = new HashMap<Integer, KeySpecialEvent>();

    public static HandlerThread sHandlerThreadKeyHandler;
    private Handler mHandlerKey;
    private int mKeyCode = -1;
    private long mTimeFlagKeyDown = -1, mTimeFlagKeyUp = -1;
    private List<OnClickListener> mListeners = new ArrayList<OnClickListener>();

    private KeySpecialEvent(final int keyCode) {
        this.mKeyCode = keyCode;
        //存在连续快速单击不同按键,只能以最后的按键为主的问题.暂时无视
        if (null == sHandlerThreadKeyHandler) {
            sHandlerThreadKeyHandler = new HandlerThread("KeySpecialEvent.sub.thread");
            sHandlerThreadKeyHandler.start();
        }
        mHandlerKey = new Handler(sHandlerThreadKeyHandler.getLooper()) {
            private int keyCodeHandle = keyCode;

            public void handleMessage(Message msg) {
                if (mKeyCodeHandlerCache.containsKey(keyCodeHandle))
                    mKeyCodeHandlerCache.get(keyCodeHandle).handleMessage(msg);
            }
        };
    }

    public static KeySpecialEvent getInstance(final int keyCode, OnClickListener listener) {
        if (!mKeyCodeHandlerCache.containsKey(keyCode)) {
            KeySpecialEvent kse = new KeySpecialEvent(keyCode);
            kse.addOnKeyDoubleClickListener(keyCode, listener);
            mKeyCodeHandlerCache.put(keyCode, kse);
        }
        return mKeyCodeHandlerCache.get(keyCode);
    }

    public void resetKeyStatus() {
        mHandlerKey.removeMessages(MSG_SINGLE_CLICK_DISPATCH);
        mHandlerKey.removeMessages(MSG_DOUBLE_CLICK_DISPATCH);
        mHandlerKey.removeMessages(MSG_SINGLE_LONG_CLICK_DISPATCH);
        mTimeFlagKeyDown = -1;
    }

    public boolean onKeyEvent(KeyEvent event) {
        if (event.getKeyCode() != mKeyCode) {
            return false;
        }

        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            if (mHandlerKey.hasMessages(MSG_SINGLE_CLICK_DISPATCH)) {
                mHandlerKey.sendEmptyMessageDelayed(MSG_DOUBLE_CLICK_DISPATCH, FLAG_INTERVAL_CLICK);
            } else {
                mHandlerKey.removeMessages(MSG_DOUBLE_CLICK_DISPATCH);
            }
            mHandlerKey.removeMessages(MSG_SINGLE_LONG_CLICK_DISPATCH);
            mHandlerKey.removeMessages(MSG_SINGLE_CLICK_DISPATCH);

            if (event.getRepeatCount() > 5) {
                if (-1 != mTimeFlagKeyDown) {
                    mHandlerKey.sendEmptyMessage(MSG_SINGLE_LONG_CLICK_DISPATCH);
                }
                mTimeFlagKeyDown = -1;
                return true;
            } else if (!mHandlerKey.hasMessages(MSG_DOUBLE_CLICK_DISPATCH)) {
                mHandlerKey.sendEmptyMessageDelayed(MSG_SINGLE_CLICK_DISPATCH, FLAG_INTERVAL_CLICK);
            }

            mTimeFlagKeyDown = System.currentTimeMillis();
            return true;
        }
        return false;
    }

    private void addOnKeyDoubleClickListener(final int keyCode, OnClickListener listener) {
        if (keyCode != mKeyCode || null == listener)
            return;
        synchronized (mListeners) {
            if (mListeners.contains(listener))
                return;
            mListeners.add(listener);
        }
    }

    private void handleMessage(Message msg) {
        mHandlerKey.removeMessages(msg.what);
        switch (msg.what) {
            case MSG_SINGLE_CLICK_DISPATCH:
                onKeyClick(mKeyCode);
                break;
            case MSG_SINGLE_LONG_CLICK_DISPATCH:
                onKeyLongClick(mKeyCode);
                break;
            case MSG_DOUBLE_CLICK_DISPATCH:
                onKeyDoubleClick(mKeyCode);
                break;
            default:
                break;
        }
    }

    private void onKeyDoubleClick(int keyCode) {
        Log.d(TAG, "onKeyDoubleClick keyCode=" + keyCode);
        for (OnClickListener listener : mListeners) {
            listener.onKeyDoubleClick(keyCode);
        }
    }

    private void onKeyLongClick(int keyCode) {
        Log.d(TAG, "onKeyLongClick keyCode=" + keyCode);
        for (OnClickListener listener : mListeners) {
            listener.onKeyLongClick(keyCode);
        }
    }

    private void onKeyClick(int keyCode) {
        Log.d(TAG, "onKeyClick keyCode=" + keyCode);
        for (OnClickListener listener : mListeners) {
            listener.onKeyClick(keyCode);
        }
    }
}