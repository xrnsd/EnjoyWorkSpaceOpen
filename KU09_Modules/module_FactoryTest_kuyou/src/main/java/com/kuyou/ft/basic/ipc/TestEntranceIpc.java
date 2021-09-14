package com.kuyou.ft.basic.ipc;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;

import com.kuyou.ft.basic.TestEntrance;
import com.kuyou.ft.basic.event.EventTestItemResult;

/**
 * action :测试流程入口[抽象][IPC]
 * <p>
 * remarks:  <br/>
 * author: wuguoxian <br/>
 * date: 21-3-15 <br/>
 * </p>
 */
public abstract class TestEntranceIpc extends TestEntrance implements ITestIpcConfig {

    private BroadcastReceiver mResultBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (!intent.getAction().equals(getIpcAction())) {
                Log.e(TAG, "onReceive > process fail : invalid action = " + intent.getAction());
                return;
            }
            onReceiveIpc(intent);
        }
    };

    protected void onReceiveIpc(Intent intent){
        int testId = intent.getIntExtra(KEY_TEST_ITEM_RESULT_ID, -1);
        boolean testResult = intent.getBooleanExtra(KEY_TEST_ITEM_RESULT_STATUS, false);
        if (!intent.hasExtra(KEY_TEST_ITEM_RESULT_ID)
                || !intent.hasExtra(KEY_TEST_ITEM_RESULT_STATUS)
                || -1 == testId) {
            Log.e(TAG, "onReceive > process fail : invalid intent extra= " + intent);
            return;
        }
        onEventTestItemResult(
                new EventTestItemResult(testId, testResult));
    }

    protected String getIpcAction() {
        return ACTION_TEST_ITEM_RESULT;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        registerReceiver(mResultBroadcastReceiver, new IntentFilter(ACTION_TEST_ITEM_RESULT));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mResultBroadcastReceiver);
    }
}
