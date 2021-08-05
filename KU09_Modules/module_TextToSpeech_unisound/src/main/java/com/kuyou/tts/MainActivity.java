package com.kuyou.tts;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

/**
 * action :
 * <p>
 * author: wuguoxian <br/>
 * date: 20-10-31 <br/>
 * <p>
 */
public class MainActivity extends AppCompatActivity {

    private static final String TAG = "com.kuyou.tts > MainActivity";
    private static final String KEY_WATCH_DOG_FLAG = "isLaunchByWatchDog";
    private static final int REQUEST_PERMISSIONS_CODE = 99;

    private static final String KEY_IS_SYSTEM_BOOT_FIRST = "key.system.boot.first";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestPermission();
        setTitle("");
        onBackPressed();
        if(getIntent().hasExtra(KEY_IS_SYSTEM_BOOT_FIRST)
                && getIntent().getBooleanExtra(KEY_IS_SYSTEM_BOOT_FIRST,false)){
            ModuleApplication.getInstance().play("欢迎使用智能安全帽");
        }
    }

    private void requestPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED) {
            //申请WRITE_EXTERNAL_STORAGE权限
            ActivityCompat.requestPermissions(this, new String[]{
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.READ_PHONE_STATE},
                    REQUEST_PERMISSIONS_CODE);
        } else {
            Log.d(TAG, " no request Permission ");
            try{
                ModuleApplication.getInstance().initTts();
            }catch(Exception e){
                Log.e(TAG,android.util.Log.getStackTraceString(e));
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        //通过requestCode来识别是否同一个请求
        if (REQUEST_PERMISSIONS_CODE == requestCode) {
            Log.d(TAG, " onRequestPermissionsResult ");
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, " onRequestPermissionsResult > request Permissions success");
                ModuleApplication.getInstance().initTts();
            } else {
                Log.e(TAG, " onRequestPermissionsResult > request Permissions fail");
            }
        } else {
            Log.w(TAG, " onRequestPermissionsResult >requestCode is not REQUEST_PERMISSIONS_CODE");
        }
    }
}
