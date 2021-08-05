package kuyou.common.serialport.protocol;

import android.util.Log;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import kuyou.common.serialport.base.Param;
import kuyou.common.serialport.base.SerialPort;
import kuyou.common.bytes.ByteUtils;


/**
 * action :
 * <p>
 * remarks:  <br/>
 * author: wuguoxian <br/>
 * date: 21-1-14 <br/>
 * </p>
 */
public class SerialPortImpl extends SerialPort {

    protected final static Map<String, SerialPortImpl> sSerialPortCache = new HashMap<String, SerialPortImpl>();

    private ReadThread mReadThread;
    private int mReadFreq = 15;
    private int mBufferSize = 64;
    private boolean isOpen = false;

    public static SerialPortImpl getInstance(Param param) {
        if (sSerialPortCache.containsKey(param.toString()))
            return sSerialPortCache.get(param.toString());

        SerialPortImpl spc = new SerialPortImpl(param);
        if (-1 != param.getReadFreq())
            spc.mReadFreq = param.getReadFreq();
        if (-1 != param.getBufferSize())
            spc.mBufferSize = param.getBufferSize();
        sSerialPortCache.put(param.toString(), spc);
        return spc;
    }

    private SerialPortImpl(Param configParam) {
        super(configParam);
    }

    @Override
    public boolean open() {
        if (isOpen && !getParam().isEnableAlreadyOpen()) {
            Log.w(TAG, "open > is already open");
            return true;
        }
        if (super.open()) {
            try {
                mReadThread = new ReadThread();
                mReadThread.start();
                Log.d(TAG, "open > process success");
                isOpen = true;
                return true;
            } catch (Exception e) {
                dispatchEvent2Listener(e);
            }
        }
        return false;
    }

    @Override
    public void close() {
        Log.d(TAG, "close > ");
        try {
            mFileInputStream.close();
            mFileOutputStream.close();
        } catch (Exception e) {
            dispatchEvent2Listener(e);
        }
        if (mReadThread != null) {
            try {
                mReadThread.interrupt();
                mReadThread.join();
                mReadThread = null;
            } catch (InterruptedException e) {
                dispatchEvent2Listener(e);
            }
        }
        isOpen = false;
        super.close();
    }

    public boolean write(byte[] data) {
        if (null == data || data.length <= 0) {
            Log.e(TAG, "write > process fail : data is invalid");
            return false;
        }
        if (null == getOutputStream()) {
            Log.e(TAG, "write > process fail : OutputStream is null");
            return false;
        }
        Log.d(TAG, "write > data = " + ByteUtils.bytes2hex(data));
        try {
            getOutputStream().write(data);
        } catch (Exception e) {
            Log.e(TAG, android.util.Log.getStackTraceString(e));
            return false;
        }
        return true;
    }

    private class ReadThread extends Thread {

        @Override
        public void run() {
            super.run();
            if (null == getInputStream()) {
                Log.w(TAG, "getInputStream is null");
                return;
            }
            if (null == getListener()) {
                Log.w(TAG, "getListener is null");
                return;
            }
            byte[] buffer;
            int size;
            while (!isInterrupted()) {
                try {
                    buffer = new byte[mBufferSize];
                    size = getInputStream().read(buffer);
                    if (size > 0) {
                        dispatchEvent2Listener(Arrays.copyOfRange(buffer, 0, size));
                    } else {
                        Log.d(TAG, "data is null");
                    }
                    Thread.sleep(mReadFreq);
                } catch (Exception e) {
                    dispatchEvent2Listener(e);
                }
            }
        }
    }
}
