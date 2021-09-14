package com.kuyou.ft.item;

import android.content.Context;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.TextView;

import com.kuyou.ft.R;
import com.kuyou.ft.basic.TestItem;

public class TestItemSim extends TestItem {

    private boolean Sim1State = false, Sim2State = false;
    private Context mContext;
    private TextView mTvShow;

    @Override
    public int getTestId() {
        return R.id.test_sim;
    }

    @Override
    public int getSubContentId() {
        return R.layout.test_item_sim;
    }

    @Override
    public String getTestTitle(Context context) {
        return context.getString(R.string.sim_test);
    }

    @Override
    protected void initViews() {
        super.initViews();
        mTvShow = findViewById(R.id.show_sim);
        mTvShow.setText(new StringBuilder()
                .append(isSimInserted(TestItemSim.this) ? getString(R.string.sim_yes) : getString(R.string.sim_no))
                .append("SIM\n")
        );
    }

    private boolean isSimInserted(Context context) {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(TELEPHONY_SERVICE);
        String simSerialNumber = tm.getSimSerialNumber();
        boolean isSimInsert = simSerialNumber != null && !simSerialNumber.equals("");
        mBtnSuccess.setEnabled(isSimInsert);
        Log.d(TAG, "isSimInsert:" + isSimInsert);

        if(isTestModeAging()){
            onAgingTestItem(() -> onResult(isSimInsert), 3000);
        }
        return isSimInsert;
    }
}
