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

/**
 * action :测试流程入口[老化]
 * <p>
 * remarks:  <br/>
 * author: wuguoxian <br/>
 * date: 21-3-15 <br/>
 * </p>
 */
public class TestEntranceAging extends TestEntrance {

    private int mIndex = 0;
    private List<TestItem> mTestItemRandomList = null;
    private List<TestItem> mTestItemOrderList = new ArrayList<TestItem>();
    private boolean isEnableTest = true;

    @Override
    protected int getTestProcessType() {
        return TestItem.POLICY_TEST_AGING;
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
        mTestItemOrderList.add(item);
    }

    protected void getTestItemRandomList() {
        List<TestItem> orderList = new ArrayList<TestItem>(mTestItemOrderList);
        List<TestItem> randomList = new ArrayList<TestItem>();
        for (int i = 0, count = orderList.size(); i < count; i++) {
            int _index = (int) (Math.random() * orderList.size());
            randomList.add(orderList.get(_index));
            orderList.remove(orderList.get(_index));
        }
        if (null != mTestItemRandomList && mTestItemRandomList.size() > 0) {
            final int id = mTestItemRandomList.get(mTestItemRandomList.size() - 1).getTestId();
            if (id == randomList.get(0).getTestId()) {
                TestItem item = randomList.get(0);
                Log.d(TAG, "loadAllTestItem > getTestTitle 重复 = " + item.getTestTitle(getApplicationContext()));
                randomList.remove(0);
                randomList.add(item);
            }
            mTestItemRandomList.clear();
        } else {
            mTestItemRandomList = new ArrayList<>();
        }
        mTestItemRandomList.addAll(randomList);
    }

    private void onTestNext() {
        if (!isEnableTest) {
            Log.w(TAG, "onTestNext > test was ended by the user ");
            return;
        }
        if (null == mTestItemRandomList || mIndex >= mTestItemRandomList.size()) {
            getTestItemRandomList();
            mIndex = 0;
        }
        final TextView btn = mTestItemRandomList.get(mIndex).getButton(null, null, 0);
        Log.d(TAG, "onTestItemResult > process test item : " + btn.getText());
        btn.performClick();
        mIndex += 1;
    }

    @Subscribe
    @Override
    public void onEventTestItemResult(EventTestItemResult event) {
        final int id = event.getTestId();
        if (R.id.test_aging == id) {
            isEnableTest = false;
            Log.d(TAG, "onEventTestItemResult > 退出老化测试 ");
            finish();
            return;
        }
        String resultTitle = sTestItemList.get(id).getTestTitle(getApplicationContext());
        resultTitle += (event.isResult() ? " : 测试成功" : " : 测试失败");
        Log.d(TAG, "onEventTestItemResult > resultTitle = " + resultTitle);
        onTestNext();
    }
}
