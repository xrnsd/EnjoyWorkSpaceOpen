package kuyou.common.protocol.flow;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;

import androidx.annotation.NonNull;

import java.util.List;

import kuyou.common.protocol.Codec;
import kuyou.common.utils.ByteUtils;
import kuyou.common.protocol.Info;


/**
 * action :命令流程抽象
 * <p>
 * author: wuguoxian <br/>
 * date: 21-1-5 <br/>
 * <p>
 */
public abstract class Flow<T> extends LifeListener {

    protected final String TAG = this.getClass().getSimpleName() + "_123456";

    protected final static int FLAG_READY = 0;

    protected final static int MSG_TIME_OUT = 1200;

    protected int mFlag = -1;
    private int mTimeOutCount = 0;

    protected Step mStepNow;
    protected List<Step> mAllStepList;

    protected IOnFlowCallback mCallback;
    protected Handler mHandlerTimeOut;
    protected HandlerThread mHandlerThreadTimeOut;

    private T mListener;

    public Flow() {
        initTimeOutThread();
    }

    protected abstract Codec getCodec();

    protected abstract List<Step> getAllSteps();

    protected void init() {
        Log.d(TAG, "init > ");
    }

    public int getFlag() {
        return mFlag;
    }

    public void setFlag(int val) {
        mFlag = val;
    }

    private void initTimeOutThread() {
        if (null != mHandlerThreadTimeOut)
            return;
        mHandlerThreadTimeOut = new HandlerThread("");
        mHandlerThreadTimeOut.start();
        mHandlerTimeOut = new Handler(mHandlerThreadTimeOut.getLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                mHandlerTimeOut.removeMessages(msg.what);
                handleSubMessage(msg);
            }
        };
    }

    private void handleSubMessage(@NonNull Message msg) {
        switch (msg.what) {
            case MSG_TIME_OUT:
                Step step = (Step) msg.obj;
                if (null != mCallback)
                    mCallback.onStepResult(step.getStatusTitle());
                if (step.isProcessSuccess()) {
                    break;
                }
                if (!step.isReTry()) {
                    Log.d(TAG, "handleSubMessage > MSG_TIME_OUT > 放弃执行:" + step.getTitle());
                    if (null != mCallback)
                        mCallback.onFail();
                    break;
                }
                if (!mStepNow.equals(step)) {
                    Log.e(TAG, "handleSubMessage > MSG_TIME_OUT > process fail : mStepNow is change");
                    break;
                }
                onStep((Step) msg.obj);
                break;
            default:
                break;
        }
    }

    public void setCallback(IOnFlowCallback callback) {
        mCallback = callback;
    }

    protected Step getStep() {
        if (null == mStepNow) {
            Log.e(TAG, "getStep > process : mStepNow is null");
            mStepNow = getAllSteps().get(0);
        }
        if (mStepNow.isProcessSuccess()) {
            mHandlerTimeOut.removeMessages(MSG_TIME_OUT);
            mStepNow = getStepNext();
        }
        return mStepNow;
    }

    protected Step getStepNext() {
        int size = getAllSteps().size();
        int flag = getAllSteps().indexOf(mStepNow) + 1;
        if (flag == size)
            flag = size - 1;
        Log.d(TAG, "getStepNext > cmd = " + ByteUtils.bytes2hex(getAllSteps().get(flag).getCmdBytes()));
        return getAllSteps().get(flag);
    }

    protected Step getStepUp() {
        int flag = getAllSteps().indexOf(mStepNow) - 1;
        if (flag < 0)
            flag = 0;
        return getAllSteps().get(flag);
    }

    public boolean onStep() {
        Step step = getStep();
        if (step.isProcessSuccess())
            return true;
        return onStep(step);
    }

    protected boolean onStep(Step step) {
        if (null == step) {
            Log.d(TAG, "onNextStep > process fail : step is null");
            return false;
        }
        if (null == mCallback) {
            Log.d(TAG, "onNextStep > process fail : mCallback is null");
            return false;
        }
        boolean result = mCallback.onStep(step.getCmdBytes());
        mStepNow = step;
        onTimeOut(mStepNow);
        Log.d(TAG, "onNextStep > 指令写入： " + (result ? "成功" : "失败"));
        return result;
    }

    protected void onTimeOut(Step step) {
        if (!step.isReTry()) {
            Log.d(TAG, "onTimeOut > step cancel retry");
            mHandlerTimeOut.removeMessages(MSG_TIME_OUT);
            return;
        }
        step.setReTryCount(step.getReTryCount() + 1);
        mHandlerTimeOut.removeMessages(MSG_TIME_OUT);
        Message msg = new Message();
        msg.what = MSG_TIME_OUT;
        msg.obj = step;
        mHandlerTimeOut.sendMessageDelayed(msg, step.getReTryFreq());
    }

    protected void onReady() {
        mHandlerTimeOut.removeMessages(MSG_TIME_OUT);
        setFlag(FLAG_READY);
        if (null == mCallback)
            return;
        mCallback.onReady();
    }

    public void reset() {
        mHandlerTimeOut.removeMessages(MSG_TIME_OUT);
        setFlag(-1);
        for (Step step : getAllSteps()) {
            step.reset();
        }
        getAllSteps().clear();
        getCodec().clearCache();
        mStepNow = null;
    }

    public boolean isReady() {
        return FLAG_READY == getFlag();
    }

    public void handle(byte[] data) {
        getCodec().handle(data);
    }

    public void start() {
        if (null != mCallback)
            mCallback.onStart();
        init();
        onStep();
    }

    public void stop() {
        if (null != mCallback)
            mCallback.onStop();
        if (null != getCodec()) {
            getCodec().clearCache();
        }
        reset();
    }

    @Override
    public void onStart() {
        start();
    }

    @Override
    public void onStop() {
        stop();
    }

    public T getListener() {
        return mListener;
    }

    public void setListener(T listener) {
        mListener = listener;
    }

    protected boolean isInvalidInfo(Info info) {
        if (!info.isSuccess()) {
            Log.w(TAG, "onParseOut > process fail : info status is fail");
            return true;
        }
        getCodec().getStep(info.getCmdCode()).setProcessSuccess(info.isSuccess());
        return false;
    }
}
