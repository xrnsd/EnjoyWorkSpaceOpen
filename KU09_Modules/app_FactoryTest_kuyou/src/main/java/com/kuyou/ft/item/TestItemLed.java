package com.kuyou.ft.item;

import android.app.NotificationManager;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import java.io.FileOutputStream;

import com.kuyou.ft.R;
import com.kuyou.ft.basic.TestItemBasic;

public class TestItemLed extends TestItemBasic {

    private final byte[] LIGHT_ON = {'2', '5', '5'},
            LIGHT_OFF = {'0'};

    private final int RED = 0,
            GREEN = 1,
            BLUE = 2,
            COLSE = 3;

    protected final String RED_LED_DEV = "/sys/class/leds/red/brightness",
            GREEN_LED_DEV = "/sys/class/leds/green/brightness",
            BLUE_LED_DEV = "/sys/class/leds/blue/brightness";

    private Button mBtnRed, mBtnBlue, mBtnGreen, mBtnClean;
    private NotificationManager mNotificationManager = null;

    @Override
    public int getTestId() {
        return R.id.test_led_light;
    }

    @Override
    public int getSubContentId() {
        return R.layout.test_item_led;
    }

    @Override
    public String getTestTitle(Context context) {
        return context.getString(R.string.test_led_light);
    }

    @Override
    protected void initWindowConfig() {
        //super.initWindowConfig();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    @Override
    protected void initViews() {
        super.initViews();

        mBtnRed = findViewById(R.id.led_light_red);
        mBtnGreen = findViewById(R.id.led_light_green);
        mBtnBlue = findViewById(R.id.led_light_blue);
        mBtnClean = findViewById(R.id.clean_led);

        mBtnRed.setOnClickListener(TestItemLed.this);
        mBtnGreen.setOnClickListener(TestItemLed.this);
        mBtnBlue.setOnClickListener(TestItemLed.this);
        mBtnClean.setOnClickListener(TestItemLed.this);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.led_light_red:
                setledlightcolor(RED);
                break;
            case R.id.led_light_green:
                setledlightcolor(GREEN);
                break;
            case R.id.led_light_blue:
                setledlightcolor(BLUE);
                break;
            case R.id.clean_led:
                setledlightcolor(COLSE);
                break;
            default:
                break;
        }
    }

    @Override
    protected void onAging() {
        super.onAging();
        onAgingTestItem(() -> setledlightcolor(RED), 50);
        onAgingTestItem(() -> setledlightcolor(GREEN), 1500 * 1);
        onAgingTestItem(() -> setledlightcolor(BLUE), 1500 * 2);
        onAgingTestItem(() -> setledlightcolor(COLSE), 1500 * 3);
        onAgingTestItem(() -> onResult(true), 1500 * 4);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        setledlightcolor(COLSE);
    }

    private void setledlightcolor(int color) {
        boolean red = false, green = false, blue = false;
        switch (color) {
            case RED:
                red = true;
                break;
            case GREEN:
                green = true;
                break;
            case BLUE:
                blue = true;
                break;
            default:
                break;
        }
        try {
            FileOutputStream fRed = new FileOutputStream(RED_LED_DEV);
            fRed.write(red ? LIGHT_ON : LIGHT_OFF);
            fRed.close();
            FileOutputStream fGreen = new FileOutputStream(GREEN_LED_DEV);
            fGreen.write(green ? LIGHT_ON : LIGHT_OFF);
            fGreen.close();
            FileOutputStream fBlue = new FileOutputStream(BLUE_LED_DEV);
            fBlue.write(blue ? LIGHT_ON : LIGHT_OFF);
            fBlue.close();
        } catch (Exception e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }
    }
}

