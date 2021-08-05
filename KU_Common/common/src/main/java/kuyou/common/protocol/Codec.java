package kuyou.common.protocol;

import android.content.Context;
import android.util.Log;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import kuyou.common.protocol.flow.Step;
import kuyou.common.bytes.ByteUtils;
import kuyou.common.utils.ClassUtils;


/**
 * action :协议编解码器[抽象]
 * <p>
 * author: wuguoxian <br/>
 * date: 21-1-12 <br/>
 * <p>
 */
public abstract class Codec<T> {

    protected final String TAG = this.getClass().getSimpleName() + "_123456";

    private final Map<Integer, Step> mStepCache = new HashMap<Integer, Step>();
    private final Map<Integer, Info> mInfoCache = new HashMap<Integer, Info>();

    private T mListener;

    private IAutoLoadAllInfoCallBack mAutoLoadAllInfoCallBack;

    protected abstract int getFlagIndex();

    public byte[] getBodyByCmdCode(final int cmdCode) {
        return null;
    }

    public String getTitleByCmdCode(final int cmdCode) {
        return "未定义指令ID";
    }

    public void addInfo(Info info) {
        mInfoCache.put(info.getCmdCode(), info);
    }

    protected Map<Integer, Info> getInfoCache() {
        return mInfoCache;
    }

    protected Map<Integer, Step> getStepCache() {
        return mStepCache;
    }

    public IAutoLoadAllInfoCallBack getAutoLoadAllInfoCallBack() {
        return mAutoLoadAllInfoCallBack;
    }

    protected void setAutoLoadAllInfoCallBack(IAutoLoadAllInfoCallBack autoLoadAllInfoCallBack) {
        mAutoLoadAllInfoCallBack = autoLoadAllInfoCallBack;
    }

    public Step getStep(final int cmdCode) {
        if (mStepCache.containsKey(cmdCode)) {
            return mStepCache.get(cmdCode);
        }
        Step step = new Step();
        if (mInfoCache.containsKey(cmdCode)) {
            step.setParamByInfo(mInfoCache.get(cmdCode));
        } else {
            step.setCmdBytes(getBodyByCmdCode(cmdCode));
            step.setTitle(getTitleByCmdCode(cmdCode));
        }

        if (ByteUtils.isEmpty(step.getCmdBytes())) {
            Log.e(TAG, "getStep > process fail : invalid cmdCode  = " + cmdCode);
            return null;
        }
        mStepCache.put(cmdCode, step);
        return step;
    }

    public Info getInfo(final int cmdCode) {
        if (0 == mInfoCache.size())
            return null;
        return mInfoCache.get(cmdCode);
    }

    public void clearCache() {
        Set<Integer> set = mStepCache.keySet();
        Iterator<Integer> it = set.iterator();
        while (it.hasNext()) {
            mStepCache.get(it.next()).reset();
        }
        mStepCache.clear();

        set = mInfoCache.keySet();
        it = set.iterator();
        while (it.hasNext()) {
            mInfoCache.get(it.next()).reset();
        }
        mInfoCache.clear();
        setListener(null);
    }

    public T getListener() {
        return mListener;
    }

    public void setListener(T listener) {
        mListener = listener;
    }

    protected boolean loadAllInfo() {
        return loadAllInfo(getAutoLoadAllInfoCallBack());
    }

    public boolean loadAllInfo(IAutoLoadAllInfoCallBack callBack) {
        if (0 != mInfoCache.size()) {
            return true;
        }
        if (null != callBack && null == getAutoLoadAllInfoCallBack()) {
            setAutoLoadAllInfoCallBack(callBack);
        }
        if (null == getAutoLoadAllInfoCallBack() || null == getAutoLoadAllInfoCallBack().getInfoClass()) {
            Log.e(TAG, "loadAllInfo > process fail : callBack is null or invalid");
            return false;
        }
        try {
            Info info;
            for (Class item : ClassUtils.getAllClasses(getAutoLoadAllInfoCallBack().getApplicationContext(),
                    getAutoLoadAllInfoCallBack().getInfoClass())) {
                //Log.d(TAG, "handle > auto load all info = " + item);
                info = (Info) item.newInstance();
                Codec.this.addInfo(info);
            }
        } catch (Exception e) {
            Log.e(TAG, Log.getStackTraceString(e));
            return false;
        }
        return true;
    }

    public void handle(byte[] data) {
        if (!loadAllInfo(getAutoLoadAllInfoCallBack())) {
            return;
        }
        final int flag = getFlagByCmdBytes(data);
        Set<Integer> set = mInfoCache.keySet();
        Iterator<Integer> it = set.iterator();
        Info info = null;
        while (it.hasNext()) {
            info = mInfoCache.get(it.next());
            if (flag == info.getFlag()) {
                Log.d(TAG, "handle > parse data = " + ByteUtils.bytes2hex(data));
                info.parse(data, getListener());
                return;
            }
        }
        Log.e(TAG, new StringBuilder("handle > process fail : 未知指令 = ").append(ByteUtils.bytes2hex(data))
                .append("\n flag = ").append(flag)
                .toString());
    }

    public int getFlagByCmdBytes(byte[] cmd) {
        if (null == cmd || cmd.length <= getFlagIndex())
            return -1;
        return ByteUtils.byte2Int(cmd[getFlagIndex()]);
    }

    public static interface IAutoLoadAllInfoCallBack {
        public Class<?> getInfoClass();

        public Context getApplicationContext();
    }
}
