package com.kuyou.jt808.alarm;

import android.content.Context;
import android.util.Log;

import kuyou.common.file.FileUtils;
import com.kuyou.jt808.info.LocationInfo;

/**
 * action :近电报警处理器
 * <p>
 * author: wuguoxian <br/>
 * date: 20-11-30 <br/>
 * <p>
 */
public class NearPowerAlarm extends AlarmHandler {
    private static final String DEV_PATH = "/sys/kernel/lactl/attr/pressure";

    public static interface Config {
        public static final String MODE_220V = "pressure_grade_a";
        public static final String MODE_10KV = "pressure_grade_b";
        public static final String MODE_35KV = "pressure_grade_c";
        public static final String MODE_220KV = "pressure_grade_d";
        public static final String MODE_CLOSE = "pressure_grade_n";
    }

    @Override
    protected int getSensorType() {
        return 0;
    }

    public boolean open(String modeConfig) {
        Log.d(TAG, "open > flag = " + modeConfig);
        return FileUtils.writeInternalAntennaDevice(DEV_PATH, modeConfig);
    }

    public boolean open220V(Context context) {
        final String status = getStatus(context);
        if (!Config.MODE_CLOSE.equals(status) && !Config.MODE_220V.equals(status))
            return open(Config.MODE_220V);
        return false;
    }

    @Override
    public void close() {
        Log.d(TAG, "close");
        FileUtils.writeInternalAntennaDevice(DEV_PATH, Config.MODE_CLOSE);
    }

    public String getStatus(Context context) {
        return FileUtils.getInstance(context).readData(DEV_PATH);
    }

    protected LocationInfo getLocationInfo() {
        return null;
    }

    protected void sendMsgNearPowerAlarm() {
        Log.d(TAG, " sendMsgNearPowerAlarm");
        getLocationInfo().setAlarmFlag(FLAG_NEAR_POWER);
    }
}
