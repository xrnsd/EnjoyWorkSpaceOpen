package com.kuyou.jt808.ui;

import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.cuichen.jt808_sdk.sdk.exceptions.SocketManagerException;
import com.cuichen.jt808_sdk.sdk.jt808bean.Header808Bean;
import com.cuichen.jt808_sdk.sdk.jt808bean.JTT808Bean;
import com.cuichen.jt808_sdk.sdk.jt808coding.JTT808Coding;
import com.cuichen.jt808_sdk.sdk.jt808utils.HexUtil;
import com.kuyou.jt808.Jt808Application;
import com.kuyou.jt808.R;
import com.kuyou.jt808.adapter.LogAdapter;
import com.kuyou.jt808.bean.LogBean;
import com.kuyou.jt808.protocol.AudioVideoInfo;
import com.kuyou.jt808.protocol.AuthenticationInfo;
import com.kuyou.jt808.protocol.ImageInfo;
import com.kuyou.jt808.protocol.LocationInfo;
import com.kuyou.jt808.protocol.MsgInfo;
import com.kuyou.jt808.protocol.TextInfo;
import com.kuyou.jt808.utils.L;
import com.kuyou.jt808.utils.TU;
import com.cuichen.jt808_sdk.oksocket.client.sdk.client.ConnectionInfo;
import com.cuichen.jt808_sdk.oksocket.client.sdk.client.OkSocketOptions;
import com.cuichen.jt808_sdk.oksocket.client.sdk.client.action.ISocketActionListener;
import com.cuichen.jt808_sdk.oksocket.core.iocore.interfaces.IPulseSendable;
import com.cuichen.jt808_sdk.oksocket.core.iocore.interfaces.ISendable;
import com.cuichen.jt808_sdk.oksocket.core.pojo.OriginalData;
import com.cuichen.jt808_sdk.sdk.SocketConfig;
import com.cuichen.jt808_sdk.sdk.SocketManagerTest;
import com.cuichen.jt808_sdk.sdk.jt808utils.ByteUtil;
import com.cuichen.jt808_sdk.sdk.jt808utils.HexUtils;

import java.util.ArrayList;

/**
 * action :
 * <p>
 * author: wuguoxian <br/>
 * date: 20-10-27 <br/>
 * <p>
 */
public class BaseActivity extends AppCompatActivity implements ISocketActionListener, Jt808Application.ILiveResultListener {
    private static final String TAG = "BaseActivity";

    protected final static int REQUEST_IMAGE_CAPTURE = 1;

    //暂时存储注册返回的鉴权码
    protected byte[] authCode;
    protected SocketManagerTest socketManager;
    protected OkSocketOptions okSocketOptions;
    protected LogAdapter logAdapter = new LogAdapter();
    private RecyclerView rv;

    protected TextInfo mTextInfo;
    protected AudioVideoInfo mAudioVideoInfo;
    protected ImageInfo mImageInfo;
    protected LocationInfo mLocationInfo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ok_socket);
        init();
    }

    protected void init() {
        Jt808Application.getInstance().setSocketActionListener(this);
        socketManager = Jt808Application.getInstance().getSocketManager();
        okSocketOptions = socketManager.getOption();

        rv = getLogListView();
        LinearLayoutManager manager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        rv.setLayoutManager(manager);
        rv.setAdapter(logAdapter);

        Jt808Application.getInstance().setLiveResultListener(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }

        if (requestCode == REQUEST_IMAGE_CAPTURE) {
            mImageInfo.setShootState(true);
        }
    }

    protected RecyclerView getLogListView() {
        if (null == rv) {
            rv = findViewById(R.id.rv);
        }
        return rv;
    }

    protected void send(byte[] data) {
        if (null == data || data.length <= 0) {
            Log.d("123456", " send cancel > data is null ");
            return;
        }
        socketManager.send(data);
    }

    protected boolean isConnectIng() {
        return socketManager.isConnect();
    }

    protected String getDeviceId() {
        return SocketConfig.getmPhont();
    }

    protected void openPulse() {
        socketManager.openPulse();
    }

    @Override
    public void onSocketIOThreadStart(String action) {
    }

    @Override
    public void onSocketIOThreadShutdown(String action, Exception e) {
        Log.e("123456", Log.getStackTraceString(e));
    }

    @Override
    public void onSocketDisconnection(ConnectionInfo info, String action, Exception e) {
        logPrint("onSocketDisconnection > Connection：连接已断开");
        Log.e("123456", Log.getStackTraceString(e));
    }

    @Override
    public void onSocketConnectionSuccess(ConnectionInfo info, String action) {
        logPrint("Connection：连接成功");
        logPrint("Connection：自动鉴权");
    }

    @Override
    public void onSocketConnectionFailed(ConnectionInfo info, String action, Exception e) {
        logPrint("Connection：连接失败");
        Log.e("123456", Log.getStackTraceString(e));
    }

    @Override
    public void onSocketReadResponse(ConnectionInfo info, String action, OriginalData data) {
        byte[] body = ByteUtil.byteMergerAll(data.getBodyBytes());
        logPrint("Read(原数据):" + HexUtils.formatHexString(body));
        parseJt808Msg(info, action, data);
    }

    @Override
    public void onSocketWriteResponse(ConnectionInfo info, String action, ISendable data) {
        String s = HexUtils.formatHexString(data.parse());
        logPrint("Write:" + s);
    }

    @Override
    public void onPulseSend(ConnectionInfo info, IPulseSendable data) {
        logPrint("PulseSend: 发送心跳成功");
    }

    protected void logPrint(final String log) {
        L.c(log);
        if (Looper.myLooper() == Looper.getMainLooper()) {
            LogBean logBean = new LogBean(System.currentTimeMillis(), log);
            logAdapter.getDataList().add(0, logBean);
            logAdapter.notifyDataSetChanged();
        } else {
            final String threadName = Thread.currentThread().getName();
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    logPrint(threadName + " 线程打印(In Thread):" + log);
                }
            });
        }
    }

    @Override
    public void onLiveResult(int resultCode, String msg) {
        if (null != mAudioVideoInfo)
            send(mAudioVideoInfo.getResultMsgBytes(resultCode, msg));
    }

    protected void play(String text) {
        if (null == text) {
            Log.e("123456", " play fail > text is null ");
            return;
        }
        Jt808Application.getInstance().getTtsServiceManager().play(text);
    }

    /**
     * 获取位置信息
     *
     * @param callback
     */
    private AMapLocationClient mlocationClient;
    private ArrayList<byte[]> locations = new ArrayList();

    protected void startReportLocation(final long interval, Context context, final boolean isBatch) {
        locations.clear();
        mlocationClient = new AMapLocationClient(context);
        AMapLocationClientOption mLocationOption = new AMapLocationClientOption();
        if (interval <= 0) {
            mLocationOption.setOnceLocation(true); //只定位一次
        } else {
            mLocationOption.setOnceLocation(false); //连续定位
            mLocationOption.setInterval(interval);
        }
        //获取最近3s内精度最高的一次定位结果：
        //设置setOnceLocationLatest(boolean b)接口为true，启动定位时SDK会返回最近3s内精度最高的一次定位结果。
        // 如果设置其为true，setOnceLocation(boolean b)接口也会被设置为true，反之不会，默认为false。
        mLocationOption.setOnceLocationLatest(false);
        mlocationClient.setLocationListener(new AMapLocationListener() {
            @Override
            public void onLocationChanged(AMapLocation amapLocation) {
                reportLocation(getApplicationContext(), amapLocation, isBatch);

                if (interval <= 0) {
                    mlocationClient.stopLocation();
                    mlocationClient.onDestroy();
                }
            }
        });
        //设置定位模式为高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        mLocationOption.setLocationCacheEnable(true);
        mlocationClient.setLocationOption(mLocationOption);
        mlocationClient.enableBackgroundLocation(1, new Notification());
        mlocationClient.startLocation();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mlocationClient != null) {
            mlocationClient.stopLocation();
            mlocationClient.onDestroy();
        }
    }

    protected boolean reportLocation(Context context, AMapLocation amapLocation, boolean isBatch) {
        if (null == amapLocation) {
            L.c(" reportLocation >  AMapLocation is null");
            return false;
        }
        if (amapLocation.getErrorCode() != 0) {
            TU.s("ErrCode:" + amapLocation.getErrorCode() + ", errInfo:" + amapLocation.getErrorInfo());
            //显示错误信息ErrCode是错误码，errInfo是错误信息，详见错误码表。
            Log.e("AmapError", "ErrCode:" + amapLocation.getErrorCode() + ", errInfo:" + amapLocation.getErrorInfo());
            return false;
        }
        logPrint(amapLocation.toString());

        if (null == mLocationInfo)
            mLocationInfo = new LocationInfo();
        byte[] bytes = mLocationInfo.reportLocation(amapLocation, isBatch);

        mLocationInfo.setCapOffAlarmFlag(1);//脱帽报警标志位
        mLocationInfo.setSOSAlarmFlag(0);//sos报警标志位
        mLocationInfo.setNearPowerAlarmFlag(1);//近电报警标志位
        mLocationInfo.setEntryAndExitAlarmFlag(0);//进出报警标志位
        mLocationInfo.setFallAlarmFlag(1);//跌倒报警标志位

        send(bytes);
        return true;
    }

    protected void parseJt808Msg(ConnectionInfo info, String action, OriginalData data) {
        byte[] bytes;
        try {
            bytes = JTT808Coding.check808DataThrows(ByteUtil.byteMergerAll(data.getBodyBytes()));
        } catch (SocketManagerException e) {
            e.printStackTrace();
            logPrint("Read:" + e.getMessage());
            return;
        }
        if (null == bytes) {
            return;
        }
        L.c("Read(去除包头尾的7E标识和校验码):" + HexUtils.formatHexString(bytes));

        JTT808Bean bean = JTT808Coding.resolve808(bytes);
        Header808Bean head808 = JTT808Coding.resolve808ToHeader(bytes);
        L.c("ReadHead:" + head808.toString());
        L.c("ReadBody:" + HexUtil.byte2HexStr(data.getBodyBytes()));

        switch (bean.getMsgId()) {
            case 0x8100: //是注册回复得消息
                //authCode = bean.getAuthenticationCode();
                break;
            case 0x8001:
//                if (bean.getReturnMsgId() == 0x0002) { //心跳
//                    Log.d("123456", "----------------------心跳----------------------------");
//                    if (socketManager != null)
//                        socketManager.feedPulse();
//                }
                break;
            case 0x8300: //文本信息下发
                Log.d("123456", "----------------------文本信息下发-----------------------------");
                mTextInfo = new TextInfo();
                mTextInfo.setMsgHandler(MsgInfo.MSG_ID_8300, new MsgInfo.onMsgHandlerTts() {
                    @Override
                    public void onHandlerTts(String text) {
                        Log.d("123456", " public void onHandlerTts   >  " + text);
                        play(text);
                    }
                });
                mTextInfo.parse(bytes);
                break;
            case 0x8F01://平台接收照片应答
                Log.d("123456", "----------------------平台接收照片应答-----------------------------");
                if (null == mImageInfo)
                    mImageInfo = new ImageInfo();
                mImageInfo.setMsgHandler(MsgInfo.MSG_ID_8F01, new MsgInfo.onMsgHandlerTts() {
                    @Override
                    public void onHandlerTts(String text) {
                        play(text);
                    }
                });
                mImageInfo.parse(bytes);
                break;
            case 0x8F02://摄像头立即拍摄命令
                Log.d("123456", "----------------------请立即拍照-----------------------------");
                if (null == mImageInfo)
                    mImageInfo = new ImageInfo();
                //解析成功开始执行
                mImageInfo.setMsgHandler(MsgInfo.MSG_ID_8F02, new MsgInfo.onMsgHandlerTts() {
                    @Override
                    public void onHandlerTts(String text) {
                        play(text);
                        mImageInfo.shootAndUpload(BaseActivity.this, REQUEST_IMAGE_CAPTURE);
                    }
                });
                //执行成功发送回复
                mImageInfo.setMsgHandler(MsgInfo.MSG_ID_0F01, new MsgInfo.onMsgHandler() {
                    @Override
                    public void onHandler(int resultCode) {
                        play("拍照成功上传");
                        socketManager.send(mImageInfo.getResultMsgBytes());
                    }
                });
                if (!mImageInfo.parse(bytes)) {
                    play("拍照指令解析失败");
                }
                break;
            case 0x8F03://平台下发音视频参数
                Log.d("123456", "----------------------平台下发音视频参数-----------------------------");
                mAudioVideoInfo = new AudioVideoInfo();
                mAudioVideoInfo.parse(bytes);
                break;
            default:
                Log.d("123456", "收到其他特殊ID信息,请注意:"
                        + String.format("0x%04x", bean.getMsgId())
                        + "\n ===================================");
                break;
        }
    }
}
