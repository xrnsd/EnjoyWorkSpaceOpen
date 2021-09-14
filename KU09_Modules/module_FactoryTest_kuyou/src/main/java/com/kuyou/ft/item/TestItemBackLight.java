package com.kuyou.ft.item;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import androidx.annotation.Nullable;

import com.kuyou.ft.R;
import com.kuyou.ft.basic.TestItem;

public class TestItemBackLight extends TestItem {

    private Button mBtnStrong, mBtnWeak;

    @Override
    public int getTestId() {
        return R.id.test_backlight;
    }

    @Override
    public int getSubContentId() {
        return R.layout.test_item_backlight;
    }

    @Override
    public String getTestTitle(Context context) {
        return context.getString(R.string.backlight_test);
    }

    @Override
    protected void initWindowConfig() {
        //super.initWindowConfig();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initViews() {
        super.initViews();
        mBtnWeak = findViewById(R.id.back_weak);
        mBtnStrong = findViewById(R.id.back_strong);
        mBtnWeak.setOnClickListener(TestItemBackLight.this);
        mBtnStrong.setOnClickListener(TestItemBackLight.this);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.back_weak:
                setWindowBrightness(40);
                break;
            case R.id.back_strong:
                setWindowBrightness(200);
                break;
            default:
                break;
        }
    }

    @Override
    protected void onAging() {
        super.onAging();
        onAgingTestItem(() -> mBtnWeak.performClick(), 50);
        onAgingTestItem(() -> mBtnStrong.performClick(), 2500);
        onAgingTestItem(() -> onResult(true), 5000);
    }

    private void setWindowBrightness(int brightness) {
        Window window = getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.screenBrightness = brightness / 255.0f;
        window.setAttributes(lp);
    }
}
