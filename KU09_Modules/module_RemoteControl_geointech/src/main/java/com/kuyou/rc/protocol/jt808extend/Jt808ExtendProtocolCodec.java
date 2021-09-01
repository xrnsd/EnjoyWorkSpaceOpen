package com.kuyou.rc.protocol.jt808extend;

import android.content.Context;
import android.util.Log;

import com.kuyou.rc.protocol.jt808extend.basic.InstructionParserListener;
import com.kuyou.rc.protocol.jt808extend.basic.SicBasic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kuyou.common.bytes.ByteUtils;
import kuyou.common.ku09.config.IDeviceConfig;
import kuyou.common.protocol.Codec;
import kuyou.common.utils.ClassUtils;
import kuyou.sdk.jt808.basic.exceptions.SocketManagerException;
import kuyou.sdk.jt808.basic.jt808bean.JTT808Bean;
import kuyou.sdk.jt808.basic.jt808coding.JTT808Coding;
import kuyou.sdk.jt808.oksocket.core.pojo.OriginalData;

/**
 * action :JT808扩展指令编解码器
 * <p>
 * remarks:  <br/>
 * author: wuguoxian <br/>
 * date: 21-8-9 <br/>
 * </p>
 */
public class Jt808ExtendProtocolCodec {
    protected static final String TAG = "com.kuyou.rc.protocol > Jt808ExtendProtocolCodec";

    private static Jt808ExtendProtocolCodec sMain;

    private Jt808ExtendProtocolCodec() {
    }

    public static Jt808ExtendProtocolCodec getInstance(Context context) {
        if (null == sMain) {
            sMain = new Jt808ExtendProtocolCodec();
            sMain.mContext = context.getApplicationContext();
        }
        return sMain;
    }

    private Context mContext;

    private Map<Integer, SicBasic> mRequestParserList = new HashMap<Integer, SicBasic>();

    private List<SicBasic> mSicBasicList = new ArrayList<>();

    private InstructionParserListener mInstructionParserListener;

    private Codec.IAutoLoadAllInfoCallBack mAutoLoadAllInfoCallBack;

    public Context getContext() {
        return mContext;
    }

    public Map<Integer, SicBasic> getRequestParserList() {
        return mRequestParserList;
    }

    public List<SicBasic> getSicBasicList() {
        return mSicBasicList;
    }

    public InstructionParserListener getInstructionParserListener() {
        return mInstructionParserListener;
    }

    public Jt808ExtendProtocolCodec setInstructionParserListener(InstructionParserListener instructionParserListener) {
        mInstructionParserListener = instructionParserListener;
        return Jt808ExtendProtocolCodec.this;
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
        Log.i(TAG, "handler > " + ByteUtils.bytes2hex(bytes));

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
                    return Jt808ExtendProtocolCodec.this.getContext();
                }
            };
        }
        return mAutoLoadAllInfoCallBack;
    }

    public boolean load(IDeviceConfig config) {
        try {
            SicBasic instruction;
            for (Class item : ClassUtils.getAllClasses(getAutoLoadAllInfoCallBack().getApplicationContext(),
                    getAutoLoadAllInfoCallBack().getInfoClass())) {
                instruction = (SicBasic) item.newInstance();
                instruction.setDeviceConfig(config);
                mSicBasicList.add(instruction);
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
