package com.kuyou.ft.item;

import android.content.Context;
import android.util.Log;

import com.kuyou.ft.R;
import com.kuyou.ft.basic.TestItemBasic;

import kuyou.common.file.FileUtils;
import kuyou.common.ku09.protocol.basic.IHardwareControl;

public class TestItemGAS extends TestItemBasic {

    @Override
    public int getTestPolicy() {
        int policy = 0;
        policy |= POLICY_TEST;
        //policy |= POLICY_TEST_AUTO;
        //policy |= POLICY_TEST_AGING;
        return policy;
    }

    @Override
    public int getTestId() {
        return R.id.test_gps;
    }

    @Override
    public int getSubContentId() {
        return R.layout.test_item_gas;
    }

    @Override
    public String getTestTitle(Context context) {
        return context.getString(R.string.test_gas);
    }

    @Override
    protected void initViews() {
        super.initViews();
        mTvTitle = findViewById(R.id.tv_test_title);

        boolean result = isNotDetection();
        mBtnSuccess.setEnabled(!result);
        mTvTitle.setText(result ? "未检测到" : "模块已搭载");
    }

    protected boolean isNotDetection() {
        final String filePathDevGasDetection = IHardwareControl.DEV_PTAH_GAS;
        FileUtils fu = FileUtils.getInstance(getApplicationContext());
        if (null == fu) {
            Log.e(TAG, "isNotDetection > process fail : fu is null");
            return true;
        }
        String devStatus = fu.readData(filePathDevGasDetection);
        Log.d(TAG, "isNotDetection > devStatus =" + devStatus);
        return null == devStatus
                || devStatus.replaceAll(" ", "").length() == 0
                || devStatus.startsWith(IHardwareControl.DEV_VAL_GAS_POWER_OFF);
    }
}