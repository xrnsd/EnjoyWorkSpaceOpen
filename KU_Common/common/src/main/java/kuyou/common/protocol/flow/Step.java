package kuyou.common.protocol.flow;

import android.util.Log;

import kuyou.common.protocol.Info;

/**
 * action :
 * <p>
 * author: wuguoxian <br/>
 * date: 21-1-8 <br/>
 * <p>
 */
public class Step {
    protected final String TAG = "kuyou.common.protocol.flow > " + this.getClass().getSimpleName();

    private String mTitle, mCmdString;
    private byte[] mCmdBytes;
    private boolean isProcessSuccess = false;
    private boolean isSupportReTry = false;

    private int mFlag = -1;
    private int mCmdCode = -1;
    private int mReTryFreq = 1000;
    private int mReTryCount = 0;

    public void reset() {
        mTitle = "";
        mCmdString = "";
        mCmdBytes = new byte[0];
        isSupportReTry = false;
        mReTryFreq = 1000;
        resetProcessParam();
    }

    public Step resetProcessParam() {
        isProcessSuccess = false;
        mReTryCount = 0;
        return Step.this;
    }

    public void setParamByInfo(Info info) {
        if (null == info)
            return;
        setTitle(info.getTitle());
        setCmdBytes(info.getBody());
        setCmdCode(info.getCmdCode());
    }

    public int getReTryCount() {
        return mReTryCount;
    }

    public void setReTryCount(int reTryCount) {
        mReTryCount = reTryCount;
    }

    public boolean isSupportReTry() {
        return isSupportReTry;
    }

    public Step setSupportReTry(boolean val) {
        isSupportReTry = val;
        return Step.this;
    }

    public boolean isReTry() {
//        Log.d(TAG, "isReTry > isSupportReTry="+isSupportReTry);
//        Log.d(TAG, "isReTry > mReTryCount="+mReTryCount);
//        Log.d(TAG, "isReTry > isProcessSuccess="+isProcessSuccess);
        return isSupportReTry && mReTryCount < 4 && !isProcessSuccess;
    }

    public int getReTryFreq() {
        return mReTryFreq;
    }

    public Step setReTryFreq(int reTryFreq) {
        mReTryFreq = reTryFreq;
        return Step.this;
    }

    public byte[] getCmdBytes() {
        return mCmdBytes;
    }

    public String getCmdString() {
        return mCmdString;
    }

    public Step setCmdBytes(byte[] cmdBytes) {
        if (null == cmdBytes) {
            Log.d(TAG, "setCmdBytes > process fail : cmdBytes is null");
            return Step.this;
        }
        mCmdBytes = cmdBytes;
        mCmdString = new String(cmdBytes);
        //mFlag = CodecHsurm.getInstance().getFlagByCmdBytes(cmdBytes);
        //if (-1 == mFlag) {
        //    Log.e(TAG, "setCmdBytes > process fail : cmdBytes is invalid =" + ByteUtilsbytes2hex(cmdBytes));
        //}
        return Step.this;
    }

    public int getCmdCode() {
        return mCmdCode;
    }

    public Step setCmdCode(int cmdCode) {
        mCmdCode = cmdCode;
        return Step.this;
    }

    public int getFlag() {
        return mFlag;
    }

    public Step setFlag(int flag) {
        mFlag = flag;
        return Step.this;
    }

    public boolean isProcessSuccess() {
        return isProcessSuccess;
    }

    public Step setProcessSuccess(boolean val) {
        isProcessSuccess = val;
        return Step.this;
    }

    public String getTitle() {
        return mTitle;
    }

    public Step setTitle(String val) {
        mTitle = val;
        return Step.this;
    }

    public String getStatusTitle() {
        return mTitle + (isProcessSuccess() ? "：成功" : ":失败");
    }
}
