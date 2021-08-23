package com.kuyou.tts;

import android.Manifest;
import android.content.pm.PackageManager;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import kuyou.common.ku09.event.tts.EventTTSModuleLiveInitRequest;
import kuyou.common.ku09.event.tts.EventTextToSpeechPlayRequest;
import kuyou.common.ku09.ui.BasePermissionsActivity;

public class MainActivity extends BasePermissionsActivity {

    private static final String TAG = "com.kuyou.tts > MainActivity";
    private static final int REQUEST_PERMISSIONS_CODE = 99;
    private static final String KEY_IS_SYSTEM_BOOT_FIRST = "key.system.boot.first";

    @Override
    protected boolean isEnableContentView() {
        return false;
    }

    @Override
    protected void init() {
        setTitle("");
        requestPermission();
        onBackPressed();
        if (getIntent().hasExtra(KEY_IS_SYSTEM_BOOT_FIRST)
                && getIntent().getBooleanExtra(KEY_IS_SYSTEM_BOOT_FIRST, false)) {
            play("欢迎使用智能安全帽");
        }
    }

    @Override
    protected void play(String content) {
        dispatchEvent(new EventTextToSpeechPlayRequest(content).setRemote(false));
    }

    @Override
    protected String[] getPermissions() {
        return new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.READ_PHONE_STATE};
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (REQUEST_PERMISSIONS_CODE == requestCode) {
            Log.d(TAG, " onRequestPermissionsResult ");
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, " onRequestPermissionsResult > request Permissions success");
                dispatchEvent(new EventTTSModuleLiveInitRequest().setRemote(false));
            } else {
                Log.e(TAG, " onRequestPermissionsResult > request Permissions fail");
            }
        } else {
            Log.w(TAG, " onRequestPermissionsResult >requestCode is not REQUEST_PERMISSIONS_CODE");
        }
    }

    private void requestPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.READ_PHONE_STATE},
                    REQUEST_PERMISSIONS_CODE);
        } else {
            Log.d(TAG, " no request Permission ");
            dispatchEvent(new EventTTSModuleLiveInitRequest().setRemote(false));
        }
    }
}
