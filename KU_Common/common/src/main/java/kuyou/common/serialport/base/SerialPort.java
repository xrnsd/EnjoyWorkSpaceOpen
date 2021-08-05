package kuyou.common.serialport.base;

import android.util.Log;

import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import kuyou.common.utils.ByteUtils;

public abstract class SerialPort {

    protected final String TAG = "kuyou.common.serialport.base > " + this.getClass().getSimpleName();

    static {
        System.loadLibrary("serial_port_common");
    }

    public interface IOnSerialPortListener {
        public void onReceiveData(byte[] data);

        public void onExceptionResult(Exception e);
    }

    protected FileDescriptor mFd;
    protected FileInputStream mFileInputStream;
    protected FileOutputStream mFileOutputStream;

    protected IOnSerialPortListener mDataReceiveListener = null;
    protected Param mParam;

    public SerialPort(Param configParam) {
        setParam(configParam);
    }

    /**
     * 打开串口
     *
     * @return 串口打开是否成功
     */
    public boolean open() {
        try {
            if (null == mParam) {
                Log.e(TAG, "open > process fail : SerialPort Param is null");
                return false;
            }
            if (!mParam.isValid()) {
                Log.e(TAG, "open > process fail : SerialPort Param is invalid = " + mParam.toString());
                throw new SecurityException("SerialPort > process fail : request read/write permission fail");
            }
            mFd = open(mParam.getFileDevSerialPort().getAbsolutePath(), mParam.getBaudRate());
            if (mFd == null) {
                Log.e(TAG, "native open returns null > Param = " + mParam.toString());
                throw new IOException();
            }
            mFileInputStream = new FileInputStream(mFd);
            mFileOutputStream = new FileOutputStream(mFd);
            Log.d(TAG, "open > process success Param = " + mParam.toString());
            return true;
        } catch (Exception e) {
            dispatchEvent2Listener(e);
        }
        return false;
    }

    public native void setDataBits(int dataBits);

    public native void setStopBits(int stopBits);

    public native void setCheckBits(char checkBits);

    public native void setVTime(int vTime);

    public native void setVMini(int vMini);

    private native static FileDescriptor open(String path, int baudrate);

    public native void close();

    public Param getParam() {
        return mParam;
    }

    public void setParam(Param param) {
        mParam = param;

        setOnDataReceiveListener(param.getListener());
        setDataBits(param.getDataBits());
        setStopBits(param.getStopBits());
        setCheckBits(param.getCheckBitsParity());

        setVMini(param.getVMini());
        setVTime(param.getVTime());
    }

    public InputStream getInputStream() {
        return mFileInputStream;
    }

    public OutputStream getOutputStream() {
        return mFileOutputStream;
    }


    public void setOnDataReceiveListener(IOnSerialPortListener listener) {
        mDataReceiveListener = listener;
    }

    public IOnSerialPortListener getListener() {
        return mDataReceiveListener;
    }

    protected void dispatchEvent2Listener(byte[] val) {
        if (null == getListener())
            return;
        Log.d(TAG, "dispatchEvent2Listener > val = "+ByteUtils.bytes2hex(val));
        getListener().onReceiveData(val);
    }

    protected void dispatchEvent2Listener(Exception val) {
        Log.d(TAG, Log.getStackTraceString(val));
        if (null == getListener())
            return;
        getListener().onExceptionResult(val);
    }

    /**
     * 发送指令到串口
     *
     * @param body
     * @return 发送指令写入是否成功
     */
    public boolean send(byte[] body) {
        if (null == getOutputStream()) {
            Log.d(TAG, "sendCmds > process fail : OutputStream is null");
            return false;
        }
        Log.d(TAG, "sendCmds > cmd = " + ByteUtils.bytes2hex(body));
        try {
            getOutputStream().write(body);
            return true;
        } catch (IOException e) {
            dispatchEvent2Listener(e);
        }
        return false;
    }
}
