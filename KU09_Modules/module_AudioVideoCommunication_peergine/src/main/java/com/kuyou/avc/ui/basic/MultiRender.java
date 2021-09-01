package com.kuyou.avc.ui.basic;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
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
import com.peergine.android.livemulti.pgLibLiveMultiError;
import com.peergine.android.livemulti.pgLibLiveMultiRender;
import com.peergine.android.livemulti.pgLibLiveMultiView;
import com.peergine.plugin.lib.pgLibJNINode;

import java.util.Date;

import kuyou.common.ku09.protocol.IJT808ExtensionProtocol;

public abstract class MultiRender extends AVCActivity implements ActivityCompat.OnRequestPermissionsResultCallback {
    protected final String TAG = "com.kuyou.avc.ui.custom > " + this.getClass().getSimpleName();

    private EditText m_editServer;
    private EditText m_editDevID;
    private Button m_btnServerSet;
    private Button m_btnStart;
    private Button m_btnStop;

    private EditText m_editMsg;
    private Button m_btnSend;

    private Button m_btnCamera;
    private Button m_btnScan;
    private Button m_btnWhole;
    private Button m_btnCut;

    private Button m_btnPut;
    private Button m_btnGet;
    private Button m_btnCancel;

    public static TextView m_sDebug;
    public static TextView m_sTransferInfo;
    public static TextView m_sStatusInfo;

    String m_sServerAddr = "connect.peergine.com:7781";
    String m_sDevID = "";

    pgLibLiveMultiRender m_Live = new pgLibLiveMultiRender();
    LinearLayout m_View = null;
    SurfaceView m_Wnd = null;

    public static void outString(String str) {
        if (MultiRender.m_sDebug.getText().length() > 600) {
            MultiRender.m_sDebug.setText("");
        }
        MultiRender.m_sDebug.setText(MultiRender.m_sDebug.getText() + "  " + str);
    }

    private boolean m_bTransfering = false;

    public void setTransfering(boolean bTransfering) {
        m_btnPut.setEnabled(!bTransfering);
        m_btnGet.setEnabled(!bTransfering);
        m_btnCancel.setEnabled(bTransfering);
        m_bTransfering = bTransfering;
    }

    public boolean getTransfering() {
        return m_bTransfering;
    }

    //MyPermission m_myPerm = new MyPermission();

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        //m_myPerm.onResult(this, requestCode, permissions[0], grantResults[0]);
    }

    private pgLibLiveMultiRender.OnEventListener m_OnEvent = new pgLibLiveMultiRender.OnEventListener() {

        @Override
        public void event(String sAct, String sData, String sCapID) {
            // TODO Auto-generated method stub

            if (sAct.equals("VideoStatus")) {
                // Video status report
            } else if (sAct.equals("Notify")) {
                // Receive the notify from capture side
                String sInfo = "Receive notify: data=" + sData;
                Log.d(TAG, "pgLibLiveMultiCapture.OnEventListener > sInfo = " + sInfo);
            } else if (sAct.equals("Message")) {
                // Receive the message from render or capture
                String sInfo = "Receive msg: data=" + sData + ", sCapID=" + sCapID;
                Log.d(TAG, "pgLibLiveMultiCapture.OnEventListener > sInfo = " + sInfo);

                int iInd = sData.indexOf("stamp:");
                if (iInd >= 0) {
                    String sStamp = sData.substring(6);
                    long iStamp1 = Long.parseLong(sStamp);
                    long iStamp2 = (new Date()).getTime();
                    long iDelta = iStamp2 - iStamp1;
                    Toast.makeText(MultiRender.this, ("Delta:" + iDelta), Toast.LENGTH_SHORT).show();
                }
            } else if (sAct.equals("Login")) {
                // Login reply
                if (sData.equals("0")) {
                    onResult(IJT808ExtensionProtocol.RESULT_SUCCESS);
                    String sInfo = "Login success";
                    Log.d(TAG, "pgLibLiveMultiCapture.OnEventListener > sInfo = " + sInfo);
                } else {
                    onResult(IJT808ExtensionProtocol.RESULT_FAIL_FAILURE_AUDIO_VIDEO_SERVER_EXCEPTION);
                    String sInfo = "Login failed, error=" + sData;
                    Log.d(TAG, "pgLibLiveMultiCapture.OnEventListener > sInfo = " + sInfo);
                }
            } else if (sAct.equals("Logout")) {
                // Logout
                String sInfo = "Logout";
                Log.d(TAG, "pgLibLiveMultiCapture.OnEventListener > sInfo = " + sInfo);
            } else if (sAct.equals("Connect")) {
                // Connect to capture
                String sInfo = "Connect to capture";
                Log.d(TAG, "pgLibLiveMultiCapture.OnEventListener > sInfo = " + sInfo);
            } else if (sAct.equals("Disconnect")) {
                // Disconnect from capture
                String sInfo = "Disconnect from capture";
                Log.d(TAG, "pgLibLiveMultiCapture.OnEventListener > sInfo = " + sInfo);
            } else if (sAct.equals("Reject")) {
                String sInfo = "Reject by capture";
                Log.d(TAG, "pgLibLiveMultiCapture.OnEventListener > sInfo = " + sInfo);
            } else if (sAct.equals("Offline")) {
                // The capture is offline.
                String sInfo = "Capture offline";
                Log.d(TAG, "pgLibLiveMultiCapture.OnEventListener > sInfo = " + sInfo);
            } else if (sAct.equals("LanScanResult")) {
                // Lan scan result.
                String sInfo = "Lan scan result: " + sData;
                Log.d(TAG, "pgLibLiveMultiCapture.OnEventListener > sInfo = " + sInfo);
                m_sTransferInfo.setText(sData);
            } else if (sAct.equals("RecordStopVideo")) {
                // Record stop video.
                String sInfo = "Record stop video: " + sData;
                Log.d(TAG, "pgLibLiveMultiCapture.OnEventListener > sInfo = " + sInfo);
            } else if (sAct.equals("RecordStopAudio")) {
                // Record stop video.
                String sInfo = "Record stop audio: " + sData;
                Log.d(TAG, "pgLibLiveMultiCapture.OnEventListener > sInfo = " + sInfo);
            } else if (sAct.equals("ForwardAllocReply")) {
                String sInfo = "Forward alloc relpy: error=" + sData;
                Log.d(TAG, "pgLibLiveMultiCapture.OnEventListener > sInfo = " + sInfo);
            } else if (sAct.equals("ForwardFreeReply")) {
                String sInfo = "Forward free relpy: error=" + sData;
                Log.d(TAG, "pgLibLiveMultiCapture.OnEventListener > sInfo = " + sInfo);
            } else if (sAct.equals("VideoCamera")) {
                String sInfo = "The picture is save to: " + sData;
                Log.d(TAG, "pgLibLiveMultiCapture.OnEventListener > sInfo = " + sInfo);
            } else if (sAct.equals("FileAccept")) {
                String sInfo = "File accept: " + sData;
                Log.d(TAG, "pgLibLiveMultiCapture.OnEventListener > sInfo = " + sInfo);
            } else if (sAct.equals("FileReject")) {
                FileReject();
            } else if (sAct.equals("FileAbort")) {
                // 取消 上传、下载
                FileAbort();
            } else if (sAct.equals("FileFinish")) {
                // 文件传输完毕
                FileFinish();
            } else if (sAct.equals("FileProgress")) {
                FileProgress(sData);
            } else if (sAct.equals("SvrNotify")) {
                String sInfo = "Receive server notify: " + sData;
                Log.d(TAG, "pgLibLiveMultiCapture.OnEventListener > sInfo = " + sInfo);
            } else if (sAct.equals("PeerInfo")) {
                String sInfo = "PeerInfo: " + sData;
                Log.d(TAG, "pgLibLiveMultiCapture.OnEventListener > sInfo = " + sInfo);
            }

            Log.d("pgLiveRender", "OnEvent: Act=" + sAct + ", Data=" + sData
                    + ", sCapID=" + sCapID);
        }
    };

    private void FileProgress(String sData) {
        String sPath = getcontent(sData, "path");
        String sReqSize = getcontent(sData, "total");
        String sCurSize = getcontent(sData, "position");
        m_sStatusInfo.setText("本地文件路径:" + sPath + " 已传输长度" + sCurSize + "/文件总长度" + sReqSize);
    }

    private void FileReject() {
        setTransfering(false);
        m_sTransferInfo.setText("对方拒绝了文件传输...");
    }

    private void FileAbort() {
        setTransfering(false);
        m_sTransferInfo.setText("文件传输中断传输...");
    }

    private void FileFinish() {
        setTransfering(false);
        m_sTransferInfo.setText("文件传输完毕...");
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

        m_editServer = (EditText) findViewById(R.id.editServer);
        m_editDevID = (EditText) findViewById(R.id.editDevID);
        m_sTransferInfo = (TextView) findViewById(R.id.TransferInfo);
        m_sStatusInfo = (TextView) findViewById(R.id.StatusInfo);
        m_sDebug = (TextView) findViewById(R.id.debug);

        m_btnServerSet = (Button) findViewById(R.id.btnServerSet);
        m_btnServerSet.setOnClickListener(m_OnClink);
        m_btnStart = (Button) findViewById(R.id.btnStart);
        m_btnStart.setOnClickListener(m_OnClink);
        m_btnStop = (Button) findViewById(R.id.btnStop);
        m_btnStop.setOnClickListener(m_OnClink);

        m_editMsg = (EditText) findViewById(R.id.editMsg);
        m_btnSend = (Button) findViewById(R.id.btnSend);
        m_btnSend.setOnClickListener(m_OnClink);

        m_btnCamera = (Button) findViewById(R.id.btnCamera);
        m_btnCamera.setOnClickListener(m_OnClink);
        m_btnScan = (Button) findViewById(R.id.btnScan);
        m_btnScan.setOnClickListener(m_OnClink);
        m_btnWhole = (Button) findViewById(R.id.btnViewWhole);
        m_btnWhole.setOnClickListener(m_OnClink);
        m_btnCut = (Button) findViewById(R.id.btnViewCut);
        m_btnCut.setOnClickListener(m_OnClink);

        LinearLayout layout = (LinearLayout) findViewById(R.id.layoutTransferBtn);
        m_btnPut = (Button) layout.findViewById(R.id.btnPut);
        m_btnPut.setOnClickListener(m_OnClink);
        m_btnGet = (Button) findViewById(R.id.btnGet);
        m_btnGet.setOnClickListener(m_OnClink);

        m_btnCancel = (Button) findViewById(R.id.btnCancel);
        m_btnCancel.setOnClickListener(m_OnClink);
        m_btnCancel.setEnabled(false);

        if (!CheckPlugin()) {
            return;
        }

        m_editServer.setText(m_sServerAddr);
        m_editDevID.requestFocus();

        m_Live.SetEventListener(m_OnEvent);

        String[] sPermList = {Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        String[] sTextList = {"麦克风", "写存储"};
        //m_myPerm.Request(this, sPermList, sTextList);

        LiveLogin();
    }

    public void onDestroy() {
        LiveLogout();
        super.onDestroy();
    }

    @Override
    protected void onStart() {
        super.onStart();
        m_Live.LoginNow(0);
        Toast.makeText(MultiRender.this, "Enter foreground, login now", Toast.LENGTH_SHORT).show();
    }

    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            Log.d(TAG, "onKeyDown, KEYCODE_BACK");
            ExitDialog();
            return true;
        }
        return super.onKeyUp(keyCode, event);
    }

    private DialogInterface.OnClickListener m_DlgClick = new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int which) {
            if (which == AlertDialog.BUTTON_POSITIVE) {
                LiveLogout();
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

    protected void LiveConnect() {
        m_sDevID = getConfig().getDevCollectingEndId();

        m_Live.Connect(m_sDevID);
        //m_Live.VideoStart(m_sDevID, 0, "", m_Wnd);

        String sAudioParam = "";

        // On the low-end device, you can use this configure to enable the AEC of minified version.
        //String sAudioParam = "(AecConfig){1,-1,-1,-1,-1}";

        m_Live.AudioStart(m_sDevID, 0, sAudioParam);
        m_Live.AudioSyncDelay(m_sDevID, 0, 0);
    }

    protected void LiveDisconnect() {
        if (getTransfering()) {
            btnCancel();
            setTransfering(false);
        }

        m_Live.AudioStop(m_sDevID, 0);
        m_Live.VideoStop(m_sDevID, 0);
        m_Live.Disconnect(m_sDevID);
    }

    private void LiveLogin() {
        String sInitParam = "(Debug){1}(VideoSoftDecode){1}";

        int iErr = m_Live.Initialize("ANDROID_DEMO", "1234",
                m_sServerAddr, "", 1, sInitParam, this);
        if (iErr != pgLibLiveMultiError.PG_ERR_Normal) {
            Log.e(TAG, "LiveStart: Live.Initialize failed! iErr=" + iErr);
            onResult(IJT808ExtensionProtocol.RESULT_FAIL_FAILURE_AUDIO_VIDEO_SERVER_EXCEPTION);
            exit();
            return;
        }

        m_Wnd = (SurfaceView) pgLibLiveMultiView.Get("view0");
        m_View = (LinearLayout) findViewById(R.id.layoutVideo);
        m_View.addView(m_Wnd);
        m_Wnd.setVisibility(View.VISIBLE);

        /* Customize: Chuangan
        m_Live.VideoModeSize(12, 640, 360);
        m_Live.VideoModeSize(13, 704, 480);
        m_Live.VideoModeSize(14, 720, 480);
        m_Live.VideoModeSize(15, 720, 576);
        m_Live.VideoModeSize(16, 960, 540);
        m_Live.VideoModeSize(17, 960, 720);
        m_Live.VideoModeSize(18, 1280, 960);
        m_Live.VideoModeSize(19, 1600, 1200);
        m_Live.VideoModeSize(20, 2048, 1520);
        m_Live.VideoModeSize(21, 2160, 1080);
        m_Live.VideoModeSize(22, 2160, 1200);
        m_Live.VideoModeSize(23, 2592, 1520);
        m_Live.VideoModeSize(24, 2592, 1920);
        m_Live.VideoModeSize(25, 3200, 2400);
        m_Live.VideoModeSize(26, 3840, 2160);
        m_Live.VideoModeSize(27, 4000, 3000);
        */
        
        /* Customize: Ledong
        m_Live.VideoModeSize(12, 1280, 960);
        */

        //SetVolumeGate(1);
        //SetMobileAec(0);
    }

    private void LiveLogout() {
        LiveDisconnect();
        if (m_Wnd != null) {
            m_View.removeView(m_Wnd);
            pgLibLiveMultiView.Release(m_Wnd);
            m_View = null;
            m_Wnd = null;
            m_Live.Clean();
        }
    }

    protected void LiveServerSet() {
        String sServerAddr = getConfig().getServerAddress();
        sServerAddr = sServerAddr.trim();
        if (!sServerAddr.equals("") && !sServerAddr.equals(m_sServerAddr)) {
            m_sServerAddr = sServerAddr;
            LiveLogout();
            LiveLogin();
        }
    }

    private View.OnClickListener m_OnClink = new View.OnClickListener() {
        // Control clicked
        public void onClick(View args0) {
            switch (args0.getId()) {
                case R.id.btnServerSet:
                    LiveServerSet();
                    break;

                case R.id.btnStart:
                    LiveConnect();
                    break;

                case R.id.btnStop:
                    LiveDisconnect();
                    break;

                case R.id.btnSend:
                    btnSend();
                    break;

                case R.id.btnCamera:
                    m_Live.VideoCamera(m_sDevID, 0, "/sdcard/Download/liverender.jpg");
                    break;

                case R.id.btnScan:
                    m_Live.LanScanStart();
                    break;

                case R.id.btnViewWhole:
                    m_Live.VideoShowMode(1);
                    break;

                case R.id.btnViewCut:
                    m_Live.VideoShowMode(0);
                    break;

                case R.id.btnPut:
                    btnPut();
                    break;

                case R.id.btnGet:
                    btnGet();
                    break;

                case R.id.btnCancel:
                    btnCancel();
                    break;

                default:
                    break;
            }
        }
    };

    private void btnSend() {
        String sMsg = m_editMsg.getText().toString();

        // Test message transfer delay stamp.
        if (sMsg.equals("stamp")) {
            Date date = new Date();
            sMsg = "stamp:" + date.getTime();
        }

        m_Live.MessageSend(m_sDevID, sMsg);

        Log.d(TAG, "btnSend: sMsg = " + sMsg);
        
        /*
        if (sMsg.equals("+")) {
            LinearLayout.LayoutParams linearParams = (LinearLayout.LayoutParams)m_View.getLayoutParams();
            linearParams.height += 50;
            m_View.setLayoutParams(linearParams);    
        }
        else if (sMsg.equals("-")) {
            LinearLayout.LayoutParams linearParams = (LinearLayout.LayoutParams)m_View.getLayoutParams();
            if (linearParams.height > 100) {
                linearParams.height -= 50;
                m_View.setLayoutParams(linearParams);
            }
        }

        if (sMsg.equals("rotate=90")) {
            m_Live.VideoParam(m_sDevID, 0, "(OutputRotate){90}");
        }
        else if (sMsg.equals("rotate=180")) {
            m_Live.VideoParam(m_sDevID, 0, "(OutputRotate){180}");
        }
        else if (sMsg.equals("rotate=270")) {
            m_Live.VideoParam(m_sDevID, 0, "(OutputRotate){270}");
        }
        */

        // Just for test
        if (sMsg.equals("recstart")) {
            m_Live.RecordStart(m_sDevID, "/sdcard/Download/liverender.mp4", 0, 0);
        } else if (sMsg.equals("recstop")) {
            m_Live.RecordStop(m_sDevID);
        } else if (sMsg.equals("muteon")) {
            m_Live.AudioMute(m_sDevID, 0, false, true);
        } else if (sMsg.equals("muteoff")) {
            m_Live.AudioMute(m_sDevID, 0, false, false);
        } else if (sMsg.equals("videocamera")) {
            m_Live.VideoCamera(m_sDevID, 0, "/sdcard/Download/liverender.jpg");
        }
    }

    private void btnPut() {
        String sPath = "/sdcard/Download/pgtest";
        int iErr = m_Live.FilePutRequest2(m_sDevID, sPath, "", 0, 0);
        if (iErr > 0) {
            Log.d(TAG, "btnPut: iErr = " + iErr);
            outString("btnPut error: " + iErr);
        } else {
            setTransfering(true);
            m_sTransferInfo.setText("您正在请求上传文件...");
        }
    }

    private void btnGet() {
        String sPath = "/sdcard/Download/pgtest";
        int iErr = m_Live.FileGetRequest2(m_sDevID, sPath, "", 0, 0);
        if (iErr > 0) {
            Log.d(TAG, "btnGet: iErr = " + iErr);
            outString("btnGet error: " + iErr);
        } else {
            setTransfering(true);
            m_sTransferInfo.setText("您正在请求下载文件...");
        }
    }

    private void btnCancel() {
        int iErr = m_Live.FileCancel(m_sDevID);
        if (iErr > 0) {
            Log.d(TAG, "btnCancel: iErr = " + iErr);
        } else {
            setTransfering(false);
            m_sTransferInfo.setText("取消文件传输");
        }
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
    //@{ added by wgx Usefulness: 
    
    @Override
    public void recover() {
        super.recover();
        if (null!=m_Live) {
            m_Live.LoginNow(0);
        }
    }
    //}@ end wgx

}
