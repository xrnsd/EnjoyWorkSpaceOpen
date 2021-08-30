package com.kuyou.avc.ui.basic;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.hardware.Camera.CameraInfo;
import android.util.Log;
import android.view.KeyEvent;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import com.kuyou.avc.R;
import com.kuyou.avc.ui.thermal.AudioOutput;
import com.kuyou.avc.ui.thermal.CameraView;
import com.peergine.android.livemulti.pgLibLiveMultiCapture;
import com.peergine.android.livemulti.pgLibLiveMultiError;
import com.peergine.plugin.android.pgDevAudioConvert;
import com.peergine.plugin.android.pgDevAudioOut;
import com.peergine.plugin.android.pgDevVideoIn;
import com.peergine.plugin.lib.pgLibJNINode;

import java.io.File;

public abstract class MultiCapExter extends AVCActivity implements ActivityCompat.OnRequestPermissionsResultCallback {

    private EditText m_editServer;
    private EditText m_editDevID;
    private Button m_btnStart;
    private Button m_btnStop;
    private Button m_btnAccept;
    private Button m_btnRefuse;

    public static TextView m_sTransferInfo;
    public static TextView m_sStatusInfo;
    public static TextView m_sDebug;

    private String m_sReplyPeer = "";
    private String m_sReplyFile = "";
    private String m_sTransferPeer = "";
    private boolean m_bIsReplying = false;

    String m_sServerAddr = "connect.peergine.com:7781";
    String m_sDevID = "";

    pgLibLiveMultiCapture m_Live = new pgLibLiveMultiCapture();
    LinearLayout m_View = null;
    CameraView m_CameraView = null;
    SurfaceView m_Wnd = null;

    private AudioOutput m_AudioOut = new AudioOutput();
    private byte[] m_byAudioCvt = null;
    private int m_iAudioCvt = -1;

    private boolean m_bTransfering = false;

    private boolean m_bExtAudioOut = true;  // If play audio output at external p2p SDK.
    private boolean m_bAudioCvt8K = false; // If convert audio output to 8K sample rate.
    private int m_iAudioPlaySilent = 1; // Force to play silent data.

    private boolean m_bExtVideoIn = true; // If Capture video input at external p2p SDK.

    private int m_iVideoMode = 10;
    private String m_sVideoParamHD = "(Code){3}(Mode){10}(Rate){66}(BitRate){1000}(Delay){500}(SendCache){1}";
    private String m_sVideoParamSD = "(Code){3}(Mode){3}(Rate){66}(BitRate){500}(Delay){300}(SendCache){1}";
    private String m_sVideoParamQD = "(Code){3}(Mode){2}(Rate){66}(BitRate){300}(Delay){200}(SendCache){1}";

    public void setTransfering(boolean bTransfering) {
        m_btnRefuse.setEnabled(bTransfering);
        m_bTransfering = bTransfering;
    }

    public boolean getTransfering() {
        return m_bTransfering;
    }

    //MyPermission m_myPerm = new MyPermission();

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        //m_myPerm.onResult(this, requestCode, permissions[0], grantResults[0]);
    }

    private pgLibLiveMultiCapture.OnEventListener m_OnEvent = new pgLibLiveMultiCapture.OnEventListener() {

        @Override
        public void event(String sAct, String sData, String sRenID) {
            // TODO Auto-generated method stub
            if (sAct.equals("VideoStatus")) {
                // Video status report
            } else if (sAct.equals("Notify")) {
                // Receive the notify from capture side
                String sInfo = "Receive notify: data=" + sData;
                Toast.makeText(MultiCapExter.this, sInfo, Toast.LENGTH_SHORT).show();
            } else if (sAct.equals("RenderJoin")) {
                // A render join
                String sInfo = "Render join: render=" + sRenID;
                Toast.makeText(MultiCapExter.this, sInfo, Toast.LENGTH_SHORT).show();

                int i = 0;
                while (true) {
                    String sRenID1 = m_Live.RenderEnum(i);
                    if (sRenID1.equals("")) {
                        break;
                    }
                    Log.d("pgLiveCapture", "RenderEnum: Index=" + i + ", RenID=" + sRenID1);
                    i++;
                }
            } else if (sAct.equals("RenderLeave")) {
                // A render leave
                String sInfo = "Render leave: render=" + sRenID;
                Toast.makeText(MultiCapExter.this, sInfo, Toast.LENGTH_SHORT).show();

                int i = 0;
                while (true) {
                    String sRenID1 = m_Live.RenderEnum(i);
                    if (sRenID1.equals("")) {
                        break;
                    }
                    Log.d("pgLiveCapture", "RenderEnum: Index=" + i + ", RenID=" + sRenID1);
                    i++;
                }
            } else if (sAct.equals("Message")) {
                // Receive the message from render or capture
                String sInfo = "Receive msg: data=" + sData + ", render=" + sRenID;
                Toast.makeText(MultiCapExter.this, sInfo, Toast.LENGTH_SHORT).show();

                LiveControl(sData);
            } else if (sAct.equals("Login")) {
                // Login reply
                if (sData.equals("0")) {
                    String sInfo = "Login success";
                    Toast.makeText(MultiCapExter.this, sInfo, Toast.LENGTH_SHORT).show();
                } else {
                    String sInfo = "Login failed, error=" + sData;
                    Toast.makeText(MultiCapExter.this, sInfo, Toast.LENGTH_SHORT).show();
                }
            } else if (sAct.equals("Logout")) {
                // Logout
                String sInfo = "Logout";
                Toast.makeText(MultiCapExter.this, sInfo, Toast.LENGTH_SHORT).show();
            } else if (sAct.equals("Connect")) {
                // Connect to capture
                String sInfo = "Connect to capture";
                Toast.makeText(MultiCapExter.this, sInfo, Toast.LENGTH_SHORT).show();
            } else if (sAct.equals("Disconnect")) {
                // Disconnect from capture
                String sInfo = "Diconnect from capture";
                Toast.makeText(MultiCapExter.this, sInfo, Toast.LENGTH_SHORT).show();
            } else if (sAct.equals("Offline")) {
                // The capture is offline.
                String sInfo = "Capture offline";
                Toast.makeText(MultiCapExter.this, sInfo, Toast.LENGTH_SHORT).show();
            } else if (sAct.equals("LanScanResult")) {
                // Lan scan result.
                String sInfo = "Lan scan result: " + sData;
                Toast.makeText(MultiCapExter.this, sInfo, Toast.LENGTH_SHORT).show();
            } else if (sAct.equals("ForwardAllocReply")) {
                String sInfo = "Forward alloc relpy: error=" + sData;
                Toast.makeText(MultiCapExter.this, sInfo, Toast.LENGTH_SHORT).show();
            } else if (sAct.equals("ForwardFreeReply")) {
                String sInfo = "Forward free relpy: error=" + sData;
                Toast.makeText(MultiCapExter.this, sInfo, Toast.LENGTH_SHORT).show();
            } else if (sAct.equals("VideoCamera")) {
                String sInfo = "The picture is save to: " + sData;
                Toast.makeText(MultiCapExter.this, sInfo, Toast.LENGTH_SHORT).show();
            } else if (sAct.equals("FilePutRequest")) {
                FilePutRequest(sData, sRenID);
            } else if (sAct.equals("FileGetRequest")) {
                FileGetRequest(sData, sRenID);
            } else if (sAct.equals("FileAccept")) {
                String sInfo = "File accept: " + sData;
                Toast.makeText(MultiCapExter.this, sInfo, Toast.LENGTH_SHORT).show();
            } else if (sAct.equals("FileReject")) {
                FileReject();
            } else if (sAct.equals("FileAbort")) {
                // 取消 上传或下载
                FileAbort();
            } else if (sAct.equals("FileFinish")) {
                // 文件传输完毕
                FileFinish();
            } else if (sAct.equals("FileProgress")) {
                FileProgress(sData);
            } else if (sAct.equals("SvrNotify")) {
                String sInfo = "Receive server notify: " + sData;
                Toast.makeText(MultiCapExter.this, sInfo, Toast.LENGTH_SHORT).show();
            }

            Log.d("pgLiveMultiCapture", "OnEvent: Act=" + sAct + ", Data=" + sData
                    + ", Render=" + sRenID);
        }
    };

    private pgDevAudioOut.OnCallback m_AudioOutCB = new pgDevAudioOut.OnCallback() {

        @Override
        public int Open(int iSpeakerNO, int iSampleBits, int iSampleRate, int iChannels, int iPackBytes) {
            // TODO Auto-generated method stub
            Log.d("pgLiveMultiCapture", "m_AudioOutCB.Open: iSpeakerNO=" + iSpeakerNO
                    + ", iSampleBits=" + iSampleBits + ", iSampleRate=" + iSampleRate + ", iPackBytes=" + iPackBytes);

            if (m_bAudioCvt8K) {
                m_byAudioCvt = new byte[2048];

                m_iAudioCvt = pgDevAudioConvert.Alloc(1,
                        pgDevAudioConvert.PG_DEV_AUDIO_CVT_FMT_PCM16, 8000, 320);
                if (m_iAudioCvt < 0) {
                    Log.d("pgLiveMultiCapture", "m_AudioOutCB.Open: alloc failed");
                    return -1;
                }

                int iDevID = m_AudioOut.Open(iSpeakerNO, iSampleBits, 8000, iChannels, 640);
                if (iDevID < 0) {
                    Log.d("pgLiveMultiCapture", "m_AudioOutCB.Open: open failed");
                    pgDevAudioConvert.Free(m_iAudioCvt);
                    m_iAudioCvt = -1;
                    return -1;
                }

                pgDevAudioOut.PlaySilent(iSpeakerNO, m_iAudioPlaySilent);

                Log.d("pgLiveMultiCapture", "m_AudioOutCB.Open: iDevID=" + iDevID);
                return iDevID;
            } else {
                int iDevID = m_AudioOut.Open(iSpeakerNO, iSampleBits, iSampleRate, iChannels, iPackBytes);
                if (iDevID < 0) {
                    Log.d("pgLiveMultiCapture", "m_AudioOutCB.Open: open failed");
                    return -1;
                }

                pgDevAudioOut.PlaySilent(iSpeakerNO, m_iAudioPlaySilent);

                Log.d("pgLiveMultiCapture", "m_AudioOutCB.Open: iDevID=" + iDevID);
                return iDevID;
            }
        }

        @Override
        public int Play(int iDevID, byte[] byData, int iFormat) {
            // TODO Auto-generated method stub
            //Log.d("pgLiveMultiCapture", "m_AudioOutCB.Play: iDevID=" + iDevID + ", length=" + byData.length);

            if (m_bAudioCvt8K) {
                int iDataSize = byData.length;
                if (pgDevAudioConvert.Push(m_iAudioCvt,
                        pgDevAudioConvert.PG_DEV_AUDIO_CVT_FMT_PCM16, byData, 0, iDataSize) < 0) {
                    Log.d("pgLiveMultiCapture", "m_AudioOutCB.Play: Push failed, m_iAudioCvt=" + m_iAudioCvt);
                    return -1;
                }

                iDataSize = pgDevAudioConvert.Pop(m_iAudioCvt, m_byAudioCvt, 0, m_byAudioCvt.length);
                if (iDataSize < 0) {
                    Log.d("pgLiveMultiCapture", "m_AudioOutCB.Play: Pop failed, m_iAudioCvt=" + m_iAudioCvt);
                    return -1;
                }
                if (iDataSize == 0) {
                    Log.d("pgLiveMultiCapture", "m_AudioOutCB.Play: Pop pending");
                    return byData.length;
                }

                if (m_AudioOut.Write(m_byAudioCvt, iDataSize) <= 0) {
                    Log.d("pgLiveMultiCapture", "m_AudioOutCB.Play: Write failed");
                    return -1;
                }

                //Log.d("pgLiveMultiCapture", "m_AudioOutCB.Play: iDevID=" + iDevID + ", iDataSize=" + iDataSize);
                return byData.length;
            } else {
                if (m_AudioOut.Write(byData, byData.length) <= 0) {
                    Log.d("pgLiveMultiCapture", "m_AudioOutCB.Play: Write failed");
                    return -1;
                }

                //dumpbuffer(byData);

                //Log.d("pgLiveMultiCapture", "m_AudioOutCB.Play: iDevID=" + iDevID + ", iDataSize=" + iDataSize);
                return byData.length;
            }
        }

        @Override
        public void Close(int iDevID) {
            // TODO Auto-generated method stub
            if (m_bAudioCvt8K) {
                m_AudioOut.Close();
                if (m_iAudioCvt > 0) {
                    pgDevAudioConvert.Free(m_iAudioCvt);
                    m_iAudioCvt = -1;
                }

                m_byAudioCvt = null;
                Log.d("pgLiveMultiCapture", "m_AudioOutCB.Close: iDevID=" + iDevID);
            } else {
                m_AudioOut.Close();
                Log.d("pgLiveMultiCapture", "m_AudioOutCB.Close: iDevID=" + iDevID);
            }
        }
    };

    public void dumpbuffer(byte[] byData) {
        int iInd = 0;
        while (iInd < byData.length) {
            String sLine = "";
            for (int iInd1 = 0; (iInd1 < 16 && iInd < byData.length); iInd1++, iInd++) {
                sLine += String.format("%02X ", byData[iInd]);
            }
            Log.d("pgLiveCapExter", "MainActivity.dumpbuffer: " + sLine);
        }
    }

    public pgDevVideoIn.OnCallback m_oVideoInCB = new pgDevVideoIn.OnCallback() {

        @Override
        public int Open(int iDevNO, int iPixBytes, int iWidth, int iHeight,
                        int iBitRate, int iFrmRate, int iKeyFrmRate) {
            // TODO Auto-generated method stub
            Log.d("pgLiveCapExter", "pgDevVideoIn.OnCallback.Open");

            // Enable assem h264/h265 frames in SDK.
            //pgDevVideoIn.SetParam(iDevNO, pgDevVideoIn.PG_DEV_VIDEO_IN_PARAM_ASSEM_FRAME, 1);

            // The iDevID is '1234'.
            int iDevID = 1234;
            if (!m_CameraView.Start(iDevID, iDevNO, iWidth, iHeight, iBitRate, iFrmRate, iKeyFrmRate)) {
                return -1;
            }

            return iDevID;
        }

        @Override
        public void Close(int iDevID) {
            // TODO Auto-generated method stub

            m_CameraView.Stop();
            Log.d("pgLiveCapExter", "pgDevVideoIn.OnCallback.Close");
        }

        @Override
        public void Ctrl(int iDevID, int iCtrl, int iParam) {
            // TODO Auto-generated method stub
            Log.d("pgLiveCapExter", "pgDevVideoIn.OnCallback.Ctrl");

            m_CameraView.Ctrl(iDevID, iCtrl, iParam);
        }
    };

    private void FilePutRequest(String sDate, String sPeer) {
        String file = getcontent(sDate, "peerpath");

        m_sReplyFile = file;
        m_sReplyPeer = sPeer;
        m_bIsReplying = true;

        String info = "是否接受" + sPeer + "上传的文件" + file + " ...";
        m_sTransferInfo.setText(info);

        m_btnAccept.setEnabled(true);
        m_btnRefuse.setEnabled(true);
    }

    private void FileGetRequest(String sDate, String sPeer) {
        String file = getcontent(sDate, "peerpath");

        m_sReplyFile = file;
        m_sReplyPeer = sPeer;
        m_bIsReplying = true;

        String info = "是否接受" + sPeer + "下载文件(" + file + ")的请求...";
        m_sTransferInfo.setText(info);

        m_btnAccept.setEnabled(true);
        m_btnRefuse.setEnabled(true);
    }

    private void FileProgress(String sData) {
        String sPath = getcontent(sData, "path");
        String sReqSize = getcontent(sData, "total");
        String sCurSize = getcontent(sData, "position");
        m_sStatusInfo.setText("本地文件路径:" + sPath + " 已传输大小:" + sCurSize + "/文件总大小:" + sReqSize);
    }

    private void FileReject() {
        m_btnRefuse.setEnabled(false);
        m_btnAccept.setEnabled(false);
        setTransfering(false);
        m_sTransferInfo.setText("对方拒绝了文件传输...");
    }

    private void FileAbort() {
        m_btnRefuse.setEnabled(false);
        m_btnAccept.setEnabled(false);
        setTransfering(false);
        m_sTransferInfo.setText("文件传输中断了...");
    }

    private void FileFinish() {
        m_btnRefuse.setEnabled(false);
        setTransfering(false);
        m_sTransferInfo.setText("文件传输完毕...");
    }

    public static void outString(String str) {
        if (MultiCapExter.m_sDebug.getText().length() > 600) {
            MultiCapExter.m_sDebug.setText("");
        }
        MultiCapExter.m_sDebug.setText(MultiCapExter.m_sDebug.getText() + "  " + str);
    }

    private boolean CheckPlugin() {
        if (pgLibJNINode.Initialize(this)) {
            pgLibJNINode.Clean();
            return true;
        } else {
            Alert("Error", "Please import 'pgPluginLib' peergine middle ware!");
            return false;
        }
    }

    @Override
    protected void initViews() {
        super.initViews();

        // LinearLayout layoutinfo = (LinearLayout)
        // findViewById(R.id.layoutTransferInfo);
        m_editServer = (EditText) findViewById(R.id.editServer);
        m_editDevID = (EditText) findViewById(R.id.editDevID);

        m_sTransferInfo = (TextView) findViewById(R.id.TransferInfo);
        m_sStatusInfo = (TextView) findViewById(R.id.StatusInfo);
        m_sTransferInfo.setText("您未进行任何操作!");
        m_sStatusInfo.setText("流信息");
        m_sDebug = (TextView) findViewById(R.id.debug);
        m_btnStart = (Button) findViewById(R.id.btnStart);
        m_btnStart.setOnClickListener(m_OnClink);
        m_btnStop = (Button) findViewById(R.id.btnStop);
        m_btnStop.setOnClickListener(m_OnClink);

        LinearLayout layoutGet = (LinearLayout) findViewById(R.id.layoutSelectBtn);
        m_btnAccept = (Button) layoutGet.findViewById(R.id.btnAccept);
        m_btnAccept.setOnClickListener(m_OnClink);
        m_btnRefuse = (Button) layoutGet.findViewById(R.id.btnRefuse);
        m_btnRefuse.setOnClickListener(m_OnClink);
        m_btnAccept.setEnabled(false);
        m_btnRefuse.setEnabled(false);

        if (!CheckPlugin()) {
            return;
        }

        m_editServer.setText(m_sServerAddr);
        m_editDevID.requestFocus();

        m_Live.SetEventListener(m_OnEvent);

        String[] sPermList = {Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        String[] sTextList = {"摄像头", "麦克风", "写存储"};
        //m_myPerm.Request(this, sPermList, sTextList);
    }

    public void onDestroy() {
        LiveStop();
        super.onDestroy();
    }

    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            Log.d("pgLiveCapture", "onKeyDown, KEYCODE_BACK");
            ExitDialog();
            return true;
        }
        return super.onKeyUp(keyCode, event);
    }

    private DialogInterface.OnClickListener m_DlgClick = new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int which) {
            if (which == AlertDialog.BUTTON_POSITIVE) {
                LiveStop();
                android.os.Process.killProcess(android.os.Process.myPid());
            }
        }
    };

    public void ExitDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirm");
        builder.setMessage("Are you sure to exit?");
        builder.setPositiveButton("YES", m_DlgClick);
        builder.setNegativeButton("NO", m_DlgClick);
        builder.show();
    }

    public void Alert(String sTitle, String sMsg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(sTitle);
        builder.setMessage(sMsg);
        builder.setPositiveButton("OK", null);
        builder.show();
    }

    private void LiveStart() {
        if (m_bExtVideoIn) {
            if (m_CameraView != null) {
                return;
            }
        } else {
            if (m_Wnd != null) {
                return;
            }
        }

        String sServerAddr = m_editServer.getText().toString();
        if (sServerAddr.equals("")) {
            Alert("Error", "LiveStart: The server address is empty!");
            return;
        }

        String sInitParam = "(Debug){1}";
        if (m_bExtAudioOut) {
            sInitParam += "(AudioOutExternal){1}";
        }

        if (m_bExtVideoIn) {
            sInitParam += "(VideoInExternal){1}";
        }

        m_sDevID = m_editDevID.getText().toString();
        int iErr = m_Live.Initialize(m_sDevID, "", sServerAddr, "", 3, sInitParam, this);
        if (iErr != 0) {
            Log.d("pgLiveMultiCapture", "LiveStart: Live.Initialize failed! iErr=" + iErr);
            Alert("Error", "LiveStart: Live.Initialize failed! iErr=" + iErr);
            return;
        }

        m_View = (LinearLayout) findViewById(R.id.layoutVideo);

        // vertical
        boolean bVertical = true;

        if (m_bExtVideoIn) {
            // Set audio output callback.
            pgDevAudioOut.SetCallback(m_AudioOutCB);
            pgDevVideoIn.SetCallback(m_oVideoInCB);
            m_CameraView = new CameraView(this);
            m_CameraView.Initialize();
            m_CameraView.setVisibility(View.GONE);
            m_View.addView(m_CameraView);
        } else {
            m_Wnd = m_Live.CameraViewGet();
            m_Wnd.setVisibility(View.GONE);
            m_View.addView(m_Wnd);
        }

        //SetVolumeGate(1);
        //SetMobileAec(0);

        // (Code){3}: H.264
        // (Mode){3}: 640 x 480
        // (Rate){66}: 1000 / 66 = 15fps
        // (BitRate){600}: 600Kbps

        String sVideoParam = "";
        if (m_iVideoMode == 10) {
            sVideoParam = m_sVideoParamHD;
        } else if (m_iVideoMode == 3) {
            sVideoParam = m_sVideoParamSD;
        } else if (m_iVideoMode == 2) {
            sVideoParam = m_sVideoParamQD;
        }

        if (bVertical) {
            sVideoParam += "(Portrait){1}";
        }

        m_Live.VideoStart(0, sVideoParam, null);

        // Enable full echo cancel.
        String sAudioParam = "";
        //String sAudioParam = "(AecConfig){1,-1,-1,-1,-1}";
        //String sAudioParam = "(EchoCancel){0}";
        m_Live.AudioStart(0, sAudioParam);
    }

    private void LiveStop() {
        m_Live.AudioStop(0);
        m_Live.VideoStop(0);

        if (m_bExtVideoIn) {
            if (m_CameraView != null) {
                m_View.removeView(m_CameraView);
                m_View = null;
                m_CameraView = null;
                pgDevAudioOut.SetCallback(null);
                pgDevVideoIn.SetCallback(null);
                m_Live.Clean();
            }
        } else {
            if (m_Wnd != null) {
                m_View.removeView(m_Wnd);
                m_View = null;
                m_Live.CameraViewRelease();
                m_Live.Clean();
            }
        }
    }

    private void LiveControl(String sData) {
        if (sData.equals("VideoStart")) {
            String sVideoParam = "";
            if (m_iVideoMode == 10) {
                sVideoParam = m_sVideoParamHD;
            } else if (m_iVideoMode == 3) {
                sVideoParam = m_sVideoParamSD;
            } else if (m_iVideoMode == 2) {
                sVideoParam = m_sVideoParamQD;
            }
            m_Live.VideoStart(0, sVideoParam, null);
        } else if (sData.equals("VideoStop")) {
            m_Live.VideoStop(0);
        } else if (sData.equals("VideoHD")) {
            m_iVideoMode = 10;
            m_Live.VideoParam(0, m_sVideoParamHD);
        } else if (sData.equals("VideoSD")) {
            m_iVideoMode = 3;
            m_Live.VideoParam(0, m_sVideoParamSD);
        } else if (sData.equals("VideoQD")) {
            m_iVideoMode = 2;
            m_Live.VideoParam(0, m_sVideoParamQD);
        } else if (sData.equals("AudioStart")) {
            String sAudioParam = "";
            m_Live.AudioStart(0, sAudioParam);
        } else if (sData.equals("AudioStop")) {
            m_Live.AudioStop(0);
        }
    }

    private View.OnClickListener m_OnClink = new View.OnClickListener() {
        // Control clicked
        public void onClick(View args0) {
            switch (args0.getId()) {
                case R.id.btnStart:
                    LiveStart();
                    break;

                case R.id.btnStop:
                    outString(getTransfering() + "" + m_bIsReplying + "");
                    if (getTransfering()) {
                        btnRefuse();
                        setTransfering(false);
                    }
                    LiveStop();
                    break;

                case R.id.btnAccept:
                    btnAccept();
                    break;

                case R.id.btnRefuse:
                    btnRefuse();
                    break;

                default:
                    break;
            }
        }
    };

    private void btnAccept() {
        if (m_bIsReplying) {
            String sPath = "/sdcard/Download/pgtest";
            int iErr = m_Live.FileAccept(m_sReplyPeer, sPath);
            if (iErr != 0) {
                Log.d("pgLiveMultiCapture", "btnAccept: iErr = " + iErr);
            } else {
                m_bIsReplying = false;
                m_sTransferPeer = m_sReplyPeer;
                FileReplyStatus(m_sReplyPeer, m_sReplyFile, 0);
            }
        }
    }

    private void btnRefuse() {
        int iErr = 0;
        if (m_bIsReplying) {
            iErr = m_Live.FileReject(m_sReplyPeer, pgLibLiveMultiError.PG_ERR_Reject);
        } else {
            iErr = m_Live.FileCancel(m_sTransferPeer);
        }

        if (iErr != 0) {
            Log.d("pgLiveMultiCapture", "btnRefuse: iErr = " + iErr);
        } else {
            m_bIsReplying = false;
            FileReplyStatus(m_sReplyPeer, m_sReplyFile, 1);
        }
    }

    private void FileReplyStatus(String sPeer, String sPeerPath, int iAction) {
        if (iAction != 1) {
            setTransfering(true);
            m_btnRefuse.setEnabled(true);
            m_btnAccept.setEnabled(false);
            String str = "您正在进行文件传输: " + sPeerPath + "...";
            m_sTransferInfo.setText(str);
        } else {
            setTransfering(false);
            m_btnAccept.setEnabled(false);
            m_btnRefuse.setEnabled(false);
            String str = "您拒绝了文件传输: " + sPeerPath + "...";
            m_sTransferInfo.setText(str);
        }
    }

    private String PathGetName(String _sPath) {
        return _sPath.substring(_sPath.lastIndexOf(File.separator)
                + File.separator.toString().length());
    }

    public String getcontent(String sData, String key) {
        String result = "";
        String[] values = sData.split("&");
        for (int i = 0; i < values.length; i++) {
            String[] entry = values[i].split("=");
            if (entry[0].equals(key)) {
                result = entry[1];
                break;
            }
        }
        return result;
    }

    private boolean setFlashlightEnabled(boolean isEnable) {
        pgLibJNINode Node = m_Live.GetNode();
        if (Node != null) {
            if (Node.ObjectAdd("_vTemp", "PG_CLASS_Video", "", 0)) {
                Node.ObjectRequest("_vTemp", 2, "(Item){14}(Value){" + (isEnable ? 1 : 0) + "}", "");
                Node.ObjectDelete("_vTemp");
                return true;
            }
        }
        return false;
    }

    public boolean SetAudioSuppress(int iDebug, int iDelay, int iKeep) {
        pgLibJNINode Node = m_Live.GetNode();
        if (Node != null) {
            if (Node.ObjectAdd("_AudioTemp", "PG_CLASS_Audio", "", 0)) {
                String sValue = "(Debug){" + iDebug + "}(Delay){" + iDelay + "}(Keep){" + iKeep + "}";
                Node.ObjectRequest("_AudioTemp", 2, "(Item){0}(Value){" + Node.omlEncode(sValue) + "}", "");
                Node.ObjectDelete("_AudioTemp");
                return true;
            }
        }
        return false;
    }

    public boolean SetVolumeGate(int iVolumeGate) {
        pgLibJNINode Node = m_Live.GetNode();
        if (Node != null) {
            if (Node.ObjectAdd("_aTemp", "PG_CLASS_Audio", "", 0)) {
                String sValue = Node.omlEncode("(TailLen){0}(VolGate){" + iVolumeGate + "}");
                Node.ObjectRequest("_aTemp", 2, "(Item){3}(Value){" + sValue + "}", "");
                Node.ObjectDelete("_aTemp");
                return true;
            }
        }
        return false;
    }

    public boolean SetMobileAec(int iEnable) {
        pgLibJNINode Node = m_Live.GetNode();
        if (Node != null) {
            if (Node.ObjectAdd("_aTemp", "PG_CLASS_Audio", "", 0)) {
                Node.ObjectRequest("_aTemp", 2, "(Item){11}(Value){" + iEnable + "}", "");
                Node.ObjectDelete("_aTemp");
                return true;
            }
        }
        return false;
    }

    public boolean SetDefaultMicNo(int iMicNo) {
        pgLibJNINode Node = m_Live.GetNode();
        if (Node != null) {
            if (Node.ObjectAdd("_vTemp", "PG_CLASS_Video", "", 0)) {
                int iErr = Node.ObjectRequest("_vTemp", 2, "(Item){1}(Value){" + iMicNo + "}", "");
                Node.ObjectDelete("_vTemp");
                Log.d("pgLiveCapture", "SetDefaultMicNo, iErr=" + iErr);
                return true;
            }
        }
        return false;
    }

    private void SetVideoRotate(int iCameraNo, int iAngle) {
        pgLibJNINode Node = m_Live.GetNode();
        if (Node != null) {
            if (Node.ObjectAdd("_vTemp", "PG_CLASS_Video", "", 0)) {

                int iRotate = 0;
                if (iCameraNo == CameraInfo.CAMERA_FACING_BACK) {
                    iRotate = iAngle;
                } else {
                    iRotate = 360 - iAngle;
                }

                String sValue = "(No){" + iCameraNo + "}(Rotate){" + iRotate + "}";
                Node.ObjectRequest("_vTemp", 2, "(Item){17}(Value){" + Node.omlEncode(sValue) + "}", "");
                Node.ObjectDelete("_vTemp");
            }
        }
    }

    @Override
    protected void recover() {
        if (null!=m_Live) {
            m_Live.LoginNow(0);
        }
    }
}
