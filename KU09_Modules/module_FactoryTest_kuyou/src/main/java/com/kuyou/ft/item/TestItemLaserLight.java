package com.kuyou.ft.item;

import android.content.Context;
import android.util.Log;
import android.view.View;

import com.kuyou.ft.R;
import com.kuyou.ft.basic.TestItem;

import kuyou.common.ku09.event.avc.EventLaserLightRequest;

public class TestItemLaserLight extends TestItem {

    @Override
    public int getTestId() {
        return R.id.test_laser_light;
    }

    @Override
    public String getTestTitle(Context context) {
        return context.getString(R.string.title_laserlight);
    }

    @Override
    public int getSubContentId() {
        return R.layout.test_item_laser_light;
    }

    public void onClickSwitch(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.btn_on:
                dispatchEvent(new EventLaserLightRequest()
                        .setSwitch(true)
                        .setRemote(true));
                break;
            case R.id.btn_off:
                dispatchEvent(new EventLaserLightRequest()
                        .setSwitch(false)
                        .setRemote(true));
                break;
            default:
                break;
        }
    }

}