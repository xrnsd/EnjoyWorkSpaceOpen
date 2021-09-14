package com.kuyou.ft.basic.ipc;

import android.content.Intent;

import com.kuyou.ft.basic.TestItem;

/**
 * action :测试功能项[抽象][IPC]
 * <p>
 * remarks:  <br/>
 * author: wuguoxian <br/>
 * date: 21-3-9 <br/>
 * </p>
 */
public abstract class TestItemIpc extends TestItem implements ITestIpcConfig {

    protected void onResult(boolean status) {
        //super.onResult(status);
        Intent intent = new Intent(ACTION_TEST_ITEM_RESULT);
        intent.putExtra(KEY_TEST_ITEM_RESULT_STATUS, status);
        intent.putExtra(KEY_TEST_ITEM_RESULT_ID, getTestId());
        intent.setPackage("kuyou.factorytest");
        intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
        intent.addFlags(Intent.FLAG_RECEIVER_FOREGROUND);
        sendBroadcast(intent);

        finish();
    }
}
