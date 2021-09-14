package com.kuyou.ft.item;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.EditText;

import com.kuyou.ft.R;
import com.kuyou.ft.basic.TestItem;

public class TestItemKey extends TestItem {

    private final static String KEY_CODE_TEST = "kuyou.factorytest.TestItemKey";
    private static final String FLAG = "|";

    private String mStrKeyInfo = "";
    private EditText mEtKey;

    private BroadcastReceiver mBroadcastReceiverKeyCode = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(KEY_CODE_TEST)) {
                Bundle bundle = intent.getExtras();
                int keycode = bundle.getInt("keycode", 0);
                Log.d(TAG, "onKeyDown > keycode = " + keycode);
                runOnUiThread(() -> onKeyDown(keycode, null));
            }
        }
    };

    @Override
    public int getTestPolicy() {
        int policy = 0;
        policy |= POLICY_TEST;
        policy |= POLICY_TEST_AUTO;
        //policy |= POLICY_TEST_AGING;
        return policy;
    }

    @Override
    public int getTestId() {
        return R.id.test_key;
    }

    @Override
    public int getSubContentId() {
        return R.layout.test_item_key;
    }

    @Override
    public String getTestTitle(Context context) {
        return context.getString(R.string.key_test);
    }

    @Override
    public void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);
        registerReceiver(mBroadcastReceiverKeyCode, new IntentFilter(KEY_CODE_TEST));
    }

    @Override
    protected void initViews() {
        super.initViews();
        mEtKey = findViewById(R.id.show_key);
    }

    @Override
    protected void initWindowConfig() {
        //super.initWindowConfig();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mBroadcastReceiverKeyCode);
    }

    private void setText(String val) {
        mEtKey.getEditableText().append(FLAG);
        mEtKey.getEditableText().append(val);
        mEtKey.setSelection(mEtKey.getText().length());
    }

    public boolean onKeyDown(int keyCode, KeyEvent paramKeyEvent) {
        //super.onKeyDown(keyCode,paramKeyEvent);
        Log.d(TAG, "keyCode" + keyCode);
        switch (keyCode) {
            case KeyEvent.KEYCODE_MENU:
                mStrKeyInfo = getString(R.string.key_menu);
                android.util.Log.d(TAG, "onKeyDown:key_menu");
                break;
            case KeyEvent.KEYCODE_BACK:
                mStrKeyInfo = getString(R.string.key_back);
                android.util.Log.d(TAG, "onKeyDown:key_back");
                break;
            case KeyEvent.KEYCODE_HOME:
                mStrKeyInfo = getString(R.string.key_home);
                android.util.Log.d(TAG, "onKeyDown:key_home");
                break;
            case KeyEvent.KEYCODE_SEARCH:
                mStrKeyInfo = getString(R.string.key_search);
                android.util.Log.d(TAG, "onKeyDown:key_search");
                break;
            case KeyEvent.KEYCODE_VOLUME_UP:
                mStrKeyInfo = getString(R.string.key_volume_up);
                android.util.Log.d(TAG, "onKeyDown:key_volume_up");
                break;
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                mStrKeyInfo = getString(R.string.key_volume_down);
                android.util.Log.d(TAG, "onKeyDown:key_volume_down");
                break;
            case KeyEvent.KEYCODE_POWER:
                mStrKeyInfo = getString(R.string.key_power);
                android.util.Log.d(TAG, "onKeyDown:key_power");
                break;
            case KeyEvent.KEYCODE_ALT_LEFT:
                android.util.Log.d(TAG, "onKeyDown:key_call");
                mStrKeyInfo = getString(R.string.key_call);
                break;
            case KeyEvent.KEYCODE_SHIFT_LEFT:
                mStrKeyInfo = getString(R.string.key_voice);
                android.util.Log.d(TAG, "onKeyDown:key_voice");
                break;
            case KeyEvent.KEYCODE_CAMERA:
                mStrKeyInfo = getString(R.string.key_camera);
                android.util.Log.d(TAG, "onKeyDown:key_camera");
                break;
            case KeyEvent.KEYCODE_ALT_RIGHT:
                mStrKeyInfo = getString(R.string.key_flashlight);
                android.util.Log.d(TAG, "onKeyDown:key_flashlight");
                break;
            default:
                mStrKeyInfo = KeyEvent.keyCodeToString(keyCode);
                break;
        }
        setText(mStrKeyInfo);
        return true;
    }
}
