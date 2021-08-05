package com.kuyou.openlive.utils;

import android.util.Log;

import kuyou.common.file.FileUtils;

/**
 * action :
 * <p>
 * remarks:  <br/>
 * author: wuguoxian <br/>
 * date: 21-4-28 <br/>
 * </p>
 */
public class InfearedCameraControl {
    private static final String TAG = "com.kuyou.openlive.camera > ";

    public static boolean open() {
        Log.d(TAG, "openInfearedCamera");
        final String devPath = "/sys/kernel/lactl/attr/usbir";
        return FileUtils.writeInternalAntennaDevice(devPath, "usbir_pwr_on");
    }

    public static boolean close() {
        Log.d(TAG, "closeInfearedCamera");
        final String devPath = "/sys/kernel/lactl/attr/usbir";
        return FileUtils.writeInternalAntennaDevice(devPath, "usbir_pwr_off");
    }
}
