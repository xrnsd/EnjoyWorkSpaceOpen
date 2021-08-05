package com.kuyou.jt808.ui;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.widget.SwitchCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.amap.api.location.AMapLocation;
import com.kuyou.jt808.Jt808Application;
import com.kuyou.jt808.R;
import com.kuyou.jt808.protocol.AudioVideoInfo;
import com.kuyou.jt808.protocol.AuthenticationInfo;
import com.kuyou.jt808.protocol.ImageInfo;
import com.kuyou.jt808.protocol.LocationInfo;
import com.kuyou.jt808.protocol.MsgInfo;
import com.kuyou.jt808.protocol.TextInfo;
import com.kuyou.jt808.utils.L;
import com.cuichen.jt808_sdk.oksocket.client.sdk.client.ConnectionInfo;
import com.cuichen.jt808_sdk.oksocket.client.sdk.client.OkSocketOptions;
import com.cuichen.jt808_sdk.oksocket.client.sdk.client.connection.NoneReconnect;
import com.cuichen.jt808_sdk.oksocket.core.pojo.OriginalData;
import com.cuichen.jt808_sdk.sdk.SocketConfig;
import com.cuichen.jt808_sdk.sdk.exceptions.SocketManagerException;
import com.cuichen.jt808_sdk.sdk.jt808bean.Header808Bean;
import com.cuichen.jt808_sdk.sdk.jt808bean.JTT808Bean;
import com.cuichen.jt808_sdk.sdk.jt808coding.JT808Directive;
import com.cuichen.jt808_sdk.sdk.jt808coding.JTT808Coding;
import com.cuichen.jt808_sdk.sdk.jt808utils.ByteUtil;
import com.cuichen.jt808_sdk.sdk.jt808utils.HexUtil;
import com.cuichen.jt808_sdk.sdk.jt808utils.HexUtils;


import static android.widget.Toast.LENGTH_SHORT;

public class Jt808DebugActivity extends BaseActivity {

    private EditText etIp, etPort;
    private Button btConnect, btSend, btSendPluse, btZhuCe, btAuto, btZhuXiao, btLocation;
    private SwitchCompat switch_reconnect;
    private RecyclerView rv;

    String IpBase = null, PortBase = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_ok_socket);
        super.onCreate(savedInstanceState);
        initViews();
    }

    @Override
    protected RecyclerView getLogListView() {
        return findViewById(R.id.rv);
    }

    private void initViews() {
        //@{ added by wgx Usefulness:
        initSubViews();
        //}@ end wgx

        etIp = findViewById(R.id.etIp);
        etPort = findViewById(R.id.etPort);
        btConnect = findViewById(R.id.lianjie);
        switch_reconnect = findViewById(R.id.switch_reconnect);
        btSendPluse = findViewById(R.id.btSendPluse);
        btZhuCe = findViewById(R.id.btZhuCe);
        btAuto = findViewById(R.id.btAuto);
        btZhuXiao = findViewById(R.id.btZhuXiao);
        btLocation = findViewById(R.id.btLocation);

        etIp.setText(SocketConfig.socketIp);
        etPort.setText(SocketConfig.socketPort);
        switch_reconnect.setChecked(!(okSocketOptions.getReconnectionManager() instanceof NoneReconnect));

        btConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (null != IpBase) {
                    etIp.setText(IpBase);
                    etPort.setText(PortBase);
                }
                Jt808Application.getInstance().connect();
            }
        });
        btConnect.performClick();

        switch_reconnect.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!isChecked) {
                    if (!(socketManager.getManager().getReconnectionManager() instanceof NoneReconnect)) {
                        socketManager.getManager().option(new OkSocketOptions.Builder(socketManager.getManager().getOption()).setReconnectionManager(new NoneReconnect()).build());
                        logPrint("关闭重连管理器");
                    }
                } else {
                    if (socketManager.getManager().getReconnectionManager() instanceof NoneReconnect) {
                        socketManager.getManager().option(
                                new OkSocketOptions.Builder(
                                        socketManager.getManager().getOption()).
                                        setReconnectionManager(
                                                OkSocketOptions.getDefault().getReconnectionManager()).build());
                        logPrint("打开重连管理器");
                    }
                }
            }
        });

        btSendPluse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (socketManager == null || !socketManager.isConnect()) {
                    Toast.makeText(getApplicationContext(), "Unconnected", LENGTH_SHORT).show();
                } else {
                    socketManager.openPulse();
                }
            }
        });

        btZhuCe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (socketManager == null || !socketManager.isConnect()) {
                    Toast.makeText(getApplicationContext(), "Unconnected", LENGTH_SHORT).show();
                } else {
                    byte[] register = JT808Directive.register(SocketConfig.mManufacturerId, SocketConfig.mTerminalModel, SocketConfig.getmTerminalId());
                    byte[] body = JTT808Coding.generate808(0x0100, SocketConfig.getmPhont(), register);
                    socketManager.send((body));
                }
            }
        });

        btAuto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (socketManager == null || !socketManager.isConnect()) {
                    Toast.makeText(getApplicationContext(), "Unconnected", LENGTH_SHORT).show();
                } else {
                    logPrint("发送鉴权信息");
                    send(AuthenticationInfo.getInstance().getAuthenticationMsgBytes());

                    //send(AuthenticationInfo.getInstance().getAuthenticationMsgBytes(authCode));
//                    socketManager.send(new byte[]{
//                            (byte)0x7E ,(byte)0x01 ,(byte)0x02 ,(byte)0x00 ,(byte)0x18 ,(byte)0x60 ,(byte)0x20 ,(byte)0x20 ,
//                            (byte)0x06 ,(byte)0x00 ,(byte)0x03 ,(byte)0x00 ,(byte)0x00 ,(byte)0x34 ,(byte)0x6B ,(byte)0x51 ,
//                            (byte)0x2B ,(byte)0x32 ,(byte)0x32 ,(byte)0x38 ,(byte)0x37 ,(byte)0x49 ,(byte)0x2F ,(byte)0x63 ,
//                            (byte)0x67 ,(byte)0x74 ,(byte)0x2F ,(byte)0x42 ,(byte)0x45 ,(byte)0x50 ,(byte)0x38 ,(byte)0x61 ,
//                            (byte)0x59 ,(byte)0x56 ,(byte)0x77 ,(byte)0x3D ,(byte)0x3D ,(byte)0x1B ,(byte)0x7E
//                    });
//                    socketManager.send(new byte[]{
//                            (byte)0x7E ,(byte)0x01 ,(byte)0x02 ,(byte)0x00 ,(byte)0x18 ,(byte)0x01 ,(byte)0x83 ,(byte)0x39 ,(byte)0x96 ,(byte)0x22 ,(byte)0x22 ,(byte)0x00 ,(byte)0x43 ,(byte)0x7A ,(byte)0x68 ,(byte)0x46 ,(byte)0x38 ,(byte)0x38 ,(byte)0x54 ,(byte)0x67 ,(byte)0x46 ,(byte)0x52 ,(byte)0x39 ,(byte)0x56 ,(byte)0x78 ,(byte)0x31 ,(byte)0x4A ,(byte)0x66 ,(byte)0x47 ,(byte)0x59 ,(byte)0x66 ,(byte)0x30 ,(byte)0x70 ,(byte)0x4E ,(byte)0x51 ,(byte)0x3D ,(byte)0x3D ,(byte)0x2B ,(byte)0x7E
//                    });
                }
            }
        });

        btZhuXiao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (socketManager == null || !socketManager.isConnect()) {
                    Toast.makeText(getApplicationContext(), "Unconnected", LENGTH_SHORT).show();
                } else {
                    byte[] body = JTT808Coding.generate808(0x0003, SocketConfig.getmPhont(), new byte[]{});
                    socketManager.send((body));
                }
            }
        });

        btLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (socketManager == null || !socketManager.isConnect()) {
                    Toast.makeText(getApplicationContext(), "Unconnected", LENGTH_SHORT).show();
                } else {
                    startReportLocation(5000, Jt808DebugActivity.this, false);
                }
            }
        });
    }

    private void initSubViews() {
        findViewById(R.id.btStop).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        findViewById(R.id.connectLocal).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null == IpBase) {
                    IpBase = etIp.getText().toString();
                    PortBase = etPort.getText().toString();
                }
                etIp.setText("10.168.1.5");
                etPort.setText("7611");

                Jt808Application.getInstance().connect(etIp.toString(), Integer.valueOf(etPort.getText().toString()).intValue());
            }
        });

        findViewById(R.id.btVideo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null == mAudioVideoInfo)
                    mAudioVideoInfo = new AudioVideoInfo();
                socketManager.send(mAudioVideoInfo.getRequestOpenVideoParameterMsg());
            }
        });

        findViewById(R.id.btCamera).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mImageInfo = new ImageInfo();
                mImageInfo.shootAndUpload(Jt808DebugActivity.this, REQUEST_IMAGE_CAPTURE);
                //执行成功发送回复
                mImageInfo.setMsgHandler(MsgInfo.MSG_ID_0F01, new MsgInfo.onMsgHandler() {
                    @Override
                    public void onHandler(int resultCode) {
                        play("拍照成功上传");
                        socketManager.send(mImageInfo.getResultMsgBytes());
                    }
                });

//                imageInfo.setMediaId(1950019715);
//                imageInfo.setFileFormatTypeCode(0);
//                imageInfo.setEventType(1);
//                imageInfo.setShootTime(CommitUtils.formatLocalTimeByMilSecond("yyMMddHHmmss"));
//                imageInfo.setImgUriRemote("https://aum-photo-1258630328.cos.ap-nanjing.myqcloud.com/1950019715.jpeg");
//                socketManager.send(imageInfo.getResultMsgBytes());

//                UploadUtil.uploadImage(ImageInfo.SERVER_URL, new File("/sdcard/kuyou/img/1603260218290.jpeg"), SocketConfig.getmPhont(),
//                        new UploadUtil.OnUploadImageListener(){
//                            @Override
//                            public void onUploadFinish(JSONObject jsonResult) {
//                                if (null == jsonResult) {
//                                    return;
//                                }
//                                try{
//                                    if (0 == jsonResult.getInt("code")) {
//                                        Log.d("123456", " onHandler(MSG_ID_0F01) ");
//                                    }
//                                }catch(Exception e){
//                                    Log.e("123456", Log.getStackTraceString(e));
//                                }
//                            }
//                        });
                //play("拍照指令");
            }
        });

        findViewById(R.id.btClean).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logAdapter.getDataList().clear();
                logAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onSocketConnectionSuccess(ConnectionInfo info, String action) {
        super.onSocketConnectionSuccess(info, action);
        btConnect.setText("已连接");
    }

    @Override
    public void onSocketConnectionFailed(ConnectionInfo info, String action, Exception e) {
        super.onSocketConnectionFailed(info, action, e);
        btConnect.setText("连接失败");
    }

    @Override
    public void onSocketDisconnection(ConnectionInfo info, String action, Exception e) {
        super.onSocketDisconnection(info, action, e);
        btConnect.setText("连接已断开");
    }

    @Override
    public void onSocketReadResponse(ConnectionInfo info, String action, OriginalData data) {
        super.onSocketReadResponse(info, action, data);
//        byte[] bytes;
//        try {
//            bytes = JTT808Coding.check808DataThrows(ByteUtil.byteMergerAll(data.getBodyBytes()));
//        } catch (SocketManagerException e) {
//            e.printStackTrace();
//            logPrint("Read:" + e.getMessage());
//            return;
//        }
//        if (null == bytes) {
//            return;
//        }
//        L.c("Read(去除包头尾的7E标识和校验码):" + HexUtils.formatHexString(bytes));
//
//        JTT808Bean bean = JTT808Coding.resolve808(bytes);
//        Header808Bean head808 = JTT808Coding.resolve808ToHeader(bytes);
//        L.c("ReadHead:" + head808.toString());
//        L.c("ReadBody:" + HexUtil.byte2HexStr(data.getBodyBytes()));
    }
}
