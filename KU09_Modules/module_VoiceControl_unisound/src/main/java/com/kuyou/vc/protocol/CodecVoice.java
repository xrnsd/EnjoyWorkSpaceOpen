package com.kuyou.vc.protocol;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import androidx.annotation.NonNull;

import com.kuyou.vc.protocol.basic.IOnParseListener;
import com.kuyou.vc.protocol.info.InfoBase;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import kuyou.common.bytes.BitOperator;
import kuyou.common.protocol.Codec;
import kuyou.common.protocol.Info;
import kuyou.common.bytes.ByteUtils;
import kuyou.common.utils.ClassUtils;

/**
 * action :语音控制解码器[硬件实现]
 * <p>
 * remarks:  <br/>
 * author: wuguoxian <br/>
 * date: 21-1-26 <br/>
 * </p>
 */
public class CodecVoice extends Codec<IOnParseListener> {

    private final static Map<String, InfoBase> sInfoBaseCache = new HashMap<String, InfoBase>();

    public static interface CMD {
        public static final int WAKEUP = 0;
        public static final int CALL_EG = 1;
        public static final int CALL_HOME = 2;
        public static final int CAMERA_OFF = 3;
        public static final int CAMERA_ON = 4;
        public static final int FLASHLIGHT_OFF = 5;
        public static final int FLASHLIGHT_ON = 6;
        public static final int SHOOT = 7;
        public static final int THERMAL_CAMERA_OFF = 8;
        public static final int THERMAL_CAMERA_ON = 9;
        public static final int VOLUME_DOWNLOAD = 10;
        public static final int VOLUME_UP = 11;
        public static final int VOLUME_MINI = 12;
        public static final int VOLUME_MAX = 13;
        public static final int GAS_ON = 14;
        public static final int GAS_OFF = 15;

        public static final int EXIT = 10240;
    }

    private static CodecVoice sMain;

    private CodecVoice() {

    }

    public static CodecVoice getInstance() {
        if (null == sMain)
            sMain = new CodecVoice();
        return sMain;
    }

    public CodecVoice initInfo(Context context, InfoBase.IParseFinishListener listener) {
        Log.d(TAG, "initInfo > ");
        InfoBase info;
        clearCache();
        try {
            for (Class item : ClassUtils.getAllClasses(context, InfoBase.class)) {
                info = (InfoBase) item.newInstance();
                info.setParseFinishListener(listener);
                Log.d(TAG, "initInfo > flag = " + info.getPayloadDef());
                sInfoBaseCache.put(info.getPayloadDef(), info);
            }
        } catch (Exception e) {
            Log.e(TAG, "initInfo > process fail ");
            Log.e(TAG, Log.getStackTraceString(e));
        }
        return CodecVoice.this;
    }

    @Override
    public void clearCache() {
        //super.clearCache();
        sInfoBaseCache.clear();
    }

    @Override
    public void handle(byte[] bytes) {
        final String flag = getPayloadByBytes(bytes);
        if (sInfoBaseCache.containsKey(flag)) {
            if (isBite(sInfoBaseCache.get(flag).getCmdCode())) {
                return;
            }
            sInfoBaseCache.get(flag).parse(bytes, getListener());
            return;
        } else {
            Log.e(TAG, "handle > process fail : flag = " + flag);
        }
        Log.e(TAG, "handle > process fail : 未知指令 = " + ByteUtils.bytes2hex(bytes));
    }

    public void handle(String cmd) {
        Set<String> set = sInfoBaseCache.keySet();
        Iterator<String> it = set.iterator();
        InfoBase info;
        while (it.hasNext()) {
            info = sInfoBaseCache.get(it.next());
            for (String cmdFlag : info.getCmdContext()) {
                if (null != cmdFlag && cmdFlag.equals(cmd)) {
                    info.perform(getListener());
                    return;
                }
            }
        }
        Log.e(TAG, "handle > process fail : 未知指令 = " + cmd);
    }

    public static int getLenByBytes(byte[] bytes) {
        return BitOperator.twoBytesToInteger(Arrays.copyOfRange(bytes, 12, 14));
    }

    public static int getCsLenByBytes(byte[] bytes) {
        return BitOperator.twoBytesToInteger(Arrays.copyOfRange(bytes, 14, 16));
    }

    public static String getPayloadByBytes(byte[] bytes) {
        return new String(Arrays.copyOfRange(bytes, bytes.length - getLenByBytes(bytes), bytes.length));
    }

    @Override
    public void addInfo(Info info) {
        //super.addInfo(info);
        throw new RuntimeException("此接口已禁用");
    }

    @Override
    public int getFlagIndex() {
        throw new RuntimeException("此接口已禁用");
    }

    //模块存在重复吐出指令问题，临时修复
    private HandlerWatchDog mHandlerWatchDog;

    private boolean isBite(int flag) {
        if (null == mHandlerWatchDog) {
            mHandlerWatchDog = new HandlerWatchDog();
        }
        return mHandlerWatchDog.isBite(flag);
    }

    class HandlerWatchDog extends Handler {
        protected final static int MSG_FEED = 9012;
        int mFlag = -1;

        public HandlerWatchDog() {
            super(Looper.getMainLooper());
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            HandlerWatchDog.this.removeMessages(msg.what);
            if (MSG_FEED != msg.what)
                return;
            mFlag = -1;
            Log.d(TAG, "isBite > reset mFlag");
        }

        private void onFeed(int flag) {
            HandlerWatchDog.this.removeMessages(MSG_FEED);
            HandlerWatchDog.this.sendEmptyMessageDelayed(MSG_FEED, 10000);
            mFlag = flag;
            Log.d(TAG, "onFeed > mFlag = " + mFlag);
        }

        public boolean isBite(int flag) {
            if (-1 != mFlag && flag == mFlag && flag > CMD.WAKEUP && flag < CMD.EXIT) {
                Log.e(TAG, "handle > process fail : cmd is repeat > flag = " + flag + " mFlag= " + mFlag);
                return true;
            }
            onFeed(flag);
            return false;
        }
    }
}