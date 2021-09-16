package com.kuyou.ft.entrance;

import android.content.Context;
import android.util.Log;
import android.widget.TextView;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import com.kuyou.ft.R;
import com.kuyou.ft.basic.TestEntrance;
import com.kuyou.ft.basic.TestItem;
import com.kuyou.ft.basic.event.EventTestItemResult;
import com.kuyou.ft.item.TestItemBackLight;
import com.kuyou.ft.item.TestItemBlutooth;
import com.kuyou.ft.item.TestItemCameraBack;
import com.kuyou.ft.item.TestItemFlashlight;
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
 * action :测试流程入口[自动]
 * <p>
 * remarks:  <br/>
 * author: wuguoxian <br/>
 * date: 21-3-15 <br/>
 * </p>
 */
public class TestEntranceAuto extends TestEntrance {

    private ArrayList<Integer> mNonList = null;
    private ArrayList<Integer> mTestItemAutoList = null;
    private ArrayList<String> mTestItemTitleList = null;
    private TextView mTvResultSuccess, mTvResultFail, mTvResultNone;

    private int mIndex = 0;

    @Override
    protected int getTestProcessType() {
        return TestItem.POLICY_TEST_AUTO;
    }

    @Override
    protected List<Class> getTestItemList() {
        List<Class> classes = new ArrayList<>();

        classes.add(TestItemBackLight.class);
        classes.add(TestItemBlutooth.class);
        classes.add(TestItemCameraBack.class);
        classes.add(TestItemFlashlight.class);
        classes.add(TestItemGps.class);
        classes.add(TestItemGsensor.class);
        classes.add(TestItemHeadset.class);
        classes.add(TestItemKey.class);
        classes.add(TestItemLcd.class);
        classes.add(TestItemLed.class);
        classes.add(TestItemLoudspeaker.class);
        classes.add(TestItemLSensor.class);
        classes.add(TestItemMicrophone.class);
        classes.add(TestItemPower.class);
        classes.add(TestItemRam.class);
        classes.add(TestItemRangeSensor.class);
        classes.add(TestItemReceiver.class);
        classes.add(TestItemSdcard.class);
        classes.add(TestItemSim.class);
        classes.add(TestItemWifi.class);
        classes.add(TestItemHwSwInfo.class);
        classes.add(TestItemUWB.class);
        classes.add(TestItemLaserLight.class);
        classes.add(TestItemVoiceControl.class);
        classes.add(TestItemThermalCamera.class);

        return classes;
    }

    @Override
    protected void initViews() {
        setContentView(R.layout.test_entrance_auto);
        mTvResultSuccess = findViewById(R.id.tv_result_success);
        mTvResultFail = findViewById(R.id.tv_result_fail);
        mTvResultNone = findViewById(R.id.tv_result_none);
    }

    @Override
    protected void loadAllTestItem(Context context) {
        super.loadAllTestItem(context);
        //开始测试
        onTestNext();
    }

    @Override
    protected void onTestItemLoaded(TestItem item) {
        super.onTestItemLoaded(item);
        if (null == mTestItemAutoList) {
            mTestItemAutoList = new ArrayList<Integer>();
            mTestItemTitleList = new ArrayList<String>();
        }
        mTestItemAutoList.add(item.getTestId());
        mTestItemTitleList.add(item.getTestTitle(getApplicationContext()));
    }

    private void onTestNext() {
        if (mIndex >= mTestItemAutoList.size() - 1) {
            Log.d(TAG, "onTestItemResult > auto test finish ");
            StringBuilder info = new StringBuilder();
            for (String title : mTestItemTitleList) {
                info.append(title).append("|");
            }
            mTvResultNone.setText(info.toString());
            return;
        }
        final TextView btn = sTestItemList.get(mTestItemAutoList.get(mIndex)).getButton(null, null, 0);
        Log.d(TAG, "onTestItemResult > process test item : " + btn.getText());
        btn.performClick();
        mIndex += 1;
    }

    @Subscribe
    @Override
    public void onEventTestItemResult(EventTestItemResult event) {
        onTestNext();
        String resultTitle = sTestItemList.get(event.getTestId()).getTestTitle(getApplicationContext());
        mTestItemTitleList.remove(resultTitle);
        if (event.isResult()) {
            mTvResultSuccess.setText(new StringBuilder(mTvResultSuccess.getText())
                    .append(resultTitle)
                    .append(" | ")
                    .toString());
        } else {
            mTvResultFail.setText(new StringBuilder(mTvResultFail.getText())
                    .append(resultTitle)
                    .append(" | ")
                    .toString());
        }
    }
}
