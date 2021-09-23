package com.kuyou.ft.item;

import android.content.Context;
import android.os.Bundle;
import android.widget.TextView;

import com.kuyou.ft.R;
import com.kuyou.ft.basic.TestItemBasic;
import com.kuyou.ft.util.SdUtils;

public class TestItemSdcard extends TestItemBasic {

    private String mStrResult = "";
    private TextView mTvShow;

    @Override
    public int getTestId() {
        return R.id.test_tfcard;
    }

    @Override
    public int getSubContentId() {
        return R.layout.test_item_sdcard;
    }

    @Override
    public String getTestTitle(Context context) {
        return context.getString(R.string.test_tf);
    }

    @Override
    public void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);
        StringBuilder sdInfo = new StringBuilder();
        if (SdUtils.isStorageMounted(getApplicationContext())) {
            sdInfo.append(getString(R.string.tf_yes)).append("\n");
            sdInfo.append(getString(R.string.ram_avaial)).append(SdUtils.formatFileSize(
                    SdUtils.getAvailableExternalMemorySize(getApplicationContext()), false));
            if(isTestModeAging()){
                onAgingTestItem(() -> onResult(true), 3000);
            }
        } else {
            sdInfo.append(getString(R.string.tf_no));
            if(isTestModeAging()){
                onAgingTestItem(() -> onResult(false), 3000);
            }
        }
        mTvShow.setText(sdInfo.toString());
    }

    @Override
    protected void initViews() {
        super.initViews();
        mTvShow = findViewById(R.id.show_sdcard);
    }
}