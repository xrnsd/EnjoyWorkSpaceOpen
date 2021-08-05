package com.kuyou.vc.protocol;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.kuyou.vc.protocol.base.VoiceControl;

import java.util.List;

import kuyou.common.file.FileUtils;
import kuyou.common.serialport.base.Param;
import kuyou.common.serialport.base.SerialPort;
import kuyou.common.serialport.protocol.SerialPortImpl;
import kuyou.common.bytes.ByteUtils;

/**
 * action :
 * <p>
 * remarks:  <br/>
 * author: wuguoxian <br/>
 * date: 21-1-22 <br/>
 * </p>
 */
public class VoiceControlHardware extends VoiceControl {

    private SerialPortImpl mSerialPort;
    private Param mParam;
    private Runnable mRunnableTimeOut = new Runnable() {
        @Override
        public void run() {
            Log.d(TAG, "run > 超时自动关闭语音模块");
            VoiceControlHardware.this.stop();
        }
    };
    private Handler mHandler = new Handler();

    @Override
    protected int getPolicy() {
        return TYPE.HARDWARE;
    }

    @Override
    public void init(Context context) {
        super.init(context);
        mParam = new Param()
                .setPathDev("/sys/kernel/lactl/attr/wakeup")
                .setPathDevOnVal("wakeup_pwr_on")
                .setPathDevOffVal("wakeup_pwr_off")
                .setSerialPortDevPath("/dev/ttyS0")
                .setChecker(CheckerVoice.getInstance())
                .setVMini(CheckerVoice.getInstance().getMsgLengthMini())
                .setVTime(10)
                .setDataBits(Param.DATA.BITE_8)
                .setStopBits(Param.STOP.BITE_1)
                .setCheckBitsParity(Param.CHECK.BITE_N)
                .setEnableAlreadyOpen(false)
                .setReadFreq(45)
                .setBufferSize(64)
                .setBaudRate(115200)
                .setListener(new SerialPort.IOnSerialPortListener() {
                    @Override
                    public void onReceiveData(byte[] data) {
                        Log.d(TAG, "onReceiveData > data = " + ByteUtils.bytes2hex(data));
                        disPatchVoiceCommand(data);
                    }

                    @Override
                    public void onExceptionResult(Exception e) {

                    }
                });
        mSerialPort = SerialPortImpl.getInstance(mParam);
    }

    @Override
    public void onWakeup() {

    }

    @Override
    public void start() {
        Log.d(TAG, "start > ");
        FileUtils.writeInternalAntennaDevice(mParam.getPathDev(), mParam.getPathDevOnVal());
        mSerialPort.open();

        mHandler.removeCallbacks(mRunnableTimeOut);
        mHandler.postDelayed(mRunnableTimeOut,70000);
    }

    @Override
    public void stop() {
        if (null == mSerialPort)
            mSerialPort.close();
        Log.d(TAG, "stop > ");
        FileUtils.writeInternalAntennaDevice(mParam.getPathDev(), mParam.getPathDevOffVal());

        mHandler.removeCallbacks(mRunnableTimeOut);
    }

    @Override
    public void onSleep() {
        mHandler.removeCallbacks(mRunnableTimeOut);
        mHandler.postDelayed(mRunnableTimeOut,60000);
    }

    @Override
    protected List<String> getCommandList() {
        return null;
    }
}
