package kuyou.common.serialport.base;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.File;

import kuyou.common.protocol.Checker;

/**
 * action :串口参数
 * <p>
 * remarks:  <br/>
 * author: wuguoxian <br/>
 * date: 21-1-14 <br/>
 * </p>
 */
public class Param {

    public static interface DATA {
        public final static int BITE_7 = 7;
        public final static int BITE_8 = 8;
    }

    public static interface STOP {
        public final static int BITE_1 = 1;
        public final static int BITE_2 = 2;
    }

    public static interface CHECK {
        public final static char BITE_O = 'O';
        public final static char BITE_E = 'E';
        public final static char BITE_N = 'N';
    }

    private String mPathDev = null;
    private String mPathDevOnVal = null;
    private String mPathDevOffVal = null;
    private String mPathDevSerialPort = null;
    private String mPortPath = null;
    private int mBaudRate = -1;
    private int mFlag = -1;
    private int mDataBits = -1;
    private int mStopBits = -1;
    private int mVTime = 0;
    private int mVMini = 1;
    private char mCheckBitsParity;

    private int mReadFreq = 15;
    private int mBufferSize = 64;

    private boolean isEnableAlreadyOpen = false;

    private SerialPort.IOnSerialPortListener mListener;
    private File mFileDevSerialPort = null;
    private Checker mChecker = null;

    public int getFlag() {
        return mFlag;
    }

    public Param setFlag(int flag) {
        mFlag = flag;
        return Param.this;
    }

    public boolean isEnableAlreadyOpen() {
        return isEnableAlreadyOpen;
    }

    public Param setEnableAlreadyOpen(boolean enableAlreadyOpen) {
        isEnableAlreadyOpen = enableAlreadyOpen;
        return Param.this;
    }

    public String getPathDev() {
        return mPathDev;
    }

    public Param setPathDev(String pathDev) {
        mPathDev = pathDev;
        return Param.this;
    }

    public String getPathDevOnVal() {
        return mPathDevOnVal;
    }

    public Param setPathDevOnVal(String pathDevOnVal) {
        mPathDevOnVal = pathDevOnVal;
        return Param.this;
    }

    public String getPathDevOffVal() {
        return mPathDevOffVal;
    }

    public Param setPathDevOffVal(String pathDevOffVal) {
        mPathDevOffVal = pathDevOffVal;
        return Param.this;
    }

    public SerialPort.IOnSerialPortListener getListener() {
        if (null != getChecker())
            return getChecker();
        return mListener;
    }

    public Param setListener(SerialPort.IOnSerialPortListener listener) {
        mListener = listener;
        if (null != getChecker())
            getChecker().setListener(listener);
        return Param.this;
    }

    public String getSerialPortDevPath() {
        return mPathDevSerialPort;
    }

    public Param setSerialPortDevPath(String val) {
        mPathDevSerialPort = val;
        mFileDevSerialPort = new File(mPathDevSerialPort);
        return Param.this;
    }

    public File getFileDevSerialPort() {
        return mFileDevSerialPort;
    }

    public int getBaudRate() {
        return mBaudRate;
    }

    public Param setBaudRate(int baudRate) {
        mBaudRate = baudRate;
        return Param.this;
    }

    public int getDataBits() {
        return mDataBits;
    }

    public Param setDataBits(int dataBits) {
        mDataBits = dataBits;
        return Param.this;
    }

    public int getStopBits() {
        return mStopBits;
    }

    public Param setStopBits(int stopBits) {
        mStopBits = stopBits;
        return Param.this;
    }

    public char getCheckBitsParity() {
        return mCheckBitsParity;
    }

    public Param setCheckBitsParity(char checkBitsParity) {
        mCheckBitsParity = checkBitsParity;
        return Param.this;
    }

    public int getVTime() {
        return mVTime;
    }

    public Param setVTime(int VTime) {
        mVTime = VTime;
        return Param.this;
    }

    public int getVMini() {
        return mVMini;
    }

    public Param setVMini(int VMini) {
        mVMini = VMini;
        return Param.this;
    }

    public int getReadFreq() {
        return mReadFreq;
    }

    public Param setReadFreq(int readFreq) {
        mReadFreq = readFreq;
        return Param.this;
    }

    public int getBufferSize() {
        return mBufferSize;
    }

    public Param setBufferSize(int bufferSize) {
        mBufferSize = bufferSize;
        return Param.this;
    }

    public Checker getChecker() {
        return mChecker;
    }

    public Param setChecker(Checker checker) {
        mChecker = checker;
        mChecker.setListener(mListener);
        return Param.this;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj instanceof Param) {
            Param pm = (Param) obj;
            return pm.getFlag() == Param.this.getFlag()
                    && pm.getSerialPortDevPath().equals(Param.this.getSerialPortDevPath())
                    && pm.getBaudRate() == Param.this.getBaudRate();
        }
        return super.equals(obj);
    }

    public boolean isValid() {
        if (null == getSerialPortDevPath())
            return false;

        return getFileDevSerialPort().exists()
                && getFileDevSerialPort().canRead()
                && getFileDevSerialPort().canWrite()
                && -1 != getBaudRate()
                && null != getListener();
    }

    @NonNull
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(32);
        sb.append("mFlag=").append(mFlag);
        sb.append("\nmPathDevSerialPort=").append(mPathDevSerialPort);
        sb.append("\nmPathDevSerialPort is exists=").append(getFileDevSerialPort().exists());
        sb.append("\nmPathDevSerialPort is canRead=").append(getFileDevSerialPort().canRead());
        sb.append("\nmPathDevSerialPort is canWrite=").append(getFileDevSerialPort().canWrite());
        sb.append("\nmBaudRate=").append(mBaudRate);
        return sb.toString();
    }
}
