package com.kuyou.jt808.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class TransparentActivity extends BaseActivity {
    private static final String KEY_WATCH_DOG_FLAG= "isLaunchByWatchDog";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("");
        launcheHomeApp();
    }

    private void launcheHomeApp(){
        if(!getIntent().hasExtra(KEY_WATCH_DOG_FLAG)){
            return;
        }
        Log.d("123456"," launcheHomeApp ");
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addCategory(Intent.CATEGORY_HOME);
        startActivity(intent);
    }
}
