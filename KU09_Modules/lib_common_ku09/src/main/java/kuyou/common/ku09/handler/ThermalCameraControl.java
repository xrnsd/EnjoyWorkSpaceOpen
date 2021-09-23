package kuyou.common.ku09.handler;

import android.util.Log;

import kuyou.common.file.FileUtils;
import kuyou.common.ku09.protocol.basic.IHardwareControlDetectionV1_1;

/**
 * action :激光灯控制
 * <p>
 * remarks:  <br/>
 * author: wuguoxian <br/>
 * date: 21-4-28 <br/>
 * </p>
 */
public class ThermalCameraControl implements IHardwareControlDetectionV1_1 {
    private static final String TAG = "kuyou.common.ku09.handler > InfearedCameraControl";

    public static boolean open() {
        Log.d(TAG, "openInfearedCamera");
        return FileUtils.writeInternalAntennaDevice(DEV_PTAH_THERMAL, DEV_VAL_THERMAL_POWER_ON);
    }

    public static boolean close() {
        Log.d(TAG, "closeInfearedCamera");
        return FileUtils.writeInternalAntennaDevice(DEV_PTAH_THERMAL, DEV_VAL_THERMAL_POWER_OFF);
    }
}
