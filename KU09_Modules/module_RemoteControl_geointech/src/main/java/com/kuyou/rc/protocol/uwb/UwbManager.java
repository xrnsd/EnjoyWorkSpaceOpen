package com.kuyou.rc.protocol.uwb;

import android.content.Context;
import android.util.Log;

import com.kuyou.rc.handler.hmd.UsbDeviceHandler;
import com.kuyou.rc.protocol.uwb.basic.IModuleInfoListener;
import com.kuyou.rc.protocol.uwb.basic.InfoUwb;
import com.kuyou.rc.protocol.uwb.info.InfoSetModuleId;

import kuyou.common.file.FileUtils;
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

    protected final static String TAG = "com.kuyou.rc.location.uwb > UwbManager";

    private volatile static UwbManager sInstance;

    private UwbManager(Context context) {
        init(context);
    }

    public static UwbManager getInstance(Context context) {
        if (sInstance == null) {
            synchronized (UsbDeviceHandler.class) {
                if (sInstance == null) {
                    sInstance = new UwbManager(context);
                }
            }
        }
        return sInstance;
    }

    private static final String VAL_ON_SERIAL_PORT = "uwb_uart_on";
    private static final String VAL_OFF_SERIAL_PORT = "uwb_uart_off";

    private Param mSerialPortParam;
    private SerialPortImpl mSerialPort;
    private CodecUwb mCodec;

    protected void init(Context context) {
        setParam(new Param()
                .setPathDev("/sys/kernel/lactl/attr/uwb")
                .setPathDevOnVal("uwb_pwr_on")
                .setPathDevOffVal("uwb_pwr_off")
                .setSerialPortDevPath("/dev/ttyS0")
                .setChecker(CheckerUwb.getInstance())
                .setVMini(CheckerUwb.getInstance().getMsgLengthMini())
                .setDataBits(Param.DATA.BITE_8)
                .setStopBits(Param.STOP.BITE_1)
                .setCheckBitsParity(Param.CHECK.BITE_N)
                .setBufferSize(32)
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
            FileUtils.writeInternalAntennaDevice(mSerialPortParam.getPathDev(), VAL_ON_SERIAL_PORT);
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
        mSerialPort.close();
        return UwbManager.this;
    }

    /**
     * 关闭串口开关
     */
    public UwbManager closeSerialPort() {
        if (null != mSerialPortParam) {
            FileUtils.writeInternalAntennaDevice(mSerialPortParam.getPathDev(), VAL_OFF_SERIAL_PORT);
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
