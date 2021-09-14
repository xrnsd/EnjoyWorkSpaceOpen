package com.kuyou.ft.item;

import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.kuyou.ft.R;
import com.kuyou.ft.basic.TestItem;

public class TestItemLcd extends TestItem {

    private int mFlag = 0;
    private TextView mTvTest = null, mTvTitle = null;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Log.v(TAG, "FactoryTest handler msg is:" + msg.what);
            switch (msg.what) {
                case 0:
                    mTvTest.setBackgroundColor(0XFFFF0000);
                    break;
                case 1:
                    mTvTest.setBackgroundColor(0XFF00FF00);
                    break;
                case 2:
                    mTvTest.setBackgroundColor(0XFF0000FF);
                    break;
                case 3:
                    mTvTest.setBackgroundColor(0XFFFFFFFF);
                    break;
                case 4:
                    mTvTest.setBackgroundColor(0xFF000000);
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    public int getTestId() {
        return R.id.test_lcd;
    }

    @Override
    public int getSubContentId() {
        return R.layout.test_item_lcd;
    }

    @Override
    public String getTestTitle(Context context) {
        return context.getString(R.string.lcd_test);
    }

    @Override
    protected void initWindowConfig() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    @Override
    protected void initViews() {
        super.initViews();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
        mBtns.setVisibility(View.GONE);
        mTvTest = findViewById(R.id.testview);
        mTvTest.setBackgroundColor(0XFFFF0000);
        mTvTest.setOnClickListener(TestItemLcd.this);

        mTvTitle = findViewById(R.id.result_title);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.testview:
                if (mFlag == 0) {
                    mTvTest.setBackgroundColor(0XFF00FF00);
                    mFlag = 1;
                } else if (mFlag == 1) {
                    mTvTest.setBackgroundColor(0XFF0000FF);
                    mFlag = 2;
                } else if (mFlag == 2) {
                    mTvTest.setBackgroundColor(0XFFFFFFFF);
                    mFlag = 3;
                } else if (mFlag == 3) {
                    mTvTest.setBackgroundColor(0xFF000000);
                    mFlag = 4;
                } else {
                    mBtns.setVisibility(View.VISIBLE);
                    mTvTitle.setVisibility(View.VISIBLE);
                    mTvTest.setVisibility(View.GONE);
                }
                break;
            default:
                break;
        }
    }

    @Override
    protected void onAging() {
        super.onAging();
        mHandler.sendEmptyMessageDelayed(1, 1500);
        mHandler.sendEmptyMessageDelayed(2, 1500 * 2);
        mHandler.sendEmptyMessageDelayed(3, 1500 * 3);
        mHandler.sendEmptyMessageDelayed(4, 1500 * 4);
        onAgingTestItem(() -> onResult(true), 1500 * 6);
    }
}
