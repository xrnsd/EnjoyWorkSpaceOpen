package com.kuyou.rc.basic.uwb;

import android.content.Context;
import android.util.Log;

import com.kuyou.rc.basic.uwb.basic.IModuleInfoListener;
import com.kuyou.rc.basic.uwb.basic.InfoUwb;
import com.kuyou.rc.basic.uwb.info.InfoSetModuleId;

import kuyou.common.file.FileUtils;
import kuyou.common.ku09.protocol.basic.IHardwareControlDetectionV1_1;
import kuyou.common.serialport.base.Param;
import kuyou.common.serialport.base.SerialPort;
import kuyou.common.serialport.protocol.SerialPortImpl;

/**
 * action :UWB信息读写实现
 * <p>
 * remarks:  <br/>
 * author: wuguoxian <br/>
 * date: 21-4-2 <br/>
 * </p>
 */
public class UwbManager {

    protected final static String TAG = "com.kuyou.rc.basic.uwb > UwbManager";

    private volatile static UwbManager sInstance;

    private UwbManager(Context context) {
        init(context);
    }

    public static UwbManager getInstance(Context context) {
        if (sInstance == null) {
            synchronized (UwbManager.class) {
                if (sInstance == null) {
                    sInstance = new UwbManager(context);
                }
            }
        }
        return sInstance;
    }

    private Param mSerialPortParam;
    private SerialPortImpl mSerialPort;
    private CodecUwb mCodec;

    protected void init(Context context) {
        setParam(new Param()
                .setPathDev(IHardwareControlDetectionV1_1.DEV_PTAH_UWB)
                .setPathDevOnVal(IHardwareControlDetectionV1_1.DEV_VAL_UWB_POWER_ON)
                .setPathDevOffVal(IHardwareControlDetectionV1_1.DEV_VAL_UWB_POWER_OFF)
                .setSerialPortDevPath("/dev/ttyS1")
                .setChecker(CheckerUwb.getInstance())
                .setVMini(CheckerUwb.getInstance().getMsgLengthMini())
                .setDataBits(Param.DATA.BITE_8)
                .setStopBits(Param.STOP.BITE_1)
                .setCheckBitsParity(Param.CHECK.BITE_N)
                .setBufferSize(64)
                .setBaudRate(115200));
        mCodec = CodecUwb.getInstance(context.getApplicationContext());
    }

    protected UwbManager setParam(Param param) {
        param.setListener(new SerialPort.IOnSerialPortListener() {
            @Override
            public void onReceiveData(byte[] data) {
                mCodec.handle(data);
            }

            @Override
            public void onExceptionResult(Exception e) {
                Log.e(TAG, Log.getStackTraceString(e));
            }
        });
        mSerialPortParam = param;

        if (null == mSerialPort) {
            mSerialPort = SerialPortImpl.getInstance(param);
        }
        return UwbManager.this;
    }

    public UwbManager setModuleInfoListener(IModuleInfoListener listener) {
        if (null == mCodec) {
            Log.e(TAG, "getEventDispatchList > process fail : IModuleInfoListener is null");
        } else {
            mCodec.setListener(listener);
        }
        return UwbManager.this;
    }

    /**
     * 模块上电
     */
    public UwbManager open() {
        FileUtils.writeInternalAntennaDevice(mSerialPortParam.getPathDev(), mSerialPortParam.getPathDevOnVal());
        return UwbManager.this;
    }


    /**
     * 打开串口开关,打开串口
     */
    public UwbManager openSerialPort() {
        if (null != mSerialPortParam) {
            Log.d(TAG, "openSerialPort > ");
            FileUtils.writeInternalAntennaDevice(mSerialPortParam.getPathDev(), IHardwareControlDetectionV1_1.DEV_VAL_UWB_UART_ON);
            mSerialPort.open();
        } else {
            Log.e(TAG, "openSerialPort > process fail : mSerialPortParam is null");
        }
        return UwbManager.this;
    }

    /**
     * 模块下电,关闭串口
     */
    public UwbManager close() {
        FileUtils.writeInternalAntennaDevice(mSerialPortParam.getPathDev(), mSerialPortParam.getPathDevOffVal());
        closeSerialPort();
        return UwbManager.this;
    }

    /**
     * 关闭串口开关
     */
    public UwbManager closeSerialPort() {
        if (null != mSerialPortParam) {
            Log.d(TAG, "closeSerialPort > ");
            FileUtils.writeInternalAntennaDevice(mSerialPortParam.getPathDev(), IHardwareControlDetectionV1_1.DEV_VAL_UWB_UART_OFF);
            mSerialPort.close();
        } else {
            Log.e(TAG, "closeSerialPort > process fail : mSerialPortParam is null");
        }
        return UwbManager.this;
    }

    public void performGetId() {
        mSerialPort.send(mCodec
                .getInfo(InfoUwb.CmdCode.GET_MODULE_ID)
                .getBody());
    }

    public void setId(int devId) {
        InfoSetModuleId info = (InfoSetModuleId) mCodec.getInfo(InfoUwb.CmdCode.SET_MODULE_ID);
        mSerialPort.send(info.getBody(devId));
    }
}
