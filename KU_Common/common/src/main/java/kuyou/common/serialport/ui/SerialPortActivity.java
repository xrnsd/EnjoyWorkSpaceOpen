package kuyou.common.serialport.ui;

import android.util.Log;

import kuyou.common.file.FileUtils;
import kuyou.common.serialport.base.Param;
import kuyou.common.serialport.base.SerialPort;
import kuyou.common.serialport.protocol.SerialPortImpl;
import kuyou.common.ui.ActivityBase;
import kuyou.common.bytes.ByteUtils;

/**
 * action :串口读写封装，用于快速调试开发
 * <p>
 * author: wuguoxian <br/>
 * date: 21-1-4 <br/><p>
 */
public abstract class SerialPortActivity extends ActivityBase {

    private SerialPortImpl mSerialPort;

    @Override
    protected void onStart() {
        Log.d(TAG, "onStart > ");
        openSerialPort();
        super.onStart();
    }

    @Override
    protected void onStop() {
        Log.d(TAG, "onStop > ");
        super.onStop();
        closeSerialPort();
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy > ");
        super.onDestroy();
        closeSerialPort();
    }

    /**
     * action:串口配置参数
     * 使用说明：
     * return new Param()
     * .setPathDev("/dev/uhf_rfid")
     * .setSerialPortDevPath("/dev/ttyMT0")
     * .setBaudRate(115200)
     * .setDataBits(Param.DATA.BITE_8)
     * .setChecker(CheckerICM.getInstance())
     * .setListener(new SerialPort.IOnSerialPortListener() {
     *
     * @Override public void onReceiveData(byte[] data) {
     * }
     * @Override public void onExceptionResult(Exception e) {
     * }
     * });</p>
     * <p>
     */
    protected abstract Param getConfigParam();

    protected abstract void onDevSwitchPower(boolean on);

    protected void openSerialPort() {
        Log.d(TAG, "openSerialPort > ");
        getConfigParam().setListener(new SerialPort.IOnSerialPortListener() {
            @Override
            public void onReceiveData(byte[] data) {
                Log.d(TAG, "onReceiveData > data = " + ByteUtils.bytes2hex(data));
                SerialPortActivity.this.onReceive(data);
            }

            @Override
            public void onExceptionResult(Exception e) {

            }
        });
        mSerialPort = SerialPortImpl.getInstance(getConfigParam());
        FileUtils.writeInternalAntennaDevice(mSerialPort.getParam().getPathDev(), mSerialPort.getParam().getPathDevOnVal());
        onDevSwitchPower(true);
        mSerialPort.open();
    }

    protected void closeSerialPort() {
        Log.d(TAG, "closeSerialPort > ");
        FileUtils.writeInternalAntennaDevice(mSerialPort.getParam().getPathDev(), mSerialPort.getParam().getPathDevOffVal());
        onDevSwitchPower(false);
        if (null == mSerialPort)
            mSerialPort.close();
    }

    /**
     * action:串口输出的数据
     */
    protected abstract void onReceive(byte[] serialPortData);

    /**
     * action:输入串口的数据
     */
    protected boolean send(byte[] body) {
        if (null == mSerialPort) {
            Log.e(TAG, "send > process fail : mSerialPort is null");
            return false;
        }
        Log.d(TAG, "send > ");
        return mSerialPort.send(body);
    }
}
