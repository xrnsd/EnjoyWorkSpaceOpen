package com.kuyou.ft.item;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.kuyou.ft.R;
import com.kuyou.ft.basic.TestItemBasic;

public class TestItemPower extends TestItemBasic {

    private int mBatteryN, mBatteryV, mBatteryTemp;
    private String mStrBatteryPlugged = "", mStrChargeStatus, mStrBatteryStatus, mStrPowerInfo = "";

    private TextView mTvPowerShow, mTime;

    private BroadcastReceiver mGetBatteryReceiver = new BroadcastReceiver() {
        public void onReceive(Context paramContext, Intent paramIntent) {
            if (!Intent.ACTION_BATTERY_CHANGED.equals(paramIntent.getAction())) {
                Log.e(TAG, "onReceive > process fail : action is invalid = " + paramIntent.getAction());
                return;
            }

            mBatteryN = paramIntent.getIntExtra("level", 0);
            mBatteryV = paramIntent.getIntExtra("voltage", 0);
            mBatteryTemp = paramIntent.getIntExtra("temperature", 0);

            Log.d(TAG, "onReceive > " + new StringBuilder()
                    .append("level = ").append(mBatteryN)
                    .append("voltage = ").append(mBatteryV)
                    .append("temperature = ").append(mBatteryTemp)
                    .append("status = ").append(paramIntent.getIntExtra("status", 0))
                    .append("plugged = ").append(paramIntent.getIntExtra("plugged", 0))
                    .append("health = ").append(paramIntent.getIntExtra("health", 0))
                    .toString());

            switch (paramIntent.getIntExtra("plugged", 1)) {
                case 1:
                    mStrChargeStatus = getString(R.string.power_chongdian) + "(AC)";
                    break;
                case 2:
                    mStrChargeStatus = getString(R.string.power_chongdian) + "(USB)";
                    break;
                default:
                    mStrChargeStatus = getString(R.string.power_weifangdian);
                    break;
            }

            switch (paramIntent.getIntExtra("health", 1)) {
                case 2:
                    mStrBatteryStatus = getString(R.string.power_lianghao);
                    break;
                case 3:
                    mStrBatteryStatus = getString(R.string.power_guore);
                    break;
                case 4:
                    mStrBatteryStatus = getString(R.string.power_meiyoudian);
                    break;
                case 5:
                    mStrBatteryStatus = getString(R.string.power_dianyagao);
                    break;
                default:
                    mStrBatteryStatus = getString(R.string.power_unknown);
                    break;
            }
            mStrPowerInfo = new StringBuilder()
                    .append(getString(R.string.power_status_now)).append(mStrChargeStatus).append("\n\n")
                    .append(getString(R.string.power_now)).append(mBatteryN).append("\n\n")
                    .append(getString(R.string.power_all)).append(100).append("\n\n")
                    .append(getString(R.string.power_status)).append(mStrBatteryStatus).append("\n\n")
                    .append(getString(R.string.power_dianya_now)).append(mBatteryV).append("mv\n\n")
                    .append(getString(R.string.power_wendu)).append(0.1D * mBatteryTemp).append("  \n ")
                    .toString();
            mTvPowerShow.setText(mStrPowerInfo);
        }
    };

    @Override
    public int getTestPolicy() {
        int policy = 0;
        policy |= POLICY_TEST;
        policy |= POLICY_TEST_AUTO;
        return policy;
    }

    @Override
    public int getTestId() {
        return R.id.test_power;
    }

    @Override
    public int getSubContentId() {
        return R.layout.test_item_power;
    }

    @Override
    public String getTestTitle(Context context) {
        return context.getString(R.string.test_power);
    }

    @Override
    public void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);

        IntentFilter filter = new IntentFilter();
        filter.addAction(BatteryManager.ACTION_CHARGING);
        filter.addAction(BatteryManager.ACTION_DISCHARGING);
        filter.addAction(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(mGetBatteryReceiver, filter);
    }

    @Override
    protected void initWindowConfig() {
        //super.initWindowConfig();
    }

    @Override
    protected void initViews() {
        super.initViews();
        mTvPowerShow = findViewById(R.id.powershow);
        mTime = findViewById(R.id.time);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mGetBatteryReceiver);
    }
}
