package com.kuyou.ft;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;

import com.kuyou.ft.entrance.TestEntranceAging;
import com.kuyou.ft.entrance.TestEntranceAuto;
import com.kuyou.ft.entrance.TestEntranceSingle;

/**
 * action :工厂测试主入口
 * <p>
 * remarks:  <br/>
 * author: wuguoxian <br/>
 * date: 21-3-15 <br/>
 * </p>
 */
public class FactoryTest extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_entrance_main);
    }

    @Override
    protected void onResume() {
        if (getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
        super.onResume();
    }

    public void onClickAutoTest(View v) {
        enterEntrance(TestEntranceAuto.class);
    }

    public void onClickAgingTest(View v) {
        enterEntrance(TestEntranceAging.class);
    }

    public void onClickSingleTest(View v) {
        enterEntrance(TestEntranceSingle.class);
    }

    public void onClickReset(View v) {
        AlertDialog.Builder b = new AlertDialog.Builder(FactoryTest.this);
        b.setTitle(R.string.test_factoryreset_reset_device_title);
        b.setMessage(R.string.test_factoryreset_reset_device_readme);
        b.setPositiveButton(R.string.test_factoryreset_ok,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        resetDevice(getApplicationContext());
                    }
                });
        b.setNegativeButton(R.string.test_factoryreset_cancel,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                    }
                });
        b.create().show();
    }

    private void resetDevice(Context context) {
        Intent intent = new Intent("android.intent.action.FACTORY_RESET");//Intent.ACTION_FACTORY_RESET
        intent.addFlags(Intent.FLAG_RECEIVER_FOREGROUND);
        intent.setPackage("android");
        intent.putExtra("android.intent.extra.REASON", "convert_fbe");//Intent.EXTRA_REASON
        context.sendBroadcast(intent);
    }

    private void enterEntrance(Class<?> cls) {
        Intent targetIntent = new Intent(FactoryTest.this, cls);
        startActivity(targetIntent);
    }
}
