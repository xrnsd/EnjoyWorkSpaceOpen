package kuyou.common.protocol;

import android.util.Log;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

import kuyou.common.serialport.base.SerialPort.IOnSerialPortListener;
import kuyou.common.bytes.ByteUtils;

/**
 * action :串口数据校验器[抽象]
 * <p>
 * author: wuguoxian <br/>
 * date: 21-1-4 <br/>
 * <p>
 */
public abstract class Checker implements IOnSerialPortListener {

    protected final String TAG = "kuyou.common.protocol > " + this.getClass().getSimpleName();

    protected byte[] mCheckCache;

    private IOnSerialPortListener mListener;

    private int mFlag = -1;

    private Thread mThreadChecker = null;

    private static Queue<byte[]> mCheckDataCaches = new LinkedList<>();
    private boolean isCheckIng = false;

    public void setListener(IOnSerialPortListener listener) {
        if (listener instanceof Checker) {
            throw new RuntimeException("setListener > process fail : 错误设定，出现自己监听自己");
        }
        if (null == listener)
            return;
        mListener = listener;
        initCheckSubThread();
    }

    private void dispatchEvent2Listener(byte[] data) {
        if (null == mListener)
            return;
        mListener.onReceiveData(data);
    }

    private void initCheckSubThread() {
        if (null != mThreadChecker) {
            return;
        }
        mThreadChecker = new Thread() {
            @Override
            public void run() {
                super.run();
                byte[] checkData = null;
                while (true) {
                    synchronized (mCheckDataCaches) {
                        if (mCheckDataCaches.isEmpty() || mCheckDataCaches.size() <= 0) {
                            try {
                                mCheckDataCaches.wait();
                            } catch (Exception e) {
                                Log.e(TAG, Log.getStackTraceString(e));
                            }
                        }
                        try {
                            checkData = mCheckDataCaches.poll();
                        } catch (Exception e) {
                            Log.e(TAG, Log.getStackTraceString(e));
                        }
                    }
                    if (null == checkData)
                        continue;
                    byte[] result = handler(checkData);
                    if (null == result) {
                        continue;
                    }
                    dispatchEvent2Listener(result);
                    checkData = null;
                }
            }
        };
        mThreadChecker.start();
    }

    @Override
    public void onReceiveData(byte[] data) {
        synchronized (mCheckDataCaches) {
            if (!mCheckDataCaches.offer(data)) {
                Log.e(TAG, "onReceiveData > process fail : 待校验数据添加失败");
                return;
            }
            mCheckDataCaches.notify();
        }
    }

    @Override
    public void onExceptionResult(Exception e) {
        Log.e(TAG, Log.getStackTraceString(e));
    }

    /**
     * action:最小长度，长度小于这个将无法解析
     */
    public abstract int getMsgLengthMini();

    /**
     * action:消息头的值
     */
    protected abstract int getMsgHeadFlag();

    /**
     * action:消息体的固定长度,包含消息头和校验尾
     */
    protected abstract int getMsgHeadLength();

    /**
     * action:获取[消息头和校验尾]之外数据的长度
     * data不完整无法获取长度时，请返回-1
     */
    protected abstract int getMsgBodyLength(byte[] data);

    /**
     * action:消息校验
     */
    protected abstract boolean check(byte[] data);

    /**
     * action:数据处理
     * mCheckCache必须存放协议头开头的数据
     */
    protected byte[] handler(byte[] data) {
        Log.d(TAG, "handler > " + ByteUtils.bytes2hex(data));
        if (null == data) {
            mCheckCache = null;
            return null;
        }

        if (-1 == mFlag) {
            mFlag = getMsgHeadFlag();
        }

        if (ByteUtils.byte2Int(data[0]) == mFlag) {
            final int policyMsgBodyLength = getMsgBodyLength(data);
            if (-1 == policyMsgBodyLength) { //指令不全
                mCheckCache = data;
                Log.d(TAG, "handler > checksum fail: validLength == -1");
                return null;
            }

            final int dataMsgBodyLength = data.length - getMsgHeadLength();

            if (dataMsgBodyLength < policyMsgBodyLength) {//指令不全
                mCheckCache = data;
                return null;
            }

            if (dataMsgBodyLength > policyMsgBodyLength) { //两条指令拼接
                int length = policyMsgBodyLength + getMsgHeadLength();
                mCheckCache = Arrays.copyOfRange(data, length, data.length);

                if (null == mCheckCache
                        || mCheckCache.length <= 0
                        || ByteUtils.byte2Int(mCheckCache[0]) != mFlag) {
                    mCheckCache = null;
                }
                Log.d(TAG, "handler > 指令拼接，进行截断");
                return handler(Arrays.copyOfRange(data, 0, length));
            }

            if (!check(data)) {
                Log.d(TAG, "handler > checksum fail: data = " + ByteUtils.bytes2hex(data));
                return null;
            }
            Log.d(TAG, "handler > \n\n");
            return data;
        } else {
            for (int index = 0, count = data.length; index < count; index++) {
                if (mFlag == ByteUtils.byte2Int(data[index])) {
                    Log.d(TAG, "handler > 指令拼接，进行删除");
                    return handler(Arrays.copyOfRange(data, index, count - index + 1));
                }
            }
        }

        if (null != mCheckCache) {
            Log.d(TAG, "handler > 指令缺失，进行拼接");
            return handler(ByteUtils.byteMergerAll(mCheckCache, data));
        }
        return null;
    }
}
