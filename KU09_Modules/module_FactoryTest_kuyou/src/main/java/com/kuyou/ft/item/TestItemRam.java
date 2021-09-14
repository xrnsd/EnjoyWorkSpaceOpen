package com.kuyou.ft.item;

import android.app.ActivityManager;
import android.content.Context;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import com.kuyou.ft.R;
import com.kuyou.ft.basic.TestItem;
import com.kuyou.ft.util.SdUtils;

public class TestItemRam extends TestItem {

    private TextView mTvRamInfo;

    @Override
    public int getTestId() {
        return R.id.test_ram;
    }

    @Override
    public int getSubContentId() {
        return R.layout.test_item_ram;
    }

    @Override
    public String getTestTitle(Context context) {
        return context.getString(R.string.ram_test);
    }

    @Override
    protected void initViews() {
        super.initViews();
        mTvRamInfo = findViewById(R.id.show_ram);
        mTvRamInfo.setTextColor(0xFFFFFFFF);
        String formatFileSize = SdUtils.formatFileSize(getTotalMemorySize(this), false);
        String formatFileSize2 = SdUtils.formatFileSize(getAvailableMemory(this), false);
        mTvRamInfo.setText(getString(R.string.ram_all) + formatFileSize + "\n\n" + getString(R.string.ram_avaial) + formatFileSize2);
    }

    /**
     * 获取当前可用内存，返回数据以字节为单位。
     *
     * @param context 可传入应用程序上下文。
     * @return 当前可用内存单位为B。
     */
    private long getAvailableMemory(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        am.getMemoryInfo(memoryInfo);
        return memoryInfo.availMem;
    }

    /**
     * 获取系统总内存
     *
     * @param context 可传入应用程序上下文。
     * @return 总内存大单位为B。
     */
    private long getTotalMemorySize(Context context) {
        String dir = "/proc/meminfo";
        try {
            FileReader fr = new FileReader(dir);
            BufferedReader br = new BufferedReader(fr, 2048);
            String memoryLine = br.readLine();
            String subMemoryLine = memoryLine.substring(memoryLine.indexOf("MemTotal:"));
            br.close();
            return Integer.parseInt(subMemoryLine.replaceAll("\\D+", "")) * 1024l;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    protected void onAging() {
        super.onAging();
        onAgingTestItem(() -> onResult(true), 5000);
    }
}