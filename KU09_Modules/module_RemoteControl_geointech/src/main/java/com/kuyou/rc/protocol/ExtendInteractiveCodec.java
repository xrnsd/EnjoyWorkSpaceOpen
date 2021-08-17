package com.kuyou.rc.protocol;

import android.content.Context;
import android.util.Log;

import com.kuyou.rc.protocol.base.SicBasic;

import java.util.HashMap;
import java.util.Map;

import kuyou.common.protocol.Codec;
import kuyou.common.utils.ClassUtils;
import kuyou.sdk.jt808.base.exceptions.SocketManagerException;
import kuyou.sdk.jt808.base.jt808bean.JTT808Bean;
import kuyou.sdk.jt808.base.jt808coding.JTT808Coding;
import kuyou.sdk.jt808.oksocket.core.pojo.OriginalData;

/**
 * action :JT808扩展指令编解码器
 * <p>
 * remarks:  <br/>
 * author: wuguoxian <br/>
 * date: 21-8-9 <br/>
 * </p>
 */
public class ExtendInteractiveCodec {
    protected static final String TAG = "com.kuyou.rc.platform > ExtendInteractiveCodec";

    private static ExtendInteractiveCodec sMain;

    private ExtendInteractiveCodec() {
    }

    public static ExtendInteractiveCodec getInstance(Context context) {
        if (null == sMain) {
            sMain = new ExtendInteractiveCodec();
            sMain.mContext = context.getApplicationContext();
        }
        return sMain;
    }

    private Context mContext;

    private Map<Integer, SicBasic> mRequestParserList = new HashMap<Integer, SicBasic>();

    private Map<Integer, SicBasic> mResultBodyList = new HashMap<Integer, SicBasic>();

    private InstructionParserListener mInstructionParserListener;

    private Codec.IAutoLoadAllInfoCallBack mAutoLoadAllInfoCallBack;

    public Context getContext() {
        return mContext;
    }

    public Map<Integer, SicBasic> getRequestParserList() {
        return mRequestParserList;
    }

    public Map<Integer, SicBasic> getResultBodyList() {
        return mResultBodyList;
    }

    public InstructionParserListener getInstructionParserListener() {
        return mInstructionParserListener;
    }

    public ExtendInteractiveCodec setInstructionParserListener(InstructionParserListener instructionParserListener) {
        mInstructionParserListener = instructionParserListener;
        return ExtendInteractiveCodec.this;
    }

    public void handler(OriginalData data) {
        byte[] bytes = null;
        try {
            bytes = JTT808Coding.check808DataThrows(data.getBodyBytes());
        } catch (SocketManagerException e) {
            Log.e(TAG, Log.getStackTraceString(e));
            return;
        }
        if (null == bytes) {
            Log.e(TAG, "handler > process fail : check fail");
            return;
        }

        SicBasic instruction = null;
        JTT808Bean bean = JTT808Coding.resolve808(bytes);

        if (mRequestParserList.size() <= 0) {
            Log.e(TAG, "handler > process fail : mInfoBaseCache is none");
            return;
        }
        instruction = mRequestParserList.get(bean.getMsgId());
        if (null == instruction) {
            //Log.d(TAG, "handler > process fail : instruction is not exist = 0x" + ByteUtils.bytes2Hex(ByteUtils.int2Word(bean.getMsgId())));
            getInstructionParserListener().onRemote2LocalBasic(bean, bytes);
            return;
        }
        instruction.parse(bytes, getInstructionParserListener());
    }

    protected Codec.IAutoLoadAllInfoCallBack getAutoLoadAllInfoCallBack() {
        if (null == mAutoLoadAllInfoCallBack) {
            mAutoLoadAllInfoCallBack = new Codec.IAutoLoadAllInfoCallBack() {
                @Override
                public Class<?> getInfoClass() {
                    return SicBasic.class;
                }

                @Override
                public Context getApplicationContext() {
                    return ExtendInteractiveCodec.this.getContext();
                }
            };
        }
        return mAutoLoadAllInfoCallBack;
    }

    public boolean load() {
        try {
            SicBasic instruction;
            for (Class item : ClassUtils.getAllClasses(getAutoLoadAllInfoCallBack().getApplicationContext(),
                    getAutoLoadAllInfoCallBack().getInfoClass())) {
                instruction = (SicBasic) item.newInstance();
                if (instruction.getMatchEventCode() > 0) {
                    mResultBodyList.put(instruction.getMatchEventCode(), instruction);
                } else {
                    //Log.d(TAG, "load > get up event instruction = " + item);
                }
                if (instruction.getFlag() > 0) {
                    mRequestParserList.put(instruction.getFlag(), instruction);
                } else {
                    //Log.d(TAG, "load > get up cmd instruction = " + item);
                }
            }
            return true;
        } catch (Exception e) {
            Log.e(TAG, Log.getStackTraceString(e));
            Log.e(TAG, "load > process fail : load instruction parser");
        }
        return false;
    }
}
