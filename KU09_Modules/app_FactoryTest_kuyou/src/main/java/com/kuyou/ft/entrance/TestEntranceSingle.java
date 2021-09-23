package com.kuyou.ft.entrance;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.kuyou.ft.R;
import com.kuyou.ft.basic.TestItemBasic;
import com.kuyou.ft.basic.event.EventTestItemResult;
import com.kuyou.ft.basic.ipc.TestEntranceIpc;
import com.kuyou.ft.item.TestItemBackLight;
import com.kuyou.ft.item.TestItemBlutooth;
import com.kuyou.ft.item.TestItemCameraBack;
import com.kuyou.ft.item.TestItemFlashlight;
import com.kuyou.ft.item.TestItemGAS;
import com.kuyou.ft.item.TestItemGps;
import com.kuyou.ft.item.TestItemGsensor;
import com.kuyou.ft.item.TestItemHeadset;
import com.kuyou.ft.item.TestItemHwSwInfo;
import com.kuyou.ft.item.TestItemKey;
import com.kuyou.ft.item.TestItemLSensor;
import com.kuyou.ft.item.TestItemLaserLight;
import com.kuyou.ft.item.TestItemLcd;
import com.kuyou.ft.item.TestItemLed;
import com.kuyou.ft.item.TestItemLoudspeaker;
import com.kuyou.ft.item.TestItemMicrophone;
import com.kuyou.ft.item.TestItemNearElectricity;
import com.kuyou.ft.item.TestItemPower;
import com.kuyou.ft.item.TestItemRam;
import com.kuyou.ft.item.TestItemRangeSensor;
import com.kuyou.ft.item.TestItemReceiver;
import com.kuyou.ft.item.TestItemSdcard;
import com.kuyou.ft.item.TestItemSim;
import com.kuyou.ft.item.TestItemThermalCamera;
import com.kuyou.ft.item.TestItemUWB;
import com.kuyou.ft.item.TestItemVoiceControl;
import com.kuyou.ft.item.TestItemWifi;

/**
 * action :测试流程入口[单项]
 * <p>
 * remarks:  <br/>
 * author: wuguoxian <br/>
 * date: 21-3-15 <br/>
 * </p>
 */
public class TestEntranceSingle extends TestEntranceIpc {

    protected int mColumnsIndex = 0;
    protected ViewGroup mViewGroupContent = null;
    protected LinearLayout mLine = null;
    protected LinearLayout.LayoutParams mLayoutParamsBtn = null;
    protected Map<Integer, TextView> mTestItemButtonList;

    protected int getLineColumns() {
        return 8;
    }

    @Override
    protected int getTestProcessType() {
        return TestItemBasic.POLICY_TEST;
    }

    @Override
    protected List<Class> getTestItemList() {
        List<Class> classes = new ArrayList<>();

        classes.add(TestItemHwSwInfo.class);
        classes.add(TestItemLcd.class);
        classes.add(TestItemPower.class);
        classes.add(TestItemKey.class);
        classes.add(TestItemLoudspeaker.class);
        classes.add(TestItemHeadset.class);
        classes.add(TestItemMicrophone.class);
        classes.add(TestItemReceiver.class);
        classes.add(TestItemWifi.class);
        classes.add(TestItemBlutooth.class);
        classes.add(TestItemSim.class);
        classes.add(TestItemBackLight.class);
        classes.add(TestItemRam.class);
        classes.add(TestItemGsensor.class);
        classes.add(TestItemLSensor.class);
        classes.add(TestItemRangeSensor.class);
        classes.add(TestItemSdcard.class);
        classes.add(TestItemCameraBack.class);
        classes.add(TestItemLed.class);
        classes.add(TestItemFlashlight.class);
        classes.add(TestItemGps.class);
        classes.add(TestItemUWB.class);
        classes.add(TestItemLaserLight.class);
        classes.add(TestItemVoiceControl.class);
        classes.add(TestItemThermalCamera.class);
        classes.add(TestItemGAS.class);
        classes.add(TestItemNearElectricity.class);

        return classes;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setContentView(R.layout.test_entrance_single);
        mViewGroupContent = findViewById(com.kuyou.ft.R.id.test_content);
        mLayoutParamsBtn = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT);
        mLayoutParamsBtn.weight = 1;
        mTestItemButtonList = new HashMap<Integer, TextView>();
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onTestItemLoaded(TestItemBasic item) {
        super.onTestItemLoaded(item);

        if (mLine == null || mColumnsIndex >= getLineColumns()) {
            mColumnsIndex = 0;
            mLine = (LinearLayout) LayoutInflater.from(getApplicationContext()).inflate(
                    com.kuyou.ft.R.layout.test_entrance_line, null);
            mViewGroupContent.addView(mLine);
        }
        mTestItemButtonList.put(item.getTestId(), item.getButton(null, null, 0));
        mLine.addView(item.getButton(null, null, 0), mLayoutParamsBtn);
        mColumnsIndex += item.getOccupyColumns();
    }

    @Subscribe
    @Override
    public void onEventTestItemResult(EventTestItemResult event) {
        Log.d(TAG, "onEventTestItemResult > single item = " + event.getTestId());
        mTestItemButtonList.get(event.getTestId())
                .setTextColor(event.isResult() ? 0XFF0000FF : 0XFFFF0000);
    }
}
